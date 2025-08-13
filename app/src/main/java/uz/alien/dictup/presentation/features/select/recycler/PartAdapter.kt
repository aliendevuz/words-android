package uz.alien.dictup.presentation.features.select.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.presentation.features.select.model.PartUIState

class PartAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<PartUIState, PartViewHolder>(PartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_item_part, parent, false)
        return PartViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        holder.bind( getItem(position))
    }
}