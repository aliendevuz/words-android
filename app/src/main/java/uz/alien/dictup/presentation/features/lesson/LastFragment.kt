package uz.alien.dictup.presentation.features.lesson

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonFragmentLastBinding
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.quiz.QuizActivity

class LastFragment : Fragment() {

    private var _binding: LessonFragmentLastBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LessonFragmentLastBinding.inflate(inflater, container, false)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.bRestart.setOnClickListener {
            safeFragmentOperation {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentById(R.id.drawerLayout) as? BaseFragment
                fragment?.setCurrentPage(0)
            }
        }

        binding.bOpenQuiz.setOnClickListener {
            safeFragmentOperation {
                dismissWithAnimation {
                    val activity = (activity as? LessonActivity) ?: return@dismissWithAnimation
                    if (activity.isFinishing || activity.isDestroyed) return@dismissWithAnimation

                    val selectedUnits = arrayListOf(
                        SelectedUnit(activity.collection, activity.part, activity.unit)
                    )
                    val intent = Intent(activity, QuizActivity::class.java).apply {
                        putExtra("quiz_count", 20)
                        putParcelableArrayListExtra("selected_units", selectedUnits)
                    }

                    try {
                        activity.baseViewModel.startActivityWithAnimation(intent, AnimationType.FADE)
                    } catch (e: Exception) {
                        // Silently handle
                    }
                }
            }
        }

        binding.bExit.setOnClickListener {
            safeFragmentOperation {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentById(R.id.drawerLayout) as? BaseFragment
                fragment?.dismissWithAnimation()
            }
        }
    }

    private fun safeFragmentOperation(operation: () -> Unit) {
        if (!isAdded || activity == null || activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                operation()
            } catch (e: Exception) {
                // Handle exception silently
            }
        }
    }

    private fun dismissWithAnimation(onDone: () -> Unit = {}) {
        if (!isAdded || activity == null) {
            return
        }

        val fragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.drawerLayout) as? BaseFragment
        fragment?.dismissWithAnimation(onDone)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): LastFragment {
            return LastFragment().apply {
                arguments = Bundle()
            }
        }
    }
}