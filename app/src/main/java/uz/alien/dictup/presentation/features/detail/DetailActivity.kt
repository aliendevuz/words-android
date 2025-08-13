package uz.alien.dictup.presentation.features.detail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import uz.alien.dictup.databinding.DetailActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity

class DetailActivity : BaseActivity() {

  private lateinit var binding: DetailActivityBinding
  private var lastDragOffset = 0f

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = DetailActivityBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)

    val book = intent.getIntExtra("book", 0)
    val pick = intent.getIntExtra("pick", 0)
    val unit = intent.getIntExtra("unit", 0)
    val word = intent.getIntExtra("word", 0)

    binding.root.orientation = ViewPager2.ORIENTATION_VERTICAL

    binding.root.adapter = AdapterPagerDetails(this)
    binding.root.setCurrentItem(word, false)


    Handler(Looper.getMainLooper()).postDelayed({
      animateViewPagerSwipe(binding.root)
    }, 500)
  }

  private fun animateViewPagerSwipe(viewPager: ViewPager2) {
    val animator = ValueAnimator.ofFloat(0f, -300f) // minus: pastga swipe
    animator.duration = 700
    animator.interpolator = AccelerateDecelerateInterpolator()

    animator.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator) {
        viewPager.beginFakeDrag()
      }

      override fun onAnimationEnd(animation: Animator) {
        if (viewPager.isFakeDragging) {
          viewPager.endFakeDrag()
        }
      }
    })

    animator.addUpdateListener { valueAnimator ->
      val dragValue = valueAnimator.animatedValue as Float
      if (viewPager.isFakeDragging) {
        viewPager.fakeDragBy(dragValue - lastDragOffset)
        lastDragOffset = dragValue
      }
    }

    lastDragOffset = 0f
    animator.start()
  }


  override fun finish() {
    super.finish()
    applyExitZoomTransition()
  }
}