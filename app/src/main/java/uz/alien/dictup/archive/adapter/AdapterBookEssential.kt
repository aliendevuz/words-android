package uz.alien.dictup.archive.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.archive.activity.PickerActivity
import uz.alien.dictup.databinding.ItemBookEssentialBinding
import uz.alien.dictup.archive.utils.Book
import uz.alien.dictup.presenter.base.ActivityBase

class AdapterBookEssential(private val books: ArrayList<Book>, private val activity: ActivityBase) :
  RecyclerView.Adapter<AdapterBookEssential.EssentialViewHolder>() {

  inner class EssentialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemBookEssentialBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return books.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssentialViewHolder {
    return EssentialViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_book_essential, parent, false)
    )
  }

  override fun onBindViewHolder(holder: EssentialViewHolder, position: Int) {
    holder.binding.ivBackground.setBackgroundColor(activity.getColor(books[position].color))
    holder.binding.ivBackground.setImageDrawable(activity.getDrawable(books[position].image))
    holder.binding.progressBook.progress = 100
    holder.binding.progressBook.setIndicatorColor(activity.getColor(books[position].color))

    holder.binding.root.setOnClickListener {
      val intent = Intent(activity, PickerActivity::class.java)
      intent.putExtra("book", 1)
      intent.putExtra("pick", position)
      activity.setAlphaAnimation(intent)
    }
  }
}