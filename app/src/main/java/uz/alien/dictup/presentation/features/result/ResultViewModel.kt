package uz.alien.dictup.presentation.features.result

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.utils.Logger
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor() : ViewModel() {
    fun resolveAttempt(attempts: List<Attempt>) {

        attempts.forEach {
            Logger.d("Attempt", "${it.quizId}/${it.isCorrect}/${it.timestamp}")
        }
    }
}