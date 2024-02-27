package com.example.demo.biz.base.storage.sp


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.dafay.demo.lib.base.utils.ApplicationUtils

/**
 * SharedPreferences 帮助类
 * tip: 所有泛型都不允许为 null
 */
object SPUtils {
    private fun <T> getSharedPrefs(): Preference<T> {
        return Preference<T>()
    }

    fun <T> findPreference(name: String, default: T): T {
        return getSharedPrefs<T>().findPreference(name, default)
    }

    fun <T> putPreference(name: String, value: T) {
        getSharedPrefs<T>().putPreference(name, value)
    }
}

class Preference<T> {
    private val prefs: SharedPreferences by lazy {
        ApplicationUtils.getApp().getSharedPreferences(getAppPackageName(), Context.MODE_PRIVATE)
    }

    private fun getAppPackageName(): String {
        return ApplicationUtils.getApp().packageName
    }

    @Suppress("UNCHECKED_CAST")
    public fun findPreference(name: String, default: T): T = with(prefs) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        res as T
    }

    @SuppressLint("CommitPrefEdits")
    public fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}
