package uz.alien.test.nested_pager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.alien.test.databinding.ActivityPagerBinding

class PagerActivity : AppCompatActivity() {

  private lateinit var binding: ActivityPagerBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityPagerBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.vp.adapter = AdapterPage()
  }
}