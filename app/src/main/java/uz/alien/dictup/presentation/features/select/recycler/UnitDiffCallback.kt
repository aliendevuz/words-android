package uz.alien.dictup.presentation.features.select.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.select.model.UnitUIState

class UnitDiffCallback : DiffUtil.ItemCallback<UnitUIState>() {

    override fun areItemsTheSame(old: UnitUIState, new: UnitUIState): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: UnitUIState, new: UnitUIState): Boolean {
        return old.name == new.name && old.progress == new.progress && old.isSelected == new.isSelected
    }
}