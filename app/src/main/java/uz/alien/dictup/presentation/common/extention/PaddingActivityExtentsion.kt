package uz.alien.dictup.presentation.common.extention

import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun AppCompatActivity.setSystemPadding(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(0, systemBars.top, 0, 0)
        insets
    }
}

fun AppCompatActivity.getSystemStatusPadding(view: View, onGet: (Int) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        onGet(systemBars.top)
        insets
    }
}

fun AppCompatActivity.clearPadding(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view, null)
    view.setPadding(0, 0, 0, 0)
}

fun AppCompatActivity.setSystemExclusion(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        ViewCompat.setSystemGestureExclusionRects(v, listOf(Rect(0, 0, 100, v.height)))
        insets
    }
}