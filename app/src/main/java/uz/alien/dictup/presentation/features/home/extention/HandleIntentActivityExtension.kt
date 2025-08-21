package uz.alien.dictup.presentation.features.home.extention

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.presentation.common.extention.readTextFromUri

fun AppCompatActivity.handleIntent(intent: Intent) {
    val action = intent.action
    val data = intent.data

    when {
        (Intent.ACTION_VIEW == action && data != null &&
                (data.scheme == "file" || data.scheme == "content")) -> {

            val text = readTextFromUri(data)
            Logger.d("@@@@", "File content:\n$text")
        }

        (Intent.ACTION_VIEW == action && data != null &&
                (data.scheme == "http" || data.scheme == "https" || data.scheme == "app")) -> {

            val path = data.path ?: ""
            val queryParams = data.queryParameterNames.joinToString(", ") {
                "$it = ${data.getQueryParameter(it)}"
            }
            Logger.d("@@@@", "Path: $path\nQuery: $queryParams")
        }

        (Intent.ACTION_MAIN == action) -> {
            Logger.d("@@@@", "By launcher")
        }

        else -> {
            Logger.d("@@@@", "Unknown launch type")
        }
    }
}