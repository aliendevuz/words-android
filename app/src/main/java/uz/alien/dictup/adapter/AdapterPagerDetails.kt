package uz.alien.dictup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.alien.dictup.R
import uz.alien.dictup.activity.BaseActivity
import uz.alien.dictup.databinding.PageDetailsBinding
import uz.alien.dictup.manager.AppDictionary

class AdapterPagerDetails(private val activity: BaseActivity) : RecyclerView.Adapter<AdapterPagerDetails.DetailViewHolder>() {

  inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = PageDetailsBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return 20
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
    return DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.page_details, parent, false))
  }

  override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
    val book = activity.intent.getIntExtra("book", 0)
    val pick = activity.intent.getIntExtra("pick", 0)
    val unit = activity.intent.getIntExtra("unit", 0)

    when (book) {
      0 -> {
        AppDictionary.beginnerEn?.let {
          val w = it[600 * pick + 20 * unit + position]
          holder.binding.tvWord.text = w.w
          AppDictionary.beginnerUz?.let { it1 ->
            holder.binding.tvWordTranslation.text = it1[600 * pick + 20 * unit + position].w
          }
          holder.binding.tvTranscription.text = w.t
          holder.binding.tvType.text = w.ty
          holder.binding.tvSample.text = w.s
          holder.binding.tvDescription.text = w.d
        }
      }
      1 -> {
        AppDictionary.essentialEn?.let {
          val index = 600 * pick + 20 * unit + position
          val w = it[index]
          holder.binding.tvWord.text = w.w
          AppDictionary.essentialUz?.let { it1 ->
            holder.binding.tvWordTranslation.text = it1[index].w
          }
          holder.binding.tvTranscription.text = w.t
          holder.binding.tvType.text = w.ty
          holder.binding.tvSample.text = w.s
          holder.binding.tvDescription.text = w.d
          holder.binding.image.load("http://raw.githubusercontent.com/xalilovdev/i/refs/heads/main/picture/1000.jpg") {
            crossfade(true)
            placeholder(R.drawable.v_image)
            error(R.drawable.v_image)
          }
        }
      }
    }
  }
}