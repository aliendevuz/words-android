package uz.alien.dictup.presentation.features.select.recycler

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.SelectItemCollectionBinding
import uz.alien.dictup.presentation.features.select.model.CollectionUIState

class CollectionViewHolder(
    itemView: View,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = SelectItemCollectionBinding.bind(itemView)

    fun bind(collection: CollectionUIState) {

        val isSelected = collection.isSelected
        val isCurrent = collection.isCurrent

        binding.tvCollection.text = collection.title

        val backgroundRes = when {
            isSelected and isCurrent -> R.drawable.select_item_collection_background_current
            isCurrent -> R.drawable.select_item_collection_background_current
//            isSelected -> R.drawable.select_item_collection_background_selected
            isSelected -> R.drawable.select_item_collection_background
            else -> R.drawable.select_item_collection_background
        }

        itemView.setBackgroundResource(backgroundRes)

        binding.tvCollection.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                if (isCurrent) R.color.white else R.color.primary_text
            )
        )

        itemView.setOnClickListener {
            onClick(collection.id)
        }
    }
}