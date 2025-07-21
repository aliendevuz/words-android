package uz.alien.test.words

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import uz.alien.test.databinding.ActivityWordsBinding
import uz.alien.test.words.PrefsManager.Companion.SRC_VERSION
import java.io.File
import java.io.IOException

class WordsActivity : AppCompatActivity() {

  private lateinit var binding: ActivityWordsBinding

  private val BASE_URL = "https://d19jnt393legne.cloudfront.net/assets/src"

  private val client = OkHttpClient()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityWordsBinding.inflate(layoutInflater)

    setContentView(binding.root)

//    WordDownloader.fetchFile2("version") {
//      Log.d("@@@@", it.toString())
//    }

    if (PrefsManager.getInstance(this).getInt(PrefsManager.SRC_VERSION) == 0) {
      downloadAndSaveFiles(this)
    } else {
      AppDictionary.initialize(this)

      binding.rv.layoutManager = LinearLayoutManager(this)
      binding.rv.adapter = AdapterWords(AppDictionary.uzWords, AppDictionary.enWords)
    }
  }

//  private fun showInitialDialog(context: Context, onReady: () -> Unit) {
//    AlertDialog.Builder(context)
//      .setTitle("Ma'lumotlar kerak")
//      .setMessage("So'zlar hali mavjud emas. Internetga ulanib yuklab olish uchun ruxsat bering.")
//      .setCancelable(false)
//      .setPositiveButton("OK") { _, _ ->
//        if (isConnected(context)) {
//          val success = downloadAndSaveFiles(context)
//          if (success) onReady() else showInitialDialog(context, onReady)
//        } else {
//          showInitialDialog(context, onReady)
//        }
//      }
//      .show()
//  }

  fun fetchFile2(fileName: String, onResult: (String?) -> Unit) {
    val request = Request.Builder()
      .url("$BASE_URL/$fileName")
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

  fun downloadAndSaveFiles(context: Context): Boolean {
    fetchFile2("essential_en.json") {
      it?.let { version ->
//        if (it.toInt() > PrefsManager.getInstance(context).getInt(SRC_VERSION)) {

          fetchFile2("essential_en.json") {

            it?.let { en ->

              fetchFile2("essential_uz.json") {

                it?.let { uz ->

                  val dir = File(context.filesDir, "src")
                  if (!dir.exists()) dir.mkdirs()

                  File(dir, "essential_en.json").writeText(en)
                  File(dir, "essential_uz.json").writeText(uz)

                  Log.d("@@@@", en)
                  Log.d("@@@@", uz)

//                  PrefsManager.getInstance(context).saveInt(SRC_VERSION, version.toInt())
                }
              }
            }
          }
//        }
      }
    }
    return true
  }

  companion object {
    fun isConnected(context: Context): Boolean {
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val nw = cm.activeNetwork ?: return false
      val caps = cm.getNetworkCapabilities(nw) ?: return false
      return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
  }
}