package uz.alien.test.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.alien.test.databinding.LessonFragmentLastBinding

class LastFragment : Fragment() {

    private var _binding: LessonFragmentLastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LessonFragmentLastBinding.inflate(inflater, container, false)


        return binding.root
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