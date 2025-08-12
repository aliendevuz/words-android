package uz.alien.dictup.presentation.features.pick.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import uz.alien.dictup.presentation.features.pick.PartFragment
import uz.alien.dictup.presentation.features.pick.model.PartUIState

class PartPagerAdapter(
    fa: FragmentActivity,
    private val parts: List<PartUIState>
) : androidx.viewpager2.adapter.FragmentStateAdapter(fa) {
    override fun getItemCount() = parts.size
    override fun createFragment(position: Int): Fragment {
        return PartFragment.newInstance(parts[position])
    }
}