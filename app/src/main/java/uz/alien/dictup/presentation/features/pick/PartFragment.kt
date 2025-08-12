package uz.alien.dictup.presentation.features.pick

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.PickFragmentPartBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.features.pick.model.PartUIState
import uz.alien.dictup.presentation.features.pick.recycler.UnitAdapter

class PartFragment : Fragment() {

    private var _binding: PickFragmentPartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PickViewModel by activityViewModels()

    private lateinit var part: PartUIState
    private lateinit var unitAdapter: UnitAdapter

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

        _binding = PickFragmentPartBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {

        unitAdapter = UnitAdapter { unitId ->
            viewModel.openLesson(unitId)
        }

        val spanCount = part.unitCount / 10
        binding.rvUnits.layoutManager = AutoLayoutManager(requireContext(), spanCount)
        binding.rvUnits.itemAnimator = null
        binding.rvUnits.adapter = unitAdapter

        lifecycleScope.launch {
            viewModel.unitFlows[part.id].collectLatest { units ->
                unitAdapter.submitList(units)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        const val ARG_PART = "part"

        fun newInstance(part: PartUIState): PartFragment {
            val fragment = PartFragment()
            val args = Bundle()
            args.putParcelable(ARG_PART, part)
            fragment.arguments = args
            return fragment
        }
    }
}