package uz.alien.test.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.LessonFragmentPagerBinding
import uz.alien.test.fragment.FragmentActivity
import uz.alien.test.lesson.pager_adapter.WordPagerAdapter
import kotlin.math.abs

class PagerFragment : Fragment() {

    private var _binding: LessonFragmentPagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var wordPagerAdapter: WordPagerAdapter
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
        _binding = LessonFragmentPagerBinding.inflate(inflater, container, false)

        position = arguments?.getInt(ARG_POSITION, 0)!!
        startBounds = arguments?.getParcelable(ARG_BOUNDS) ?: Rect()

        binding.root.background = requireContext().getDrawable(R.drawable.item_background)
        binding.vpWord.isVisible = false

        val fragments = mutableListOf<Fragment>()
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

        binding.root.setOnClickListener {
            dismissWithAnimation()
        }

        return binding.root
    }

    private fun startEnterAnimation() {
        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.leftMargin = startBounds.left
        params.topMargin = startBounds.top
        params.width = startBounds.width()
        params.height = startBounds.height()
        binding.root.layoutParams = params
        binding.root.alpha = 1.0f

        animateBounds(
            view = binding.root,
            fromBounds = startBounds,
            toBounds = finalBounds,
            duration = 300,
            onStart = { binding.vpWord.isVisible = true },
            onEnd = { startScrollIndicatorAnimation() }
        )
    }

    private fun startScrollIndicatorAnimation() {

        val recyclerView = binding.vpWord.getChildAt(0) as? RecyclerView ?: return

        // Animatsiya uchun ValueAnimator
        val animator = ValueAnimator.ofFloat(0f, -100f, 0f, -100f, 0f) // Ikki marta yuqoriga-pastga
        animator.duration = 3000 // Umumiy davomiylik (1.5 sekund)
        animator.interpolator = OvershootInterpolator(0.5f) // Tabiiy tebranish effekti
        animator.addUpdateListener { valueAnimator ->
            val translationY = valueAnimator.animatedValue as Float
            recyclerView.translationY = translationY
        }

        animator.startDelay = 300 // Kirish animatsiyasidan keyin biroz kechikish
        animator.start()
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
        animator.interpolator = DecelerateInterpolator(1.5f)
        view.visibility = View.VISIBLE

        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val newLeft = fromBounds.left + (toBounds.left - fromBounds.left) * fraction
            val newTop = fromBounds.top + (toBounds.top - fromBounds.top) * fraction
            val newWidth = fromBounds.width() + (toBounds.width() - fromBounds.width()) * fraction
            val newHeight = fromBounds.height() + (toBounds.height() - fromBounds.height()) * fraction

            params.leftMargin = newLeft.toInt()
            params.topMargin = newTop.toInt()
            params.width = newWidth.toInt()
            params.height = newHeight.toInt()
            view.layoutParams = params
//            view.alpha = 0.8f + (0.2f * fraction)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                onStart()
            }
            override fun onAnimationEnd(animation: Animator) {
                onEnd()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }

    private fun dismissWithAnimation() {
        val isLastFragment = binding.vpWord.currentItem == wordPagerAdapter.itemCount - 1
        if (isLastFragment) {
            binding.vpWord.setCurrentItem(wordPagerAdapter.itemCount - 2, false)
        }
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

        fun newInstance(position: Int, bounds: Rect): PagerFragment {
            val fragment = PagerFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            args.putParcelable(ARG_BOUNDS, bounds)
            fragment.arguments = args
            return fragment
        }
    }
}