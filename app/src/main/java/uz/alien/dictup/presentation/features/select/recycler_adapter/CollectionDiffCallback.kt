package uz.alien.dictup.presentation.features.select.recycler_adapter

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.select.model.CollectionUIState

class CollectionDiffCallback : DiffUtil.ItemCallback<CollectionUIState>() {
    override fun areItemsTheSame(old: CollectionUIState, new: CollectionUIState) = old.id == new.id
    override fun areContentsTheSame(old: CollectionUIState, new: CollectionUIState): Boolean {
        return old.isSelected == new.isSelected && old.isCurrent == new.isCurrent
    }
}