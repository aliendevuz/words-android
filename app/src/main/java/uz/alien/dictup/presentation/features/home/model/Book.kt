package uz.alien.dictup.presentation.features.home.model

data class Book(
    val id: Int,
    val backgroundColor: Int,
    val imageRes: Int,
    val isLoaded: Boolean = false,
    val progress: Int = 0
)