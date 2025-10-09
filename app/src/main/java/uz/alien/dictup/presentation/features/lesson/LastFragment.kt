package uz.alien.dictup.presentation.features.lesson

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonFragmentLastBinding
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.quiz.QuizActivity

class LastFragment : Fragment() {

    private var _binding: LessonFragmentLastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LessonFragmentLastBinding.inflate(inflater, container, false)

        binding.bRestart.setOnClickListener {
            val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.drawerLayout) as? BaseFragment
            fragment?.setCurrentPage(0)
        }

        binding.bOpenQuiz.setOnClickListener {

            dismissWithAnimation {
                val activity = (activity as LessonActivity)
                val selectedUnits = arrayListOf(SelectedUnit(activity.collection, activity.part, activity.unit))
                val intent = Intent(activity, QuizActivity::class.java)
                intent.putExtra("quiz_count", 20)
                intent.putParcelableArrayListExtra("selected_units", selectedUnits)
                activity.baseViewModel.startActivityWithAnimation(intent, AnimationType.FADE)
            }
        }

        binding.bExit.setOnClickListener {
            val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.drawerLayout) as? BaseFragment
            fragment?.dismissWithAnimation()
        }

        return binding.root
    }

    private fun dismissWithAnimation(onDone: () -> Unit) {
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.drawerLayout) as? BaseFragment
        fragment?.dismissWithAnimation(onDone)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(): LastFragment {
            val args = Bundle()
            val fragment = LastFragment()
            fragment.arguments = args
            return fragment
        }
    }
}