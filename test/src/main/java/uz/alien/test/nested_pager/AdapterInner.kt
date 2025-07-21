package uz.alien.test.nested_pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.InnerBinding

class AdapterInner : RecyclerView.Adapter<AdapterInner.InnerViewHolder>() {

  inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = InnerBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 3
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
    return InnerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.inner, parent, false))
  }

  override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
    holder.binding.tv.text = "$position"
  }
}