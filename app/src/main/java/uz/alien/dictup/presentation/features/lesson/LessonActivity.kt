package uz.alien.dictup.presentation.features.lesson

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.databinding.LessonActivityBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.common.extention.startActivityWithZoomAnimation
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.detail.DetailActivity

class LessonActivity : BaseActivity() {

  private lateinit var binding: LessonActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = LessonActivityBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)

    val collection = intent.getIntExtra("collection", 0)
    val part = intent.getIntExtra("part", 0)
    val unit = intent.getIntExtra("unit", 0)
    binding.tv.text = "$collection -> $part -> $unit"

    binding.rvWords.layoutManager = AutoLayoutManager(this, 2)

    binding.tv.setOnClickListener {
      val intent = Intent(this, DetailActivity::class.java)
      startActivityWithZoomAnimation(intent)
    }
  }

  override fun finish() {
    super.finish()
    applyExitZoomTransition()
  }
}