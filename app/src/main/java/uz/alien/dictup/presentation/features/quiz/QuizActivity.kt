package uz.alien.dictup.presentation.features.quiz

import android.animation.ValueAnimator
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.R
import uz.alien.dictup.databinding.QuizActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.hideSystemUI
import uz.alien.dictup.presentation.common.extention.overrideTransitionWithZoom
import uz.alien.dictup.presentation.common.extention.startActivityForResultWithZoomAnimation
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.quiz.recycler.OptionAdapter
import uz.alien.dictup.presentation.features.result.ResultActivity
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.utils.Logger.isCalled

@AndroidEntryPoint
class QuizActivity : BaseActivity() {

    private lateinit var binding: QuizActivityBinding
    private val viewModel: QuizViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null
    private var bgPlayer: MediaPlayer? = null

    private val maxVolume = 0.32f
    private val sfxVolume = 0.8f


    private lateinit var optionAdapter: OptionAdapter

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val restart = result.data?.getBooleanExtra("restart_quiz", false) ?: false
            if (restart) {
                viewModel.resetQuiz()
                viewModel.prepareQuizzes()
                binding.bNext.text = "Keyingi"
                animateProgress(0)
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = QuizActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        hideAppBar()

        setClearEdge()
        setSystemPadding(binding.clTop)

        initBackgroundMusic()

        initViews()
    }

    private fun initViews() {

        val selectedUnits = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("selected_units", SelectedUnit::class.java)
        } else {
            intent.getParcelableArrayListExtra("selected_units")
        }
        viewModel.setSelectedUnits(selectedUnits ?: emptyList())

        val quizCount = (intent.getIntExtra("quiz_count", 20))
        viewModel.setQuizCount(quizCount)

        viewModel.prepareQuizzes()

        optionAdapter = OptionAdapter { id, wordId ->
            if (!viewModel.isCorrect.value) {
                if (viewModel.answer(id, wordId)) {
                    val percent = ((viewModel.currentIndex.value + 1).toDouble() / viewModel.quizCount.intValue.toDouble()) * 100.0
                    animateProgress(percent.toInt())
                    playSound(R.raw.correct)
                } else {
                    playSound(R.raw.wrong)
                }
            }
            if (viewModel.isCorrect.value && viewModel.currentIndex.value == viewModel.quizCount.intValue - 1) {
                binding.bNext.text = "Natijani ko'rish"
                fadeOutMusic {
                    mediaPlayer?.release()
                }
                binding.bNext.setOnClickListener {
                    val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                    intent.putParcelableArrayListExtra("attempt", viewModel.attempt)
                    startActivityForResultWithZoomAnimation(resultLauncher, intent)
                }
            }
        }

        binding.rvOptions.layoutManager = AutoLayoutManager(this)
        binding.rvOptions.adapter = optionAdapter

        collectQuiz()
        collectOptions()
        collectIndex()
    }

    private fun initBackgroundMusic() {
        bgPlayer = MediaPlayer.create(this, R.raw.quiz_loop).apply {
            setVolume(maxVolume, maxVolume)
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }

    private fun fadeInMusic(durationMs: Long = 6000L) {
        bgPlayer?.apply {
            setVolume(0f, 0f)
            start()

            val steps = 30
            val delay = durationMs / steps
            var volume = 0f

            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    volume += maxVolume / steps
                    if (volume <= maxVolume) {
                        setVolume(volume, volume)
                        handler.postDelayed(this, delay)
                    }
                }
            }
            handler.post(runnable)
        }
    }

    private fun fadeOutMusic(durationMs: Long = 1000L, onEnd: (() -> Unit)? = null) {
        bgPlayer?.apply {
            val steps = 30
            val delay = durationMs / steps
            var volume = maxVolume

            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    volume -= maxVolume / steps
                    if (volume > 0f) {
                        setVolume(volume, volume)
                        handler.postDelayed(this, delay)
                    } else {
                        pause()
                        setVolume(maxVolume, maxVolume)
                        onEnd?.invoke()
                    }
                }
            }
            handler.post(runnable)
        }
    }

    fun animateProgress(newProgress: Int) {
        val currentProgress = binding.iProgress.progress
        val animator = ValueAnimator.ofInt(currentProgress, newProgress)
        animator.duration = BuildConfig.DURATION * 2
        animator.addUpdateListener { animation ->
            binding.iProgress.progress = animation.animatedValue as Int
        }
        animator.start()
    }

    fun collectQuiz() {
        lifecycleScope.launch {
            viewModel.question.collect { question ->
                binding.tvQuestion.text = question
            }
        }
    }

    private fun playSound(soundResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer?.setVolume(sfxVolume, sfxVolume)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        mediaPlayer?.start()
    }

    fun collectOptions() {

        lifecycleScope.launch {
            viewModel.options.collectLatest { options ->
                optionAdapter.submitList(options)
            }
        }
    }

    fun collectIndex() {
        lifecycleScope.launch {
            viewModel.currentIndex.collect { id ->
                binding.tvIndex.text = "${id + 1}/${viewModel.quizCount.intValue}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bgPlayer?.start()
        fadeInMusic()
        binding.bNext.setOnClickListener {
            if (viewModel.isCorrect.value) {
                viewModel.nextQuestion()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (bgPlayer?.isPlaying == true) {
            bgPlayer?.pause()
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}