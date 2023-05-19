package cz.cvut.fel.pda.bee_calendar.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

object Vibrations {
    @Suppress("DEPRECATION")
    fun vibrate(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(
                    250, VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(250)
        }
    }
}