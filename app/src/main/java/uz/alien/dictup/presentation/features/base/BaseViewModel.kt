package uz.alien.dictup.presentation.features.base

import android.content.Intent
import android.graphics.Rect
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.value.strings.SharedPrefs.INSETS_BOTTOM
import uz.alien.dictup.value.strings.SharedPrefs.INSETS_LEFT
import uz.alien.dictup.value.strings.SharedPrefs.INSETS_RIGHT
import uz.alien.dictup.value.strings.SharedPrefs.INSETS_TOP
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val prefsRepository: SharedPrefsRepository
) : ViewModel() {

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen = _isDrawerOpen.asStateFlow()

    private val _isDrawerLocked = MutableStateFlow(false)
    val isDrawerLocked = _isDrawerLocked.asStateFlow()

    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible = _isSearchVisible.asStateFlow()

    private val _openActivity = MutableStateFlow<Intent?>(null)
    val openActivity = _openActivity.asStateFlow()

    private val _animationType = MutableStateFlow(AnimationType.ZOOM)
    val animationType = _animationType.asStateFlow()

    fun openDrawer() {
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
    }

    fun lockDrawer() {
        _isDrawerLocked.value = true
    }

    fun unlockDrawer() {
        _isDrawerLocked.value = false
    }

    fun showSearch() {
        _isSearchVisible.value = true
    }

    fun hideSearch() {
        _isSearchVisible.value = false
    }

    fun clearIntent() {
        _openActivity.value = null
    }

    fun startActivityWithAnimation(intent: Intent, animationType: AnimationType = AnimationType.ZOOM) {
        if (_openActivity.value == null) {
            _animationType.value = animationType
            _openActivity.value = intent
        }
    }

    fun saveSystemPaddings(insets: Insets) {
        prefsRepository.saveInt(INSETS_LEFT, insets.left)
        prefsRepository.saveInt(INSETS_TOP, insets.top)
        prefsRepository.saveInt(INSETS_RIGHT, insets.right)
        prefsRepository.saveInt(INSETS_BOTTOM, insets.bottom)
    }

    fun getSystemPaddings(): Rect {
        return Rect(
            prefsRepository.getInt(INSETS_LEFT, 0),
            prefsRepository.getInt(INSETS_TOP, 45),
            prefsRepository.getInt(INSETS_RIGHT, 0),
            prefsRepository.getInt(INSETS_BOTTOM, 0)
        )
    }
}