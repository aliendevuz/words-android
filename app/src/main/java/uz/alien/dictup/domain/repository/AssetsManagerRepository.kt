package uz.alien.dictup.domain.repository

interface AssetsManagerRepository {

    fun loadBeginnerWords(): String
    fun loadBeginnerStories(): String
    fun loadBeginnerUzWords(): String
    fun loadBeginnerUzStories(): String

    fun loadEssentialWords(): String
    fun loadEssentialStories(): String
    fun loadEssentialUzWords(): String
    fun loadEssentialUzStories(): String
}