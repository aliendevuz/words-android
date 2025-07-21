package uz.alien.dictup.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.GravityCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import uz.alien.dictup.R
import uz.alien.dictup.adapter.AdapterBookBeginner
import uz.alien.dictup.adapter.AdapterBookEssential
import uz.alien.dictup.databinding.ActivityMainBinding
import uz.alien.dictup.databinding.ScreenHomeBinding
import uz.alien.dictup.databinding.ScreenWelcomeBinding
import uz.alien.dictup.manager.PrefsManager
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_EN
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_STORY
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_UZ
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_EN
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_STORY
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_UZ
import uz.alien.dictup.manager.PrefsManager.Companion.IS_FIRST_TIME
import uz.alien.dictup.manager.ReferralManager
import uz.alien.dictup.receiver.ConnectionReceiver
import uz.alien.dictup.receiver.ConnectionReceiver.Companion.isConnected
import uz.alien.dictup.utils.AutoLayoutManager
import uz.alien.dictup.utils.Book
import uz.alien.dictup.utils.MarginItemDecoration
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.SecureRandom

class MainActivity : BaseActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var mainBinding: ScreenHomeBinding

  private var welcomeBinding: ScreenWelcomeBinding? = null
  private var isSearchBarVisible = false

  override fun onReady(savedInstanceState: Bundle?) {
    lockDrawer()

    binding = ActivityMainBinding.inflate(layoutInflater)
    mainBinding = ScreenHomeBinding.inflate(layoutInflater)

    handleIntent(intent)

    setClearEdge()

    setContentLayout {
      binding.root
    }

    onBackPressedDispatcher.addCallback(this) {
      if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
        getDrawerLayout().closeDrawer(GravityCompat.START)
      } else if (isSearchBarVisible) {
        hideSearchBar()
      } else {
        if (isEnabled) {
          remove()
          onBackPressedDispatcher.onBackPressed()
        }
      }
    }

    // TODO: We can use notification to make our app more useful. How?
    //  Everyday we send notification for more practicing
    //  Sometimes send youtube English podcast video for listening skills

    setSystemPadding(binding.root)

    // TODO: integrity qismi hali to'liq yaxshilanmagan, so'rovni yuborishini optimallashtirish kerak
    // Automatically verifying play integrity
    prepareIntegrity()

    // TODO: UTM uchun so'rov yuborishni tashkil qilish kerak
    ReferralManager.fetchReferral(this)

    // TODO: Remote config ni sozlash

    val remoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
      minimumFetchIntervalInSeconds = 3600
    }
    remoteConfig.setConfigSettingsAsync(configSettings)

    // boshqa narsa bu
    Firebase.messaging.token.addOnCompleteListener { task ->
      if (task.isSuccessful) Log.d("FCM", "Token: ${task.result}")
    }

    // TODO: Remote config sinovi
    remoteConfig.fetchAndActivate()
      .addOnSuccessListener {
        Log.d("RemoteConfig", remoteConfig.getBoolean("ads_available").toString())
        Log.d("RemoteConfig", remoteConfig.getString("developer"))
        Log.d("RemoteConfig", remoteConfig.getString("developer_contact"))
        Log.d("RemoteConfig", remoteConfig.getLong("frequency_of_ads").toString())
        Log.d("RemoteConfig", remoteConfig.getString("instagram"))
        Log.d("RemoteConfig", remoteConfig.getBoolean("referral_available").toString())
        Log.d("RemoteConfig", remoteConfig.getString("telegram"))
        Log.d("RemoteConfig", remoteConfig.getLong("valid_referrals_amount").toString())
        Log.d("RemoteConfig", remoteConfig.getString("website"))
      }

    requestNotificationPermission(this)

    // TODO: app update API ni ham ulab qo'yishim kerak

    if (PrefsManager.getInstance(this).getBool(IS_FIRST_TIME, true)) {
      PrefsManager.getInstance(this).saveBool(IS_FIRST_TIME, false)

      welcomeBinding = ScreenWelcomeBinding.inflate(layoutInflater)

      binding.root.addView(welcomeBinding?.root)

      startWelcome()
    } else {
      setHomeScreen()
    }
  }

  fun requestNotificationPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
          activity,
          arrayOf(Manifest.permission.POST_NOTIFICATIONS),
          1001
        )
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 1001) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.d("@@@@", "notificationga ruxsat berildi")
      } else {
        Log.d("@@@@", "notificationga ruxsat rad etildi")
      }
    }
  }

  fun prepareIntegrity() {
    val standardIntegrityManager = IntegrityManagerFactory.createStandard(applicationContext)
    val cloudProjectNumber = 968111631764L

    standardIntegrityManager.prepareIntegrityToken(
      StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
        .setCloudProjectNumber(cloudProjectNumber)
        .build()
    )
      .addOnSuccessListener { tokenProvider ->
        integrityTokenProvider = tokenProvider
      }
      .addOnFailureListener { exception ->
        Log.d("@@@@ Error", exception.message.toString())
      }
  }

  suspend fun verifyIntegrity(requestData: ByteArray) {
    // 1) Provider tayyorligini kutish
    val provider = integrityTokenProvider

    // 2) requestHash tayyorlash (SHA‑256 + random + timestamp -> 32+24 = 56 bayt < 500)
    val requestHash = buildRequestHash(requestData).toString()

    // 3) Token so‘rash

    provider?.request(
      StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
        .setRequestHash(requestHash)   // <-- yagona maydon!
        .build()
    )?.addOnSuccessListener { response ->
      sendTokenToServer(response.token())
    }?.addOnFailureListener {
      Log.d("@@@@ Error", "Tokenni olib bo'lmadi")
    }
  }

  /** Google tavsiya qilgan usul: foydali ma’lumot hash + random + timestamp */
  private fun buildRequestHash(requestData: ByteArray): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256").digest(requestData)   // 32 bayt
    val random = ByteArray(8).also { SecureRandom().nextBytes(it) }         // 8 bayt
    val time   = ByteBuffer.allocate(Long.SIZE_BYTES)
      .putLong(System.currentTimeMillis()).array()                        // 8 bayt
    return digest + random + time                                           // 48 bayt
  }

  private fun hideSearchBar() {
    // Animatsiya bilan yo’q qilish, holatni o‘zgartirish va hokazo
    isSearchBarVisible = false
    // masalan:
    // binding.searchBar.visibility = View.GONE
  }

  fun setHomeScreen() {

    unlockDrawer()

    clearPadding(binding.root)

    binding.root.removeAllViews()

    binding.root.addView(mainBinding.root)

    setSystemPadding(mainBinding.statusBarPadding)

    mainBinding.root.startAnimation(AnimationSet(false).apply {
      addAnimation(AlphaAnimation(0.0f, 1.0f))
      duration = this@MainActivity.duration * 3
    })

    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    registerReceiver(ConnectionReceiver {
      downloadAndSaveFiles(this, "beginner_en", BEGINNER_EN) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }
      downloadAndSaveFiles(this, "beginner_uz", BEGINNER_UZ) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }
      downloadAndSaveFiles(this, "beginner_story", BEGINNER_STORY) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }

      downloadAndSaveFiles(this, "essential_en", ESSENTIAL_EN) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }
      downloadAndSaveFiles(this, "essential_uz", ESSENTIAL_UZ) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }
      downloadAndSaveFiles(this, "essential_story", ESSENTIAL_STORY) {
        Log.d("Network", "Updated!")
        runOnUiThread {
          Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
      }
    }, filter)

    val beginnerBooks = arrayListOf(
      Book(R.color.blue2, R.drawable.v_image),
      Book(R.color.purple2, R.drawable.v_image),
      Book(R.color.red_pink, R.drawable.v_image),
      Book(R.color.pink, R.drawable.v_image)
    )

    val essentialBooks = arrayListOf(
      Book(R.color.book1, R.drawable.book_0),
      Book(R.color.book2, R.drawable.book_1),
      Book(R.color.book3, R.drawable.book_2),
      Book(R.color.book4, R.drawable.book_3),
      Book(R.color.book5, R.drawable.book_4),
      Book(R.color.book6, R.drawable.book_5)
    )

    mainBinding.rvBeginner.layoutManager = AutoLayoutManager(this, 4)
    mainBinding.rvBeginner.addItemDecoration(MarginItemDecoration((4 * dp()).toInt(), 4))
    mainBinding.rvBeginner.adapter = AdapterBookBeginner(beginnerBooks, this)

    mainBinding.rvEssential.layoutManager = AutoLayoutManager(this, 3)
    mainBinding.rvEssential.addItemDecoration(MarginItemDecoration((4 * dp()).toInt(), 3))
    mainBinding.rvEssential.adapter = AdapterBookEssential(essentialBooks, this)

    mainBinding.bOpenSelector.setOnClickListener {
      val intent = Intent(this, SelectorActivity::class.java)
      intent.putExtra("picked_book", 2)
      intent.putExtra("amount_of_book", 6)
      setOpenSwipeAnimation(intent)
    }

    mainBinding.bOpenSelector.setOnLongClickListener {
      runBlocking {
        launch {
          verifyIntegrity("salom".toByteArray())
        }
      }
      true
    }

    mainBinding.drawerButton.setOnClickListener {
      getDrawerLayout().openDrawer(GravityCompat.START)
    }
  }

  companion object {

    var integrityTokenProvider: StandardIntegrityManager.StandardIntegrityTokenProvider? = null
    private const val CLOUD_PROJECT = 968111631764L     // <‑‑ o'zingizning CP#

    private val BASE_URL = "https://api.github.com/repos/xalilovdev/wd/contents/src"
    private val TOKEN = "22"

    private val client = OkHttpClient()

    private fun sendTokenToServer(token: String) {
      Log.d("Token", token)
      val url = "https://h5gfn42tf4.execute-api.eu-central-1.amazonaws.com/verify"
      val json = """{"token": "$token"}"""

      val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
      val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

      CoroutineScope(Dispatchers.IO).launch {
        try {
          client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
              Log.e("HTTPError", "Unexpected code $response")
            } else {
              Log.d("HTTPResponse", response.body?.string() ?: "No response body")
            }
          }
        } catch (e: IOException) {
          Log.e("HTTPError", "Error sending token: ${e.message}")
        }
      }
    }

    private fun fetchFile(fileName: String, onResult: (String?) -> Unit) {
      val request = Request.Builder()
        .url("$BASE_URL/$fileName")
        .addHeader("Authorization", "token $TOKEN")
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

    private fun generateNonce(): String {
      val random = ByteArray(16).also { SecureRandom().nextBytes(it) }
      val time   = ByteBuffer.allocate(Long.SIZE_BYTES)
        .putLong(System.currentTimeMillis()).array()
      val nonceBytes = random + time
      return Base64.encodeToString(nonceBytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun downloadAndSaveFiles(context: Context, fileName: String, version: String, runnable: Runnable? = null): Boolean {
      fetchFile(fileName) {
        it?.let { v ->
          if (v.toInt() > PrefsManager.getInstance(context).getInt(version)) {
            fetchFile("$fileName.json") {
              it?.let { content ->
                val dir = File(context.filesDir, "src")
                if (!dir.exists()) dir.mkdirs()
                File(dir, "$fileName.json").writeText(content)
                PrefsManager.getInstance(context).saveInt(version, v.toInt())
                runnable?.run()
              }
            }
          }
        }
      }
      return true
    }

    fun showInitialDialog(context: Context) {
      AlertDialog.Builder(context)
        .setTitle("Ma'lumotlar kerak")
        .setMessage("So'zlar hali mavjud emas. Internetga ulanib yuklab olish uchun ruxsat bering.")
        .setCancelable(false)
        .setPositiveButton("OK") { _, _ ->
          if (isConnected(context)) {
            val success = downloadAndSaveFiles(context, "beginner_en", BEGINNER_EN) &&
                    downloadAndSaveFiles(context, "beginner_uz", BEGINNER_UZ) &&
                    downloadAndSaveFiles(context, "beginner_story", BEGINNER_STORY) &&
                    downloadAndSaveFiles(context, "essential_en", ESSENTIAL_EN) &&
                    downloadAndSaveFiles(context, "essential_uz", ESSENTIAL_UZ) &&
                    downloadAndSaveFiles(context, "essential_story", ESSENTIAL_STORY)
            if (!success) showInitialDialog(context)
          } else {
            showInitialDialog(context)
          }
        }
        .show()
    }
  }

  private fun startWelcome() {

    welcomeBinding?.let {

      it.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {

          it.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

          var margin = 0.0f

          it.lWelcomeLogoBackground.y += margin

          val layoutParams = it.lWelcomeLogoBackground.layoutParams as ViewGroup.MarginLayoutParams
          layoutParams.setMargins(0, 0, 0, 0)
          it.lWelcomeLogoBackground.layoutParams = layoutParams

          it.ivWelcomeLogo1.alpha = 0.0f
          it.ivWelcomeLogo2.alpha = 0.0f
          it.ivWelcomeLogo3.alpha = 0.0f

          it.tvWelcomeBrand.alpha = 0.0f
          it.tvWelcomeProduct.alpha = 0.0f

          handler.postDelayed(duration) {

            it.lWelcomeLogoBackground.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            margin = it.root.height.toFloat() / 6


            handler.postDelayed(duration * 2) {

              it.ivWelcomeLogo1.alpha = 1.0f
              it.ivWelcomeLogo1.startAnimation(AnimationSet(true).apply {
                addAnimation(AlphaAnimation(0f, 1f))
                this.duration = 300L
                this.interpolator = LinearInterpolator()
              })

              handler.postDelayed(1000L) {
                it.ivWelcomeLogo2.alpha = 1.0f
                it.ivWelcomeLogo2.startAnimation(AnimationSet(true).apply {
                  addAnimation(AlphaAnimation(0f, 1f))
                  this.duration = 300L
                  this.interpolator = LinearInterpolator()
                })

                handler.postDelayed(1000L) {
                  it.ivWelcomeLogo3.alpha = 1.0f
                  it.ivWelcomeLogo3.startAnimation(AnimationSet(true).apply {
                    addAnimation(AlphaAnimation(0f, 1f))
                    this.duration = 300L
                    this.interpolator = LinearInterpolator()
                  })

                  handler.postDelayed(1000L) {
                    it.lWelcomeLogoBackground.y -= margin
                    it.lWelcomeLogoBackground.startAnimation(AnimationSet(true).apply {
                      addAnimation(
                        TranslateAnimation(
                          0.0f,
                          0.0f,
                          margin,
                          0.0f
                        )
                      )
                      this.duration = 600L
                      this.interpolator = AccelerateDecelerateInterpolator()
                    })

                    handler.postDelayed(1600L) {
                      it.tvWelcomeBrand.alpha = 1.0f
                      it.tvWelcomeBrand.startAnimation(AnimationSet(true).apply {
                        addAnimation(AlphaAnimation(0f, 1f))
                        this.duration = 300L
                        this.interpolator = LinearInterpolator()
                      })

                      handler.postDelayed(1000L) {
                        it.tvWelcomeProduct.alpha = 1.0f
                        it.tvWelcomeProduct.startAnimation(AnimationSet(true).apply {
                          addAnimation(AlphaAnimation(0f, 1f))
                          this.duration = 300L
                          this.interpolator = LinearInterpolator()
                        })

                        handler.postDelayed(2000L) {

                          it.root.startAnimation(AnimationSet(false).apply {
                            addAnimation(AlphaAnimation(1.0f, 0.0f))
                            duration = this@MainActivity.duration / 2
                          })

                          handler.postDelayed(duration / 2) {

                            setHomeScreen()
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      })
    }
  }
}