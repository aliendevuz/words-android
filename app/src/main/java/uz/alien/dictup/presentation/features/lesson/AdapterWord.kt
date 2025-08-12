package uz.alien.dictup.presentation.features.lesson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonItemWordBinding

class AdapterWord : RecyclerView.Adapter<AdapterWord.WordViewHolder>() {

  inner class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LessonItemWordBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 20
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
    return WordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_item_word, parent, false))
  }

  override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
  }
}