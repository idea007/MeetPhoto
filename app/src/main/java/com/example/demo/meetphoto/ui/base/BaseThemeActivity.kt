package com.example.demo.meetphoto.ui.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.dafay.demo.lib.base.deprecaped.base.BaseActivity
import com.example.demo.biz.base.storage.sp.SPUtils
import com.example.demo.meetphoto.data.model.COLOR_THEME
import com.example.demo.meetphoto.data.model.PrefC
import com.example.demo.meetphoto.ui.helper.ThemeHelper

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/23
 */

open class BaseThemeActivity<VB : ViewBinding> : BaseActivity<VB>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    final override fun dynamicColorsApplyBeforeInflateView() {
        initTheme()
    }

    private fun initTheme() {
        val colorTheme = COLOR_THEME.from(SPUtils.findPreference(PrefC.COLOR_THEME, COLOR_THEME.DYNAMIC.key))
        ThemeHelper.applyTheme(colorTheme, this)
    }
}