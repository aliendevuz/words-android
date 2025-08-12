package uz.alien.dictup.presentation.features.pick.recycler

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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

    fun bind(unit: UnitUIState) {

        binding.tvUnit.text = unit.name

        val current = binding.progress.progress
        val target = unit.progress

        ObjectAnimator.ofInt(binding.progress, "progress", current, target).apply {
            duration = BuildConfig.DURATION * 5
            interpolator = DecelerateInterpolator()
            start()
        }

        binding.tvPercent.text = "${unit.progress}%"

        binding.root.setOnClickListener {
            onItemClick(unit.id)
        }
    }
}