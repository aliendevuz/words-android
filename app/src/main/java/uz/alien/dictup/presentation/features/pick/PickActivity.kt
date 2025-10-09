package uz.alien.dictup.presentation.features.pick

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.databinding.PickActivityBinding
import uz.alien.dictup.domain.model.WordCollection
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.lesson.LessonActivity
import uz.alien.dictup.presentation.features.pick.model.NavigationEvent
import uz.alien.dictup.presentation.features.pick.pager.PartPagerAdapter
import uz.alien.dictup.presentation.features.pick.recycler.PartAdapter
import uz.alien.dictup.presentation.features.select.SelectActivity

@AndroidEntryPoint
class PickActivity : BaseActivity() {

    private lateinit var binding: PickActivityBinding
    private val viewModel: PickViewModel by viewModels()

    private lateinit var partAdapter: PartAdapter
    private lateinit var partPagerAdapter: PartPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setClearEdge()

        binding = PickActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        setCaption("Choice Unit")

        val collectionId = intent.getIntExtra("collection", WordCollection.ESSENTIAL.id)
        val collection = WordCollection.fromId(collectionId)!!
        val part = intent.getIntExtra("part", 0)
        val unit = intent.getIntExtra("unit", -1)
        val storyNumber = intent.getIntExtra("sn", 0)

        val autoOpen = intent.getBooleanExtra("auto_open", false)

        if (unit != -1) {
            viewModel.openLesson(unit, storyNumber)
        }

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

        if (autoOpen) {
            binding.root.postDelayed(BuildConfig.DURATION) {
                viewModel.openLesson(fromStart = true)
            }
        }

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
                if (viewModel.scrollPage) {
                    viewModel.scrollPage = false
                    binding.vpPart.currentItem = viewModel.currentPart.value
                }
                if (isFirst) {
                    binding.vpPart.postDelayed(200L) {
                        binding.vpPart.offscreenPageLimit = if (BuildConfig.DEBUG) 1 else parts.size
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
                        intent.putExtra("collection", collectionId)
                        intent.putExtra("part", part)
                        intent.putExtra("unit", event.unitId)
                        intent.putParcelableArrayListExtra("words", event.words)
                        intent.putParcelableArrayListExtra("native_words", event.nativeWords)
                        intent.putParcelableArrayListExtra("scores", event.scores)
                        intent.putParcelableArrayListExtra("stories", event.stories)
                        intent.putExtra("sn", event.storyNumber)
                        baseViewModel.startActivityWithAnimation(intent)
                    }
                    null -> {
                        Toast.makeText(this@PickActivity, "Siz barcha darslarni tugatdingiz 🎉", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@PickActivity, SelectActivity::class.java)
                        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
                    }
                }
            }
        }

        binding.bGeneral.setOnClickListener {
            viewModel.openLesson()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.updateUnits()
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}