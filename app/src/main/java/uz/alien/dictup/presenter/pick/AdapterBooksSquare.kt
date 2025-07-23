package uz.alien.dictup.presenter.pick

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ItemLessonBooksBinding

class AdapterBooksSquare(private val size: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class BookSquareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemLessonBooksBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return BookSquareViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_books, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is BookSquareViewHolder) {
        holder.binding.tvBookNumber.text = (position + 1).toString()

      holder.binding.root.setOnClickListener {
        //
      }
    }
  }
}