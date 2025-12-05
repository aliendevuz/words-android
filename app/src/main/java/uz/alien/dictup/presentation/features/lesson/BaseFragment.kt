package uz.alien.dictup.presentation.features.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonFragmentBaseBinding
import uz.alien.dictup.presentation.features.lesson.pager.WordPagerAdapter
import kotlin.math.abs

class BaseFragment : Fragment() {

    private var _binding: LessonFragmentBaseBinding? = null
    // Safe binding accessor with null check
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private var wordPagerAdapter: WordPagerAdapter? = null
    private val viewModel: LessonViewModel by activityViewModels()

    private var position = 0
    private var startY = 0f
    private var endY = 0f
    private val threshold = 50
    private val touchSlop by lazy { ViewConfiguration.get(requireContext()).scaledTouchSlop }
    private var startBounds = Rect()
    private var finalBounds = Rect()

    // Animation state
    private var isAnimating = false
    private var currentAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LessonFragmentBaseBinding.inflate(inflater, container, false)

        position = arguments?.getInt(ARG_POSITION, 0) ?: 0
        startBounds = arguments?.getParcelable(ARG_BOUNDS) ?: Rect()

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.vpWord.isVisible = false

        val fragments = ArrayList<Fragment>()
        val wordsList = viewModel.words.value

        // Safe iteration with null checks
        wordsList.forEach { word ->
            fragments.add(WordFragment.newInstance(word))
        }
        fragments.add(LastFragment.newInstance())

        val fragmentActivity = activity
        if (fragmentActivity == null || fragmentActivity.isFinishing) {
            return
        }

        wordPagerAdapter = WordPagerAdapter(fragmentActivity, fragments)

        binding.vpWord.apply {
            adapter = wordPagerAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            setCurrentItem(position, false)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateStartBounds(position, fragments.size)
                }
            })

            setPageTransformer { page, pos ->
                page.translationY = pos * -50f
                page.scaleX = 1f - abs(pos) * 0.1f
                page.scaleY = 1f - abs(pos) * 0.1f
                page.alpha = 1f - abs(pos) * 0.2f
            }
        }

        binding.root.post {
            if (_binding != null && isAdded) {
                finalBounds.set(0, 0, binding.root.width, binding.root.height)
                startEnterAnimation()
            }
        }

        setupTouchListener()
    }

    private fun updateStartBounds(position: Int, fragmentCount: Int) {
        if (position < fragmentCount - 1) {
            (activity as? LessonActivity)?.let { activity ->
                val bounds = activity.itemBoundsMap[position]
                bounds?.let { startBounds = it }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        val recyclerView = binding.vpWord.getChildAt(0) as? RecyclerView ?: return

        recyclerView.setOnTouchListener { _, event ->
            if (isAnimating) return@setOnTouchListener true

            val adapter = wordPagerAdapter ?: return@setOnTouchListener false
            val currentItem = binding.vpWord.currentItem
            val isFirstPage = currentItem == 0
            val isLastPage = currentItem == adapter.itemCount - 1
            val canScrollDown = recyclerView.canScrollVertically(1)
            val canScrollUp = recyclerView.canScrollVertically(-1)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = event.y - startY
                    if ((isFirstPage && dy > touchSlop && !canScrollDown) ||
                        (isLastPage && dy < -touchSlop && !canScrollUp)
                    ) {
                        recyclerView.requestDisallowInterceptTouchEvent(true)
                        true
                    } else {
                        recyclerView.requestDisallowInterceptTouchEvent(false)
                        false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    endY = event.y
                    val deltaY = endY - startY

                    if (abs(deltaY) > threshold) {
                        when {
                            !canScrollDown || !canScrollUp -> {
                                dismissWithAnimation()
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
    }

    private fun startEnterAnimation() {
        if (_binding == null || !isAdded || isAnimating) return

        val params = binding.flBackground.layoutParams as FrameLayout.LayoutParams

        params.leftMargin = startBounds.left
        params.topMargin = startBounds.top
        params.width = startBounds.width()
        params.height = startBounds.height()
        binding.flBackground.layoutParams = params

        animateBounds(
            view = binding.flBackground,
            fromBounds = startBounds,
            toBounds = finalBounds,
            duration = BuildConfig.DURATION * 2,
            onStart = {
                if (_binding != null) {
                    binding.vpWord.isVisible = true
                }
            },
            onEnd = {
                if (_binding != null && isAdded) {
                    binding.vpWord.offscreenPageLimit = 2
                }
            }
        )
    }

    private fun animateBounds(
        view: View,
        fromBounds: Rect,
        toBounds: Rect,
        duration: Long,
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {}
    ) {
        if (_binding == null || !isAdded) return

        // Cancel any existing animation
        currentAnimator?.cancel()

        isAnimating = true
        val params = view.layoutParams as FrameLayout.LayoutParams
        val animator = ValueAnimator.ofFloat(0f, 1f)

        animator.duration = duration
        animator.interpolator = AccelerateInterpolator(2f)

        animator.addUpdateListener { valueAnimator ->
            if (_binding == null || !isAdded) {
                valueAnimator.cancel()
                return@addUpdateListener
            }

            val fraction = valueAnimator.animatedFraction
            val newLeft = fromBounds.left + (toBounds.left - fromBounds.left) * fraction
            val newTop = fromBounds.top + (toBounds.top - fromBounds.top) * fraction
            val newWidth = fromBounds.width() + (toBounds.width() - fromBounds.width()) * fraction
            val newHeight = fromBounds.height() + (toBounds.height() - fromBounds.height()) * fraction

            params.leftMargin = newLeft.toInt() + 1
            params.topMargin = newTop.toInt() + 1
            params.width = newWidth.toInt()
            params.height = newHeight.toInt()
            view.layoutParams = params

            binding.tvItemWord.alpha = 1f - fraction
            binding.flBackground.elevation = 4f * resources.displayMetrics.density *
                    DecelerateInterpolator(3f).getInterpolation(fraction)
            (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = fraction
            binding.vBackground.alpha = fraction
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (_binding == null || !isAdded) {
                    animation.cancel()
                    return
                }

                onStart()
                binding.flBackground.background = requireContext().getDrawable(R.drawable.lesson_item_unit_background)
                (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 0f
                binding.vBackground.alpha = 0f
                binding.tvItemWord.alpha = 1f
                binding.flBackground.elevation = 0f

                // Safe access to words list
                val wordsList = viewModel.words.value
                val currentPage = getCurrentPage()
                if (currentPage >= 0 && currentPage < wordsList.size) {
                    val currentWord = wordsList[currentPage]
                    binding.tvItemWord.text = currentWord.word

                    val color = when {
                        currentWord.score < 0 -> R.color.red_500
                        currentWord.score >= 5 -> R.color.text_highlight
                        else -> R.color.secondary_text
                    }
                    binding.tvItemWord.setTextColor(binding.tvItemWord.context.getColor(color))
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (_binding != null && isAdded) {
                    (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f
                    binding.flBackground.elevation = 4 * resources.displayMetrics.density
                    binding.vBackground.alpha = 1f
                    binding.tvItemWord.alpha = 0f
                }
                isAnimating = false
                currentAnimator = null
                onEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimating = false
                currentAnimator = null
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        currentAnimator = animator
        animator.start()
    }

    fun dismissWithAnimation(onDone: () -> Unit = {}) {
        if (isAnimating) return

        // Use lifecycle-aware coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                (activity as? LessonActivity)?.dismissFragmentWithAnimation(
                    this@BaseFragment,
                    startBounds,
                    onDone
                )
            } catch (e: Exception) {
                // Handle exception silently
            }
        }
    }

    fun getCurrentPage(): Int {
        return if (_binding != null) binding.vpWord.currentItem else 0
    }

    fun setCurrentPage(position: Int) {
        if (_binding != null && isAdded) {
            val adapter = wordPagerAdapter ?: return
            if (position >= 0 && position < adapter.itemCount) {
                binding.vpWord.setCurrentItem(position, true)
            }
        }
    }

    fun getWordPagerAdapter(): WordPagerAdapter? = wordPagerAdapter

    override fun onDestroyView() {
        currentAnimator?.cancel()
        currentAnimator = null
        wordPagerAdapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_POSITION = "arg_position"
        private const val ARG_BOUNDS = "arg_bounds"

        fun newInstance(position: Int, bounds: Rect): BaseFragment {
            val fragment = BaseFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            args.putParcelable(ARG_BOUNDS, bounds)
            fragment.arguments = args
            return fragment
        }
    }
}