package uz.alien.dictup.presentation.features.quiz.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.quiz.model.Option

class OptionDiffUtilCallback : DiffUtil.ItemCallback<Option>() {

    override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem == newItem
    }
}