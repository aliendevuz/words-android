package uz.alien.dictup.presentation.features.select.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.alien.dictup.presentation.features.select.CollectionFragment
import uz.alien.dictup.presentation.features.select.model.CollectionUIState

class CollectionPagerAdapter(
    fa: FragmentActivity,
    private val collections: List<CollectionUIState>
) : FragmentStateAdapter(fa) {
    override fun getItemCount() = collections.size
    override fun createFragment(position: Int): Fragment {
        return CollectionFragment.newInstance(collections[position])
    }
}