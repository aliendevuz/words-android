package uz.alien.test.clip_to_padding

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//class GridSpacingItemDecoration(
//  private val spanCount: Int,
//  private val spacing: Int,
//  private val includeEdge: Boolean
//) : RecyclerView.ItemDecoration() {
//
//  override fun getItemOffsets(
//    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
//  ) {
//    val position = parent.getChildAdapterPosition(view)
//    val column = position % spanCount
//
//    if (includeEdge) {
//      outRect.left = spacing - column * spacing / spanCount
//      outRect.right = (column + 1) * spacing / spanCount
//
//      if (position < spanCount) {
//        outRect.top = spacing
//      } else {
//        outRect.top = spacing / 2
//      }
//
//      outRect.bottom = spacing
//    } else {
//      outRect.left = column * spacing / spanCount
//      outRect.right = spacing - (column + 1) * spacing / spanCount
//
//      if (position >= spanCount) {
//        outRect.top = spacing
//      }
//    }
//  }
//}

//class GridSpacingItemDecoration(
//  private val spanCount: Int,
//  private val topSpacing: Int,
//  private val bottomSpacing: Int,
//  private val leftSpacing: Int,
//  private val rightSpacing: Int,
//  private val includeEdge: Boolean
//) : RecyclerView.ItemDecoration() {
//
//  override fun getItemOffsets(
//    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
//  ) {
//    val position = parent.getChildAdapterPosition(view)
//    val column = position % spanCount
//    val row = position / spanCount
//
//    // Agar includeEdge bo'lsa, itemning chetiga marginlar qo'shamiz
//    if (includeEdge) {
//      // Top spacing
//      outRect.top = if (row == 0) topSpacing else topSpacing / 2
//      // Bottom spacing
//      outRect.bottom = bottomSpacing
//      // Left spacing
//      outRect.left = if (column == 0) leftSpacing else leftSpacing / 2
//      // Right spacing
//      outRect.right = rightSpacing
//    } else {
//      // Agar includeEdge false bo'lsa, chetlarga marginlar qo'ymaymiz
//      outRect.top = if (row > 0) topSpacing else 0
//      outRect.bottom = bottomSpacing
//      outRect.left = leftSpacing * column / spanCount
//      outRect.right = rightSpacing * (spanCount - column - 1) / spanCount
//    }
//  }
//}

//class GridSpacingItemDecoration(
//  private val spanCount: Int,
//  private val rowSpacing: Int,
//  private val columnSpacing: Int,
//  private val topSpacing: Int,
//  private val bottomSpacing: Int,
//  private val startSpacing: Int,
//  private val endSpacing: Int
//) : RecyclerView.ItemDecoration() {
//
//  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//
//    val position = parent.getChildAdapterPosition(view)
//    val column = position % spanCount
//    val row = position / spanCount
//
//    outRect.top = if (row == 0) topSpacing else rowSpacing / 2
//
//    outRect.bottom = if (position >= (parent.adapter?.itemCount ?: 0) - spanCount) {
//      bottomSpacing
//    } else {
//      rowSpacing / 2
//    }
//
//    outRect.left = if (column == 0) startSpacing else columnSpacing / 2
//
//    outRect.right = if (column == spanCount - 1) endSpacing else columnSpacing / 2
//  }
//}

class GridSpacingItemDecoration(
  private val spanCount: Int,
  private val spacing: Int,
  private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val position = parent.getChildAdapterPosition(view)
    val column = position % spanCount

    if (includeEdge) {
      outRect.left = spacing - column * spacing / spanCount
      outRect.right = (column + 1) * spacing / spanCount

      if (position < spanCount) {
        outRect.top = spacing // first row
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