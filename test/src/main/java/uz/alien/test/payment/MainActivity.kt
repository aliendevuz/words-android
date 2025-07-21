package uz.alien.test.payment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import uz.alien.test.databinding.ActivityFreeBinding
import uz.alien.test.databinding.ActivityPremiumBinding
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

  private val TAG = MainActivity::class.java.simpleName

  private lateinit var freeBinding: ActivityFreeBinding
  private lateinit var premiumBinding: ActivityPremiumBinding

  private val isPremium = "isPremium"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    if (PrefsManager.getInstance(this).getBool(isPremium, false)) {
      premiumBinding = ActivityPremiumBinding.inflate(layoutInflater)
      setContentView(premiumBinding.root)
      ViewCompat.setOnApplyWindowInsetsListener(premiumBinding.root) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
      }
    } else {
      if (isInternetAvailable(this)) {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        getFileFromGitHub("dv/${deviceId}/0") {
          when(it) {
            "1" -> {
              Log.d(TAG, "Sotib olingan")
              runOnUiThread {
                Toast.makeText(this, "Sotib olingan", Toast.LENGTH_SHORT).show()
                PrefsManager.getInstance(this).saveBool(isPremium, true)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
              }
            }
            "0" -> {
              Log.d(TAG, "To'lov qilinmagan")
              runOnUiThread {
                Toast.makeText(this, "To'lov qilinmagan", Toast.LENGTH_SHORT).show()
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                  .setTitle("To'lov amalga oshmagan")
                  .setMessage("To'lov qilishni davom ettirasizmi?\nIstasangiz premium uchun arizani bekor qilishingiz ham mumkin")
                  .setPositiveButton("Ha") { dialog, which ->

                  }
                  .setNeutralButton("Keyinroq") { dialog, which ->

                  }
                  .setNegativeButton("Bekor qilaman") { dialog, which ->

                  }
                val dialog = builder.create()
                dialog.show()
              }
            }
            null -> {
              Log.d(TAG, "Ariza topshirmagan")
              runOnUiThread {
                Toast.makeText(this, "Ariza topshirmagan", Toast.LENGTH_SHORT).show()
              }
            }
            else -> {
              Log.d(TAG, "Noto'g'ri format")
            }
          }
        }
      }

      freeBinding = ActivityFreeBinding.inflate(layoutInflater)
      setContentView(freeBinding.root)
      ViewCompat.setOnApplyWindowInsetsListener(freeBinding.root) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
      }

      freeBinding.bBuyPremium.setOnClickListener {

        Thread {

          val typeOfAction = "request a premium"
          val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
          val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
          val data = "{\"type\": $typeOfAction, \"device_id\": $deviceId, \"date\": $currentDate}"
          saveToCache(data.toByteArray(), "request_premium.c")

          val chatId = "6992831232"
          val activeReception = "-1002532956074"
          val archive = "-1002611196996"


          val file = File(cacheDir, "request_premium.c")
          sendFileWithTelegramBot(file, activeReception)
          val response = sendFileWithTelegramBot(file, archive)

          file.deleteOnExit()

          if (response.contains("ok")) {
            startActivity(Intent(this, PaymentActivity::class.java))
          }
        }.start()
      }
    }

    handleIntent(intent)
  }

  fun getFileFromGitHub(
    filePath: String,
    onResult: (String?) -> Unit
  ) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://api.github.com/repos/xalilovdev/wd/contents/$filePath")
      .addHeader("Authorization", "token 33")
      .addHeader("Accept", "application/vnd.github.v3.raw")
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        onResult(null)
      }

      override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
          val body = response.body?.string()
          onResult(body)
        } else {
          onResult(null)
        }
      }
    })
  }

  fun sendFileWithTelegramBot(file: File, chatId: String): String {
    val botToken = "7646950996:AAGFFCSPMYj2dsgwQsy3eGwg3PFL32hYDt8"

    val url = URL("https://api.telegram.org/bot$botToken/sendDocument")

    val boundary = "*****" + System.currentTimeMillis() + "*****"
    val connection = url.openConnection() as HttpURLConnection
    connection.doOutput = true
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

    val outputStream = DataOutputStream(connection.outputStream)
    outputStream.writeBytes("--$boundary\r\n")
    outputStream.writeBytes("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n")
    outputStream.writeBytes("$chatId\r\n")

    outputStream.writeBytes("--$boundary\r\n")
    outputStream.writeBytes("Content-Disposition: form-data; name=\"document\"; filename=\"${file.name}\"\r\n\r\n")
    outputStream.write(file.readBytes())

    outputStream.writeBytes("\r\n--$boundary--\r\n")
    outputStream.flush()
    outputStream.close()

    val response = connection.inputStream.bufferedReader().readText()
    Log.d("TelegramBot", "Response: $response")
    return response
  }

  fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
      NetworkCapabilities.NET_CAPABILITY_VALIDATED)
  }

  private fun handleIntent(intent: Intent) {
    val action = intent.action
    val data = intent.data

    when {
      (Intent.ACTION_VIEW == action && data != null &&
              (data.scheme == "file" || data.scheme == "content")) -> {

        val text = readTextFromUri(data)
        Toast.makeText(this, "File content:\n$text", Toast.LENGTH_LONG).show()
      }

      (Intent.ACTION_MAIN == action) -> {
        Toast.makeText(this, "By launcher", Toast.LENGTH_SHORT).show()
      }

      else -> {
        Toast.makeText(this, "Unknown launch type", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun readTextFromUri(uri: Uri): String {
    return try {
      contentResolver.openInputStream(uri)?.use { input ->
        input.bufferedReader().use { it.readText() }
      } ?: "Empty file"
    } catch (e: Exception) {
      "Error reading file: ${e.message}"
    }
  }

  fun encryptData(data: String): ByteArray {
    val key = Base64.decode("OVlkoULIqz8xvWNXmIUd1DOmJbaPw5uDiB0uynb7MQA=", Base64.DEFAULT)
    val secretKey = SecretKeySpec(key, "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
  }

  fun decryptData(encryptedData: ByteArray): String {
    val key = Base64.decode("OVlkoULIqz8xvWNXmIUd1DOmJbaPw5uDiB0uynb7MQA=", Base64.DEFAULT)
    val secretKey = SecretKeySpec(key, "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val decryptedBytes = cipher.doFinal(encryptedData)
    return String(decryptedBytes, Charsets.UTF_8)
  }

  fun saveToCache(encryptedData: ByteArray, fileName: String) {
    val file = File(cacheDir, fileName)
    file.writeBytes(encryptedData)
  }
}