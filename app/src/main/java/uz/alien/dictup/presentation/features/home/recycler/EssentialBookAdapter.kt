package uz.alien.dictup.presentation.features.home.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.alien.dictup.R
import uz.alien.dictup.databinding.HomeItemBookBeginnerBinding
import uz.alien.dictup.presentation.features.home.model.Book

class EssentialBookAdapter(
    private val onBookClickListener: (Book) -> Unit
) : ListAdapter<Book, EssentialBookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssentialBookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item_book_essential, parent, false)
        return EssentialBookViewHolder(view, onBookClickListener)
    }

    override fun onBindViewHolder(holder: EssentialBookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }
}