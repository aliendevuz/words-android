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
            isSelected -> R.drawable.select_item_collection_background_selected
            isCurrent -> R.drawable.select_item_collection_background_current
            else -> R.drawable.select_item_collection_background
        }

        itemView.setBackgroundResource(backgroundRes)

        binding.tvCollection.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                if (isCurrent) R.color.select_item_unit_text_selected_color else R.color.select_item_unit_text_color
            )
        )

        itemView.setOnClickListener {
            onClick(collection.id)
        }
    }
}