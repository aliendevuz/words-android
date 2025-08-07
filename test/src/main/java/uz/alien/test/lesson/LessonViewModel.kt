package uz.alien.test.lesson

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.alien.test.lesson.model.Word

class LessonViewModel : ViewModel() {

    private val _words = MutableStateFlow(listOf(
        Word(0, "apple", "olma"),
        Word(1, "banana", "banan"),
        Word(2, "cherry", "shaftoli"),
        Word(3, "date", "qovun"),
        Word(4, "elderberry", "qalam"),
        Word(5, "fig", "mandarin"),
        Word(6, "grape", "gilos"),
        Word(7, "honeydew", "uzum"),
        Word(8, "kiwi", "kiwi"),
        Word(9, "lemon", "lemon"),
        Word(10, "mango", "nok"),
        Word(11, "orange", "mandarin"),
        Word(12, "pear", "shaftoli"),
        Word(13, "quince", "qovun"),
        Word(14, "raspberry", "qalam"),
        Word(15, "strawberry", "mandarin"),
        Word(16, "tangerine", "gilos"),
        Word(17, "ugli", "uzum"),
        Word(18, "watermelon", "kiwi"),
        Word(19, "xigua", "lemon")
    ))
    val words: StateFlow<List<Word>> = _words
}