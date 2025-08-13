package uz.alien.dictup.presentation.features.home.extention

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import uz.alien.dictup.databinding.HomeDialogBinding
import androidx.core.graphics.drawable.toDrawable
import uz.alien.dictup.R

fun initDialog(context: Context, binding: HomeDialogBinding): AlertDialog {
    val dialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    dialog.setOnShowListener {
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.DialogScaleAnimation
    }

    binding.tbOk.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun prepareDialogForFirstTime(dialog: AlertDialog) {

    dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window?.setDimAmount(0f)

    dialog.window?.decorView?.alpha = 0f
    dialog.show()
    Handler(Looper.getMainLooper()).post {
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(
            (dialog.context.resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.DialogScaleAnimation
        dialog.dismiss()

        dialog.window?.decorView?.alpha = 1f
        dialog.window?.setDimAmount(0.5f)
    }
}

fun showNoInternet(binding: HomeDialogBinding, dialog: AlertDialog) {

    if (dialog.isShowing) {
        dialog.dismiss()
    }

    binding.progressMessage.text = "So'zlarni yuklab olish uchun internetni yoqing!"
    binding.iProgress.visibility = View.GONE
    binding.iProgress.progress = 0
    binding.tbOk.visibility = View.VISIBLE
    dialog.show()
}

fun showStarting(message: String, binding: HomeDialogBinding, dialog: AlertDialog) {

    if (dialog.isShowing) {
        dialog.dismiss()
    }

    binding.iProgress.visibility = View.VISIBLE
    binding.progressMessage.text = message
    binding.tbOk.visibility = View.GONE
    dialog.show()
}


fun updateProgressDialog(message: String, step: Int, total: Int, binding: HomeDialogBinding) {
    binding.iProgress.progress = step * 100 / total
    binding.progressMessage.text = message
}

fun showError(message: String, binding: HomeDialogBinding, dialog: AlertDialog) {

    binding.progressMessage.text = message
    binding.tbOk.visibility = View.VISIBLE
    binding.iProgress.visibility = View.GONE
    dialog.show()
}