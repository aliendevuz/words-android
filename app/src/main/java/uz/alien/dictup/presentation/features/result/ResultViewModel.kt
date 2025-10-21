package uz.alien.dictup.presentation.features.result

import androidx.core.animation.AccelerateInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.RemoteConfigRepository
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.DataStore
import uz.alien.dictup.value.strings.SharedPrefs
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    remoteConfigRepository: RemoteConfigRepository,
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModel() {

    private val _result = MutableStateFlow(0.0f)
    val result = _result.asStateFlow()

    var adCountLimit = 0
    val showAdsCount = MutableStateFlow(0)

    init {

        adCountLimit = remoteConfigRepository.getFrequencyOfAds().toInt()

        showAdsCount.value = sharedPrefsRepository.getInt(SharedPrefs.DAILY_SHOWN_AD_COUNT, 0)
    }

    fun shouldShowAd(): Boolean {

        val lastShownTime = sharedPrefsRepository
            .getLong(SharedPrefs.LAST_SHOWN_AD_TIME, 0)

        val lastShownDate = Date(lastShownTime)
        val today = Calendar.getInstance()
        val lastCal = Calendar.getInstance().apply { time = lastShownDate }

        val isNewDay = today.get(Calendar.YEAR) != lastCal.get(Calendar.YEAR) ||
                today.get(Calendar.DAY_OF_YEAR) != lastCal.get(Calendar.DAY_OF_YEAR)

        return (lastShownTime == 0L || isNewDay) && showAdsCount.value < adCountLimit
    }

    fun countAdd() {
        sharedPrefsRepository.saveInt(SharedPrefs.DAILY_SHOWN_AD_COUNT, showAdsCount.value + 1)
    }

    fun setLastShownAdTime() {
        sharedPrefsRepository.saveLong(SharedPrefs.LAST_SHOWN_AD_TIME, System.currentTimeMillis())
        sharedPrefsRepository.saveInt(SharedPrefs.DAILY_SHOWN_AD_COUNT, 0)
    }

    fun resolveAttempt(attempts: List<Attempt>) {

        if (attempts.isEmpty()) return

        calculateResult(attempts)

        viewModelScope.launch {
            saveResult(attempts)
        }
    }

    private suspend fun saveResult(attempts: List<Attempt>) {
        if (attempts.isEmpty()) return

        attempts.forEach {
            if (it.isCorrect) {
                scoreRepository.getScoreById(it.quizId)
                    ?.let { score ->
                        scoreRepository.updateScore(
                            score.copy(correctCount = score.correctCount + 1)
                        )
                    }
            } else {
                scoreRepository.getScoreById(it.quizId)
                    ?.let { score ->
                        scoreRepository.updateScore(
                            score.copy(incorrectCount = score.incorrectCount + 1)
                        )
                    }
            }
        }
    }


    private fun calculateResult(attempts: List<Attempt>) {
        if (attempts.isEmpty()) return

        var totalScore = 0.0f
        var count = 0.0f
        val maxAttempts = 6.0f
        val quizCount = attempts.count { it.isCorrect }
        val scorePerQuestion = 100.0f / quizCount
        val interpolator = AccelerateInterpolator(2.0f)

        attempts.forEach {
            if (it.isCorrect) {
                val per = 1.0f - count / maxAttempts
                val score = interpolator.getInterpolation(per)
                val minus = scorePerQuestion * score
                totalScore = totalScore + minus
                count = 0f
            } else {
                count++
            }
        }
        _result.value = totalScore
    }
}