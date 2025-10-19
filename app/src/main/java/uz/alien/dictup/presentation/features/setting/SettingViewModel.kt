package uz.alien.dictup.presentation.features.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.value.strings.DataStore
import uz.alien.dictup.value.strings.DataStore.TTS_PITCH
import uz.alien.dictup.value.strings.DataStore.TTS_SPEED
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val dataStore = dataStoreRepository

    fun saveSFXVolume(volume: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveBoolean(DataStore.IS_SFX_AVAILABLE, volume)
        }
    }

    fun saveMusicVolume(volume: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveBoolean(DataStore.IS_BG_MUSIC_AVAILABLE, volume)
        }
    }

    fun saveTTSPitch(pitch: Float) {
        viewModelScope.launch {
            dataStoreRepository.saveFloat(TTS_PITCH, pitch)
        }
    }

    fun saveTTSSpeed(speed: Float) {
        viewModelScope.launch {
            dataStoreRepository.saveFloat(TTS_SPEED, speed)
        }
    }
}