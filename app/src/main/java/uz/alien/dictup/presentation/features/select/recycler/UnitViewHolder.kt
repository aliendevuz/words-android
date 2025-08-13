package uz.alien.dictup.presentation.features.select.recycler

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.SelectItemUnitBinding
import uz.alien.dictup.presentation.features.select.model.UnitUIState

class UnitViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val binding = SelectItemUnitBinding.bind(itemView)
    private var oldProgress = 0

    fun bind(unit: UnitUIState) {

        binding.tvUnit.text = unit.name

        if (oldProgress != unit.progress) {

            val currentProgress = oldProgress
            val targetProgress = unit.progress
            oldProgress = unit.progress

            ObjectAnimator.ofInt(binding.progress, "progress", currentProgress, targetProgress).apply {
                duration = BuildConfig.DURATION * 5
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        binding.tvPercent.text = "${unit.progress}%"

        itemView.setBackgroundResource(
            if (unit.isSelected) R.drawable.select_item_unit_background_selected
            else R.drawable.select_item_unit_background
        )
    }
}