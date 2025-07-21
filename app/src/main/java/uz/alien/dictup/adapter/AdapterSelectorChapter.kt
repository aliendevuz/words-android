package uz.alien.dictup.adapter

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ItemGeneralBookBinding

class AdapterSelectorChapter(
  private val count: Int,
  private val isSmall: Boolean = false,
  private val onBookClicked: (Int) -> Unit
) : RecyclerView.Adapter<AdapterSelectorChapter.BookViewHolder>() {

  private var selectedIndex = 0

  class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemGeneralBookBinding.bind(view)
  }

  override fun getItemCount(): Int = count

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
    val view = if (isSmall) {
      LayoutInflater.from(parent.context).inflate(R.layout.item_general_books, parent, false)
    } else {
      LayoutInflater.from(parent.context).inflate(R.layout.item_general_book, parent, false)
    }
    return BookViewHolder(view)
  }

  override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

    val context = holder.itemView.context
    val isSelected = position == selectedIndex

    // Rangi (fon) tanlash
    val targetColor = ContextCompat.getColor(
      context,
      if (isSelected) R.color.general_button else R.color.back_item_general_book
    )

    // Hozirgi fon rangi olish
    val currentColor = (holder.binding.root.background as? ColorDrawable)?.color
      ?: ContextCompat.getColor(context, R.color.back_item_general_book)

    // Programmatik GradientDrawable yasash
    val backgroundDrawable = GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = 24f
      setColor(currentColor)
    }
    holder.binding.root.background = backgroundDrawable

    // Silliq animatsiya (agar ranglar farq qilsa)
    if (currentColor != targetColor) {
      ValueAnimator.ofArgb(currentColor, targetColor).apply {
        duration = 250
        addUpdateListener { animator ->
          val animatedColor = animator.animatedValue as Int
          backgroundDrawable.setColor(animatedColor)
        }
        start()
      }
    }

    // Text holatini belgilash
    holder.binding.tvBookNumber.text = "${position + 1}"

    holder.itemView.setOnClickListener {
      val adapterPosition = holder.adapterPosition
      if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition != selectedIndex) {
        val oldIndex = selectedIndex
        selectedIndex = adapterPosition
        notifyItemChanged(oldIndex)
        notifyItemChanged(adapterPosition)
        onBookClicked(adapterPosition)
      }
    }
  }

  fun setSelected(index: Int) {
    if (index != selectedIndex && index in 0 until count) {
      val oldIndex = selectedIndex
      selectedIndex = index
      notifyItemChanged(oldIndex)
      notifyItemChanged(index)
    }
  }
}