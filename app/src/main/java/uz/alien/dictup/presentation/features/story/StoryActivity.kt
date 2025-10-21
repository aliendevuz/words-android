package uz.alien.dictup.presentation.features.story

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonTooltipTranslationBinding
import uz.alien.dictup.databinding.StoryActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.lesson.model.WordUIState
import kotlin.jvm.java

@AndroidEntryPoint
class StoryActivity : BaseActivity() {

    private lateinit var binding: StoryActivityBinding
    private val viewModel: StoryViewModel by viewModels()

    private lateinit var markwonSpannable: Spannable
    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StoryActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }
        setClearEdge()

        setCaption("Reading mode")

        markwon = Markwon
            .builder(this)
            .usePlugin(ImagesPlugin.create())
            .usePlugin(GlideImagesPlugin.create(this))
            .build()

        initViews()
    }

    private fun highlightCurrentSentence(currentIndex: Int) {
        val spannable = SpannableString(markwonSpannable)

        if (currentIndex != -1) {
            val currentSentence = viewModel.sentences.value[currentIndex]
            val start = spannable.toString().indexOf(currentSentence)
            if (start >= 0) {
                spannable.setSpan(
                    BackgroundColorSpan(getColor(R.color.text_selected)),
                    start,
                    start + currentSentence.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.tvContent.post {
                    val layout = binding.tvContent.layout ?: return@post
                    val line = layout.getLineForOffset(start)

                    val yTop = layout.getLineTop(line)
                    val yBottom = layout.getLineBottom(line)

                    val scrollY = binding.storyScrollView.scrollY
                    val scrollHeight = binding.storyScrollView.height

                    val scrollBottomMargin = (92 * binding.storyScrollView.resources.displayMetrics.density).toInt()

                    if (yTop < scrollY) {
                        binding.storyScrollView.smoothScrollTo(0, yTop, (BuildConfig.DURATION * 2).toInt())
                    }

                    else if (yBottom > scrollY + scrollHeight - scrollBottomMargin) {
                        binding.storyScrollView.smoothScrollTo(0, yBottom - scrollHeight + scrollBottomMargin, (BuildConfig.DURATION * 2).toInt())
                    }
                }
            }
        } else {
            binding.storyScrollView.post {
                binding.storyScrollView.smoothScrollTo(0, 0, (BuildConfig.DURATION * 5).toInt())
            }
        }

        binding.tvContent.text = spannable
    }

    private fun initViews() {

        val title = intent.getStringExtra("title") ?: "No title"
        var content = viewModel.addTabsAfterParagraphs(
            intent.getStringExtra("content") ?: "No content"
        )
        val words = intent.getStringArrayListExtra("words")
        val wordUiStates = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("word_ui_states", WordUIState::class.java)
        } else {
            intent.getParcelableArrayListExtra("word_ui_states")
        }

        if (wordUiStates != null) {
            viewModel.setWordUIStates(wordUiStates)
        }

        binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        binding.tvContent.highlightColor = Color.TRANSPARENT

        content = viewModel.setContent(content)

        val headerText = "###         ${title}\n\n        "
        val node = markwon.parse("$headerText${content}")
        markwonSpannable = markwon.render(node) as Spannable

        val contentText = markwonSpannable.toString()
        val headerLength = headerText.length

        viewModel.sentences.value.forEachIndexed { index, sentence ->

            val sentenceStart = contentText.indexOf(sentence, headerLength)
            if (sentenceStart >= 0) {

                var cursor = 0
                // regex orqali so‘zlarni topamiz
                val regex = Regex(words?.joinToString("|") { "\\b${Regex.escape(it)}\\b" } ?: "")
                val matches = regex.findAll(sentence)

                matches.forEach { match ->
                    val matchStart = match.range.first
                    val matchEnd = match.range.last + 1

                    // matchdan oldingi bo‘sh segmentga sentence span
                    if (cursor < matchStart) {
                        val absStart = sentenceStart + cursor
                        val absEnd = sentenceStart + matchStart
                        markwonSpannable.setSpan(
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    viewModel.currentIndex.value = index
                                    if (!viewModel.isPaused.value) {
                                        viewModel.cont()
                                    }
                                }
                                override fun updateDrawState(ds: TextPaint) {
                                    super.updateDrawState(ds)
                                    ds.isUnderlineText = false
                                    ds.color = getColor(R.color.primary_text)
                                }
                            },
                            absStart,
                            absEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    val absMatchStart = sentenceStart + matchStart
                    val absMatchEnd = sentenceStart + matchEnd
                    markwonSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                val tv = widget as TextView
                                val layout = tv.layout

                                // Span bosilgan joyni aniqlash
                                val start = absMatchStart
                                val end = absMatchEnd
                                val line = layout.getLineForOffset(start)

                                val startX = layout.getPrimaryHorizontal(start)
                                val endX = layout.getPrimaryHorizontal(end)
                                val wordCenterX = (startX + endX) / 2

                                val baselineY = layout.getLineBaseline(line)
                                val ascentY = layout.getLineAscent(line)
                                val wordTopY = baselineY + ascentY

                                // Tooltip view
                                val popupView = LessonTooltipTranslationBinding.inflate(layoutInflater)
                                viewModel.words.value.forEach { w ->
                                    if (w.word == contentText.substring(absMatchStart, absMatchEnd)) {
                                        popupView.root.text = w.nativeWord
                                        return@forEach
                                    }
                                }

                                val popupWindow = PopupWindow(
                                    popupView.root,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    false
                                ).apply {
                                    isOutsideTouchable = true
                                    elevation = 12f
                                }

                                popupView.root.measure(
                                    View.MeasureSpec.UNSPECIFIED,
                                    View.MeasureSpec.UNSPECIFIED
                                )
                                val popupWidth = popupView.root.measuredWidth
                                val popupHeight = popupView.root.measuredHeight

                                // So‘z markazidan hisoblab, popupni markazda chiqarish
                                val offsetX = (wordCenterX - popupWidth / 2).toInt()
                                val offsetY = (wordTopY - popupHeight - 4.dpToPx(tv.context))

                                popupWindow.showAsDropDown(tv, offsetX, offsetY)
                            }

                            // Extension: dp -> px
                            fun Int.dpToPx(context: Context): Int =
                                (this * context.resources.displayMetrics.density).toInt()

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                                ds.color = getColor(R.color.text_highlight)
                                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            }
                        },
                        absMatchStart,
                        absMatchEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    markwonSpannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        absMatchStart,
                        absMatchEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    cursor = matchEnd
                }

                // oxirgi matchdan keyin qolgan segmentni sentence span
                if (cursor < sentence.length) {
                    val absStart = sentenceStart + cursor
                    val absEnd = sentenceStart + sentence.length
                    markwonSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                viewModel.currentIndex.value = index
                                if (!viewModel.isPaused.value) {
                                    viewModel.cont()
                                }
                            }
                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                                ds.color = getColor(R.color.primary_text)
                            }
                        },
                        absStart,
                        absEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        markwon.setParsedMarkdown(binding.tvContent, markwonSpannable)

        binding.ibPlay.setOnClickListener {
            viewModel.toggle()
        }

        binding.ibPrev.setOnClickListener {
            viewModel.prev()
        }

        binding.ibNext.setOnClickListener {
            viewModel.next()
        }

        binding.tvOwner.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://t.me/+_s0z7jgCwv1lYzAy".toUri()
            startActivity(intent)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentIndex.collectLatest {
                    highlightCurrentSentence(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.speakRequest.collectLatest { text ->
                    viewModel.speakAloud(text) {
                        viewModel.next()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPaused.collectLatest {
                    if (it) {
                        binding.ibPlay.setImageResource(R.drawable.v_play)
                    } else {
                        binding.ibPlay.setImageResource(R.drawable.v_pause)
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}