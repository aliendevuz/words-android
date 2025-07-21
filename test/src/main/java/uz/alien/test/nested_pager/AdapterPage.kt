package uz.alien.test.nested_pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.PageBinding

class AdapterPage : RecyclerView.Adapter<AdapterPage.PageViewHolder>() {

  inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = PageBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 2
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
    return PageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.page, parent, false))
  }

  override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
    holder.binding.vp.adapter = AdapterInner()
    holder.binding.vp.isNestedScrollingEnabled = true
  }
}