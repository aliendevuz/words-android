package uz.alien.dictup.presentation.features.quiz.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.databinding.QuizItemOptionBinding
import uz.alien.dictup.presentation.features.lesson.model.WordUIState
import uz.alien.dictup.presentation.features.lesson.recycler.WordDiffUtilCallback
import uz.alien.dictup.presentation.features.lesson.recycler.WordViewHolder
import uz.alien.dictup.presentation.features.quiz.model.Option

class OptionAdapter(
    private val onItemClick: (Int, Int) -> Unit
) : ListAdapter<Option, OptionViewHolder>(OptionDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = QuizItemOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}