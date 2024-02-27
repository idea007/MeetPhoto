package com.example.demo.meetphoto.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dafay.demo.lib.base.utils.VibratorManager
import com.dafay.demo.lib.base.utils.debug
import com.example.demo.biz.base.storage.sp.SPUtils
import com.example.demo.lib.net.HttpConfig
import com.example.demo.meetphoto.data.http.CommonInterceptor
import com.example.demo.meetphoto.data.model.ConfigC.BASEURL_UNSPLASH
import com.example.demo.meetphoto.data.model.PrefC
import com.google.android.material.color.DynamicColors

/**
 * @Des
 * @Author lipengfei
 * @Date 2023/12/29
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initThemes()
        initDarkMode()
        initLanguage()
        initVibrator()
        initHttpConfig()
    }


    private fun initThemes() {
        val isDynamicColorAvailable = DynamicColors.isDynamicColorAvailable()
        debug("initThemes isDynamicColorAvailable=${isDynamicColorAvailable}")
        if (isDynamicColorAvailable) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }

    private fun initDarkMode() {
        val mode = SPUtils.findPreference(PrefC.DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun initLanguage() {
        val code = SPUtils.findPreference(PrefC.LANGUAGE, "")
        if (code.isNullOrEmpty()) {
            return
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }

    private fun initVibrator() {
        VibratorManager.setEnabled(SPUtils.findPreference(PrefC.VIBRATOR_STATE, false))
    }

    private fun initHttpConfig() {
        val config = HttpConfig.Config.build {
            this.baseUrl = BASEURL_UNSPLASH
            this.addInterceptor(CommonInterceptor())
        }
        HttpConfig.initConfig(config)
    }
}