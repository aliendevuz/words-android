package uz.alien.dictup.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.R
import uz.alien.dictup.domain.usecase.home.MainUseCases
import uz.alien.dictup.presentation.features.home.model.Book

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainUseCases: MainUseCases
) : ViewModel() {

    val beginnerBooks = arrayListOf(
        Book(0, R.color.beginner_1, R.drawable.beginner_1_n, true),
        Book(1, R.color.beginner_2, R.drawable.beginner_2_n, true),
        Book(2, R.color.beginner_3, R.drawable.beginner_3_n, true),
        Book(3, R.color.beginner_4, R.drawable.beginner_4_n, true)
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

    val dataStoreRepository = mainUseCases.getDataStoreRepositoryUseCase()

    init {
        viewModelScope.launch {
            updateBook()
        }
    }

    suspend fun updateBook() {

        val isSyncCompleted = mainUseCases.isSyncCompletedUseCase().firstOrNull() ?: false

        _beginnerBooks.update { it.map { book -> book.copy(isLoaded = isSyncCompleted) } }
        _essentialBooks.update { it.map { book -> book.copy(isLoaded = isSyncCompleted) } }

        val beginnerPercents = mainUseCases.getScoreOfBeginnerUseCase()

        _beginnerBooks.update { books ->
            books.mapIndexed { index, book ->
                book.copy(progress = beginnerPercents[index])
            }
        }

        val essentialPercents = mainUseCases.getScoreOfEssentialUseCase()

        _essentialBooks.update { books ->
            books.mapIndexed { index, book ->
                book.copy(progress = essentialPercents[index])
            }
        }
    }
}