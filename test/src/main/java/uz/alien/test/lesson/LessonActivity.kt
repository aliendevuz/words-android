package uz.alien.test.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import uz.alien.test.databinding.LessonActivityBinding
import uz.alien.test.lesson.recycler_adapter.WordAdapter

class LessonActivity : AppCompatActivity() {

    private lateinit var binding: LessonActivityBinding
    private val viewModel: LessonViewModel by viewModels()
    private var fragmentBounds: Rect? = null
    private var fragmentView: View? = null
    val itemBoundsMap = mutableMapOf<Int, Rect>() // Element joylashuvlarini saqlash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LessonActivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = WordAdapter { position, view ->
            // Element joylashuvini olish
            val bounds = Rect()
            view.getGlobalVisibleRect(bounds)
            itemBoundsMap[position] = bounds // Joylashuvni saqlash

            val fragment = PagerFragment.newInstance(position, bounds)
            fragmentBounds = bounds
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvWords.layoutManager = GridLayoutManager(this, 2)
        binding.rvWords.adapter = adapter

        // RecyclerView elementlarining joylashuvlarini yangilash
        binding.rvWords.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            adapter.currentList.forEachIndexed { index, _ ->
                val viewHolder = binding.rvWords.findViewHolderForAdapterPosition(index)
                viewHolder?.itemView?.let { view ->
                    val bounds = Rect()
                    view.getGlobalVisibleRect(bounds)
                    itemBoundsMap[index] = bounds
                }
            }
        }

        lifecycleScope.launch {
            viewModel.words.collect {
                adapter.submitList(it.map { word -> word.word })
            }
        }
    }

    fun dismissFragmentWithAnimation(fragment: PagerFragment, targetBounds: Rect?) {
        val view = fragment.view ?: return
        fragmentView = view
        val startBounds = targetBounds ?: fragmentBounds ?: return
        val finalBounds = Rect(0, 0, view.width, view.height)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator(1.5f)

        val params = view.layoutParams as FrameLayout.LayoutParams
        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val newLeft = finalBounds.left + (startBounds.left - finalBounds.left) * fraction
            val newTop = finalBounds.top + (startBounds.top - finalBounds.top) * fraction
            val newWidth = finalBounds.width() + (startBounds.width() - finalBounds.width()) * fraction
            val newHeight = finalBounds.height() + (startBounds.height() - finalBounds.height()) * fraction

            params.leftMargin = newLeft.toInt()
            params.topMargin = newTop.toInt()
            params.width = newWidth.toInt()
            params.height = newHeight.toInt()
            view.layoutParams = params
//            view.alpha = 1f - (0.2f * fraction)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
//                view.findViewById<View>(R.id.vpWord)?.isVisible = false
            }
            override fun onAnimationEnd(animation: Animator) {
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
                fragmentView = null
                fragmentBounds = null
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content) as? PagerFragment
        if (fragment != null) {
            // Joriy sahifa indeksiga mos bounds ni olish
            val currentPage = fragment.getCurrentPage() // Bu metodni PagerFragment da qo'shamiz
            val targetBounds = itemBoundsMap[currentPage]
            dismissFragmentWithAnimation(fragment, targetBounds)
        } else {
            super.onBackPressed()
        }
    }
}