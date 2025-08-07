package uz.alien.dictup.presentation.features.lesson

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.presentation.features.detail.DetailActivity
import uz.alien.dictup.presentation.common.AutoLayoutManager
import uz.alien.dictup.presentation.common.MarginItemDecoration
import uz.alien.dictup.databinding.ActivityLessonBinding
import uz.alien.dictup.presentation.features.base.BaseActivity

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

    binding.tv.setOnClickListener {
      val intent = Intent(this, DetailActivity::class.java)
      setOpenZoomAnimation(intent)
    }
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}