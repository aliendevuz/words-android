package uz.alien.dictup.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoLayoutManager(
    context: Context,
    spanCount: Int = 1,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
  override fun canScrollVertically(): Boolean {
    return (orientation == RecyclerView.VERTICAL && !(findFirstCompletelyVisibleItemPosition() == 0 && findLastCompletelyVisibleItemPosition() == itemCount - 1))
  }

  override fun canScrollHorizontally(): Boolean {
    return (orientation == RecyclerView.HORIZONTAL && !(findFirstCompletelyVisibleItemPosition() == 0 && findLastCompletelyVisibleItemPosition() == itemCount - 1))
  }
}