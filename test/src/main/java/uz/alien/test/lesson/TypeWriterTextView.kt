package uz.alien.test.lesson

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TypeWriterTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val handler = Handler(Looper.getMainLooper())
    private var currentText: String = ""
    private var delay: Long = 100 // Harf uchun kechikish (ms)
    private var index = 0
    private var isTyping = false
    private var cursorEnabled = false
    private var onTypingComplete: (() -> Unit)? = null

    // Matnni yozishni boshlash
    fun startTyping(text: String, typingDelay: Long = 100, enableCursor: Boolean = false) {
        currentText = text
        delay = typingDelay
        cursorEnabled = enableCursor
        index = 0
        isTyping = true
        this.text = ""
        handler.removeCallbacksAndMessages(null)
        startTypingAnimation()
    }

    // Animatsiyani to‘xtatish
    fun stopTyping() {
        isTyping = false
        handler.removeCallbacksAndMessages(null)
        onTypingComplete?.invoke()
    }

    // Yozish tugagach chaqiriladigan callback
    fun setOnTypingCompleteListener(listener: () -> Unit) {
        onTypingComplete = listener
    }

    private fun startTypingAnimation() {
        val runnable = object : Runnable {
            override fun run() {
                if (!isTyping || index >= currentText.length) {
                    if (cursorEnabled) {
                        startBlinkingCursor()
                    } else {
                        isTyping = false
                        onTypingComplete?.invoke()
                    }
                    return
                }
                append(currentText[index].toString())
                index++
                handler.postDelayed(this, delay)
            }
        }
        handler.post(runnable)
    }

    private fun startBlinkingCursor() {
        var isCursorVisible = true
        val cursorRunnable = object : Runnable {
            override fun run() {
                if (!isTyping) return
                text = if (isCursorVisible) {
                    this@TypeWriterTextView.text.toString().replace("|", "") + "|"
                } else {
                    this@TypeWriterTextView.text.toString().replace("|", "")
                }
                isCursorVisible = !isCursorVisible
                handler.postDelayed(this, 500) // Miltillash tezligi
            }
        }
        handler.post(cursorRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null) // Resurslarni tozalash
    }
}