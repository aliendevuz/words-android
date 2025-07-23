package uz.alien.dictup.presenter.lesson

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ItemWordBinding
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.presenter.base.ActivityBase
import uz.alien.dictup.presenter.detail.ActivityDetail

class AdapterWord(private val activity: ActivityBase, private val words: List<Word>) : RecyclerView.Adapter<AdapterWord.WordViewHolder>() {

  inner class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemWordBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return words.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
    return WordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false))
  }

  override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
    holder.binding.root.text = words[position].w
    holder.binding.root.setOnClickListener {
      val book = activity.intent.getIntExtra("book", 0)
      val pick = activity.intent.getIntExtra("pick", 0)
      val unit = activity.intent.getIntExtra("unit", 0)
      val intent = Intent(activity, ActivityDetail::class.java)
      intent.putExtra("book", book)
      intent.putExtra("pick", pick)
      intent.putExtra("unit", unit)
      intent.putExtra("word", position)
      activity.setOpenZoomAnimation(intent)
    }
  }
}