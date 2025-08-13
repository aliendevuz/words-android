package uz.alien.dictup.presentation.features.pick.recycler

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.databinding.PickItemUnitBinding
import uz.alien.dictup.presentation.features.pick.model.UnitUIState

class UnitViewHolder(
    val view: View,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = PickItemUnitBinding.bind(view)

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

        binding.root.setOnClickListener {
            onItemClick(unit.id)
        }
    }
}