package uz.alien.test.words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.ItemWordBinding

class AdapterWords(private val uz: List<Word>, private val en: List<Word>) :
  RecyclerView.Adapter<AdapterWords.WordViewHolder>() {

  inner class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemWordBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return uz.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
    return WordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false))
  }

  override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
    holder.binding.tvEn.text = en[position].word
    holder.binding.tvUz.text = uz[position].word
  }
}