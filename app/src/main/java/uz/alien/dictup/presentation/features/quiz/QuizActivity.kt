package uz.alien.dictup.presentation.features.quiz

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.presentation.features.result.ResultActivity
import uz.alien.dictup.databinding.QuizActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setExitZoomAnimation
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity

class QuizActivity : BaseActivity() {

  private lateinit var binding: QuizActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = QuizActivityBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)
    binding.tv.setOnClickListener {
      startActivity(Intent(this, ResultActivity::class.java))
    }
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}