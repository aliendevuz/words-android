package uz.alien.dictup.presentation.features.select.recycler_adapter

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.select.model.UnitUIState

class UnitDiffCallback : DiffUtil.ItemCallback<UnitUIState>() {
    override fun areItemsTheSame(old: UnitUIState, new: UnitUIState) = old.id == new.id
    override fun areContentsTheSame(old: UnitUIState, new: UnitUIState) = old.isSelected == new.isSelected
}