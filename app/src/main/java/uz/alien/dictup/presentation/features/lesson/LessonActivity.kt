package uz.alien.dictup.presentation.features.lesson

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.core.utils.Logger
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

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    private var isPagerOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LessonActivityBinding.inflate(layoutInflater)
        setClearEdge()
        setContentLayout {
            binding.root
        }
        setSystemPadding(binding.statusBarPadding)

        tts = TextToSpeech(this, this)

        handleBackPress()

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

        stories?.let {
            if (it.isNotEmpty()) {
                if (!it[0].content.startsWith("null_of_")) {
                    binding.tvHeader.text = it[0].title
                    binding.tvStory.text = it[0].content
                } else {
                    binding.tvStory.text = "Story is not exist"
                }
            }
        }
        binding.tvAppBar.text = "Unit ${unit + 1}"

        if (words != null && nativeWords != null && scores != null) {
            viewModel.getWords(words, nativeWords, scores)
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

        binding.bOpenStory.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            intent.putExtra("title", stories?.get(0)?.title ?: "No title")
            intent.putExtra("content", stories?.get(0)?.content ?: "No content")
            startActivityWithZoomAnimation(intent)
        }

        lifecycleScope.launch {
            viewModel.words.collectLatest {
                adapter.submitList(it)
            }
        }
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