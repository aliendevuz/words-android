package uz.alien.test.lesson.pager_adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WordPagerAdapter(
    fa: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}