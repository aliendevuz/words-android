package uz.alien.dictup.presentation.features.select.recycler

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.SelectItemPartBinding
import uz.alien.dictup.presentation.features.select.model.PartUIState

class PartViewHolder(
    itemView: View,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = SelectItemPartBinding.bind(itemView)

    fun bind(part: PartUIState) {

        val isSelected = part.isSelected
        val isCurrent = part.isCurrent

        binding.tvPartNumber.text = part.title

        val backgroundRes = when {
            isCurrent && isSelected -> R.drawable.select_item_part_background_current
            isCurrent -> R.drawable.select_item_part_background_current
            isSelected -> R.drawable.select_item_part_background_selected
            else -> R.drawable.select_item_part_background
        }

        itemView.setBackgroundResource(backgroundRes)

        binding.tvPartNumber.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                if (isCurrent) R.color.select_item_unit_text_selected_color else R.color.select_item_unit_text_color
            )
        )

        itemView.setOnClickListener {
            onClick(part.id)
        }
    }
}