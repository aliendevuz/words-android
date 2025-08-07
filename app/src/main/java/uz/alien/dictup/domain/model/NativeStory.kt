package uz.alien.dictup.domain.model

data class NativeStory(
    val id: Int? = null,
    val title: String,
    val content: String,
    val storyId: Int? = null,
    val nativeLanguage: String
)