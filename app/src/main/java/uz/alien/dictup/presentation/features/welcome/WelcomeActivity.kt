package uz.alien.dictup.presentation.features.welcome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import uz.alien.dictup.databinding.WelcomeActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.applyExitZoomReverseTransition
import uz.alien.dictup.presentation.common.extention.startActivityWithAlphaAnimation

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: WelcomeActivityBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WelcomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClearEdge()

        handler = Handler(mainLooper)

        binding.tvCompany.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://aliendevuz.t.me".toUri()
            startActivityWithAlphaAnimation(intent)
        }

        startWelcome(binding, ::finish, handler)
    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
        applyExitZoomReverseTransition()
    }
}