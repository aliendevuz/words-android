package uz.alien.dictup.presentation.features.select

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.databinding.SelectActivityBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.common.extention.startActivityWithAlphaAnimation
import uz.alien.dictup.presentation.common.model.AnimationType
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SelectActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        hideAppBar()

        setClearEdge()
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        collectionAdapter = CollectionAdapter { selectedIndex ->
            viewModel.setCurrentCollection(selectedIndex)
            binding.vpCollection.currentItem = selectedIndex
            viewModel.saveLastCollectionId(selectedIndex)
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
                viewModel.saveLastCollectionId(position)
            }
        })

        val lastCollectionId = viewModel.getLastCollectionId()
        binding.vpCollection.setCurrentItem(lastCollectionId, false)

        var isFirst = true

        lifecycleScope.launch {
            viewModel.collectionsFlow.collectLatest { collections ->
                collectionAdapter.submitList(collections)
                if (isFirst) {
                    binding.vpCollection.offscreenPageLimit = if (BuildConfig.DEBUG) 1 else collections.size
                    isFirst = false
                }
            }
        }

        binding.bStart.setOnClickListener {
            val units = viewModel.getSelectedUnits()
            if (units.isNotEmpty()) {
                val selectedUnits = ArrayList(units.filter { it.collectionId == viewModel.currentCollection.value })
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("quiz_count", viewModel.getQuizCount())
                intent.putParcelableArrayListExtra("selected_units", selectedUnits)
                baseViewModel.startActivityWithAnimation(intent, AnimationType.FADE)
            } else {
                // TODO: show snackbar
                Logger.d("Select at least one unit")
                val snackbar = Snackbar.make(binding.root, "Select at least one unit", Snackbar.LENGTH_SHORT)
                snackbar.setAction("OK") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUnits()
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}