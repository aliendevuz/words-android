package uz.alien.dictup.presentation.features.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.DetailsPageBinding
import uz.alien.dictup.presentation.features.base.BaseActivity

class AdapterPagerDetails(private val activity: BaseActivity) : RecyclerView.Adapter<AdapterPagerDetails.DetailViewHolder>() {

  inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = DetailsPageBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 20
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
    return DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.details_page, parent, false))
  }

  override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
    val book = activity.intent.getIntExtra("book", 0)
    val pick = activity.intent.getIntExtra("pick", 0)
    val unit = activity.intent.getIntExtra("unit", 0)
  }
}