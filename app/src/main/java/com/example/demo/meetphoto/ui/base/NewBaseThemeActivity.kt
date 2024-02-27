package com.example.demo.meetphoto.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.example.demo.biz.base.storage.sp.SPUtils
import com.example.demo.meetphoto.data.model.COLOR_THEME
import com.example.demo.meetphoto.data.model.PrefC
import com.example.demo.meetphoto.ui.helper.ThemeHelper

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/23
 */

abstract class NewBaseThemeActivity(@LayoutRes contentLayoutId: Int) : BaseActivity(contentLayoutId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
    }

    private fun applyTheme() {
        val colorTheme = COLOR_THEME.from(SPUtils.findPreference(PrefC.COLOR_THEME, COLOR_THEME.DYNAMIC.key))
        ThemeHelper.applyTheme(colorTheme, this)
    }
}