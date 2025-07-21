package uz.alien.dictup.activity

import android.os.Bundle
import uz.alien.dictup.databinding.ActivityResultBinding

class ResultActivity : BaseActivity() {

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