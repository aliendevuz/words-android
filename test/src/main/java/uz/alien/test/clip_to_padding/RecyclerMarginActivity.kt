package uz.alien.test.clip_to_padding

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import uz.alien.test.databinding.ActivityRecyclerMarginBinding

class RecyclerMarginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRecyclerMarginBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityRecyclerMarginBinding.inflate(layoutInflater)

    setContentView(binding.root)

    binding.rv.layoutManager = GridLayoutManager(this, 3)

    binding.rv.adapter = AdapterSample()

    binding.rv.clipToPadding = false

//    binding.rv.addItemDecoration(GridSpacingItemDecoration(
//      spanCount = 3,
//      spacing = 20.dpToPx(),
//      includeEdge = true
//    ))
  }

  fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}