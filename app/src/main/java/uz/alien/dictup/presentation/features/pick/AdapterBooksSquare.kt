package uz.alien.dictup.presentation.features.pick

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.PickItemPartBinding

class AdapterBooksSquare(private val size: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class BookSquareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = PickItemPartBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return BookSquareViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pick_item_part, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is BookSquareViewHolder) {
        holder.binding.tvPart.text = (position + 1).toString()

      holder.binding.root.setOnClickListener {
        //
      }
    }
  }
}