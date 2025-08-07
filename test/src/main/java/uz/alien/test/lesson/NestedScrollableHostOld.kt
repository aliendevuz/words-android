package uz.alien.test.lesson

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.isNotEmpty
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.absoluteValue
import kotlin.math.sign

class NestedScrollableHostOld(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

  private var touchSlop = 0
  private var initialY = 0f
  private val parentViewPager: ViewPager2?
    get() {
      var v: View? = parent as? View
      while (v != null && v !is ViewPager2) {
        v = v.parent as? View
      }
      return v
    }

  private val child: View? get() = if (childCount > 0) getChildAt(0) else null

  init {
    touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  }

  private fun canChildScrollVertically(direction: Int): Boolean {
    return child?.canScrollVertically(direction) ?: false
  }

  override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
    handleInterceptTouchEvent(e)
    return super.onInterceptTouchEvent(e)
  }

  private fun handleInterceptTouchEvent(e: MotionEvent) {
    val orientation = parentViewPager?.orientation ?: return

    if (orientation != ViewPager2.ORIENTATION_VERTICAL) return

    // Agar ViewPager2 scroll qila olmasa, nazoratni o'zimizga olamiz
    if (!canChildScrollVertically(-1) && !canChildScrollVertically(1)) {
      parent.requestDisallowInterceptTouchEvent(false)
      return
    }

    when (e.action) {
      MotionEvent.ACTION_DOWN -> {
        initialY = e.y
        parent.requestDisallowInterceptTouchEvent(true)
      }
      MotionEvent.ACTION_MOVE -> {
        val dy = e.y - initialY
        val scaledDy = dy.absoluteValue

        if (scaledDy > touchSlop) {
          val direction = -dy.sign.toInt()
          if (canChildScrollVertically(direction)) {
            parent.requestDisallowInterceptTouchEvent(true)
          } else {
            parent.requestDisallowInterceptTouchEvent(false)
          }
        }
      }
    }
  }
}