package uz.alien.dictup.presentation.common.extention

import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun AppCompatActivity.hideSystemUI() {
    // Window’ni insetslarni boshqarishga tayyorlaymiz
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

fun AppCompatActivity.showSystemUI() {
    // Default holatga qaytaramiz
    WindowCompat.setDecorFitsSystemWindows(window, true)

    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())
}