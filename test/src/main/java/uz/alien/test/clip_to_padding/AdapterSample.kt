package uz.alien.test.clip_to_padding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.ItemSampleBinding

class AdapterSample : RecyclerView.Adapter<AdapterSample.SampleViewHolder>() {

  inner class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemSampleBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 30
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
    return SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sample, parent, false))
  }

  override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
    if (position < 3) { // 3 - span count
      val layoutParams = holder.binding.root.layoutParams as MarginLayoutParams
      layoutParams.updateMargins(top = 0)
      holder.binding.root.layoutParams = layoutParams
    }
  }
}