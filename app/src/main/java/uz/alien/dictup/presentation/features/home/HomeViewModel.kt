package uz.alien.dictup.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.R
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.domain.usecase.GetScoreOfBeginnerUseCase
import uz.alien.dictup.domain.usecase.GetScoreOfEssentialUseCase
import uz.alien.dictup.domain.usecase.sync.UpdateUseCase
import uz.alien.dictup.presentation.features.home.model.Book
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.SharedPrefs.IS_FIRST_TIME
import uz.alien.dictup.value.strings.SharedPrefs.LAST_COLLECTION
import uz.alien.dictup.value.strings.SharedPrefs.LAST_PART

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefsRepository: SharedPrefsRepository,
    private val getScoreOfBeginnerUseCase: GetScoreOfBeginnerUseCase,
    private val getScoreOfEssentialUseCase: GetScoreOfEssentialUseCase,
    private val updateUseCase: UpdateUseCase
) : ViewModel() {

    val beginnerBooks = arrayListOf(
        Book(0, R.color.beginner_1, R.drawable.beginner_1, true),
        Book(1, R.color.beginner_2, R.drawable.beginner_2, true),
        Book(2, R.color.beginner_3, R.drawable.beginner_3, true),
        Book(3, R.color.beginner_4, R.drawable.beginner_4, true)
    )

    val essentialBooks = arrayListOf(
        Book(0, R.color.book1, R.drawable.book_0, true),
        Book(1, R.color.book2, R.drawable.book_1, true),
        Book(2, R.color.book3, R.drawable.book_2, true),
        Book(3, R.color.book4, R.drawable.book_3, true),
        Book(4, R.color.book5, R.drawable.book_4, true),
        Book(5, R.color.book6, R.drawable.book_5, true)
    )

    private val _beginnerBooks = MutableStateFlow<List<Book>>(beginnerBooks)
    val beginnerBooksState: SharedFlow<List<Book>> = _beginnerBooks

    private val _essentialBooks = MutableStateFlow<List<Book>>(essentialBooks)
    val essentialBooksState: SharedFlow<List<Book>> = _essentialBooks

    suspend fun updateBook() {

        _beginnerBooks.update { it.map { book -> book.copy(isLoaded = true) } }
        _essentialBooks.update { it.map { book -> book.copy(isLoaded = true) } }

        val beginnerPercents = getScoreOfBeginnerUseCase()
        beginnerPercents.forEach {
            Logger.d(HomeViewModel::class.java.simpleName, "beginnerPercents: $it")
        }
        val essentialPercents = getScoreOfEssentialUseCase()

        _beginnerBooks.update { books ->
            books.mapIndexed { index, book ->
                book.copy(progress = beginnerPercents[index])
            }
        }

        _essentialBooks.update { books ->
            books.mapIndexed { index, book ->
                book.copy(progress = essentialPercents[index])
            }
        }
    }

    fun isFirstTime() = prefsRepository.getBoolean(IS_FIRST_TIME, true)
    fun setFirstTimeFalse() = prefsRepository.saveBoolean(IS_FIRST_TIME, false)

    fun saveLastCollection(id: Int) {
        prefsRepository.saveInt(LAST_COLLECTION, id)
    }

    fun getLastPart(): Int {
        return prefsRepository.getInt(LAST_PART, 0)
    }

    fun getLastCollection(): Int {
        return prefsRepository.getInt(LAST_COLLECTION, 0)
    }

    fun updateData() {
        viewModelScope.launch {
            updateUseCase()
        }
    }
}