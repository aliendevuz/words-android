package uz.alien.dictup.presentation.features.quiz.recycler

import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.QuizItemOptionBinding
import uz.alien.dictup.presentation.features.quiz.model.Option
import uz.alien.dictup.presentation.features.quiz.model.Status

class OptionViewHolder(
    private val binding: QuizItemOptionBinding,
    private val onItemClick: (Int, Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(option: Option) {

        binding.tvOption.text = option.name

        when (option.status) {
            Status.WRONG -> {
                binding.tvOption.setTextColor(binding.root.context.getColor(R.color.white))
                binding.root.setBackgroundResource(R.drawable.back_item_quiz_option_incorrect)
            }
            Status.CORRECT -> {
                binding.tvOption.setTextColor(binding.root.context.getColor(R.color.white))
                binding.root.setBackgroundResource(R.drawable.back_item_quiz_option_correct)
            }
            else -> {
                binding.tvOption.setTextColor(binding.root.context.getColor(R.color.secondary_text))
                binding.root.setBackgroundResource(R.drawable.back_item_quiz_option)
            }
        }

        if (option.status == Status.NOT_ANSWERED) {
            binding.root.setOnClickListener {
                onItemClick(option.id, option.wordId)
            }
        }
    }
}