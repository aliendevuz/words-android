package uz.alien.dictup.presentation.features.pick.recycler

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
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

//        if (oldProgress != unit.progress) {
//
//            val currentProgress = (oldProgress.toFloat() / 100f)
//            val targetProgress = (unit.progress.toFloat() / 100f)
//            oldProgress = unit.progress
//
//            // rang animatsiyasi (HSV)
//            val startColor = ContextCompat.getColor(binding.root.context, R.color.red_500)
//            val endColor = ContextCompat.getColor(binding.root.context, R.color.green_500)
//
//            val startHSV = FloatArray(3)
//            val endHSV = FloatArray(3)
//            Color.colorToHSV(startColor, startHSV)
//            Color.colorToHSV(endColor, endHSV)
//
//            val colorAnim = ValueAnimator.ofFloat(0f, 1f).apply {
//                duration = BuildConfig.DURATION * 5
//                interpolator = DecelerateInterpolator()
//                addUpdateListener { animator ->
//
//                    val fraction = currentProgress + (targetProgress - currentProgress) * animator.animatedFraction
//
//                    val hue = startHSV[0] + (endHSV[0] - startHSV[0]) * fraction
//                    val sat = startHSV[1] + (endHSV[1] - startHSV[1]) * fraction
//                    val value = startHSV[2] + (endHSV[2] - startHSV[2]) * fraction
//
//                    val hsv = floatArrayOf(hue, sat, value)
//                    val color = Color.HSVToColor(hsv)
//
//                    binding.progress.progress = (fraction * 100).toInt()
//                    binding.progress.setIndicatorColor(color)
//                    binding.tvPercent.text = "${(fraction * 100).toInt()}%"
//                }
//            }
//
//            colorAnim.start()
//        }

        binding.tvPercent.text = "${unit.progress}%"

        binding.root.setOnClickListener {
            onItemClick(unit.id)
        }
    }
}