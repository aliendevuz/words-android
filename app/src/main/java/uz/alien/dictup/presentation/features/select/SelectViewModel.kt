package uz.alien.dictup.presentation.features.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.presentation.features.select.model.CollectionUIState
import uz.alien.dictup.presentation.features.select.model.PartUIState
import uz.alien.dictup.presentation.features.select.model.UnitUIState
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.SharedPrefs.LAST_COLLECTION_ID
import uz.alien.dictup.value.strings.SharedPrefs.LAST_PART_ID
import javax.inject.Inject
import kotlin.random.Random.Default.nextBoolean

@HiltViewModel
class SelectViewModel @Inject constructor(
    private val getUnitsPercentUseCase: GetUnitsPercentUseCase,
    private val prefsRepository: SharedPrefsRepository
) : ViewModel() {

    private val collections = listOf(
        CollectionUIState(
            id = 0,
            title = "Beginner",
            partCount = 4,
            unitCount = 20,
            isCurrent = true
        ),
        CollectionUIState(
            id = 1,
            title = "Essential",
            partCount = 6,
            unitCount = 30
        )
    )

    private val _collectionsFlow = MutableStateFlow(collections)
    val collectionsFlow: StateFlow<List<CollectionUIState>> = _collectionsFlow.asStateFlow()

    private var currentCollection = 0
    private val currentParts: MutableMap<Int, Int> = mutableMapOf()

    val partsFlows: MutableList<MutableStateFlow<List<PartUIState>>> = mutableListOf()
    val unitFlows: MutableList<List<MutableStateFlow<List<UnitUIState>>>> = mutableListOf()

    val quizCount = MutableStateFlow(20f)
    val selectedUnitsCount = MutableStateFlow(0)

    init {
        _collectionsFlow.value.forEachIndexed { collectionIndex, collection ->
            var partId = 0
            val parts = List(collection.partCount) {
                PartUIState(
                    id = partId++,
                    title = "${it + 1}",
                    collectionId = collection.id,
                    unitCount = collection.unitCount
                )
            }
            partsFlows.add(MutableStateFlow(parts))
            unitFlows.add(
                List(collection.partCount) { partIndex ->
                    var unitId = 0
                    MutableStateFlow(
                        List(collection.unitCount) {
                            UnitUIState(
                                id = unitId++,
                                name = "Unit ${it + 1}",
                                progress = 0,
                                collectionId = collection.id,
                                partId = parts[partIndex].id
                            )
                        }
                    )
                }
            )
        }
    }

    fun updateUnits() {

        viewModelScope.launch {

            val curColl = currentCollection
            currentParts[currentCollection]?.let { currentPart ->
                val progressList = getUnitsPercentUseCase(currentCollection, currentPart)

                if (currentCollection == curColl) {

                    unitFlows[currentCollection][currentPart].update { units ->
                        units.mapIndexed { index, unit ->
                            unit.copy(progress = progressList.getOrNull(index) ?: unit.progress)
                        }
                    }
                }
            }
        }
    }

    fun setCurrentCollection(collectionId: Int) {
        _collectionsFlow.update { collections ->
            collections.map { collection ->
                if (collection.id == collectionId) collection.copy(isCurrent = true)
                else collection.copy(isCurrent = false)
            }
        }
        currentCollection = collectionId
        updateUnits()
    }

    fun setQuizCount(count: Float) {
        quizCount.value = count
    }

    fun getQuizCount(): Float {
        return quizCount.value
    }

    fun setCurrentPart(partId: Int) {
        val collectionId = currentCollection
        partsFlows[collectionId].update { parts ->
            parts.map { part ->
                part.copy(isCurrent = part.id == partId)
            }
        }
        currentParts[collectionId] = partId
        updateUnits()
        Logger.d(SelectViewModel::class.java.simpleName, "setCurrentPart: $partId")
        Logger.d(SelectViewModel::class.java.simpleName, partsFlows.joinToString { "Part: ${it.value.joinToString { it.isCurrent.toString() }}" })
    }

    fun setCurrentPart(collectionId: Int, partId: Int) {
        partsFlows[collectionId].update { parts ->
            parts.map { part ->
                part.copy(isCurrent = part.id == partId)
            }
        }
        currentParts[collectionId] = partId
        updateUnits()
        Logger.d(SelectViewModel::class.java.simpleName, "setCurrentPart: $partId")
        Logger.d(SelectViewModel::class.java.simpleName, partsFlows.joinToString { "Part: ${it.value.joinToString { it.isCurrent.toString() }}" })
    }

    fun toggleUnitSelection(unitId: Int) {
        unitFlows[currentCollection][currentParts[currentCollection]!!].update { units ->
            units.map { unit ->
                if (unit.id == unitId) {
                    val u = unit.copy(isSelected = !unit.isSelected)
                    updateSelectedUnitCount(u)
                    u
                } else unit
            }
        }
    }

    fun selectUnit(unitId: Int) {
        unitFlows[currentCollection][currentParts[currentCollection]!!].update { units ->
            units.map { unit ->
                if (unit.id == unitId && !unit.isSelected) {
                    val u = unit.copy(isSelected = true)
                    updateSelectedUnitCount(u)
                    u
                } else unit
            }
        }
    }

    fun unselectUnit(unitId: Int) {
        unitFlows[currentCollection][currentParts[currentCollection]!!].update { units ->
            units.map { unit ->
                if (unit.id == unitId && unit.isSelected) {
                    val u = unit.copy(isSelected = false)
                    updateSelectedUnitCount(u)
                    u
                } else unit
            }
        }
    }

    fun isUnitSelected(unitId: Int): Boolean {
        return unitFlows[currentCollection][currentParts[currentCollection]!!].value.any { it.id == unitId && it.isSelected }
    }

    fun selectAll() {
        viewModelScope.launch {
            unitFlows[currentCollection][currentParts[currentCollection]!!].value.forEach { unit ->
                selectUnit(unit.id)
                delay(5L)
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            unitFlows[currentCollection][currentParts[currentCollection]!!].value.forEach { unit ->
                unselectUnit(unit.id)
                delay(5L)
            }
        }
    }

    fun invertAll() {
        viewModelScope.launch {
            unitFlows[currentCollection][currentParts[currentCollection]!!].value.forEach { unit ->
                toggleUnitSelection(unit.id)
                delay(5L)
            }
        }
    }

    fun randomSelect() {
        viewModelScope.launch {
            unitFlows[currentCollection][currentParts[currentCollection]!!].value.forEach { unit ->
                if (nextBoolean()) selectUnit(unit.id)
                else unselectUnit(unit.id)
                delay(5L)
            }
        }
    }

    fun getSelectedUnits(): List<SelectedUnit> {
        return unitFlows.flatMapIndexed { collectionIndex, partList ->
            partList.mapIndexedNotNull { partIndex, unitFlow ->
                val selectedUnits = unitFlow.value.filter { it.isSelected }
                if (selectedUnits.isEmpty()) return@mapIndexedNotNull null

                selectedUnits.map {
                    SelectedUnit(
                        collectionId = collections[collectionIndex].id,
                        partId = partsFlows[collectionIndex].value[partIndex].id,
                        unitId = it.id
                    )
                }
            }.flatten()
        }
    }

    private fun updateSelectedUnitCount(unit: UnitUIState) {
        val collectionId = unit.collectionId
        val partId = unit.partId

        val isSelected = unit.isSelected

        selectedUnitsCount.update { it + if (isSelected) 1 else -1 }

        partsFlows[collectionId].update { parts ->
            parts.map {
                if (it.id == partId) {
                    val newCount = it.selectedUnitCount + if (isSelected) 1 else -1
                    it.copy(
                        selectedUnitCount = newCount,
                        isSelected = newCount > 0
                    )
                } else it
            }
        }

        _collectionsFlow.update { collections ->
            collections.map {
                if (it.id == collectionId) {
                    val newCount = it.selectedUnitCount + if (isSelected) 1 else -1
                    it.copy(
                        selectedUnitCount = newCount,
                        isSelected = newCount > 0
                    )
                } else it
            }
        }
    }

    fun saveLastCollectionId(int: Int) {
        prefsRepository.saveInt(LAST_COLLECTION_ID, int)
    }

    fun getLastCollectionId(): Int {
        return prefsRepository.getInt(LAST_COLLECTION_ID, 0)
    }

    fun saveLastPartId(id: Int) {
        prefsRepository.saveInt("${LAST_PART_ID}_$currentCollection", id)
    }

    fun getLastPartId(): Int {
        return prefsRepository.getInt("${LAST_PART_ID}_$currentCollection", 0)
    }
}