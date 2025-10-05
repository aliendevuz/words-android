package uz.alien.dictup.presentation.features.quiz.recycler

import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.databinding.QuizItemOptionBinding

class OptionViewHolder(private val binding: QuizItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.tvOption.id
    }
}