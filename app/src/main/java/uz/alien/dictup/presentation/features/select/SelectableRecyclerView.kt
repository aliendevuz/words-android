package uz.alien.dictup.presentation.features.select

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SoundEffectConstants
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
class SelectableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    interface OnUnitSelectListener {
        fun onSingleTap(position: Int)
        fun onLongPress(position: Int)
        fun onMove(position: Int, selection: Boolean)
    }

    var selectListener: OnUnitSelectListener? = null
    private var isScrolling = true
    private var isSelecting = false
    private var oldX = 0f
    private var oldY = 0f
    private var scrollThreshold = 0
    private var scrollDistance = 0
    private val scrollInterval = 274L
    private var isAutoScrolling = false

    private val scrollHandler = Handler(Looper.getMainLooper())
    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (isAutoScrolling) {
                if (lastY < scrollThreshold) {
                    smoothScrollBy(0, -scrollDistance)
                } else if (lastY > height - scrollThreshold) {
                    smoothScrollBy(0, scrollDistance)
                }
                scrollHandler.postDelayed(this, scrollInterval)
            }
        }
    }

    private var lastY = 0f

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                findChildViewUnder(e.x, e.y)?.let {
                    val position = getChildAdapterPosition(it)
                    it.playSoundEffect(SoundEffectConstants.CLICK)
                    selectListener?.onSingleTap(position)
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                findChildViewUnder(e.x, e.y)?.let {
                    val position = getChildAdapterPosition(it)
                    it.playSoundEffect(SoundEffectConstants.CLICK)
                    selectListener?.onLongPress(position)
                }
                isScrolling = false
                oldX = e.x
                oldY = e.y
                lastY = e.y
                startAutoScrollIfNeeded()
            }
        })

    init {
        setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (isScrolling) {
                        onTouchEvent(event)
                    } else {
                        lastY = event.y
                        findChildViewUnder(event.x, event.y)?.let {
                            val position = getChildAdapterPosition(it)
                            selectListener?.onMove(position, !isSelecting)
                        }
                        startAutoScrollIfNeeded()
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isScrolling) {
                        onTouchEvent(event)
                    } else {
                        val now = SystemClock.uptimeMillis()
                        val fakeUp = MotionEvent.obtain(
                            now, now,
                            MotionEvent.ACTION_UP,
                            oldX, oldY, 0
                        )
                        onTouchEvent(fakeUp)
                        fakeUp.recycle()
                    }
                    isScrolling = true
                    stopAutoScroll()
                }
            }
            true
        }
    }

    private fun updateScrollDistance() {
        if (scrollDistance != 0) return
        getChildAt(0)?.let { child ->
            var itemHeight = child.height
            val offsets = Rect()
            getItemDecorationAt(0).getItemOffsets(offsets, child, this, State())
            itemHeight += offsets.top + offsets.bottom
            scrollDistance = itemHeight
        } ?: run {
            scrollDistance = 50
        }
        scrollThreshold = scrollDistance / 2
    }

    private fun startAutoScrollIfNeeded() {
        updateScrollDistance()
        if (!isAutoScrolling && (lastY < scrollThreshold || lastY > height - scrollThreshold)) {
            isAutoScrolling = true
            scrollHandler.post(scrollRunnable)
        } else if (isAutoScrolling && lastY >= scrollThreshold && lastY <= height - scrollThreshold) {
            stopAutoScroll()
        }
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        scrollHandler.removeCallbacks(scrollRunnable)
    }

    fun setSelection(selecting: Boolean) {
        isSelecting = selecting
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (!isScrolling) {
            true
        } else {
            super.canScrollVertically(direction)
        }
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (!isScrolling) {
            true
        } else {
            super.canScrollHorizontally(direction)
        }
    }
}