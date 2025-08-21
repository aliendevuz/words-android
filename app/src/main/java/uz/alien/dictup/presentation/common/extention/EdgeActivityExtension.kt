package uz.alien.dictup.presentation.common.extention

import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.setHomeEdge(statusBarColor: Int) {
    if (isNight()) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = statusBarColor
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            )
        )
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                darkScrim = statusBarColor,
                scrim = statusBarColor
            ),
            navigationBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            )
        )
    }
}

fun AppCompatActivity.setClearEdge() {
    if (isNight()) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            )
        )
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            )
        )
    }
}