package uz.alien.dictup.presentation.features.pick

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.PickActivityBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.startActivityWithZoomAnimation
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.lesson.LessonActivity
import uz.alien.dictup.presentation.features.pick.model.NavigationEvent
import uz.alien.dictup.presentation.features.pick.pager.PartPagerAdapter
import uz.alien.dictup.presentation.features.pick.recycler.PartAdapter
import uz.alien.dictup.shared.WordCollection

@AndroidEntryPoint
class PickActivity : BaseActivity() {

  private lateinit var binding: PickActivityBinding
  private val viewModel: PickViewModel by viewModels()

  private lateinit var partAdapter: PartAdapter
  private lateinit var partPagerAdapter: PartPagerAdapter

  private val prefs by lazy {
    getSharedPreferences("app_prefs", MODE_PRIVATE)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setClearEdge()

    binding = PickActivityBinding.inflate(layoutInflater)
    setContentLayout {
      binding.root
    }

    if (getStatusPadding() != 0) {
      binding.statusBarPadding.setPadding(0, getStatusPadding(), 0, 0)
    }

    val collectionId = intent.getIntExtra("collection", WordCollection.ESSENTIAL.id)
    val collection = WordCollection.fromId(collectionId)!!
    val part = intent.getIntExtra("part", 0)

    viewModel.setCollection(collection)

    val partCount: Int
    val unitCount: Int

    when (collection) {
      WordCollection.BEGINNER -> {
        partCount = 4
        unitCount = 20
      }
      WordCollection.ESSENTIAL -> {
        partCount = 6
        unitCount = 30
      }
    }

    viewModel.prepareUnits(partCount, unitCount, part)

    partAdapter = PartAdapter { partId ->
      viewModel.setCurrentPart(partId)
      binding.vpPart.currentItem = partId
    }

    binding.rvParts.layoutManager = AutoLayoutManager(this, partCount)
    binding.rvParts.adapter = partAdapter


    partPagerAdapter = PartPagerAdapter(this, viewModel.parts.value)
    binding.vpPart.adapter = partPagerAdapter
    binding.vpPart.setCurrentItem(part, false)

    binding.vpPart.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        viewModel.setCurrentPart(position)
      }
    })

    var isFirst = true

    lifecycleScope.launch {
      viewModel.parts.collectLatest { parts ->
        partAdapter.submitList(parts)
        if (isFirst) {
          binding.vpPart.postDelayed(200L) {
            binding.vpPart.offscreenPageLimit = parts.size
          }
          isFirst = false
        }
      }
    }

    lifecycleScope.launch {
      viewModel.navigationEvent.collectLatest { event ->
        when(event) {
          is NavigationEvent -> {
            val intent = Intent(this@PickActivity, LessonActivity::class.java)
            intent.putExtra("collection", event.collectionId)
            intent.putExtra("part", event.partId)
            intent.putExtra("unit", event.unitId)
            startActivityWithZoomAnimation(intent)
          }
          null -> {}
        }
      }
    }

    binding.bGeneral.setOnClickListener {
      viewModel.openLesson()
    }

    binding.drawerButton.setOnClickListener {
      openDrawer()
    }
  }

  private fun getStatusPadding(): Int {
    return prefs.getInt("status_padding", 45)
  }

  override fun finish() {
    super.finish()
    applyExitZoomTransition()
  }
}