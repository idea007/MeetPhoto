package com.example.demo.meetphoto.ui.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView

/**
 * @ClassName LoadingDialogExt
 * @Description 加载弹窗工具类
 * @Author
 * @Date 2023/5/8 10:47
 */
//loading框
private var loadingPopup: LoadingPopupView? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "加载中") {
    if (!this.isFinishing) {
        if (loadingPopup == null) {
            loadingPopup = XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .autoDismiss(false)
                .isLightNavigationBar(true)
                .isViewMode(true)
                .asLoading(message)
                .show() as LoadingPopupView
        } else {
            loadingPopup?.show()
        }
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
        if (!it.isFinishing) {
            if (loadingPopup == null) {
                loadingPopup = XPopup.Builder(activity)
                    .dismissOnBackPressed(false)
                    .isLightNavigationBar(true)
                    .isViewMode(true)
                    .asLoading(message)
                    .show() as LoadingPopupView
            } else {
                loadingPopup?.show()
            }
        }
    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    loadingPopup?.dismiss()
    loadingPopup = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingPopup?.dismiss()
    loadingPopup = null
}
