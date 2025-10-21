package uz.alien.dictup.presentation.features.about

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import uz.alien.dictup.databinding.AboutActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitSwipeAnimation
import uz.alien.dictup.presentation.common.extention.clearPadding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity

class AboutActivity : BaseActivity() {

    private lateinit var binding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AboutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClearEdge()
        clearPadding(binding.root)
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        makeNameClickable(
            binding.tvDeveloper,
            name = "Ibrohim Xalilov",
            url = "https://t.me/aliendevuz"
        )

        makeNameClickable(
            binding.tvContentSpecialist,
            name = "Olimjon Xolbekov",
            url = "https://t.me/abc_2202"
        )
    }

    private fun makeNameClickable(textView: TextView, name: String, url: String) {
        val fullText = textView.text.toString()
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(name)
        val end = start + name.length

        if (start >= 0) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    widget.context.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = "#1E88E5".toColorInt()
                    ds.isUnderlineText = true
                }
            }

            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.text = spannable
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.highlightColor = Color.TRANSPARENT
        }
    }

    override fun finish() {
        super.finish()
        applyExitSwipeAnimation()
    }
}