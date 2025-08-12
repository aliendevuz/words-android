package uz.alien.dictup.presentation.features.select.recycler_adapter

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.pick.model.PartUIState

class PartDiffCallback : DiffUtil.ItemCallback<PartUIState>() {

    override fun areItemsTheSame(old: PartUIState, new: PartUIState): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: PartUIState, new: PartUIState): Boolean {
        return old.isCurrent  == new.isCurrent
    }
}