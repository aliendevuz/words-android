package uz.alien.dictup.presentation.features.select

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.databinding.SelectFragmentCollectionBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.features.select.model.CollectionUIState
import uz.alien.dictup.presentation.features.select.pager.PartPagerAdapter
import uz.alien.dictup.presentation.features.select.recycler.PartAdapter

class CollectionFragment : Fragment() {

    private var _binding: SelectFragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SelectViewModel by activityViewModels()

    private lateinit var partAdapter: PartAdapter
    private lateinit var partPagerAdapter: PartPagerAdapter
    private lateinit var collection: CollectionUIState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_COLLECTION, CollectionUIState::class.java)!!
        } else {
            arguments?.getParcelable(ARG_COLLECTION)!!
        }

        _binding = SelectFragmentCollectionBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {

        binding.bSelectAll.setOnClickListener {
            viewModel.selectAll()
        }
        binding.bInvert.setOnClickListener {
            viewModel.invertAll()
        }
        binding.bRandom.setOnClickListener {
            viewModel.randomSelect()
        }
        binding.bClearAll.setOnClickListener {
            viewModel.clearAll()
        }

        partAdapter = PartAdapter { selectedIndex ->
            viewModel.setCurrentPart(selectedIndex)

            viewModel.saveLastPartId(selectedIndex)
            binding.vpPart.currentItem = selectedIndex
        }

        val parts = viewModel.partsFlows[collection.id]

        binding.rvPart.layoutManager = AutoLayoutManager(requireContext(), parts.value.size)
        binding.rvPart.adapter = partAdapter

        partPagerAdapter = PartPagerAdapter(requireActivity(), parts.value)
        binding.vpPart.adapter = partPagerAdapter

        binding.vpPart.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPart(collection.id, position)
                viewModel.saveLastPartId(position)
            }
        })

        val lastPartId = viewModel.getLastPartId()
        binding.vpPart.setCurrentItem(lastPartId, false)

        binding.sbQuizCount.addOnChangeListener { slider, value, fromUser ->
            viewModel.setQuizCount(value)
        }

        var isFirst = true

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.partsFlows[collection.id].collectLatest { parts ->
                    partAdapter.submitList(parts)
                    if (isFirst) {
                        delay(50L)
                        binding.vpPart.offscreenPageLimit = if (BuildConfig.DEBUG) 1 else collection.partCount
                        isFirst = false
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedUnitsCount.collectLatest { count ->
                    if (collection.id == viewModel.currentCollection.value) {
                        if (collection.id == 0) {
                            if (count.first in 1..6) {
                                if (binding.sbQuizCount.value > count.first * 20) {
                                    binding.sbQuizCount.value = (count.first * 20).toFloat()
                                }
                                binding.sbQuizCount.valueTo = (count.first * 20).toFloat()
                            }
                        } else {
                            if (count.second in 1..6) {
                                if (binding.sbQuizCount.value > count.second * 20) {
                                    binding.sbQuizCount.value = (count.second * 20).toFloat()
                                }
                                binding.sbQuizCount.valueTo = (count.second * 20).toFloat()
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.quizCount.collectLatest { c ->
                    val count = if (collection.id == 0) c.first
                    else c.second
                    binding.sbQuizCount.value = count
                    binding.tvQuizCount.text = "${count.toInt()}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_COLLECTION = "arg_collection"

        fun newInstance(collection: CollectionUIState): CollectionFragment {
            val fragment = CollectionFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_COLLECTION, collection)
            fragment.arguments = bundle
            return fragment
        }
    }
}