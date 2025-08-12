package uz.alien.dictup.presentation.common.extention

import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.isNight(): Boolean {
    return (
            resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
        ) == Configuration.UI_MODE_NIGHT_YES
}

fun dp(): Float {
    return Resources.getSystem().displayMetrics.density
}

fun AppCompatActivity.readTextFromUri(uri: Uri): String {
    return try {
        contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader().use { it.readText() }
        } ?: "Empty file"
    } catch (e: Exception) {
        "Error reading file: ${e.message}"
    }
}
