package uz.alien.dictup.presentation.features.lesson

import android.animation.Animator
import android.animation.ValueAnimator
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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonFragmentBaseBinding
import uz.alien.dictup.presentation.features.lesson.pager.WordPagerAdapter
import kotlin.math.abs

class BaseFragment : Fragment() {

    private var _binding: LessonFragmentBaseBinding? = null
    private val binding get() = _binding!!
    lateinit var wordPagerAdapter: WordPagerAdapter
    private val viewModel: LessonViewModel by activityViewModels()

    private var position = 0
    private var startY = 0f
    private var endY = 0f
    private val threshold = 50
    private val touchSlop by lazy { ViewConfiguration.get(requireContext()).scaledTouchSlop }
    private var startBounds = Rect()
    private var finalBounds = Rect()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LessonFragmentBaseBinding.inflate(inflater, container, false)

        position = arguments?.getInt(ARG_POSITION, 0)!!
        startBounds = arguments?.getParcelable(ARG_BOUNDS) ?: Rect()

        binding.vpWord.isVisible = false

        val fragments = ArrayList<Fragment>()
        viewModel.words.value.forEach {
            fragments.add(WordFragment.newInstance(it))
        }
        fragments.add(LastFragment.newInstance())

        val fragmentActivity = requireActivity()
        wordPagerAdapter = WordPagerAdapter(fragmentActivity, fragments)

        binding.vpWord.adapter = wordPagerAdapter
        binding.vpWord.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.vpWord.setCurrentItem(position, false)

        // Sahifa o'zgarishini kuzatish
        binding.vpWord.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Yangi sahifa tanlanganda startBounds ni yangilash
                if (position < fragments.size - 1) { // LastFragment dan tashqari
                    (activity as? LessonActivity)?.let { activity ->
                        val bounds = activity.itemBoundsMap[position]
                        bounds?.let { startBounds = it }
                    }
                }
            }
        })

        binding.vpWord.setPageTransformer { page, position ->
            page.translationY = position * -50f
            page.scaleX = 1f - abs(position) * 0.1f
            page.scaleY = 1f - abs(position) * 0.1f
            page.alpha = 1f - abs(position) * 0.2f
        }

        binding.root.post {
            finalBounds.set(0, 0, binding.root.width, binding.root.height)
            startEnterAnimation()
        }

        val recyclerView = binding.vpWord.getChildAt(0) as? RecyclerView
        recyclerView?.setOnTouchListener { _, event ->
            val isFirstPage = binding.vpWord.currentItem == 0
            val isLastPage = binding.vpWord.currentItem == wordPagerAdapter.itemCount - 1
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

        return binding.root
    }

    private fun startEnterAnimation() {

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
            onStart = { binding.vpWord.isVisible = true },
            onEnd = { startScrollIndicatorAnimation() }
        )
    }

    private fun startScrollIndicatorAnimation() {

        val recyclerView = binding.vpWord.getChildAt(0) as? RecyclerView ?: return

        // Animatsiya uchun ValueAnimator
//        val animator = ValueAnimator.ofFloat(0f, -100f, 0f, -100f, 0f) // Ikki marta yuqoriga-pastga
//        animator.duration = 3000 // Umumiy davomiylik (1.5 sekund)
//        animator.interpolator = OvershootInterpolator(0.5f) // Tabiiy tebranish effekti
//        animator.addUpdateListener { valueAnimator ->
//            val translationY = valueAnimator.animatedValue as Float
//            recyclerView.translationY = translationY
//        }
//
//        animator.startDelay = 300 // Kirish animatsiyasidan keyin biroz kechikish
//        animator.start()
    }

    private fun animateBounds(
        view: View,
        fromBounds: Rect,
        toBounds: Rect,
        duration: Long,
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {}
    ) {

        val params = view.layoutParams as FrameLayout.LayoutParams
        val animator = ValueAnimator.ofFloat(0f, 1f)

        animator.duration = duration
        animator.interpolator = AccelerateInterpolator(2f)

        animator.addUpdateListener { valueAnimator ->

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
//            binding.root.elevation = AccelerateInterpolator(10f).getInterpolation(fraction)
            binding.flBackground.elevation = 4f * resources.displayMetrics.density * DecelerateInterpolator(3f).getInterpolation(fraction)
            (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = fraction
            binding.vBackground.alpha = fraction
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                onStart()
                binding.flBackground.background = requireContext().getDrawable(R.drawable.lesson_item_unit_background)
                (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 0f
                binding.vBackground.alpha = 0f
                binding.tvItemWord.alpha = 1f
                binding.flBackground.elevation = 0f
                binding.tvItemWord.text = viewModel.words.value[getCurrentPage()].word
                if (viewModel.words.value[getCurrentPage()].score < 0) {
                    binding.tvItemWord.setTextColor(binding.tvItemWord.context.getColor(R.color.red_500))
                } else if (viewModel.words.value[getCurrentPage()].score >= 5) {
                    binding.tvItemWord.setTextColor(binding.tvItemWord.context.getColor(R.color.green_700))
                } else {
                    binding.tvItemWord.setTextColor(binding.tvItemWord.context.getColor(R.color.secondary_text))
                }
            }
            override fun onAnimationEnd(animation: Animator) {
                onEnd()
                (binding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f
                binding.flBackground.elevation = 4 * resources.displayMetrics.density
                binding.vBackground.alpha = 1f
                binding.tvItemWord.alpha = 0f
                binding.vpWord.offscreenPageLimit = 2
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }

    private fun dismissWithAnimation() {
        (activity as? LessonActivity)?.dismissFragmentWithAnimation(this, startBounds)
    }

    fun getCurrentPage(): Int {
        return binding.vpWord.currentItem
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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