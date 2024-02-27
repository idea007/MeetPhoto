package com.example.demo.meetphoto.utils

import android.os.Handler
import android.os.Looper

/**
 * @ClassName HandlerSingleton
 * @Des handler 单例
 * @Author lipengfei
 * @Date 2023/6/15 14:01
 */
object HandlerSingleton {
    // 这里直接初始化时创建，避免在子线程创建
    val mainHandler = Handler(Looper.getMainLooper())

}