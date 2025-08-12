package uz.alien.dictup.presentation.features.pick.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.pick.model.UnitUIState

class UnitDiffUtilCallback : DiffUtil.ItemCallback<UnitUIState>() {

    override fun areItemsTheSame(oldItem: UnitUIState, newItem: UnitUIState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UnitUIState, newItem: UnitUIState): Boolean {
        return oldItem.progress == newItem.progress && oldItem.name == newItem.name
    }
}