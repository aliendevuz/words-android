package uz.alien.dictup.presentation.features.lesson.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.lesson.model.WordUIState

class WordDiffUtilCallback : DiffUtil.ItemCallback<WordUIState>() {

    override fun areItemsTheSame(oldItem: WordUIState, newItem: WordUIState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WordUIState, newItem: WordUIState): Boolean {
        return oldItem == newItem
    }
}