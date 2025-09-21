package uz.alien.dictup.presentation.common.extention

import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import uz.alien.dictup.databinding.BaseDialogBinding
import androidx.core.graphics.drawable.toDrawable
import uz.alien.dictup.R
import uz.alien.dictup.utils.Logger
import java.util.WeakHashMap

private val dialogBindingMap = WeakHashMap<AppCompatActivity, BaseDialogBinding?>()

var AppCompatActivity.dialogBinding: BaseDialogBinding?
    get() = dialogBindingMap[this]
    set(value) {
        dialogBindingMap[this] = value
    }

private val dialogMap = WeakHashMap<AppCompatActivity, AlertDialog?>()

var AppCompatActivity.dialog: AlertDialog?
    get() = dialogMap[this]
    set(value) {
        dialogMap[this] = value
    }

fun AppCompatActivity.initDialog(): AlertDialog {
    val dialog = AlertDialog.Builder(this)
        .setView(dialogBinding?.root)
        .setCancelable(false)
        .create()

    dialog.setOnShowListener {
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(
            (this.resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.DialogScaleAnimation
    }

    dialogBinding?.tbOk?.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun AppCompatActivity.prepareDialogForFirstTime() {

    this.dialogBinding = BaseDialogBinding.inflate(layoutInflater)
    this.dialog = initDialog()

    dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog?.window?.setDimAmount(0f)

    dialog?.window?.decorView?.alpha = 0f
    dialog?.show()
    Handler(mainLooper).post {
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog?.let {
            it.window?.setLayout(
                (it.context.resources.displayMetrics.widthPixels * 0.85).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        dialog?.window?.attributes?.windowAnimations = R.style.DialogScaleAnimation
        dialog?.dismiss()

        dialog?.window?.decorView?.alpha = 1f
        dialog?.window?.setDimAmount(0.5f)
    }
}

fun AppCompatActivity.showNoInternet() {

    dialog?.let {
        if (it.isShowing) {
            it.dismiss()
        }
    }

    dialogBinding?.progressMessage?.text = "So'zlarni yuklab olish uchun internetni yoqing!"
    dialogBinding?.iProgress?.visibility = View.GONE
    dialogBinding?.iProgress?.progress = 0
    dialogBinding?.tbOk?.visibility = View.VISIBLE
    dialog?.show()
}

fun AppCompatActivity.showStarting(message: String) {

    dialog?.let {
        if (it.isShowing) {
            it.dismiss()
        }
    }

    dialogBinding?.iProgress?.visibility = View.VISIBLE
    dialogBinding?.progressMessage?.text = message
    dialogBinding?.tbOk?.visibility = View.GONE
    dialog?.show()
}


fun AppCompatActivity.updateProgressDialog(message: String, step: Int, total: Int) {
    dialogBinding?.iProgress?.progress = step * 100 / total
    dialogBinding?.progressMessage?.text = message
}

fun AppCompatActivity.showError(message: String) {

    dialogBinding?.progressMessage?.text = message
    dialogBinding?.tbOk?.visibility = View.VISIBLE
    dialogBinding?.iProgress?.visibility = View.GONE
    dialog?.show()
}

fun AppCompatActivity.hideDialog() {
    dialog?.dismiss()
}

fun AppCompatActivity.destroyDialog() {

    dialog?.dismiss()
    dialog = null
    dialogBinding = null
}