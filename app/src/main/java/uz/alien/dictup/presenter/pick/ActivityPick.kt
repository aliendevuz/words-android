package uz.alien.dictup.presenter.pick

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import uz.alien.dictup.presenter.pick.AdapterPagerPickerUnits
import uz.alien.dictup.presenter.pick.AdapterPickerChapter
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.MarginItemDecoration
import uz.alien.dictup.databinding.ActivityPickBinding
import uz.alien.dictup.presenter.base.ActivityBase
import uz.alien.dictup.presenter.select.ActivitySelect

class ActivityPick : ActivityBase() {

  private lateinit var binding: ActivityPickBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityPickBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }

    val book = intent.getIntExtra("book", 0)
    val pick = intent.getIntExtra("pick", 0)

    val amountOfBooks = if (book == 0) 4 else 6
    val amountOfUnits = if (book == 0) 20 else 30

    setSystemPadding(binding.statusBarPadding)

    val spacing = 4.dpToPx()

    binding.rvBooks.layoutManager = AutoLayoutManager(this, amountOfBooks)
    val adapterBooks = AdapterPickerChapter(amountOfBooks, true) { clickedIndex ->
        binding.vpUnits.setCurrentItem(clickedIndex, true)
        intent.putExtra("pick", clickedIndex)
    }
    binding.rvBooks.adapter = adapterBooks
    binding.rvBooks.addItemDecoration(MarginItemDecoration(spacing, amountOfBooks))
    adapterBooks.setSelected(pick)

    val vpAdapter = AdapterPagerPickerUnits(amountOfBooks, this, dp()) {
        binding.vpUnits.offscreenPageLimit = 6
        if (ActivitySelect.Companion.withAnimation) {
            ActivitySelect.Companion.withAnimation = false
            Log.d("@@@@", "cancelAnimation: Animation disabled")
        }
    }

    binding.vpUnits.adapter = vpAdapter
    binding.vpUnits.setCurrentItem(pick, false)

    binding.vpUnits.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        adapterBooks.setSelected(position)
        intent.putExtra("pick", position)
      }
    })

//    binding.rvUnits.addItemDecoration(MarginItemDecoration(spacing, 3))

    binding.bGeneral.setOnClickListener {
      Toast.makeText(this, "Keyingi darsga o'tishi kerak edi", Toast.LENGTH_SHORT).show()
    }

    binding.drawerButton.setOnClickListener {
      getDrawerLayout().openDrawer(GravityCompat.START)
    }
  }

  fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

  override fun finish() {
    super.finish()
    ActivitySelect.Companion.withAnimation = true
    setAlphaAnimation()
  }
}