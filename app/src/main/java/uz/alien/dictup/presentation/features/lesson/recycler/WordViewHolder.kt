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

//        if (word.score >= 5) {
//            itemTextView.setBackgroundColor(itemTextView.context.resources.getColor(R.color.general_progress_indicator))
//        } else {
//            itemTextView.setBackgroundColor(itemTextView.context.resources.getColor(R.color.back_item_lesson_unit))
//        }

        itemTextView.text = word.word
    }
}