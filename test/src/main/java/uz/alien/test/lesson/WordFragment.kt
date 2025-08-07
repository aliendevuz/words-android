package uz.alien.test.lesson

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.alien.test.databinding.LessonFragmentWordBinding
import uz.alien.test.lesson.model.Word

class WordFragment : Fragment() {

    private var _binding: LessonFragmentWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var word: Word

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LessonFragmentWordBinding.inflate(inflater, container, false)

        word = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_WORD, Word::class.java)!!
        } else {
            arguments?.getParcelable(ARG_WORD)!!
        }

        binding.vpWord.text = word.word

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_WORD = "word"

        fun newInstance(word: Word): WordFragment {
            val fragment = WordFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORD, word)
            fragment.arguments = args
            return fragment
        }
    }
}