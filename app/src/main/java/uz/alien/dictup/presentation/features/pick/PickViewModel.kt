package uz.alien.dictup.presentation.features.pick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.presentation.features.pick.model.NavigationEvent
import uz.alien.dictup.presentation.features.pick.model.PartUIState
import uz.alien.dictup.presentation.features.pick.model.UnitUIState
import uz.alien.dictup.domain.model.WordCollection
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.SharedPrefs.LAST_PART
import javax.inject.Inject

@HiltViewModel
class PickViewModel @Inject constructor(
    private val getUnitsPercentUseCase: GetUnitsPercentUseCase,
    private val wordRepository: WordRepository,
    private val nativeWordRepository: NativeWordRepository,
    private val scoreRepository: ScoreRepository,
    private val storyRepository: StoryRepository,
    private val prefsRepository: SharedPrefsRepository
) : ViewModel() {

    private val _collection = MutableStateFlow(WordCollection.ESSENTIAL)
    val collection = _collection.asStateFlow()

    private val _currentPart = MutableStateFlow(0)
    val currentPart = _currentPart.asStateFlow()

    private val _parts = MutableStateFlow<List<PartUIState>>(emptyList())
    val parts = _parts.asStateFlow()

    var scrollPage = false

    val unitFlows: MutableList<MutableStateFlow<List<UnitUIState>>> = mutableListOf()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun prepareUnits(partCount: Int, unitCount: Int, currentPartId: Int) {

        _currentPart.value = currentPartId

        val partsList = List(partCount) { partIndex ->
            PartUIState(
                id = partIndex,
                unitCount = unitCount,
                isCurrent = partIndex == currentPartId
            )
        }
        _parts.value = partsList

        unitFlows.clear()
        partsList.forEach { part ->
            val unitsForPart = List(unitCount) { unitIndex ->
                UnitUIState(
                    id = unitIndex,
                    name = "Unit ${unitIndex + 1}",
                    progress = 0,
                    partId = part.id
                )
            }
            unitFlows.add(MutableStateFlow(unitsForPart))
        }
    }

    fun updateUnits() {

        viewModelScope.launch {

            val progressList = getUnitsPercentUseCase(collection.value.id, currentPart.value)
            progressList.forEach {
                Logger.d("Progress", it.toString())
            }

            unitFlows[currentPart.value].update { units ->
                units.mapIndexed { index, unit ->
                    unit.copy(progress = progressList.getOrNull(index) ?: unit.progress)
                }
            }
        }
    }

    fun setCollection(collection: WordCollection) {
        _collection.value = collection
    }

    fun setCurrentPart(partId: Int) {
        _parts.update { parts ->
            parts.map { it.copy(isCurrent = it.id == partId) }
        }
        _currentPart.value = partId
        saveLastPart(partId)
        updateUnits()
    }

    fun saveLastPart(partId: Int) {
        prefsRepository.saveInt(LAST_PART, partId)
    }

    fun openLesson(unitId: Int? = null, storyNumber: Int = 0, fromStart: Boolean = false) {
        viewModelScope.launch {
            val collectionId = collection.value.id

            val partId: Int
            val nextLessonUnit: Int

            if (unitId != null) {
                // unitId kelsa faqat currentPart ishlatiladi
                partId = currentPart.value
                nextLessonUnit = unitId
            } else {
                // avtomatik qidirish
                scrollPage = true
                if (fromStart) setCurrentPart(0)
                val next = findNextLesson()
                if (next == null) {
                    // hammasi tugagan
                    _navigationEvent.emit(null)
                    return@launch
                }
                partId = next.first
                nextLessonUnit = next.second

                setCurrentPart(partId)
            }

            // endi shu joyda oldingidek word/story/scores fetch qilish
            val words = wordRepository.getWordsByFullPath(collectionId, partId, nextLessonUnit)
            val nativeWords = nativeWordRepository.getNativeWordsByFullPath(collectionId, partId, nextLessonUnit)
            val scores = scoreRepository.getScoresByFullPath(collectionId, partId, nextLessonUnit)
            val stories = storyRepository.getStoriesByUnit(collectionId, partId, nextLessonUnit)

            _navigationEvent.emit(
                NavigationEvent(
                    collectionId,
                    partId,
                    nextLessonUnit,
                    ArrayList(words),
                    ArrayList(nativeWords),
                    ArrayList(scores),
                    ArrayList(stories),
                    storyNumber
                )
            )
            parts
        }
    }

    private suspend fun findNextLesson(): Pair<Int, Int>? {

        val collectionId = collection.value.id
        val partCount = _parts.value.size
        val startPart = currentPart.value

        // 1️⃣ Avval currentPart ichida qidirish
        var progressList = getUnitsPercentUseCase(collectionId, startPart)
        val inCurrent = progressList.indexOfFirst { it < 100 }
        if (inCurrent != -1) {
            return startPart to inCurrent
        }

        // 2️⃣ CurrentPart dan keyingi partlar
        for (partId in (startPart + 1) until partCount) {
            progressList = getUnitsPercentUseCase(collectionId, partId)
            val idx = progressList.indexOfFirst { it < 100 }
            if (idx != -1) {
                return partId to idx
            }
        }

        // 3️⃣ Boshidan currentPart gacha
        for (partId in 0 until startPart) {
            progressList = getUnitsPercentUseCase(collectionId, partId)
            val idx = progressList.indexOfFirst { it < 100 }
            if (idx != -1) {
                return partId to idx
            }
        }

        // 4️⃣ Hammasi tugagan
        return null
    }
}