package uz.alien.dictup.presentation.features.pick.model

import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word

data class NavigationEvent(
    val collectionId: Int,
    val partId: Int,
    val unitId: Int,
    val words: ArrayList<Word>,
    val nativeWords: ArrayList<NativeWord>,
    val scores: ArrayList<Score>,
    val stories: ArrayList<Story>
)