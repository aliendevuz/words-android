package uz.alien.dictup.presentation.features.select.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.select.model.UnitUIState

class UnitAdapter : ListAdapter<UnitUIState, UnitViewHolder>(UnitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}