package uz.alien.dictup.presentation.features.select.recycler_adapter

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.pick.model.UnitUIState

class UnitDiffCallback : DiffUtil.ItemCallback<UnitUIState>() {

    override fun areItemsTheSame(old: UnitUIState, new: UnitUIState): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: UnitUIState, new: UnitUIState): Boolean {
        return old.name == new.name && old.progress == new.progress
    }
}