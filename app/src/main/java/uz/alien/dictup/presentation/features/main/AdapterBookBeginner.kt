package uz.alien.dictup.presentation.features.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ItemBookBeginnerBinding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.pick.PickActivity

class AdapterBookBeginner(private val books: ArrayList<Book>, private val activity: BaseActivity) :
  RecyclerView.Adapter<AdapterBookBeginner.BeginnerViewHolder>() {

  class Book(val color: Int, val image: Int)

  inner class BeginnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemBookBeginnerBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return books.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeginnerViewHolder {
    return BeginnerViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_book_beginner, parent, false)
    )
  }

  override fun onBindViewHolder(holder: BeginnerViewHolder, position: Int) {
    holder.binding.ivBackground.setBackgroundColor(activity.getColor(books[position].color))
    holder.binding.ivBackground.setImageDrawable(activity.getDrawable(books[position].image))
    holder.binding.progressBook.progress = 100
    holder.binding.progressBook.setIndicatorColor(activity.getColor(books[position].color))

    holder.binding.root.setOnClickListener {
      val intent = Intent(activity, PickActivity::class.java)
      intent.putExtra("book", 0)
      intent.putExtra("pick", position)
      activity.setAlphaAnimation(intent)
    }
  }
}