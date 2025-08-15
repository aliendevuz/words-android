package uz.alien.dictup.presentation.features.select

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.SelectActivityBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.common.extention.startActivityWithAlphaAnimation
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.quiz.QuizActivity
import uz.alien.dictup.presentation.features.select.pager.CollectionPagerAdapter
import uz.alien.dictup.presentation.features.select.recycler.CollectionAdapter

@AndroidEntryPoint
class SelectActivity : BaseActivity() {

    private lateinit var binding: SelectActivityBinding
    private val viewModel: SelectViewModel by viewModels()

    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var collectionPagerAdapter: CollectionPagerAdapter

    private var isOpened = false

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SelectActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        setClearEdge()
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        collectionAdapter = CollectionAdapter { selectedIndex ->
            viewModel.setCurrentCollection(selectedIndex)
            binding.vpCollection.currentItem = selectedIndex
            saveLastCollectionId(selectedIndex)
        }

        val collections = viewModel.collectionsFlow.value

        binding.rvCollection.layoutManager =
            AutoLayoutManager(this, collections.size)
        binding.rvCollection.adapter = collectionAdapter

        collectionPagerAdapter = CollectionPagerAdapter(this, collections)
        binding.vpCollection.adapter = collectionPagerAdapter

        binding.vpCollection.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentCollection(position)
                saveLastCollectionId(position)
            }
        })

        val lastCollectionId = lastCollectionId()
        binding.vpCollection.setCurrentItem(lastCollectionId, false)

        var isFirst = true

        lifecycleScope.launch {
            viewModel.collectionsFlow.collectLatest { collections ->
                collectionAdapter.submitList(collections)
                if (isFirst) {
                    binding.vpCollection.offscreenPageLimit = collections.size
                    isFirst = false
                }
            }
        }

        binding.bStart.setOnClickListener {
            if (!isOpened) {
                isOpened = true
                val selectedUnits = viewModel.getSelectedUnits()
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("quiz_count", viewModel.getQuizCount())
                intent.putExtra("units", selectedUnits.toTypedArray())
                startActivityWithAlphaAnimation(intent)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        isOpened = false
    }

    private fun saveLastCollectionId(id: Int) {
        prefs.edit {
            putInt("last_collection_id", id)
        }
    }

    private fun lastCollectionId(): Int  {
        return prefs.getInt("last_collection_id", 0)
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}