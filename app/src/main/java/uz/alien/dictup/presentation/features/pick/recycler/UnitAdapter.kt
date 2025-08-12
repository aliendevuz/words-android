package uz.alien.dictup.presentation.features.pick.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.pick.model.UnitUIState
import uz.alien.dictup.presentation.features.select.recycler_adapter.UnitDiffCallback

class UnitAdapter(
    private val onItemClick: (position: Int) -> Unit
) : ListAdapter<UnitUIState, UnitViewHolder>(UnitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pick_item_unit, parent, false)
        return UnitViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}