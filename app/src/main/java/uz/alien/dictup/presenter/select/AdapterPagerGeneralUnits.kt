package uz.alien.dictup.presenter.select

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.presenter.select.AdapterSelectorUnits
import uz.alien.dictup.presenter.base.ActivityBase
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration

class AdapterPagerGeneralUnits(
    private val count: Int,
    private val activity: ActivityBase,
    private val dp: Float,
    private val onAllUnitsAnimated: () -> Unit
) : RecyclerView.Adapter<AdapterPagerGeneralUnits.ViewHolder>() {

  inner class ViewHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val rv = RecyclerView(activity)
    rv.clipToPadding = false
    val m12 = (12 * dp).toInt()
    rv.setPadding(m12, m12, m12, m12)
    rv.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    val spanCount = if (count == 4) 2 else 3
    rv.layoutManager = AutoLayoutManager(activity, spanCount)
    rv.addItemDecoration(MarginItemDecoration((3.2 * dp).toInt(), spanCount))
    return ViewHolder(rv)
  }

  override fun getItemCount() = count

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val amount = if (count == 4) 20 else 30
    val adapter = AdapterSelectorUnits(
        amount,
        activity,
        { onAllUnitsAnimated() }
    )
    holder.recyclerView.adapter = adapter
  }
}