package uz.alien.dictup.presentation.features.lesson.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.lesson.model.WordUIState

class WordAdapter(
    private val onItemClick: (Int, View) -> Unit
) : ListAdapter<WordUIState, WordViewHolder>(WordDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.lesson_item_word,
                parent,
                false
            ) as TextView
        return WordViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}