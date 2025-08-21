package uz.alien.dictup.presentation.features.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.addCallback
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
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.databinding.LessonActivityBinding
import uz.alien.dictup.databinding.LessonFragmentBaseBinding
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.common.extention.startActivityWithZoomAnimation
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.lesson.recycler.WordAdapter
import uz.alien.dictup.presentation.features.story.StoryActivity
import java.util.Locale

@AndroidEntryPoint
class LessonActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: LessonActivityBinding
    private val viewModel: LessonViewModel by viewModels()

    private var fragmentBounds: Rect? = null
    private var fragmentView: View? = null
    val itemBoundsMap = mutableMapOf<Int, Rect>()
    lateinit var tts: TextToSpeech

    private var isPagerOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LessonActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }
        setClearEdge()
        setSystemPadding(binding.statusBarPadding)

        tts = TextToSpeech(this, this)

        handleBackPress()

        initViews()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.e("TTS", "Til qo‘llab-quvvatlanmaydi")
            } else {
                tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null, "warmup")
            }
        } else {
            Logger.e("TTS", "TTS ishga tushmadi")
        }
    }

    private fun initViews() {

        binding.drawerButton.setOnClickListener {
            openDrawer()
        }

        val collection = intent.getIntExtra("collection", 0)
        val part = intent.getIntExtra("part", 0)
        val unit = intent.getIntExtra("unit", 0)


        val words = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("words", Word::class.java)
        } else {
            intent.getParcelableArrayListExtra("words")
        }

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

        binding.tvAppBar.text = "Unit ${unit + 1}"

        if (words != null && nativeWords != null && scores != null) {

            if (viewModel.words.value.isEmpty()) {
                viewModel.getWords(words, nativeWords, scores)
            }
        }

        val adapter = WordAdapter { position, view ->
            if (!isPagerOpened) {
                isPagerOpened = true
                val bounds = Rect()
                view.getGlobalVisibleRect(bounds)
                itemBoundsMap[position] = bounds // Joylashuvni saqlash

                val fragment = BaseFragment.newInstance(position, bounds)
                fragmentBounds = bounds
                supportFragmentManager.beginTransaction()
                    .add(R.id.contentFrame, fragment)
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

        binding.tvAppBar.setOnLongClickListener {
            stories?.get(0)?.let {
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
                    startActivityWithZoomAnimation(intent)
                }
            }
            true
        }

        stories?.get(0)?.let {
            if (!it.title.startsWith("null_of_")) {

                val markwon = Markwon
                    .builder(this)
                    .usePlugin(ImagesPlugin.create())
                    .usePlugin(GlideImagesPlugin.create(this))
                    .build()

                val content = addTabsAfterParagraphs(it.content)

                val headerText = "###         ${it.title}\n\n"
                val node = markwon.parse("$headerText${content}")
                val markwonSpannable = markwon.render(node) as Spannable

                val headerLength = headerText.length

                words?.forEach { word ->
                    val regex = Regex("\\b${Regex.escape(word.word)}\\b", RegexOption.IGNORE_CASE)
                    val matches = regex.findAll(markwonSpannable, startIndex = headerLength)

                    matches.forEach { match ->
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                speakAloud(word.word)
                            }

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

        lifecycleScope.launch {
            viewModel.dataStore.getTTSPitch().collectLatest {
                tts.setPitch(it)
            }
        }

        lifecycleScope.launch {
            viewModel.dataStore.getTTSSpeed().collectLatest {
                tts.setSpeechRate(it)
            }
        }
    }

    private fun speakAloud(content: String) {
        tts.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun addTabsAfterParagraphs(content: String): String {
        return content
            .replace(Regex("(\n\n)(?! )"), "\n\n        ") // \n\n dan keyin tab qo‘shadi, agar allaqachon qo‘yilmagan bo‘lsa
            .let { if (!it.startsWith("        ")) "        $it" else it } // birinchi paragrafga ham tab qo‘yish
    }

    fun dismissFragmentWithAnimation(fragment: BaseFragment, targetBounds: Rect?) {

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

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            if (isDrawerOpen()) {
                closeDrawer()
            } else {
                if (isPagerOpened) {
                    isPagerOpened = false
                    val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? BaseFragment
                    val currentPage = fragment?.getCurrentPage() ?: return@addCallback
                    val targetBounds = itemBoundsMap[currentPage]
                    dismissFragmentWithAnimation(fragment, targetBounds)
                } else {
                    if (isEnabled) {
                        remove()
                        onBackPressedDispatcher.onBackPressed()
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