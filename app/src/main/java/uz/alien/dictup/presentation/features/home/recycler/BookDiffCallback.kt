package uz.alien.dictup.presentation.features.home.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.alien.dictup.presentation.features.home.model.Book

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.progress == newItem.progress && oldItem.isLoaded == newItem.isLoaded
    }
}