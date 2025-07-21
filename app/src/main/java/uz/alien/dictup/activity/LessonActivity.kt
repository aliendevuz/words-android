package uz.alien.dictup.activity

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.adapter.AdapterWord
import uz.alien.dictup.databinding.ActivityLessonBinding
import uz.alien.dictup.manager.AppDictionary
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration

class LessonActivity : BaseActivity() {

  private lateinit var binding: ActivityLessonBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityLessonBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)

    val book = intent.getIntExtra("book", 0)
    val pick = intent.getIntExtra("pick", 0)
    val unit = intent.getIntExtra("unit", 0)
    binding.tv.text = "$book -> $pick -> $unit"

    binding.rvWords.layoutManager = AutoLayoutManager(this, 2)
    binding.rvWords.addItemDecoration(MarginItemDecoration((4 * dp()).toInt(), 2))
    when (book) {
      0 -> {
        AppDictionary.beginnerEn?.let {
          binding.rvWords.adapter = AdapterWord(this, it.subList(600 * pick + 20 * unit, 600 * pick + 20 * unit + 20))
        }
      }
      1 -> {
        AppDictionary.essentialEn?.let {
          binding.rvWords.adapter = AdapterWord(this, it.subList(600 * pick + 20 * unit, 600 * pick + 20 * unit + 20))
        }
      }
    }

    binding.tv.setOnClickListener {
      val intent = Intent(this, DetailsActivity::class.java)
      setOpenZoomAnimation(intent)
    }
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}