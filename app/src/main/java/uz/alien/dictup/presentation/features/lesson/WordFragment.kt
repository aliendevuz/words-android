package uz.alien.dictup.presentation.features.lesson

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonFragmentWordBinding
import uz.alien.dictup.presentation.features.lesson.model.WordUIState
import kotlin.getValue

class WordFragment : Fragment() {

    private var _binding: LessonFragmentWordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LessonViewModel by activityViewModels()

    private lateinit var word: WordUIState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LessonFragmentWordBinding.inflate(inflater, container, false)

        word = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_WORD, WordUIState::class.java)!!
        } else {
            arguments?.getParcelable(ARG_WORD)!!
        }

        binding.tvWord.text = word.word

        if (word.score < 0) {
            binding.tvWord.setTextColor(binding.tvWord.context.getColor(R.color.red_500))
            binding.tvNativeWord.setTextColor(binding.tvWord.context.getColor(R.color.red_500))
        } else if (word.score >= 5) {
            binding.tvWord.setTextColor(binding.tvWord.context.getColor(R.color.text_highlight))
            binding.tvNativeWord.setTextColor(binding.tvWord.context.getColor(R.color.text_highlight))
        } else {
            binding.tvWord.setTextColor(binding.tvWord.context.getColor(R.color.secondary_text))
            binding.tvNativeWord.setTextColor(binding.tvWord.context.getColor(R.color.secondary_text))
        }

        binding.tvTranscription.text = prepareTranscription(word.transcription)

        binding.tvType.text = prepareType(word.type)

        binding.tvNativeWord.text = word.nativeWord

        getRightData(word.definition, word.nativeDefinition)?.let {
            binding.tvDefinition.text = it
        } ?: run {
            binding.tvDefinition.visibility = View.INVISIBLE
            binding.ibSpeechDefinition.visibility = View.INVISIBLE
        }

        getRightData(word.sentence, word.nativeSentence)?.let {
            binding.tvSentence.text = "→ ${prepareSentence(it)}"
        } ?: run {
            binding.tvSentence.visibility = View.INVISIBLE
            binding.ibSpeechSentence.visibility = View.INVISIBLE
        }

        binding.image.setOnClickListener {
            if (!word.sentence.startsWith("null_of_")) {
                viewModel.speakAloud(prepareSentence(word.sentence))
            }
        }

        binding.tvPageNumber.text = "${word.id + 1}"

        binding.ibSpeechWord.setOnClickListener {
            viewModel.speakAloud(word.word)
        }

        binding.tvWord.setOnClickListener {
            viewModel.speakAloud(word.word)
        }

        binding.tvTranscription.setOnClickListener {
            viewModel.speakAloud(word.word)
        }

        binding.tvType.setOnClickListener {
            viewModel.speakAloud(word.word)
        }

        binding.tvNativeWord.setOnClickListener {
            viewModel.speakAloud(word.word)
        }

        binding.ibSpeechDefinition.setOnClickListener {
            if (!word.definition.startsWith("null_of_")) {
                viewModel.speakAloud(word.definition)
            }
        }

        binding.ibSpeechSentence.setOnClickListener {
            if (!word.sentence.startsWith("null_of_")) {
                viewModel.speakAloud(prepareSentence(word.sentence))
            }
        }

        binding.tvDefinition.setOnClickListener {
            if (binding.tvDefinition.text == word.definition) {
                if (word.nativeDefinition.startsWith("null_of_")) {
                    Toast.makeText(requireContext(), "Tarjimasi mavjud emas", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    binding.tvDefinition.text = word.nativeDefinition
                }
            } else {
                if (word.definition.startsWith("null_of_")) {
                    Toast.makeText(requireContext(), "Tarjimasi mavjud emas", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    binding.tvDefinition.text = word.definition
                }
            }
        }

        binding.tvSentence.setOnClickListener {
            if (word.nativeSentence.startsWith("null_of_")) {
                Toast.makeText(requireContext(), "Tarjimasi mavjud emas", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.tvSentence.text == "→ ${prepareSentence(word.sentence)}") {
                    binding.tvSentence.text = "→ ${prepareSentence(word.nativeSentence)}"
                } else {
                    binding.tvSentence.text = "→ ${prepareSentence(word.sentence)}"
                }
            }
        }

        Glide.with(this)
            .load("https://assets.4000.uz/assets/en/${word.collection}/picture/${word.imageSource}.jpg")
            .placeholder(R.drawable.v_image)
            .fallback(R.drawable.v_no_image)
            .error(R.drawable.v_no_image)
            .into(binding.image)

        return binding.root
    }

    private fun prepareType(type: String): String {
        return "(${type})"
    }

    private fun prepareTranscription(transcription: String): String {
        if (transcription.startsWith("/")) {
            val endIndex = transcription.indexOf("/", 1)
            if (endIndex != -1) {
                return "[${transcription.substring(1, endIndex)}]"
            }
        }
        return "[$transcription]"
    }

    private fun prepareSentence(sentence: String): String {
        return when {
            sentence.startsWith("→ ") -> sentence.substring(2)
            sentence.startsWith("→") -> sentence.substring(1)
            else -> sentence
        }
    }

    private fun getRightData(data1: String, data2: String): String? {
        if (!data1.startsWith("null_of_")) {
            return data1
        }
        if (!data2.startsWith("null_of_")) {
            return data2
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_WORD = "word"

        fun newInstance(word: WordUIState): WordFragment {
            val fragment = WordFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORD, word)
            fragment.arguments = args
            return fragment
        }
    }
}