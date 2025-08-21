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

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: SettingActivityBinding
    private val viewModel: SettingViewModel by viewModels()

    private var isPitchCollected = false
    private var isSpeedCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClearEdge()
        clearPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        // Pitch
        binding.seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pitch = (40 + progress) / 100f  // 40 ↔ 140 => 0.4 ↔ 1.4
                binding.tvPitchValue.text = String.format("%.2f", pitch)
                viewModel.saveTTSPitch(pitch)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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

        // Old value ni DataStore dan yuklash
        lifecycleScope.launch {
            viewModel.dataStore.getTTSPitch().collect {
                if (!isPitchCollected) {
                    isPitchCollected = true
                    binding.seekPitch.progress = ((it * 100) - 40).toInt()
                    binding.tvPitchValue.text = String.format("%.2f", it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.dataStore.getTTSSpeed().collect {
                if (!isSpeedCollected) {
                    isSpeedCollected = true
                    binding.seekSpeed.progress = ((it * 100) - 40).toInt()
                    binding.tvSpeedValue.text = String.format("%.2f", it)
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        applyExitSwipeAnimation()
    }
}