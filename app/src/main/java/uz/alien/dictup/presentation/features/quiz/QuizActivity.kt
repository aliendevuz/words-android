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
import uz.alien.dictup.presentation.common.extention.startActivityForResultWithZoomAnimation
import uz.alien.dictup.presentation.features.quiz.recycler.OptionAdapter
import uz.alien.dictup.presentation.features.result.ResultActivity

@AndroidEntryPoint
class QuizActivity : BaseActivity() {

    private lateinit var binding: QuizActivityBinding
    private val viewModel: QuizViewModel by viewModels()

    private var sfxPlayer: MediaPlayer? = null
    private var bgPlayer: MediaPlayer? = null

    private val fadeHandler = Handler(Looper.getMainLooper())
    private var maxVolume = 0.32f
    private var sfxVolume = 0.8f


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
                    sfxPlayer?.release()
                    bgPlayer?.pause()
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
        collectSoundSettings()
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

            fadeHandler.post(object : Runnable {
                override fun run() {
                    volume += maxVolume / steps
                    if (volume <= maxVolume) {
                        setVolume(volume, volume)
                        fadeHandler.postDelayed(this, delay)
                    }
                }
            })
        }
    }

    private fun fadeOutMusic(durationMs: Long = 1000L, onEnd: (() -> Unit)? = null) {
        bgPlayer?.apply {
            val steps = 30
            val delay = durationMs / steps
            var volume = maxVolume

            fadeHandler.post(object : Runnable {
                override fun run() {
                    volume -= maxVolume / steps
                    if (volume > 0f) {
                        setVolume(volume, volume)
                        fadeHandler.postDelayed(this, delay)
                    } else {
                        pause()
                        setVolume(maxVolume, maxVolume)
                        onEnd?.invoke()
                    }
                }
            })
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

    fun collectSoundSettings() {
        lifecycleScope.launch {
            viewModel.isSFXAvailable.collect { isAvailable ->
                sfxVolume = if (isAvailable) 0.8f else 0f
            }
        }

        lifecycleScope.launch {
            viewModel.isBgMusicAvailable.collect { isAvailable ->
                maxVolume = if (isAvailable) 0.32f else 0f
            }
        }
    }

    private fun playSound(soundResId: Int) {
        sfxPlayer?.release()
        sfxPlayer = MediaPlayer.create(this, soundResId)
        sfxPlayer?.setVolume(sfxVolume, sfxVolume)
        sfxPlayer?.setOnCompletionListener {
            it.release()
        }
        sfxPlayer?.start()
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
        viewModel.updateSoundSettings()
        if (viewModel.currentIndex.value == -1) {
            fadeInMusic()
        } else {
            fadeInMusic(400)
        }
        binding.bNext.setOnClickListener {
            if (viewModel.isCorrect.value) {
                viewModel.nextQuestion()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (bgPlayer?.isPlaying == true) {
            fadeOutMusic(400L) {
                bgPlayer?.pause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fadeHandler.removeCallbacksAndMessages(null)
        sfxPlayer?.release()
        bgPlayer?.release()
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}