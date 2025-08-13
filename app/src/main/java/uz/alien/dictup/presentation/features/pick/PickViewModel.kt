package uz.alien.dictup.presentation.features.pick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.presentation.features.pick.model.NavigationEvent
import uz.alien.dictup.presentation.features.pick.model.PartUIState
import uz.alien.dictup.presentation.features.pick.model.UnitUIState
import uz.alien.dictup.shared.WordCollection
import javax.inject.Inject

@HiltViewModel
class PickViewModel @Inject constructor(
    private val getUnitsPercentUseCase: GetUnitsPercentUseCase
) : ViewModel() {

    private val _collection = MutableStateFlow(WordCollection.ESSENTIAL)
    val collection = _collection.asStateFlow()

    private val _currentPart = MutableStateFlow(0)
    val currentPart = _currentPart.asStateFlow()

    private val _parts = MutableStateFlow<List<PartUIState>>(emptyList())
    val parts = _parts.asStateFlow()

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
        updateUnits()
    }

    fun openLesson(unitId: Int? = null) {

        // TODO: hali keyingi darsni avtomatik aniqlash algoritmini ishlab chiqqanim yo'q
        val nextLessonUnit = unitId ?: 0
        viewModelScope.launch {
            _navigationEvent.emit(
                NavigationEvent(collection.value.id, currentPart.value, nextLessonUnit)
            )
        }
    }
}