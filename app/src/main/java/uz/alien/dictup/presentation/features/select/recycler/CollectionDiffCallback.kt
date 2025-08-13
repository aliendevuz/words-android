package uz.alien.dictup.presentation.features.select.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.select.model.CollectionUIState

class CollectionDiffCallback : DiffUtil.ItemCallback<CollectionUIState>() {

    override fun areItemsTheSame(old: CollectionUIState, new: CollectionUIState): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: CollectionUIState, new: CollectionUIState): Boolean {
        return old.isCurrent == new.isCurrent
    }
}