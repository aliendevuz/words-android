package uz.alien.dictup.presentation.features.setting

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.SettingActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitSwipeAnimation
import uz.alien.dictup.presentation.common.extention.clearPadding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.value.strings.DataStore
import uz.alien.dictup.value.strings.DataStore.TTS_SPEED

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: SettingActivityBinding
    private val viewModel: SettingViewModel by viewModels()

    private var isPitchCollected = false
    private var isSpeedCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: SFX va background music hali yaxshi sozlanmadi

        binding = SettingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClearEdge()
        clearPadding(binding.root)
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        // Pitch
//        binding.seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                val pitch = (40 + progress) / 100f  // 40 ↔ 140 => 0.4 ↔ 1.4
//                binding.tvPitchValue.text = String.format("%.2f", pitch)
//                viewModel.saveTTSPitch(pitch)
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })

        // Speed
        binding.seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = (40 + progress) / 100f  // 40 ↔ 140 => 0.4 ↔ 1.4
                binding.tvSpeedValue.text = String.format("%.2f", speed)
                viewModel.saveTTSSpeed(speed)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.cbSFX.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSFXVolume(isChecked)
        }

        binding.cbMusic.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveMusicVolume(isChecked)
        }

        collectTTS()
        collectVolume()
    }

    private fun collectTTS() {
//        lifecycleScope.launch {
//            viewModel.dataStore.getFloat(TTS_PITCH).collect {
//                if (!isPitchCollected) {
//                    isPitchCollected = true
//                    if (it == 0f) {
//                        binding.seekPitch.progress = 100 - 40
//                        binding.tvPitchValue.text = "1.0"
//                    }
//                    else {
//                        binding.seekPitch.progress = ((it * 100) - 40).toInt()
//                        binding.tvPitchValue.text = String.format("%.2f", it)
//                    }
//                }
//            }
//        }

        lifecycleScope.launch {
            viewModel.dataStore.getFloat(TTS_SPEED).collect {
                if (!isSpeedCollected) {
                    isSpeedCollected = true
                    if (it == 0f) {
                        binding.seekSpeed.progress = 100 - 40
                        binding.tvSpeedValue.text = "1.0"
                    } else {
                        binding.seekSpeed.progress = ((it * 100) - 40).toInt()
                        binding.tvSpeedValue.text = String.format("%.2f", it)
                    }
                }
            }
        }
    }

    private fun collectVolume() {
        lifecycleScope.launch {
            viewModel.dataStore.getBoolean(DataStore.IS_SFX_AVAILABLE).collect {
                binding.cbSFX.isChecked = it
            }
        }

        lifecycleScope.launch {
            viewModel.dataStore.getBoolean(DataStore.IS_BG_MUSIC_AVAILABLE).collect {
                binding.cbMusic.isChecked = it
            }
        }
    }

    override fun finish() {
        super.finish()
        applyExitSwipeAnimation()
    }
}