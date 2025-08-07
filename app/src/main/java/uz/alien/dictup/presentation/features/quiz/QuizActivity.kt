package uz.alien.dictup.presentation.features.quiz

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.presentation.features.result.ResultActivity
import uz.alien.dictup.databinding.ActivityQuizBinding
import uz.alien.dictup.presentation.features.base.BaseActivity

class QuizActivity : BaseActivity() {

  private lateinit var binding: ActivityQuizBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityQuizBinding.inflate(layoutInflater)
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