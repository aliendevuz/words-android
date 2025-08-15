package uz.alien.dictup.presentation.common.extention

import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import uz.alien.dictup.R

fun AppCompatActivity.startActivityWithAlphaAnimation(intent: Intent? = null) {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.fade_in,
        R.anim.fade_out
    )
    intent?.let { startActivity(it, options.toBundle()) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.fade_in,
            R.anim.fade_out
        )
    } else {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}

fun AppCompatActivity.overrideTransitionWithAlpha() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.fade_in,
            R.anim.fade_out
        )
    } else {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}

fun AppCompatActivity.startActivityWithSlideAnimation(intent: Intent? = null) {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.slide_in_right,
        R.anim.slide_out_left
    )
    intent?.let { startActivity(it, options.toBundle()) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    } else {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}

fun AppCompatActivity.applyExitSwipeAnimation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    } else {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

fun AppCompatActivity.startActivityWithZoomAnimation(intent: Intent? = null) {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.zoom_in_out,
        R.anim.zoom_out_in
    )
    intent?.let { startActivity(it, options.toBundle()) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.zoom_in_out,
            R.anim.zoom_out_in
        )
    } else {
        overridePendingTransition(R.anim.zoom_in_out, R.anim.zoom_out_in)
    }
}

fun AppCompatActivity.applyExitZoomTransition() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.zoom_in_in,
            R.anim.zoom_out_out
        )
    } else {
        overridePendingTransition(R.anim.zoom_in_in, R.anim.zoom_out_out)
    }
}

fun AppCompatActivity.applyExitZoomReverseTransition() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.zoom_out_out_reverse,
            R.anim.zoom_in_in_reverse
        )
    } else {
        overridePendingTransition(R.anim.zoom_out_out_reverse, R.anim.zoom_in_in_reverse)
    }
}
