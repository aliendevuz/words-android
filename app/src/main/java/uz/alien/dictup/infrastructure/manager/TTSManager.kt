package uz.alien.dictup.infrastructure.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.domain.repository.DataStoreRepository
import java.util.Locale
import javax.inject.Inject

class TTSManager @Inject constructor(
    context: Context,
    private val dataStoreRepository: DataStoreRepository
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    val managerScope = CoroutineScope(Dispatchers.IO)

    private var onDoneCallback: (() -> Unit)? = null

    init {

        tts = TextToSpeech(context, this)

        managerScope.launch {
            dataStoreRepository.getTTSPitch().collectLatest {
                tts?.setPitch(it)
            }
        }

        managerScope.launch {
            dataStoreRepository.getTTSSpeed().collectLatest {
                tts?.setSpeechRate(it)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onDoneCallback?.invoke() ?: Logger.d("onDoneCallback is null :(")
                }
                override fun onError(utteranceId: String?) {
                    Logger.d("TTS", "Error: $utteranceId")
                }
            })

            // TTS initialize qilingandan keyin
            val voices = tts?.voices // mavjud ovozlar ro‘yxati (Set<Voice>)

            voices?.forEach { voice ->
                Logger.d("TTS", "Voice: $voice")
            }

// Masalan, US English erkak ovozini tanlash
//            val selectedVoice = voices?.filter {
//                it.locale.language == "en" && it.locale.country == "US"
//            }

//            if (selectedVoice != null) {
//                tts?.voice = selectedVoice[0]
//            }
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        onDoneCallback = onDone
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun stop() = tts?.stop()

    fun release() {
        tts?.shutdown()
        managerScope.cancel()
    }
}