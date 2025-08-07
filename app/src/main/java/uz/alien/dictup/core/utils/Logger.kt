package uz.alien.dictup.core.utils

import android.util.Log
import uz.alien.dictup.BuildConfig
import kotlin.text.lowercase

object Logger {

    const val TAG = "LOGGER"

    fun logToLogcat(level: String, tag: String, msg: String) {
        if (
            BuildConfig.LOGGING_IS_AVAILABLE &&
            (BuildConfig.DEBUG || BuildConfig.DISABLE_LOGGING_ON_PROD)
        ) {
            when (level.lowercase()) {
                "debug" -> Log.d(tag, msg)
                "info" -> Log.i(tag, msg)
                "warn" -> Log.w(tag, msg)
                "error" -> Log.e(tag, msg)
            }
        }
    }

    fun d(tag: String, msg: String) {
        log("DEBUG", tag, msg)
    }

    fun d(msg: String) {
        log("DEBUG", TAG, msg)
    }

    fun i(tag: String, msg: String) {
        log("INFO", tag, msg)
    }

    fun i(msg: String) {
        log("INFO", TAG, msg)
    }

    fun w(tag: String, msg: String) {
        log("WARN", tag, msg)
    }

    fun w(msg: String) {
        log("WARN", TAG, msg)
    }

    fun e(tag: String, msg: String) {
        log("ERROR", tag, msg)
    }

    fun e(msg: String) {
        log("ERROR", TAG, msg)
    }

    private fun log(level: String, tag: String, msg: String) {
        try {
            logToLogcat(level, tag, msg)
        } catch (_: Exception) {
            println("$level/$tag: $msg")
        }
    }
}