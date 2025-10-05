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
import androidx.lifecycle.lifecycleScope
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

@AndroidEntryPoint
class LessonActivity : BaseActivity() {

    private lateinit var binding: LessonActivityBinding
    private val viewModel: LessonViewModel by viewModels()

    private var fragmentBounds: Rect? = null
    private var fragmentView: View? = null
    val itemBoundsMap = mutableMapOf<Int, Rect>()
    private var isPagerOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LessonActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }
        setClearEdge()

        initViews()
    }

    private fun initViews() {

        val unit = intent.getIntExtra("unit", 0)
        val storyNumber = intent.getIntExtra("sn", 0)


        val words = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("words", Word::class.java)
        } else {
            intent.getParcelableArrayListExtra("words")
        }

        words?.forEach {
            Logger.d("word", it.word)
        } ?: Logger.d("word", "No words send!")

        val nativeWords = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("native_words", NativeWord::class.java)
        } else {
            intent.getParcelableArrayListExtra("native_words")
        }

        val scores = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("scores", Score::class.java)
        } else {
            intent.getParcelableArrayListExtra("scores")
        }

        val stories = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("stories", Story::class.java)
        } else {
            intent.getParcelableArrayListExtra("stories")
        }

        setCaption("Unit ${unit + 1}")

        if (words != null && nativeWords != null && scores != null) {

            if (viewModel.words.value.isEmpty()) {
                viewModel.getWords(words, nativeWords, scores)
            }
        }

        val adapter = WordAdapter { position, view, wordId ->
            if (!isPagerOpened) {
                isPagerOpened = true
                val bounds = Rect()
                view.getGlobalVisibleRect(bounds)
                itemBoundsMap[position] = bounds // Joylashuvni saqlash

                val fragment = BaseFragment.newInstance(position, bounds)
                fragmentBounds = bounds
                supportFragmentManager.beginTransaction()
                    .add(R.id.drawerLayout, fragment)
                    .commit()
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
            stories?.get(storyNumber)?.let {
                if (!it.title.startsWith("null_of_")) {
                    val intent = Intent(this, StoryActivity::class.java)
                    intent.putStringArrayListExtra(
                        "words",
                        ArrayList(
                            words!!.map { word ->
                                word.word
                            }
                        )
                    )
                    intent.putExtra("title", it.title)
                    intent.putExtra("content", it.content)
                    intent.putExtra("word_ui_states", ArrayList(viewModel.words.value))
                    baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
                }
            }
            true
        }

        stories?.get(storyNumber)?.let {
            if (!it.title.startsWith("null_of_")) {

                val markwon = Markwon
                    .builder(this)
                    .usePlugin(ImagesPlugin.create())
                    .usePlugin(GlideImagesPlugin.create(this))
                    .build()

                var content = viewModel.addTabsAfterParagraphs(it.content)

                binding.tvStory.movementMethod = LinkMovementMethod.getInstance()
                binding.tvStory.highlightColor = Color.TRANSPARENT

                content = viewModel.setContent(content)

                val headerText = "###         ${it.title}\n\n        "
                val node = markwon.parse("$headerText${content}")
                val markwonSpannable = markwon.render(node) as Spannable

                val headerLength = headerText.length

                words?.forEach { word ->
                    val regex = Regex("\\b${Regex.escape(word.word)}\\b", RegexOption.IGNORE_CASE)
                    val matches = regex.findAll(markwonSpannable, startIndex = headerLength)

                    matches.forEach { match ->
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                val tv = widget as TextView
                                val layout = tv.layout

                                // Span bosilgan joyni aniqlash
                                val start = match.range.first
                                val end = match.range.last + 1
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
                                    if (w.word == word.word) {
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
                        }

                        markwonSpannable.setSpan(
                            clickableSpan,
                            match.range.first,
                            match.range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        markwonSpannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            match.range.first,
                            match.range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                markwon.setParsedMarkdown(binding.tvStory, markwonSpannable)

            } else null
        } ?: run {
            binding.llStoryBackground.visibility = View.GONE
        }

        lifecycleScope.launch {
            viewModel.words.collectLatest {
                adapter.submitList(it)
            }
        }
    }

    private fun addTabsAfterParagraphs(content: String): String {
        return content
            .replace(Regex("(\n\n)(?! )"), "\n\n        ") // \n\n dan keyin tab qo‘shadi, agar allaqachon qo‘yilmagan bo‘lsa
            .let { if (!it.startsWith("        ")) "        $it" else it } // birinchi paragrafga ham tab qo‘yish
    }

    fun dismissFragmentWithAnimation(fragment: BaseFragment, targetBounds: Rect?) {

        viewModel.updateLessonProgress()

        isPagerOpened = false

        val view = fragment.view ?: return
        val pagerBaseBinding = LessonFragmentBaseBinding.bind(view)

        val isLastFragment = pagerBaseBinding.vpWord.currentItem == fragment.wordPagerAdapter.itemCount - 1
        if (isLastFragment) {
            pagerBaseBinding.vpWord.setCurrentItem(fragment.wordPagerAdapter.itemCount - 2, true)
        }

        fragmentView = view
        val startBounds = targetBounds ?: fragmentBounds ?: return
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
//            pagerBaseBinding.root.elevation = DecelerateInterpolator(10f).getInterpolation(1f - fraction)
            pagerBaseBinding.flBackground.elevation = 4f * resources.displayMetrics.density * AccelerateInterpolator(3f).getInterpolation(1f - fraction)
            (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f - fraction
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 1f
                pagerBaseBinding.tvItemWord.alpha = 0f
                pagerBaseBinding.vBackground.alpha = 1f
                pagerBaseBinding.flBackground.elevation = 4f * resources.displayMetrics.density
                pagerBaseBinding.tvItemWord.text = viewModel.words.value[fragment.getCurrentPage()].word
                if (viewModel.words.value[fragment.getCurrentPage()].score < 0) {
                    pagerBaseBinding.tvItemWord.setTextColor(pagerBaseBinding.tvItemWord.context.getColor(R.color.red_500))
                } else if (viewModel.words.value[fragment.getCurrentPage()].score >= 5) {
                    pagerBaseBinding.tvItemWord.setTextColor(pagerBaseBinding.tvItemWord.context.getColor(R.color.green_700))
                } else {
                    pagerBaseBinding.tvItemWord.setTextColor(pagerBaseBinding.tvItemWord.context.getColor(R.color.secondary_text))
                }
            }
            override fun onAnimationEnd(animation: Animator) {
                (pagerBaseBinding.vpWord.getChildAt(0) as? RecyclerView)?.alpha = 0f
                pagerBaseBinding.vBackground.alpha = 0f
                pagerBaseBinding.flBackground.elevation = 0f
                pagerBaseBinding.tvItemWord.alpha = 1f
                pagerBaseBinding.root.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
                fragmentView = null
                fragmentBounds = null
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }

    override fun onCustomBackPressed(): Boolean {
        return if (isPagerOpened) {
            isPagerOpened = false
            val fragment = supportFragmentManager.findFragmentById(R.id.drawerLayout) as? BaseFragment
            val currentPage = fragment?.getCurrentPage() ?: return false
            val targetBounds = itemBoundsMap[currentPage]
            dismissFragmentWithAnimation(fragment, targetBounds)
            true
        } else {
            false
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}