package uz.alien.dictup.domain.repository

interface CacheManagerRepository {

    fun saveBeginnerWords(content: String)
    fun saveBeginnerStories(content: String)
    fun saveBeginnerUzWords(content: String)
    fun saveBeginnerUzStories(content: String)

    fun loadBeginnerWords(): String
    fun loadBeginnerStories(): String
    fun loadBeginnerUzWords(): String
    fun loadBeginnerUzStories(): String

    fun clearBeginnerWords()
    fun clearBeginnerStories()
    fun clearBeginnerUzWords()
    fun clearBeginnerUzStories()

    fun saveEssentialWords(content: String)
    fun saveEssentialStories(content: String)
    fun saveEssentialUzWords(content: String)
    fun saveEssentialUzStories(content: String)

    fun loadEssentialWords(): String
    fun loadEssentialStories(): String
    fun loadEssentialUzWords(): String
    fun loadEssentialUzStories(): String

    fun clearEssentialWords()
    fun clearEssentialStories()
    fun clearEssentialUzWords()
    fun clearEssentialUzStories()
}