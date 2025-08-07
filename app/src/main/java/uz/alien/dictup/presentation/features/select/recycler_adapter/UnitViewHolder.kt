package uz.alien.dictup.presentation.features.select.recycler_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.presentation.features.select.model.UnitUIState

class UnitViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

//    private val binding = ItemUnitBinding.bind(itemView)

    fun bind(unit: UnitUIState) {

//        binding.tvUnit.text = unit.name
//        binding.tvPercent.text = "${unit.progress}%"
//        binding.progress.progress = unit.progress
//
//        itemView.setBackgroundResource(
//            if (unit.isSelected) R.drawable.background_unit_selected
//            else R.drawable.background_unit_default
//        )
    }
}