package uz.alien.dictup.archive.activity

import android.os.Bundle
import uz.alien.dictup.databinding.ActivityResultBinding
import uz.alien.dictup.presenter.base.ActivityBase

class ResultActivity : ActivityBase() {

  private lateinit var binding: ActivityResultBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityResultBinding.inflate(layoutInflater)
    setClearEdge()
    setContentLayout {
      binding.root
    }
    setSystemPadding(binding.root)
  }

  override fun finish() {
    super.finish()
    setExitZoomAnimation()
  }
}