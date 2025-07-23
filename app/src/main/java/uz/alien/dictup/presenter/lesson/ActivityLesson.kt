package uz.alien.dictup.presenter.lesson

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.presenter.detail.ActivityDetail
import uz.alien.dictup.presenter.lesson.AdapterWord
import uz.alien.dictup.data.AppDictionary
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration
import uz.alien.dictup.databinding.ActivityLessonBinding
import uz.alien.dictup.presenter.base.ActivityBase

class ActivityLesson : ActivityBase() {

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
          binding.rvWords.adapter =
              AdapterWord(this, it.subList(600 * pick + 20 * unit, 600 * pick + 20 * unit + 20))
        }
      }
      1 -> {
        AppDictionary.essentialEn?.let {
          binding.rvWords.adapter =
              AdapterWord(this, it.subList(600 * pick + 20 * unit, 600 * pick + 20 * unit + 20))
        }
      }
    }

    binding.tv.setOnClickListener {
      val intent = Intent(this, ActivityDetail::class.java)
      setOpenZoomAnimation(intent)
    }
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}