package com.dafay.demo.lib.base.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings

/**
 * @Des vibrator 管理类
 * @Author lipengfei
 * @Date 2024/1/22
 */
object VibratorManager {

    private const val TICK: Long = 13
    private const val CLICK: Long = 20
    private const val HEAVY: Long = 50

    private var vibrator: Vibrator? = null
    private var enabled: Boolean
    private val application: Application by lazy {
        ApplicationUtils.getApp()
    }

    init {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        enabled = hasVibrator()
    }

    fun vibrate(duration: Long) {
        if (!enabled) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator!!.vibrate(duration)
        }
    }

    fun vibrate(effectId: Int) {
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator!!.vibrate(VibrationEffect.createPredefined(effectId))
        }
    }

    fun vibrate(vibrationEffect: VibrationEffect) {
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator!!.vibrate(vibrationEffect)
        }
    }

    /**
     * 勾选
     */
    fun tick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.EFFECT_TICK)
        } else {
            vibrate(TICK)
        }
    }

    /**
     * 点击
     */
    fun click() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.EFFECT_CLICK)
        } else {
            vibrate(CLICK)
        }
    }

    /**
     * 双击
     */
    fun doubleClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK)
        } else {
            vibrate(CLICK)
            Handler(Looper.getMainLooper()).postDelayed({ vibrate(CLICK) }, 100)
        }
    }

    /**
     * 重压
     */
    fun heavyClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
        } else {
            vibrate(HEAVY)
        }
    }

    /**
     * 爆炸、展开
     */
    fun explode() {
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrate(VibrationEffect.startComposition().addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE).compose())
        } else {
            heavyClick()
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled && hasVibrator()
    }

    fun isEnabled(): Boolean {
        return enabled && hasVibrator()
    }

    fun hasVibrator(): Boolean {
        return vibrator!!.hasVibrator()
    }

    fun areSystemHapticsTurnedOn(context: Context): Boolean {
        val hapticFeedbackEnabled = Settings.System.getInt(
            context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0
        )
        return hapticFeedbackEnabled != 0
    }
}
