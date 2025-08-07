package uz.alien.test.picture.presenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import uz.alien.test.databinding.PictureActivityPictureBinding

class PictureActivity : AppCompatActivity() {

    private lateinit var binding: PictureActivityPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PictureActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load("https://assets.4000.uz/assets/en/essential/picture/0.jpg")
            .into(binding.ivImage)
    }
}