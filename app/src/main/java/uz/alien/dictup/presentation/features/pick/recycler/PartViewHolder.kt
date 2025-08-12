package uz.alien.dictup.presentation.features.pick.recycler

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.PickItemPartBinding
import uz.alien.dictup.presentation.features.pick.model.PartUIState

class PartViewHolder(
    itemView: View,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = PickItemPartBinding.bind(itemView)

    fun bind(part: PartUIState) {

        val isCurrent = part.isCurrent

        binding.tvPart.text = "${part.id + 1}"

        val backgroundRes = when {
            isCurrent -> R.drawable.pick_item_part_background_current
            else -> R.drawable.pick_item_part_background
        }

        itemView.setBackgroundResource(backgroundRes)

        binding.tvPart.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                if (isCurrent) R.color.pick_item_part_text_current_color else R.color.pick_item_part_text_color
            )
        )

        itemView.setOnClickListener {
            onClick(part.id)
        }
    }
}