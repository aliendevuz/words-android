package uz.alien.test.lesson.recycler_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.LessonItemWordBinding
import uz.alien.test.lesson.model.Word

class WordAdapter(
    private val onItemClick: (Int, View) -> Unit
) : ListAdapter<String, WordAdapter.WordViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = LessonItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class WordViewHolder(private val binding: LessonItemWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: String, position: Int) {
            binding.root.setOnClickListener {
                onItemClick(position, binding.root)
            }
            // Bind other data as needed
            binding.tvWord.text = word
        }
    }
}

class WordDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}