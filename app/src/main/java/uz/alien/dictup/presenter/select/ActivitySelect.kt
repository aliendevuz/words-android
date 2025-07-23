package uz.alien.dictup.presenter.select

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import uz.alien.dictup.presenter.select.AdapterGeneralBook
import uz.alien.dictup.presenter.select.AdapterGeneralBooks
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration
import uz.alien.dictup.databinding.ActivitySelectBinding
import uz.alien.dictup.presenter.base.ActivityBase
import uz.alien.dictup.presenter.quiz.ActivityQuiz

class ActivitySelect : ActivityBase() {

  private lateinit var binding: ActivitySelectBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivitySelectBinding.inflate(layoutInflater)
    setClearEdge()
    setSystemPadding(binding.root)
    setContentLayout {
      binding.root
    }

    binding.bStart.setOnClickListener {
      startActivity(Intent(this, ActivityQuiz::class.java))
    }

    val book = intent.getIntExtra("book", 1)
    val pick = intent.getIntExtra("picked_book", 1)
    val size = intent.getIntExtra("amount_of_book", 6)

    // aslida size 6 ni o'rniga intent orqali nechta kitob borligi va qaysi kitob tanlangani ko'rinishi kerak, yoki shared preferencesda, xullas har ikkisida ham ishlay verishi kerak

    binding.rvBook.layoutManager = AutoLayoutManager(this, 2)
    val adapterBook = AdapterGeneralBook(2, false) { clickedIndex ->
        binding.vpBook.setCurrentItem(clickedIndex, true)
    }
    binding.rvBook.adapter = adapterBook
    binding.rvBook.addItemDecoration(MarginItemDecoration((12 * dp()).toInt(), 2))

    adapterBook.setSelected(book)

    val vpAdapter =
        AdapterGeneralBooks(2, this, dp(), pick, size, binding.vpBook)
    binding.vpBook.adapter = vpAdapter
    binding.vpBook.setCurrentItem(book, false)

//    binding.vpBook.setPageTransformer { page, position ->
//      val scale = 0.92f + (1 - abs(position)) * 0.08f
//      page.scaleY = scale
//      page.scaleX = scale
//      page.alpha = 0.2f + (1 - abs(position)) * 0.8f
//    }

    binding.vpBook.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        adapterBook.setSelected(position)
      }
    })
  }

  override fun finish() {
    super.finish()
    withAnimation = true
    setExitSwipeAnimation()
  }

  companion object {
    var withAnimation = true
  }
}