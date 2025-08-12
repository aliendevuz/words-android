package uz.alien.dictup.presentation.features.home.recycler

import android.animation.ObjectAnimator
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.databinding.HomeItemBookEssentialBinding
import uz.alien.dictup.presentation.features.home.model.Book

class EssentialBookViewHolder(
    view: View,
    private val onBookClickListener: (Book) -> Unit
): RecyclerView.ViewHolder(view) {

    private val binding = HomeItemBookEssentialBinding.bind(view)

    fun bind(book: Book) {

        val backgroundColor = ContextCompat.getColor(itemView.context, book.backgroundColor)

        val current = binding.progressBook.progress
        val target = book.progress

        ObjectAnimator.ofInt(binding.progressBook, "progress", current, target).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            start()
        }

        binding.progressBook.setIndicatorColor(backgroundColor)
        binding.ivBackground.setBackgroundColor(backgroundColor)

        binding.ivBackground.setImageResource(book.imageRes)

        if (book.isLoaded) {

            val current = binding.progressBook.alpha
            val target = 1.0f

            ObjectAnimator.ofFloat(binding.progressBook, "alpha", current, target).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                start()
            }
            ObjectAnimator.ofFloat(binding.ivBackground, "alpha", current, target).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                start()
            }
        } else {

            val current = binding.progressBook.alpha
            val target = 0.8f

            ObjectAnimator.ofFloat(binding.progressBook, "alpha", current, target).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                start()
            }
            ObjectAnimator.ofFloat(binding.ivBackground, "alpha", current, target).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        binding.root.setOnClickListener {
            onBookClickListener(book)
        }
    }
}