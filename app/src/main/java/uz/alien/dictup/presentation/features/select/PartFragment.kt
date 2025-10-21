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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.databinding.SelectFragmentPartBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.features.select.component.SelectableRecyclerView
import uz.alien.dictup.presentation.features.select.model.PartUIState
import uz.alien.dictup.presentation.features.select.recycler.UnitAdapter

class PartFragment : Fragment() {

    private var _binding: SelectFragmentPartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SelectViewModel by activityViewModels()

    private lateinit var unitAdapter: UnitAdapter
    private lateinit var part: PartUIState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        part = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_PART, PartUIState::class.java)!!
        } else {
            arguments?.getParcelable(ARG_PART)!!
        }

        _binding = SelectFragmentPartBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {

        unitAdapter = UnitAdapter()

        val spanCount = part.unitCount / 10
        binding.rvSelectableUnits.layoutManager = AutoLayoutManager(requireContext(), spanCount)
        binding.rvSelectableUnits.itemAnimator = null
        binding.rvSelectableUnits.adapter = unitAdapter

        binding.rvSelectableUnits.selectListener = object : SelectableRecyclerView.OnUnitSelectListener {

            override fun onSingleTap(position: Int) {
                viewModel.toggleUnitSelection(position)
            }

            override fun onLongPress(position: Int) {
                binding.rvSelectableUnits.setSelection(viewModel.isUnitSelected(position))
                viewModel.toggleUnitSelection(position)
            }

            override fun onMove(position: Int, selection: Boolean) {
                if (selection) {
                    viewModel.selectUnit(position)
                } else {
                    viewModel.unselectUnit(position)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unitFlows[part.collectionId][part.id].collectLatest { units ->
                    unitAdapter.submitList(units)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_PART = "arg_part"

        fun newInstance(part: PartUIState): PartFragment {
            val fragment = PartFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_PART, part)
            fragment.arguments = bundle
            return fragment
        }
    }
}