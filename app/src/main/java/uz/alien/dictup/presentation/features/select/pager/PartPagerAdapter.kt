package uz.alien.dictup.presentation.features.select.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.alien.dictup.presentation.features.select.PartFragment
import uz.alien.dictup.presentation.features.select.model.PartUIState

class PartPagerAdapter(
    fa: FragmentActivity,
    private val parts: List<PartUIState>
) : FragmentStateAdapter(fa) {
    override fun getItemCount() = parts.size
    override fun createFragment(position: Int): Fragment {
        return PartFragment.newInstance(parts[position])
    }
}