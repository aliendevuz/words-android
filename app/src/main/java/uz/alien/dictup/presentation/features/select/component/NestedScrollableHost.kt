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

    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var initialX = 0f
    private var initialY = 0f

    // Eng yaqin (inner) ViewPager2
    private val nearestViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is ViewPager2) {
                v = v.parent as? View
            }
            return v as? ViewPager2
        }

    // Eng yuqoridagi (outer) ViewPager2 (zanjirdagi oxirgisi)
    private val outerViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            var last: ViewPager2? = null
            while (v != null) {
                if (v is ViewPager2) last = v
                v = v.parent as? View
            }
            return last
        }

    private val child: View? get() = if (childCount > 0) getChildAt(0) else null
    private val isChildSelectableRv: Boolean get() = child is SelectableRecyclerView

    // Long-press guard (select mode uchun)
    private var longPressArmed = false
    private var longPressPosted = false
    private val longPressRunnable = Runnable {
        longPressArmed = true
        nearestViewPager?.isUserInputEnabled = false  // long-press/selection barqaror bo‘lsin
    }

    private fun postLongPressGuardIfNeeded() {
        if (isChildSelectableRv && !longPressPosted) {
            longPressPosted = true
            postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
        }
    }

    private fun cancelLongPressGuard() {
        if (longPressPosted) {
            removeCallbacks(longPressRunnable)
            longPressPosted = false
        }
        longPressArmed = false
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            ViewPager2.ORIENTATION_HORIZONTAL -> child?.canScrollHorizontally(direction) ?: false
            ViewPager2.ORIENTATION_VERTICAL   -> child?.canScrollVertically(direction) ?: false
            else -> false
        }
    }

    private fun canNearestPagerScroll(orientation: Int, delta: Float): Boolean {
        val vp = nearestViewPager ?: return false
        val direction = -delta.sign.toInt()
        return when (orientation) {
            ViewPager2.ORIENTATION_HORIZONTAL -> vp.canScrollHorizontally(direction)
            ViewPager2.ORIENTATION_VERTICAL   -> vp.canScrollVertically(direction)
            else -> false
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val innerVp = nearestViewPager ?: return
        val outerVp = outerViewPager
        val orientation = innerVp.orientation

        // Agar child hech yo‘nalishda ham scroll qila olmasa — umuman aralashmaymiz
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }

        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                // Avval childga imkon beramiz
                parent.requestDisallowInterceptTouchEvent(true)

                // Selectable RV bo‘lsa, long-press guardni tayyorlab qo‘yamiz
                postLongPressGuardIfNeeded()
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - initialX
                val dy = e.y - initialY
                val isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL

                val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
                val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

                // Katta harakatga o‘tganda, long-press guardni (agar pagerga burilayotgan bo‘lsa) bekor qilamiz
                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    val movingAlongPagerAxis = (isVpHorizontal != (scaledDy > scaledDx))

                    if (movingAlongPagerAxis) {
                        // Pager yo‘nalishi bo‘ylab harakat
                        // 1) Child scroll qila olsa — childga bering (parent intercept qilmasin)
                        if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
                            cancelLongPressGuard() // child baribir scroll qiladi
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Pagerga hojat yo‘q, inner/outerni yoqmang
                        } else {
                            // 2) Child qila olmaydi → inner pagerni sinaymiz
                            cancelLongPressGuard() // pagerga ketayapmiz
                            if (canNearestPagerScroll(orientation, if (isVpHorizontal) dx else dy)) {
                                // Inner VP2 ishlasin
                                parent.requestDisallowInterceptTouchEvent(false)
                                innerVp.isUserInputEnabled = true
                                outerVp?.isUserInputEnabled = true
                            } else {
                                // 3) Inner ham qila olmaydi → outerga beramiz
                                parent.requestDisallowInterceptTouchEvent(false)
                                innerVp.isUserInputEnabled = false
                                outerVp?.isUserInputEnabled = true
                            }
                        }
                    } else {
                        // Pager yo‘nalishiga qarshi (perp.) harakat: VP2 aralashmasin
                        parent.requestDisallowInterceptTouchEvent(true)
                        // Agar long-press hali tushmagan bo‘lsa ham, selectable RV uchun guard foydali
                        // longPressArmed true bo‘lsa — inner VP2 allaqachon o‘chirilgan bo‘ladi
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancelLongPressGuard()
                // Hammasini default holatga qaytaramiz
                innerVp.isUserInputEnabled = true
                outerVp?.isUserInputEnabled = true
            }
        }
    }
}