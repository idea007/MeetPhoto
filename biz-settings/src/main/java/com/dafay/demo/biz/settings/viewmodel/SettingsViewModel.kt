package com.dafay.demo.biz.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dafay.demo.biz.settings.helper.CacheHelper
import com.dafay.demo.lib.base.utils.ApplicationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * @ClassName TopicViewModel
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */
class SettingsViewModel : ViewModel() {

    val isVibratorTurnedOnLiveData = MutableLiveData<Boolean>()
    val totalSizeLiveData = MutableLiveData<String>()

    fun clearCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                CacheHelper.clearAllCache(ApplicationUtils.getApp())
                val result = CacheHelper.getTotalCacheSize(ApplicationUtils.getApp())
                totalSizeLiveData.postValue(result)
            }
        }
    }

    fun queryTotalCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = CacheHelper.getTotalCacheSize(ApplicationUtils.getApp())
                totalSizeLiveData.postValue(result)
            }
        }
    }

}