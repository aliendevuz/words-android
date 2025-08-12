package uz.alien.dictup.presentation.features.result

import android.os.Bundle
import uz.alien.dictup.databinding.ResultActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setExitZoomAnimation
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity

class ResultActivity : BaseActivity() {

  private lateinit var binding: ResultActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ResultActivityBinding.inflate(layoutInflater)
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