package uz.alien.dictup.presentation.features.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.LessonActivityBinding
import uz.alien.dictup.databinding.LessonFragmentBaseBinding
import uz.alien.dictup.databinding.LessonTooltipTranslationBinding
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.lesson.recycler.WordAdapter
import uz.alien.dictup.presentation.features.story.StoryActivity
import uz.alien.dictup.utils.Logger
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class LessonActivity : BaseActivity() {

    private lateinit var binding: LessonActivityBinding
    private val viewModel: LessonViewModel by viewModels()

    private var fragmentBounds: Rect? = null
    private var fragmentView: View? = null
    val itemBoundsMap = mutableMapOf<Int, Rect>()

    // Thread-safe flag
    private val isPagerOpened = AtomicBoolean(false)
    private var isAnimatingDismiss = false

    var collection = 0
    var part = 0
    var unit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LessonActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }
        setClearEdge()

        initViews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateLessonProgress()
    }

    private fun initViews() {
        val unit = intent.getIntExtra("unit", 0)
        val storyNumber = intent.getIntExtra("sn", 0)

        val words = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("words", Word::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("words")
        }

        collection = intent.getIntExtra("collection", 0)
        part = intent.getIntExtra("part", 0)
        this.unit = unit

        val nativeWords = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("native_words", NativeWord::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("native_words")
        }

        val scores = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("scores", Score::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("scores")
        }

        val stories = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("stories", Story::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("stories")
        }

        setCaption("Unit ${unit + 1}")

        if (words != null && nativeWords != null && scores != null) {
            if (viewModel.words.value.isEmpty()) {
                viewModel.getWords(words, nativeWords, scores)
            }
        }

        setupRecyclerView(words, stories, storyNumber)
        setupStory(stories, storyNumber, words)
        observeWords()
    }

    private fun setupRecyclerView(
        words: ArrayList<Word>?,
        stories: ArrayList<Story>?,
        storyNumber: Int
    ) {
        val adapter = WordAdapter { position, view, wordId ->
            // Prevent multiple simultaneous opens
            if (!isPagerOpened.compareAndSet(false, true)) {
                return@WordAdapter
            }

            if (isFinishing || isDestroyed) {
                isPagerOpened.set(false)
                return@WordAdapter
            }

            try {
                val bounds = Rect()
                view.getGlobalVisibleRect(bounds)
                itemBoundsMap[position] = bounds

                val fragment = BaseFragment.newInstance(position, bounds)
                fragmentBounds = bounds

                supportFragmentManager.beginTransaction()
                    .add(R.id.drawerLayout, fragment)
                    .commitNowAllowingStateLoss()
            } catch (e: Exception) {
                isPagerOpened.set(false)
                Logger.d("LessonActivity", "Error opening fragment: ${e.message}")
            }
        }

        binding.rvWord.layoutManager = AutoLayoutManager(this, 2)
        binding.rvWord.adapter = adapter

        binding.rvWord.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            adapter.currentList.forEachIndexed { index, _ ->
                val viewHolder = binding.rvWord.findViewHolderForAdapterPosition(index)
                viewHolder?.itemView?.let { view ->
                    val bounds = Rect()
                    view.getGlobalVisibleRect(bounds)
                    itemBoundsMap[index] = bounds
                }
            }
        }

        setCaptionOnLongClickListener {
            stories?.getOrNull(storyNumber)?.let { story ->
                if (!story.title.startsWith("null_of_")) {
                    val intent = Intent(this, StoryActivity::class.java)
                    intent.putStringArrayListExtra(
                        "words",
                        ArrayList(words?.map { it.word } ?: emptyList())
                    )
                    intent.putExtra("title", story.title)
                    intent.putExtra("content", story.content)
                    intent.putExtra("word_ui_states", ArrayList(viewModel.words.value))
                    baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
                }
            }
            true
        }
    }

    private fun setupStory(
        stories: ArrayList<Story>?,
        storyNumber: Int,
        words: ArrayList<Word>?
    ) {
        stories?.getOrNull(storyNumber)?.let { story ->
            if (!story.title.startsWith("null_of_")) {
                val markwon = Markwon.builder(this)
                    .usePlugin(ImagesPlugin.create())
                    .usePlugin(GlideImagesPlugin.create(this))
                    .build()

                var content = viewModel.addTabsAfterParagraphs(story.content)
                binding.tvStory.movementMethod = LinkMovementMethod.getInstance()
                binding.tvStory.highlightColor = Color.TRANSPARENT

                content = viewModel.setContent(content)

                val headerText = "###         ${story.title}\n\n        "
                val node = markwon.parse("$headerText${content}")
                val markwonSpannable = markwon.render(node) as Spannable

                val headerLength = headerText.length

                words?.forEach { word ->
                    addWordClickSpans(markwonSpannable, word, headerLength)
                }

                markwon.setParsedMarkdown(binding.tvStory, markwonSpannable)
            } else {
                binding.llStoryBackground.visibility = View.GONE
            }
        } ?: run {
            binding.llStoryBackground.visibility = View.GONE
        }
    }

    private fun addWordClickSpans(
        spannable: Spannable,
        word: Word,
        startIndex: Int
    ) {
        val regex = Regex("\\b${Regex.escape(word.word)}\\b", RegexOption.IGNORE_CASE)
        val matches = regex.findAll(spannable, startIndex = startIndex)

        matches.forEach { match ->
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showWordTooltip(widget as TextView, word.word, match)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = getColor(R.color.text_highlight)
                    ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }

            spannable.setSpan(
                clickableSpan,
                match.range.first,
                match.range.last + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                match.range.first,
                match.range.last + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun showWordTooltip(textView: TextView, wordText: String, match: MatchResult) {
        val layout = textView.layout ?: return

        val start = match.range.first
        val end = match.range.last + 1
        val line = layout.getLineForOffset(start)

        val startX = layout.getPrimaryHorizontal(start)
        val endX = layout.getPrimaryHorizontal(end)
        val wordCenterX = (startX + endX) / 2

        val baselineY = layout.getLineBaseline(line)
        val ascentY = layout.getLineAscent(line)
        val wordTopY = baselineY + ascentY

        val popupView = LessonTooltipTranslationBinding.inflate(layoutInflater)
        viewModel.words.value.forEach { w ->
            if (w.word == wordText) {
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

        val offsetX = (wordCenterX - popupWidth / 2).toInt()
        val offsetY = (wordTopY - popupHeight - 4.dpToPx(textView.context))

        popupWindow.showAsDropDown(textView, offsetX, offsetY)
    }

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    private fun observeWords() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.words.collectLatest { words ->
                    val adapter = binding.rvWord.adapter as? WordAdapter
                    adapter?.submitList(words)
                }
            }
        }
    }

    fun dismissFragmentWithAnimation(
        fragment: BaseFragment,
        targetBounds: Rect?,
        onDone: () -> Unit = {}
    ) {
        if (isAnimatingDismiss || isFinishing || isDestroyed) {
            return
        }

        viewModel.updateLessonProgress()
        isAnimatingDismiss = true

        val view = fragment.view
        if (view == null) {
            isPagerOpened.set(false)
            isAnimatingDismiss = false
            return
        }

        val pagerBaseBinding = try {
            LessonFragmentBaseBinding.bind(view)
        } catch (e: Exception) {
            isPagerOpened.set(false)
            isAnimatingDismiss = false
            return
        }

        val adapter = fragment.getWordPagerAdapter()
        if (adapter == null) {
            isPagerOpened.set(false)
            isAnimatingDismiss = false
            return
        }

        val isLastFragment = pagerBaseBinding.vpWord.currentItem == adapter.itemCount - 1
        if (isLastFragment && adapter.itemCount > 1) {
            pagerBaseBinding.vpWord.setCurrentItem(adapter.itemCount - 2, true)
        }

        fragmentView = view
        val startBounds = targetBounds ?: fragmentBounds ?: run {
            isPagerOpened.set(false)
            isAnimatingDismiss = false
            return
        }

        val finalBounds = Rect(0, 0, pagerBaseBinding.flBackground.width, pagerBaseBinding.flBackground.height)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = BuildConfig.DURATION * 2
        animator.interpolator = DecelerateInterpolator(2f)

        val params = pagerBaseBinding.flBackground.layoutParams as FrameLayout.LayoutParams
        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val newLeft = finalBounds.left + (startBounds.left - finalBounds.left) * fraction
            val newTop = finalBounds.top + (startBounds.top - finalBounds.top) * fraction
            val newWidth = finalBounds.width() + (startBounds.width() - finalBounds.width()) * fraction
            val newHeight = finalBounds.height() + (startBounds.height() - finalBounds.height()) * fraction

            params.leftMargin = newLeft.toInt() + 1
            params.topMargin = newTop.toInt() + 1
            params.width = newWidth.toInt()
            params.height = newHeight.toInt()
            pagerBaseBinding.flBackground.layoutParams = params
            pagerBaseBinding.tvItemWord.alpha = fraction
            pagerBaseBinding.vBackground.alpha = 1f - fraction
            pagerBaseBinding.flBackground.elevation = 4f * resources.displayMetrics.density *
                    AccelerateInterpolator(3f).getInterpolation(1f - fraction)
            (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f - fraction
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f
                pagerBaseBinding.tvItemWord.alpha = 0f
                pagerBaseBinding.vBackground.alpha = 1f
                pagerBaseBinding.flBackground.elevation = 4f * resources.displayMetrics.density

                val currentPage = fragment.getCurrentPage()
                val wordsList = viewModel.words.value
                if (currentPage >= 0 && currentPage < wordsList.size) {
                    val currentWord = wordsList[currentPage]
                    pagerBaseBinding.tvItemWord.text = currentWord.word

                    val colorRes = when {
                        currentWord.score < 0 -> R.color.red_500
                        currentWord.score >= 5 -> R.color.green_700
                        else -> R.color.secondary_text
                    }
                    pagerBaseBinding.tvItemWord.setTextColor(getColor(colorRes))
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 0f
                pagerBaseBinding.vBackground.alpha = 0f
                pagerBaseBinding.flBackground.elevation = 0f
                pagerBaseBinding.tvItemWord.alpha = 1f
                pagerBaseBinding.root.visibility = View.GONE

                if (!isFinishing && !isDestroyed) {
                    try {
                        supportFragmentManager.beginTransaction()
                            .remove(fragment)
                            .commitNowAllowingStateLoss()
                    } catch (e: Exception) {
                        Logger.d("LessonActivity", "Error removing fragment: ${e.message}")
                    }
                }

                fragmentView = null
                fragmentBounds = null
                isPagerOpened.set(false)
                isAnimatingDismiss = false
                onDone()
            }

            override fun onAnimationCancel(animation: Animator) {
                isPagerOpened.set(false)
                isAnimatingDismiss = false
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }

    override fun onCustomBackPressed(): Boolean {
        if (isPagerOpened.get() && !isAnimatingDismiss) {
            val fragment = supportFragmentManager.findFragmentById(R.id.drawerLayout) as? BaseFragment
            if (fragment != null) {
                val currentPage = fragment.getCurrentPage()
                val targetBounds = itemBoundsMap[currentPage]
                dismissFragmentWithAnimation(fragment, targetBounds)
                return true
            }
        }
        return false
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}