package uz.alien.dictup.presentation.features.lesson.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.lesson.model.WordUIState

class WordViewHolder(
    private val itemTextView: TextView,
    private val onItemClick: (Int, View, Int) -> Unit
) : RecyclerView.ViewHolder(itemTextView) {

    fun bind(word: WordUIState) {

        itemTextView.setOnClickListener {
            onItemClick(word.id, itemTextView, word.wordId ?: 0)
        }

        if (word.score < 0) {
            itemTextView.setTextColor(itemTextView.context.getColor(R.color.red_500))
        } else if (word.score >= 5) {
            itemTextView.setTextColor(itemTextView.context.getColor(R.color.text_highlight))
        } else {
            itemTextView.setTextColor(itemTextView.context.getColor(R.color.secondary_text))
        }

        itemTextView.text = word.word
    }
}