package uz.alien.dictup.presentation.features.select

import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    private val prefs by lazy {
        requireContext().getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

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
            saveLastPartId(selectedIndex)
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
                saveLastPartId(position)
            }
        })

        val lastPartId = lastPartId()
        binding.vpPart.setCurrentItem(lastPartId, false)

        var isFirst = true

        lifecycleScope.launch {
            viewModel.partsFlows[collection.id].collectLatest { parts ->
                partAdapter.submitList(parts)
                if (isFirst) {
                    delay(50L)
                    binding.vpPart.offscreenPageLimit = collection.partCount
                    isFirst = false
                }
            }
        }
    }

    private fun saveLastPartId(id: Int) {
        prefs.edit {
            putInt("last_part_id_${collection.id}", id)
        }
    }

    private fun lastPartId(): Int  {
        return prefs.getInt("last_part_id_${collection.id}", 0)
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