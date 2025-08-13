package uz.alien.dictup.presentation.features.quiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.presentation.features.result.ResultActivity
import uz.alien.dictup.databinding.QuizActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.select.model.SelectedUnit

class QuizActivity : BaseActivity() {

    private lateinit var binding: QuizActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = QuizActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        setClearEdge()
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        val units = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra("units", SelectedUnit::class.java)
        } else {
            intent.getParcelableArrayExtra("units")
        }

        units?.forEach { unit ->
            Logger.d("$unit")
        }

        binding.tv.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}