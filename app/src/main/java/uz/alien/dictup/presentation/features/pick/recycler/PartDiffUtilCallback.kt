package uz.alien.dictup.presentation.features.pick.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.pick.model.PartUIState

class PartDiffUtilCallback : DiffUtil.ItemCallback<PartUIState>() {

    override fun areItemsTheSame(oldItem: PartUIState, newItem: PartUIState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PartUIState, newItem: PartUIState): Boolean {
        return oldItem.isCurrent == newItem.isCurrent
    }
}