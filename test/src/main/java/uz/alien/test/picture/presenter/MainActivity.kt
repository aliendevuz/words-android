package uz.alien.test.picture.presenter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.alien.test.databinding.PictureActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: PictureActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PictureActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bOpen.setOnClickListener {
            val intent = Intent(this, PictureActivity::class.java)
            startActivity(intent)
        }
    }
}