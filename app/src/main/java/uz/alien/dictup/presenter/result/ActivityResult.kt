package uz.alien.dictup.presenter.result

import android.os.Bundle
import uz.alien.dictup.databinding.ActivityResultBinding
import uz.alien.dictup.presenter.base.ActivityBase

class ActivityResult : ActivityBase() {

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