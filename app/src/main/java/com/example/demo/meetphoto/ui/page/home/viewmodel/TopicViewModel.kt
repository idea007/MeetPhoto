package com.example.demo.meetphoto.ui.page.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dafay.demo.lib.base.deprecaped.base.model.LiveDataPack
import com.dafay.demo.lib.base.deprecaped.base.model.RequestState
import com.dafay.demo.lib.base.net.RetrofitFamily
import com.dafay.demo.lib.base.utils.error
import com.example.demo.meetphoto.data.http.UnsplashService
import com.example.demo.meetphoto.data.model.Topic
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @ClassName TopicViewModel
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */
class TopicViewModel : BaseViewModel() {


    val refreshTopicsLiveData = MutableLiveData<LiveDataPack<List<Topic>>>()

    fun getTopics() {
        compositeDisposable.add(RetrofitFamily.createService(UnsplashService::class.java)
            .getTopics(1, 100).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                refreshTopicsLiveData.postValue(LiveDataPack(RequestState.SUCCESS, it))
            }) { onError: Throwable? ->
                error("getTopics fail:", onError)
                refreshTopicsLiveData.postValue(LiveDataPack(RequestState.ERROR, onError))
            })
    }

}