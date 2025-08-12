package uz.alien.dictup.presentation.features.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.databinding.HomeActivityBinding
import uz.alien.dictup.databinding.HomeDialogBinding
import uz.alien.dictup.presentation.common.component.AutoLayoutManager
import uz.alien.dictup.presentation.common.component.MarginItemDecoration
import uz.alien.dictup.presentation.common.extention.getSystemStatusPadding
import uz.alien.dictup.presentation.common.extention.setAlphaAnimationOnly
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setOpenZoomAnimation
import uz.alien.dictup.presentation.common.extention.startWithSwipeAnimation
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.home.extention.handleIntent
import uz.alien.dictup.presentation.features.home.extention.initDialog
import uz.alien.dictup.presentation.features.home.extention.prepareDialogForFirstTime
import uz.alien.dictup.presentation.features.home.extention.showNoInternet
import uz.alien.dictup.presentation.features.home.extention.showStarting
import uz.alien.dictup.presentation.features.home.recycler.BeginnerBookAdapter
import uz.alien.dictup.presentation.features.home.recycler.EssentialBookAdapter
import uz.alien.dictup.presentation.features.pick.PickActivity
import uz.alien.dictup.presentation.features.select.SelectActivity
import uz.alien.dictup.presentation.features.welcome.WelcomeActivity
import uz.alien.dictup.shared.WordCollection

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: HomeActivityBinding
    private lateinit var dialogBinding: HomeDialogBinding
    private lateinit var dialog: AlertDialog

    private var isSearchBarVisible = false
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var beginnerBookAdapter: BeginnerBookAdapter
    private lateinit var essentialBookAdapter: EssentialBookAdapter

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setClearEdge()

        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        handleIntent(intent)

        handleBackPressForSearching()

        getSystemStatusPadding(binding.root) { statusPadding ->
            setStatusPadding(statusPadding)
            binding.statusBarPadding.setPadding(0, getStatusPadding(), 0, 0)
        }

        binding.statusBarPadding.setPadding(0, getStatusPadding(), 0, 0)

        // boshqa narsa bu
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) Log.d("FCM", "Token: ${task.result}")
        }

        // TODO: app update API ni ham ulab qo'yishim kerak

        if (isFirstTime()) {

            setFirstTimeFalse()

            val intent = Intent(this, WelcomeActivity::class.java)

            welcomeLauncher.launch(intent)

            setAlphaAnimationOnly()

        } else {

            dialogBinding = HomeDialogBinding.inflate(layoutInflater)

            dialog = initDialog(this, dialogBinding)

            prepareDialogForFirstTime(dialog)

            collectSyncStatus()
        }

        initViews()

        collectBooks()
    }

    private val welcomeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {

            dialogBinding = HomeDialogBinding.inflate(layoutInflater)

            dialog = initDialog(this, dialogBinding)

            prepareDialogForFirstTime(dialog)

            collectSyncStatus()
        }
    }

    private fun isFirstTime(): Boolean {
        return prefs.getBoolean("first_time", true)
    }

    private fun setFirstTimeFalse() {
        prefs.edit {
            putBoolean("first_time", false)
        }
    }

    private fun getStatusPadding(): Int {
        return prefs.getInt("status_padding", 45)
    }

    private fun setStatusPadding(padding: Int) {
        if (padding != 0) {
            prefs.edit {
                putInt("status_padding", padding)
            }
        } else {
            Logger.w("Status padding is $padding")
        }
    }

    private fun handleBackPressForSearching() {

        onBackPressedDispatcher.addCallback(this) {
            if (isDrawerOpen()) {
                closeDrawer()
            } else if (isSearchBarVisible) {
                hideSearchBar()
            } else {
                if (isEnabled) {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
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

    private fun hideSearchBar() {
        // Animatsiya bilan yo’q qilish, holatni o‘zgartirish va hokazo
        isSearchBarVisible = false
        // masalan:
        // binding.searchBar.visibility = View.GONE
    }

    private fun initViews() {

        beginnerBookAdapter = BeginnerBookAdapter { part ->
//            if (viewModel.syncManager.shouldShowProgress.value) {
//                viewModel.askForNoInternet()
//            } else {
                val intent = Intent(this, PickActivity::class.java)
                intent.putExtra("collection", WordCollection.BEGINNER.id)
                intent.putExtra("part", part.id)
                setOpenZoomAnimation(intent)
//            }
        }

        essentialBookAdapter = EssentialBookAdapter { part ->
//            if (viewModel.syncManager.shouldShowProgress.value) {
//                viewModel.askForNoInternet()
//            } else {
                val intent = Intent(this, PickActivity::class.java)
                intent.putExtra("collection", WordCollection.ESSENTIAL.id)
                intent.putExtra("part", part.id)
                setOpenZoomAnimation(intent)
//            }
        }

        binding.rvBeginner.layoutManager = AutoLayoutManager(this, 4)
        binding.rvBeginner.addItemDecoration(MarginItemDecoration(4.0f, resources, 4))
        (binding.rvBeginner.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        binding.rvBeginner.adapter = beginnerBookAdapter

        binding.rvEssential.layoutManager = AutoLayoutManager(this, 3)
        binding.rvEssential.addItemDecoration(MarginItemDecoration(4.0f, resources, 3))
        (binding.rvEssential.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        binding.rvEssential.adapter = essentialBookAdapter

        binding.bOpenSelector.setOnClickListener {
//            if (viewModel.syncManager.shouldShowProgress.value) {
//                viewModel.askForNoInternet()
//            } else {
                val intent = Intent(this, SelectActivity::class.java)
                startWithSwipeAnimation(intent)
//            }
        }

        binding.drawerButton.setOnClickListener {
            openDrawer()
        }
    }

    private fun collectBooks() {

        lifecycleScope.launch {
            viewModel.beginnerBooksState.collectLatest { books ->
                beginnerBookAdapter.submitList(books)
            }
        }

        lifecycleScope.launch {
            viewModel.essentialBooksState.collectLatest { books ->
                essentialBookAdapter.submitList(books)
            }
        }
    }

    private fun collectSyncStatus() {

        lifecycleScope.launch {
            viewModel.dataStoreRepository.isSyncCompleted().collectLatest { isCompleted ->
                if (isCompleted) {
                    viewModel.updateBook()
                    dialog.dismiss()
                    return@collectLatest
                } else {
                    if (isConnected(this@HomeActivity)) {
                        showStarting(
                            "Yuklab olish jarayoni davom etmoqda...",
                            dialogBinding,
                            dialog
                        )
                    } else {
                        showNoInternet(dialogBinding, dialog)
                    }
                }
            }
        }
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nw) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}