package com.example.demo.meetphoto.ui.page.settings

import androidx.lifecycle.MutableLiveData
import com.dafay.demo.lib.base.utils.ApplicationUtils
import com.example.demo.meetphoto.ui.helper.CacheHelper
import com.example.demo.meetphoto.ui.page.home.viewmodel.BaseViewModel
import java.util.concurrent.Executors

/**
 * @ClassName TopicViewModel
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */
class SettingsViewModel : BaseViewModel() {
    val ioThread = Executors.newSingleThreadExecutor()
    val isVibratorTurnedOnLiveData = MutableLiveData<Boolean>()

    val totalSizeLiveData = MutableLiveData<String>()

    fun clearCache() {
        ioThread.execute({
            CacheHelper.clearAllCache(ApplicationUtils.getApp())
            val result = CacheHelper.getTotalCacheSize(ApplicationUtils.getApp())
            totalSizeLiveData.postValue(result)
        })
    }

    fun queryTotalCache() {
        ioThread.execute({
            val result = CacheHelper.getTotalCacheSize(ApplicationUtils.getApp())
            totalSizeLiveData.postValue(result)
        })
    }

}