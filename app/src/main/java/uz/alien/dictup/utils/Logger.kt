package uz.alien.dictup.utils

import android.util.Log
import uz.alien.dictup.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object Logger {

    private const val TAG = "LOGGER"

    // Per-thread holat
    private val lastHolderTL = ThreadLocal<KClass<*>?>()
    private val methodNameTL = ThreadLocal<String?>()
    private val callCountTL = ThreadLocal<AtomicInteger>()

    private val timeFormatter =
        SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    fun Any.isCalled() {
        val currentClass = this::class
        val methodName = resolveCallerMethod()

        val lastClass = lastHolderTL.get()
        val lastMethod = methodNameTL.get()

        // Shu thread ichida faqat bitta funksiya tekshirilishi kerak
        if (lastClass != null && (lastClass != currentClass || lastMethod != methodName)) {
            throw IllegalStateException(
                "This tool can track only one function at a time per thread. " +
                        "Currently tracking ${lastClass.simpleName}.${lastMethod}, " +
                        "but got ${currentClass.simpleName}.$methodName on thread ${Thread.currentThread().name}. " +
                        "Call Logger.resetTracking() to switch."
            )
        }

        // Birinchi chaqiriq — state va counter ni init qilamiz
        if (lastClass == null) {
            lastHolderTL.set(currentClass)
            methodNameTL.set(methodName)
            callCountTL.set(AtomicInteger(0))
        }

        val count = callCountTL.get()?.incrementAndGet() ?: run {
            val a = AtomicInteger(1)
            callCountTL.set(a)
            1
        }

        val ts = timeFormatter.format(Date())
        i(
            "IS_CALLED",
            "Calling: $count  ${currentClass.simpleName}.$methodName is working! Timestamp: $ts (thread=${Thread.currentThread().name})"
        )
    }

    /** Agar boshqa funksiya/joyni tekshiradigan bo‘lsang, avval shu metodni chaqir. */
    fun resetTracking() {
        lastHolderTL.remove()
        methodNameTL.remove()
        callCountTL.remove()
    }

    // Chaquiruvchi method nomini robust aniqlash
    private fun resolveCallerMethod(): String {
        val st = Thread.currentThread().stackTrace
        try {
            Log.getStackTraceString(null)
            if (BuildConfig.LOGGING_IS_AVAILABLE && (BuildConfig.DEBUG || BuildConfig.DISABLE_LOGGING_ON_PROD)) {
                var isFirst = false
                for (el in st) {
                    if (el.className != Thread::class.java.name && el.className != Logger::class.java.name) {
                        if (!isFirst) {
                            isFirst = true
                        } else {
                            return el.methodName ?: "unknown"
                        }
                    }
                }
            }
        } catch (_: Exception) {
            for (el in st) {
                if (el.className != Thread::class.java.name && el.className != Logger::class.java.name) {
                    return el.methodName ?: "unknown"
                }
            }
        }
        return "unknown"
    }

    // ---- Logging wrappers (JVM/Unit test friendly) ----

    fun logToLogcat(level: String, tag: String, msg: String) {
        if (BuildConfig.LOGGING_IS_AVAILABLE && (BuildConfig.DEBUG || BuildConfig.DISABLE_LOGGING_ON_PROD)) {
            when (level.lowercase()) {
                "debug" -> Log.d(tag, msg)
                "info" -> Log.i(tag, msg)
                "warn" -> Log.w(tag, msg)
                "error" -> Log.e(tag, msg)
            }
        }
    }

    fun d(tag: String, msg: String?) = log("DEBUG", tag, msg.toString())
    fun d(msg: String?) = log("DEBUG", TAG, msg.toString())
    fun i(tag: String, msg: String?) = log("INFO", tag, msg.toString())
    fun i(msg: String?) = log("INFO", TAG, msg.toString())
    fun w(tag: String, msg: String?) = log("WARN", tag, msg.toString())
    fun w(msg: String?) = log("WARN", TAG, msg.toString())
    fun e(tag: String, msg: String?) = log("ERROR", tag, msg.toString())
    fun e(msg: String?) = log("ERROR", TAG, msg.toString())

    private fun log(level: String, tag: String, msg: String) {
        try {
            logToLogcat(level, tag, msg)
        } catch (_: Exception) {
            println("$level/$tag: $msg")
        }
    }
}