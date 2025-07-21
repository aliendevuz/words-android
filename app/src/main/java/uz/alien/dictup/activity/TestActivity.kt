package uz.alien.dictup.activity

import android.content.Intent
import android.os.Bundle
import uz.alien.dictup.databinding.ActivityTestBinding

class TestActivity : BaseActivity() {

  private lateinit var binding: ActivityTestBinding

  override fun onReady(savedInstanceState: Bundle?) {
    binding = ActivityTestBinding.inflate(layoutInflater)
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