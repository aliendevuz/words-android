package uz.alien.dictup.presentation.features.select

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import uz.alien.dictup.R
import uz.alien.dictup.databinding.SelectPageBooksBinding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.component.MarginItemDecoration

class AdapterGeneralBooks(
    private val count: Int,
    private val activity: BaseActivity,
    private val dp: Float,
    private val pick: Int,
    private val size: Int,
    private val vp: ViewPager2
) : RecyclerView.Adapter<AdapterGeneralBooks.GeneralBooksViewHolder>() {

  val pagers = ArrayList<ViewPager2>()

  inner class GeneralBooksViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val binding = SelectPageBooksBinding.bind(view)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralBooksViewHolder {
    return GeneralBooksViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.select_page_books, parent, false))
  }

  override fun getItemCount() = 2

  override fun onBindViewHolder(holder: GeneralBooksViewHolder, position: Int) {

    val amount = when (position) {
      0 -> 4
      1 -> 6
      else -> 6
    }

    holder.binding.rvParts.layoutManager = AutoLayoutManager(activity, amount)
    val adapterBooks = AdapterSelectorChapter(
        amount,
        true
    ) { clickedIndex ->
        holder.binding.vpUnits.setCurrentItem(clickedIndex, true)
    }
    holder.binding.rvParts.adapter = adapterBooks
    holder.binding.rvParts.addItemDecoration(MarginItemDecoration(4.0f, activity.resources, amount))

//    adapterBooks.setSelected(pick)

    val vpAdapter = AdapterPagerGeneralUnits(
        amount,
        activity,
        dp
    ) {
        vp.offscreenPageLimit = 2
        Handler(Looper.getMainLooper()).postDelayed(200L) {
            pagers.forEach {
                it.offscreenPageLimit = amount
            }
        }
        if (SelectActivity.Companion.withAnimation) {
            SelectActivity.Companion.withAnimation = false
            Log.d("@@@@", "cancelAnimation: Animation disabled")
        }
    }
    pagers.add(holder.binding.vpUnits)
    holder.binding.vpUnits.adapter = vpAdapter
    holder.binding.vpUnits.setCurrentItem(pick, false)
    adapterBooks.setSelected(pick)

    holder.binding.vpUnits.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        adapterBooks.setSelected(position)
      }
    })
  }
}