package com.example.demo.meetphoto.ui.page.preview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dafay.demo.lib.base.net.RetrofitFamily
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.error
import com.example.demo.meetphoto.data.http.UnsplashService
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.ui.page.home.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @ClassName TopicViewModel
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */
class PreviewViewModel : BaseViewModel() {

    fun photoDownload(id: String) {
        compositeDisposable.add(
            RetrofitFamily.createService(UnsplashService::class.java)
                .photoDownload(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    debug("photoDownload success: ${it.url}")
                }) { onError: Throwable? ->
                    error("photoDownload fail:", onError)
                })
    }

    val photoLiveData = MutableLiveData<Photo>()

    fun getPhotoDetail(id: String) {
        compositeDisposable.add(RetrofitFamily.createService(UnsplashService::class.java)
            .getPhoto(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                photoLiveData.postValue(it)
                debug("getPhotoDetail success")
            }) { onError: Throwable? ->
                error("getPhotoDetail fail:", onError)
            })
    }

}