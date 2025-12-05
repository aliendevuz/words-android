package uz.alien.dictup.presentation.features.home

import android.Manifest
import android.app.ComponentCaller
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.alien.dictup.App
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.databinding.HomeActivityBinding
import uz.alien.dictup.domain.model.WordCollection
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.extention.overrideTransitionWithAlpha
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.about.AboutActivity
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.home.recycler.BeginnerBookAdapter
import uz.alien.dictup.presentation.features.home.recycler.EssentialBookAdapter
import uz.alien.dictup.presentation.features.more.MoreActivity
import uz.alien.dictup.presentation.features.pick.PickActivity
import uz.alien.dictup.presentation.features.select.SelectActivity
import uz.alien.dictup.presentation.features.setting.SettingActivity
import uz.alien.dictup.presentation.features.statistics.StatisticsActivity
import uz.alien.dictup.presentation.features.welcome.WelcomeActivity
import uz.alien.dictup.utils.Logger

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: HomeActivityBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var beginnerBookAdapter: BeginnerBookAdapter
    private lateinit var essentialBookAdapter: EssentialBookAdapter

    private lateinit var appUpdateManager: AppUpdateManager
    private val UPDATE_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setClearEdge()

        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        if (viewModel.isFirstTime()) {

            viewModel.setFirstTimeFalse()

            val intent = Intent(this, WelcomeActivity::class.java)

            welcomeLauncher.launch(intent)

            overrideTransitionWithAlpha()
        }

        initViews()

        collectBooks()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        checkForUpdate()

        handleIntent(intent)

        // TODO: I have rewarded ad ID - ca-app-pub-7031957988362944/4534925817
    }

    private fun checkForUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlow(
                    appUpdateInfo,
                    this,
                    AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
                )
            }
        }
    }

    private fun askToPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            Logger.d("Update", "Update bekor qilindi.")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateData()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun handleIntent(intent: Intent) {

        binding.root.postDelayed(BuildConfig.DURATION) {

            val action = intent.action
            val data = intent.data

            when {
//            (Intent.ACTION_VIEW == action && data != null &&
//                    (data.scheme == "file" || data.scheme == "content")) -> {
//
//                val text = readTextFromUri(data)
//                Logger.d("Opening Method", "File content:\n$text")
//            }

                (Intent.ACTION_VIEW == action && data != null &&
                        (data.scheme == "http"
                                || data.scheme == "https"
                                || data.scheme == "app"
                                )
                        ) -> {
                    val path = data.path ?: ""
                    when {
                        path.startsWith("/lesson") -> openLesson(data)
                        path.startsWith("/next") -> openNextLesson()
                        path.startsWith("/test") -> openTest()
                        path.startsWith("/settings") -> openSettings(data)
                        path.startsWith("/share") -> shareApp()
                        path.startsWith("/rate") -> openPlayStore()
                        path.startsWith("/statistics") -> openStatistics()
                        path.startsWith("/more_apps") -> openMoreApps()
                        path.startsWith("/donate") -> openDonate(data)
                        path.startsWith("/about") -> openAbout()
                        else -> Logger.d("Opening Method", "Unknown path: $path")
                    }
                }

                (Intent.ACTION_MAIN == action) -> {
                    Logger.d("Opening Method", "By launcher")
                }

                else -> {
                    Logger.d("Opening Method", "Unknown launch type")
                }
            }
        }
    }

    private fun openLesson(data: Uri?) {

        var collection = data?.intParam("c", -1)
        var part = data?.intParam("p", -1)
        var unit = data?.intParam("u", -1)
        var sn = data?.intParam("sn", 0)

        if (collection == null || part == null || unit == null || sn == null) return
        if (collection == -1) return

        if (collection !in 0..1) collection = 0

        if (collection == 0) {
            if (part !in 0..3) part = -1
            if (unit !in 0..19) unit = -1
        } else {
            if (part !in 0..5) part = -1
            if (unit !in 0..29) unit = -1
        }

        if (part == -1 || unit == -1) return

        if (sn !in 0..2) sn = 0

        val intent = Intent(this, PickActivity::class.java)
        intent.putExtra("collection", collection)
        intent.putExtra("part", part)
        intent.putExtra("unit", unit)
        intent.putExtra("sn", sn)
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openDonate(data: Uri?) {

        val intent = Intent(Intent.ACTION_VIEW, data).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        // Browser paketlarini tanlash
        val browsers = packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }

        if (browsers.isNotEmpty()) {
            val browserPackage = browsers.first().activityInfo.packageName
            intent.setPackage(browserPackage)
            startActivity(intent)
        } else {
            Toast.makeText(this, "No browser found to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMoreApps() {
        val intent = Intent(this, MoreActivity::class.java)
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openStatistics() {
        val intent = Intent(this, StatisticsActivity::class.java)
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openPlayStore() {
        val appPackageName = packageName // shu ilovaning packageId si
        try {
            val intent =
                Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun shareApp() {
        val shareText = "https://play.google.com/store/apps/details?id=$packageName"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Do'stlaringiz bilan ulashing")
        startActivity(shareIntent)
    }

    private fun openSettings(data: Uri?) {
        val intent = Intent(this, SettingActivity::class.java)
        intent.putParcelableArrayListExtra("data", arrayListOf(data))
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openTest() {
        val intent = Intent(this, SelectActivity::class.java)
        baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
    }

    private fun openNextLesson() {
        lifecycleScope.launch {

            val lastCollection = viewModel.getLastCollection()

            // Helper: progress to‘liq bo‘lmagan collectionni aniqlash
            suspend fun getNextIncompleteCollection(collectionId: Int): Int? {
                val state = when (collectionId) {
                    WordCollection.BEGINNER.id -> viewModel.beginnerBooksState.first()
                    WordCollection.ESSENTIAL.id -> viewModel.essentialBooksState.first()
                    else -> return null
                }

                // Agar progress 100% bo‘lmagan bo‘lsa shu collectionni qaytaramiz
                return if (state.any { it.progress < 100 }) collectionId else null
            }

            val nextCollection = getNextIncompleteCollection(lastCollection)
                ?: getNextIncompleteCollection(
                    if (lastCollection == WordCollection.BEGINNER.id) WordCollection.ESSENTIAL.id
                    else WordCollection.BEGINNER.id
                )

            if (nextCollection != null) {
                val intent = Intent(this@HomeActivity, PickActivity::class.java).apply {
                    //                    val lastPart = viewModel.getLastPart()
                    val lastPart = if (nextCollection == 0 && viewModel.getLastPart() > 3) {
                        0
                    } else {
                        viewModel.getLastPart()
                    }
                    putExtra("collection", nextCollection)
                    putExtra("part", lastPart)
                    putExtra("auto_open", true)
                }
                baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
            } else {
                // Ikkalasi ham 100% bo‘lsa → Test sahifasiga yo‘naltirish
                Toast.makeText(
                    this@HomeActivity,
                    "Siz barcha darslarni tugatdingiz 🎉",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@HomeActivity, SelectActivity::class.java)
                baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
            }
        }
    }

    fun Uri?.intParam(name: String, default: Int = 0): Int =
        this?.getQueryParameter(name)?.toIntOrNull() ?: default

    private val welcomeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        binding.root.postDelayed(BuildConfig.DURATION) {
            askToPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: We can use notification to make our app more useful. How?
                //  Everyday we send notification for more practicing

                // TODO: bu yerda workerni ishga tushirishni sozlash mumkin
                Logger.d("@@@@", "notificationga ruxsat berildi")
            } else {
                Logger.d("@@@@", "notificationga ruxsat rad etildi")
            }
        }
    }

    private fun initViews() {

        beginnerBookAdapter = BeginnerBookAdapter { part ->

            val intent = Intent(this, PickActivity::class.java)
            intent.putExtra("collection", WordCollection.BEGINNER.id)
            intent.putExtra("part", part.id)
            viewModel.saveLastCollection(WordCollection.BEGINNER.id)
            baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
        }

        essentialBookAdapter = EssentialBookAdapter { part ->

            val intent = Intent(this, PickActivity::class.java)
            intent.putExtra("collection", WordCollection.ESSENTIAL.id)
            intent.putExtra("part", part.id)
            viewModel.saveLastCollection(WordCollection.ESSENTIAL.id)
            baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
        }

        binding.rvBeginner.layoutManager = AutoLayoutManager(this, 4)
        binding.rvBeginner.itemAnimator = null
        binding.rvBeginner.adapter = beginnerBookAdapter

        binding.rvEssential.layoutManager = AutoLayoutManager(this, 3)
        binding.rvEssential.itemAnimator = null
        binding.rvEssential.adapter = essentialBookAdapter

        binding.bOpenSelector.setOnClickListener {

            val intent = Intent(this, SelectActivity::class.java)
            baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateBook()
            }
        }
    }

    private fun collectBooks() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.beginnerBooksState.collectLatest { books ->
                    beginnerBookAdapter.submitList(books)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.essentialBooksState.collectLatest { books ->
                    essentialBookAdapter.submitList(books)
                }
            }
        }
    }
}