package uz.alien.dictup.presenter.quiz

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.presenter.result.ActivityResult
import uz.alien.dictup.databinding.ActivityQuizBinding
import uz.alien.dictup.presenter.base.ActivityBase

class ActivityQuiz : ActivityBase() {

  private lateinit var binding: ActivityQuizBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityQuizBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)
    binding.tv.setOnClickListener {
      startActivity(Intent(this, ActivityResult::class.java))
    }
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}