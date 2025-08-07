package uz.alien.dictup.presentation.features.test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.TestActivityTestBinding

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    private lateinit var binding: TestActivityTestBinding
    private val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TestActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getData()

        lifecycleScope.launch {
            viewModel.text.collect {
                binding.etData.setText(it)
            }
        }
    }
}