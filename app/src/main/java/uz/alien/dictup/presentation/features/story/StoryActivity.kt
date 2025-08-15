package uz.alien.dictup.presentation.features.story

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import uz.alien.dictup.R
import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.databinding.StoryActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity
import java.util.Locale

class StoryActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: StoryActivityBinding
    lateinit var tts: TextToSpeech

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    private var sentences: List<String> = emptyList()
    private var currentIndex = 0
    private var isPaused = true

    private lateinit var markwonSpannable: Spannable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StoryActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        tts = TextToSpeech(this, this)

        setClearEdge()
        setSystemPadding(binding.statusBarPadding)

        initViews()
    }

    private fun getPitch(): Float {
        return prefs.getFloat("pitch", 1.0f)
    }

    private fun getSpeed(): Float {
        return prefs.getFloat("speed", 1.0f)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.e("TTS", "Til qo‘llab-quvvatlanmaydi")
            } else {
                tts.setPitch(getPitch())
                tts.setSpeechRate(getSpeed())

                // Listener qo‘shamiz
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Logger.d("TTS", "O‘qish boshlandi: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        if (utteranceId != "warmup") {
                            runOnUiThread {
                                if (!isPaused) {
                                    currentIndex++
                                    if (currentIndex < sentences.size) {
                                        playCurrent()
                                    }
                                }
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        Logger.e("TTS", "Xato: $utteranceId")
                    }
                })

                // Warmup uchun bo‘sh gap
                tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null, "warmup")
            }
        } else {
            Logger.e("TTS", "TTS ishga tushmadi")
        }
    }

    private fun highlightCurrentSentence() {
        val spannable = SpannableString(markwonSpannable) // 🔹 original formatting saqlanadi

        val currentSentence = sentences[currentIndex]
        val start = spannable.toString().indexOf(currentSentence)
        if (start >= 0) {
            spannable.setSpan(
                BackgroundColorSpan(Color.YELLOW),
                start,
                start + currentSentence.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvContent.text = spannable
    }

    private fun speakAloud(content: String) {
        val params = Bundle()
        val utteranceId = System.currentTimeMillis().toString()
        tts.speak(content, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    private fun playCurrent() {
        if (currentIndex in sentences.indices) {
            highlightCurrentSentence()
            val text = sentences[currentIndex]
            speakAloud(text)
        }
    }

    private fun pause() {
        tts.stop()
        isPaused = true
    }

    private fun resume() {
        if (isPaused) {
            playCurrent()
            isPaused = false
        }
    }

    private fun next() {
        if (currentIndex < sentences.size - 1) {
            currentIndex++
        }
    }

    private fun prev() {
        if (currentIndex > 0) {
            currentIndex--
        }
    }

    private fun initViews() {

        val title = intent.getStringExtra("title") ?: "No title"
        val content = intent.getStringExtra("content") ?: "No content"

        sentences = splitAndCleanContent(content)

        val markwon = Markwon
            .builder(this)
            .usePlugin(ImagesPlugin.create())
            .usePlugin(GlideImagesPlugin.create(this))
            .build()

        val node = markwon.parse("# $title\n\n$content")
        markwonSpannable = markwon.render(node) as Spannable
        markwon.setParsedMarkdown(binding.tvContent, markwonSpannable)

        val markdown = markwon.render(node)

        markwon.setParsedMarkdown(binding.tvContent, markdown)

        binding.ibPlay.setOnClickListener {
            if (isPaused) {
                resume()
                binding.ibPlay.setImageResource(R.drawable.v_pause)
            } else {
                pause()
                binding.ibPlay.setImageResource(R.drawable.v_play)
            }
        }

        binding.ibPrev.setOnClickListener {
            prev()
        }

        binding.ibNext.setOnClickListener {
            next()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

    fun splitAndCleanContent(content: String): List<String> {
        return content
            .lines()  // 🔹 matnni \n bo'yicha bo'lish
            .flatMap { line ->
                line.split('.') // 🔹 keyin har bir qatorni nuqta bo'yicha bo'lish
            }
            .map { it.trim() } // 🔹 old-orqa bo'sh joylarni olish
            .filter { it.isNotEmpty() } // 🔹 bo'sh satrlarni olib tashlash
            .filter { it.any { ch -> ch.isLetterOrDigit() } } // 🔹 faqat belgilar bo‘lgan satrlar
            .map { it.replace(Regex("\\s+"), " ") } // 🔹 ortiqcha bo'sh joylarni bitta bo'sh joyga tushirish
    }
}