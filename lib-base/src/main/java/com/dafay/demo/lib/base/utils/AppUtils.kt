package com.dafay.demo.lib.base.utils

import android.content.Context
import android.util.Log


/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/22
 * <a href=" ">相关链接</a>
 */
object AppUtils {
    /**
     * 返回当前程序版本名
     */
    fun getAppVersionName(context: Context): String {
        var versionName = ""
        try {
            // ---get the package info---
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            versionName = pi.versionName
            if (versionName == null || versionName.length <= 0) {
                return ""
            }
        } catch (e: Exception) {
            Log.e("VersionInfo", "Exception", e)
        }
        return versionName
    }
}