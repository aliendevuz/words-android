package uz.alien.dictup.domain.model

enum class WordCollection(val id: Int, val key: String) {
    BEGINNER(0, "beginner"),
    ESSENTIAL(1, "essential");

    companion object {
        fun fromKey(key: String): WordCollection? =
            entries.find { it.key == key }

        fun fromId(id: Int): WordCollection? =
            entries.find { it.id == id }
    }
}