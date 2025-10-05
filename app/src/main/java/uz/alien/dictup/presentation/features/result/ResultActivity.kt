package uz.alien.dictup.presentation.features.result

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import uz.alien.dictup.databinding.ResultActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.presentation.features.base.BaseActivity

class ResultActivity : BaseActivity() {

    private lateinit var binding: ResultActivityBinding
    private val viewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ResultActivityBinding.inflate(layoutInflater)
        setClearEdge()
        setContentLayout {
            binding.root
        }

        hideAppBar()

        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        val attempt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("attempt", Attempt::class.java)
        } else {
            intent.getParcelableArrayListExtra("attempt")
        }

        viewModel.resolveAttempt(attempt ?: emptyList())
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}