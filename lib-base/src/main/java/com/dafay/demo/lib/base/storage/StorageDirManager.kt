package com.example.demo.biz.base.storage

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

/**
 * @description: 文件缓存管理
 * 1.注意 SqLite 和 缓存文件要同步，app 卸载一起被清除
 * @Author: lipengfei
 * @Date: 2023/4/3
 * @Last Modified by: lipengfei
 * @Last Modified time: 2023/4/3
 */
class StorageDirManager {
    private val TAG = StorageDirManager::class.java.simpleName

    private constructor()

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            StorageDirManager()
        }
    }

    /**
     * 判断 SD 是否挂载
     */
    fun isExternalStorageAvailable(): Boolean {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
    }

    /**
     * 获取 /storage/emulated/0/
     * 1.app 卸载重装文件不会丢失
     * 2.只使用机身内部存储路径，不考虑SD的文件
     * 3.添加权限才能操作文件
     */
    fun getExternalStorageDir(): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator
    }

    /**
     * 获取 /data/user/0/{packagename}/files
     * 1. app 卸载重装文件丢失
     */
    fun getPkgFile(ctx: Context?): File? {
        if (ctx == null) {
            Log.w(TAG, "======> getPkgFilesDir() ctx is null")
            return null
        }
        return ctx.getFilesDir()
    }

    /**
     * 获取 /storage/emulated/0/Android/data/{packagename}/files
     * 1. app 卸载重装文件丢失
     */
    fun getPkgExternalFile(ctx: Context?): File? {
        if (ctx == null) {
            Log.w(TAG, "======> getPkgExternalFilesDir() ctx is null")
            return null
        }
        return ctx.getExternalFilesDir(null)
    }


}