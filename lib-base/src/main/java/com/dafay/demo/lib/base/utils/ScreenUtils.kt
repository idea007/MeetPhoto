package com.dafay.demo.lib.base.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager
import java.security.AccessController.getContext


/**
 * @ClassName ScreenUtils
 * @Des https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
 * @Author lipengfei
 * @Date 2023/9/11 09:13
 */
object ScreenUtils {

    fun getScreenWidth(): Int {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    fun getFullScreenHeight(): Int {
        val realSize = Point()
        val realDisplay = (ApplicationUtils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        realDisplay.getRealSize(realSize)
        return realSize.y
    }


}