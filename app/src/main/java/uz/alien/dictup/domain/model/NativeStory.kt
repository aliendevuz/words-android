package uz.alien.dictup.domain.model

data class NativeStory(
    val id: Int? = null,
    val title: String,
    val content: String,
    val collectionId: Int? = null,
    val partId: Int? = null,
    val unitId: Int? = null,
    val nativeLanguage: String
)