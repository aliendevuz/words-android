package uz.alien.dictup.presentation.features.lesson.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.lesson.model.WordUIState

class WordViewHolder(
    private val itemTextView: TextView,
    private val onItemClick: (Int, View) -> Unit
) : RecyclerView.ViewHolder(itemTextView) {

    fun bind(word: WordUIState) {

        itemTextView.setOnClickListener {
            onItemClick(word.id, itemTextView)
        }

        itemTextView.text = word.word
    }
}