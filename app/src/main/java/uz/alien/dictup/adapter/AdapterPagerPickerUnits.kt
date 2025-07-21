package uz.alien.dictup.adapter

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.activity.BaseActivity
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration

class AdapterPagerPickerUnits(
  private val count: Int,
  private val activity: BaseActivity,
  private val dp: Float,
  private val onAllUnitsAnimated: () -> Unit
) : RecyclerView.Adapter<AdapterPagerPickerUnits.ViewHolder>() {

  inner class ViewHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val rv = RecyclerView(activity)
    rv.clipToPadding = false
    val m12 = (12 * dp).toInt()
    rv.setPadding(m12, m12, m12, m12)
    rv.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    val spanCount = if (count == 4) 2 else 3
    rv.layoutManager = AutoLayoutManager(activity, spanCount)
    rv.addItemDecoration(MarginItemDecoration((3.2 * dp).toInt(), spanCount))
    return ViewHolder(rv)
  }

  override fun getItemCount() = count

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val amount = if (count == 4) 20 else 30
    val adapter = AdapterPickerUnits(
      amount,
      activity,
      { onAllUnitsAnimated() }
    )
    holder.recyclerView.adapter = adapter
  }
}
