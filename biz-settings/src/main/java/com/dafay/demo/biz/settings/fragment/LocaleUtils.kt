package com.dafay.demo.biz.settings.fragment

import androidx.core.os.LocaleListCompat
import java.util.Objects

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/17
 */
object LocaleUtils {

    fun getLanguageCode(locales: LocaleListCompat): String? {
        return if (!locales.isEmpty) {
            Objects.requireNonNull(locales[0])?.toLanguageTag()
        } else {
            null
        }
    }
}