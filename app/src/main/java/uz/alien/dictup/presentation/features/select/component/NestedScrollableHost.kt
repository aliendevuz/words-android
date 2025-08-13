package uz.alien.dictup.presentation.features.select.component

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

class NestedScrollableHost(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

  private var touchSlop = 0
  private var initialX = 0f
  private var initialY = 0f
  private val parentViewPager: ViewPager2?
    get() {
      var v: View? = parent as? View
      while (v != null && v !is ViewPager2) {
        v = v.parent as? View
      }
      return v
    }

  private val child: View? get() = if (isNotEmpty()) getChildAt(0) else null
  private val isChildRecyclerView = child is SelectableRecyclerView

  init {
    touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  }

  private fun canChildScroll(orientation: Int, delta: Float): Boolean {
    val direction = -delta.sign.toInt()
    return when (orientation) {
      0 -> child?.canScrollHorizontally(direction) ?: false
      1 -> child?.canScrollVertically(direction) ?: false
      else -> throw kotlin.IllegalArgumentException()
    }
  }

  override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
    handleInterceptTouchEvent(e)
    return super.onInterceptTouchEvent(e)
  }

  private fun handleInterceptTouchEvent(e: MotionEvent) {
    val orientation = parentViewPager?.orientation ?: return

    if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
      return
    }

    when (e.action) {
      MotionEvent.ACTION_DOWN -> {
        initialX = e.x
        initialY = e.y
        parent.requestDisallowInterceptTouchEvent(true)
      }
      MotionEvent.ACTION_MOVE -> {
        val dx = e.x - initialX
        val dy = e.y - initialY
        val isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL

        val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
        val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

        if (scaledDx > touchSlop || scaledDy > touchSlop) {
          if (isVpHorizontal == (scaledDy > scaledDx)) {
            if (isChildRecyclerView) {
              parent.requestDisallowInterceptTouchEvent(false)
            }
            parentViewPager?.isUserInputEnabled = false
          } else {
            if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
              parent.requestDisallowInterceptTouchEvent(true)
            } else {
              if (isChildRecyclerView) {
                parent.requestDisallowInterceptTouchEvent(false)
              }
              parentViewPager?.isUserInputEnabled = false
            }
          }
        }
      }
      MotionEvent.ACTION_UP -> {
        parentViewPager?.isUserInputEnabled = true
      }
    }
  }
}