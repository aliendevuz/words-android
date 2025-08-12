package uz.alien.dictup.presentation.features.home.extention

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import uz.alien.dictup.presentation.common.extention.readTextFromUri

fun AppCompatActivity.handleIntent(intent: Intent) {
    val action = intent.action
    val data = intent.data

    when {
        (Intent.ACTION_VIEW == action && data != null &&
                (data.scheme == "file" || data.scheme == "content")) -> {

            val text = readTextFromUri(data)
            Log.d("@@@@", "File content:\n$text")
        }

        (Intent.ACTION_VIEW == action && data != null &&
                (data.scheme == "http" || data.scheme == "https" || data.scheme == "app")) -> {

            val path = data.path ?: ""
            val queryParams = data.queryParameterNames.joinToString(", ") {
                "$it = ${data.getQueryParameter(it)}"
            }
            Log.d("@@@@", "Path: $path\nQuery: $queryParams")
        }

        (Intent.ACTION_MAIN == action) -> {
            Log.d("@@@@", "By launcher")
        }

        else -> {
            Log.d("@@@@", "Unknown launch type")
        }
    }
}
