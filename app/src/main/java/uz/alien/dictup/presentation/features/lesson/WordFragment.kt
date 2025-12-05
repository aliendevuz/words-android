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
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private val viewModel: LessonViewModel by activityViewModels()

    private lateinit var word: WordUIState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LessonFragmentWordBinding.inflate(inflater, container, false)

        word = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_WORD, WordUIState::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_WORD)
        } ?: run {
            // If word is null, return empty view to prevent crash
            return binding.root
        }

        setupWordDisplay()
        setupClickListeners()
        loadImage()

        return binding.root
    }

    private fun setupWordDisplay() {
        binding.tvWord.text = word.word
        binding.tvPageNumber.text = "${word.id + 1}"

        // Set colors based on score
        val textColor = when {
            word.score < 0 -> R.color.red_500
            word.score >= 5 -> R.color.text_highlight
            else -> R.color.secondary_text
        }

        binding.tvWord.setTextColor(requireContext().getColor(textColor))
        binding.tvNativeWord.setTextColor(requireContext().getColor(textColor))

        binding.tvTranscription.text = prepareTranscription(word.transcription)
        binding.tvType.text = prepareType(word.type)
        binding.tvNativeWord.text = word.nativeWord

        // Definition
        getRightData(word.definition, word.nativeDefinition)?.let {
            binding.tvDefinition.text = it
        } ?: run {
            binding.tvDefinition.visibility = View.INVISIBLE
            binding.ibSpeechDefinition.visibility = View.INVISIBLE
        }

        // Sentence
        getRightData(word.sentence, word.nativeSentence)?.let {
            binding.tvSentence.text = "→ ${prepareSentence(it)}"
        } ?: run {
            binding.tvSentence.visibility = View.INVISIBLE
            binding.ibSpeechSentence.visibility = View.INVISIBLE
        }
    }

    private fun setupClickListeners() {
        // Word clicks
        val wordClickListener = View.OnClickListener {
            safeSpeak(word.word)
        }

        binding.tvWord.setOnClickListener(wordClickListener)
        binding.tvTranscription.setOnClickListener(wordClickListener)
        binding.tvType.setOnClickListener(wordClickListener)
        binding.tvNativeWord.setOnClickListener(wordClickListener)
        binding.ibSpeechWord.setOnClickListener(wordClickListener)

        // Image click
        binding.image.setOnClickListener {
            if (!word.sentence.startsWith("null_of_")) {
                safeSpeak(prepareSentence(word.sentence))
            }
        }

        // Definition click
        binding.ibSpeechDefinition.setOnClickListener {
            if (!word.definition.startsWith("null_of_")) {
                safeSpeak(word.definition)
            }
        }

        binding.tvDefinition.setOnClickListener {
            toggleDefinition()
        }

        // Sentence click
        binding.ibSpeechSentence.setOnClickListener {
            if (!word.sentence.startsWith("null_of_")) {
                safeSpeak(prepareSentence(word.sentence))
            }
        }

        binding.tvSentence.setOnClickListener {
            toggleSentence()
        }
    }

    private fun toggleDefinition() {
        if (!isAdded || context == null) return

        when {
            binding.tvDefinition.text == word.definition -> {
                if (word.nativeDefinition.startsWith("null_of_")) {
                    showToast("Tarjimasi mavjud emas")
                } else {
                    binding.tvDefinition.text = word.nativeDefinition
                }
            }
            else -> {
                if (word.definition.startsWith("null_of_")) {
                    showToast("Tarjimasi mavjud emas")
                } else {
                    binding.tvDefinition.text = word.definition
                }
            }
        }
    }

    private fun toggleSentence() {
        if (!isAdded || context == null) return

        if (word.nativeSentence.startsWith("null_of_")) {
            showToast("Tarjimasi mavjud emas")
        } else {
            val currentSentence = prepareSentence(word.sentence)
            val nativeSentence = prepareSentence(word.nativeSentence)

            binding.tvSentence.text = if (binding.tvSentence.text == "→ $currentSentence") {
                "→ $nativeSentence"
            } else {
                "→ $currentSentence"
            }
        }
    }

    private fun safeSpeak(text: String) {
        if (isAdded && context != null) {
            viewModel.speakAloud(text)
        }
    }

    private fun showToast(message: String) {
        if (isAdded && context != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage() {
        if (!isAdded || context == null) return

        try {
            Glide.with(this)
                .load("https://assets.4000.uz/assets/en/${word.collection}/picture/${word.imageSource}.jpg")
                .placeholder(R.drawable.v_image)
                .fallback(R.drawable.v_no_image)
                .error(R.drawable.v_no_image)
                .into(binding.image)
        } catch (e: Exception) {
            // Silently handle image loading errors
        }
    }

    private fun prepareType(type: String): String {
        return "($type)"
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
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_WORD = "word"

        fun newInstance(word: WordUIState): WordFragment {
            return WordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_WORD, word)
                }
            }
        }
    }
}