package com.dafay.demo.biz.settings.helper

import android.app.Activity
import com.dafay.demo.biz.settings.COLOR_THEME
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/23
 */
object ThemeHelper {
    fun applyTheme(theme: COLOR_THEME, act: Activity) {
        when (theme) {
            COLOR_THEME.DYNAMIC -> {
                if (DynamicColors.isDynamicColorAvailable()) {
                    DynamicColors.applyToActivityIfAvailable(
                        act,
                        DynamicColorsOptions.Builder().setOnAppliedCallback { activity: Activity? ->
                            HarmonizedColors.applyToContextIfAvailable(
                                act, HarmonizedColorsOptions.createMaterialDefaults()
                            )
                        }.build()
                    )
                }
            }

            else -> {
                DynamicColors.applyToActivityIfAvailable(
                    act,
                    DynamicColorsOptions.Builder()
                        .setContentBasedSource(theme.color)
                        .build()
                )
            }
        }
    }
}