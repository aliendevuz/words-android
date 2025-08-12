package uz.alien.dictup.presentation.common.component

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val spacing: Float,
    private val resources: Resources,
    private val spanCount: Int = 1,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
      outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
  ) {
    val position = parent.getChildAdapterPosition(view)
    val column = position % spanCount

    val spacing = (spacing * resources.displayMetrics.density).toInt()

    if (includeEdge) {
      outRect.left = spacing - column * spacing / spanCount
      outRect.right = (column + 1) * spacing / spanCount

      if (position < spanCount) {
        outRect.top = spacing
      } else {
        outRect.top = spacing / 2
      }

      outRect.bottom = spacing
    } else {
      outRect.left = column * spacing / spanCount
      outRect.right = spacing - (column + 1) * spacing / spanCount

      if (position >= spanCount) {
        outRect.top = spacing
      }
    }
  }
}