package uz.alien.dictup.presentation.features.welcome

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import uz.alien.dictup.databinding.WelcomeActivityBinding
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setExitZoomAnimationReverse

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: WelcomeActivityBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WelcomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClearEdge()

        handler = Handler(mainLooper)

        startWelcome(binding, ::finish, handler)
    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
        setExitZoomAnimationReverse()
    }
}