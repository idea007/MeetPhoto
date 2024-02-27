package com.example.demo.meetphoto.ui.page.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dafay.demo.lib.base.deprecaped.base.model.LiveDataPack
import com.dafay.demo.lib.base.deprecaped.base.model.RequestState
import com.dafay.demo.lib.base.net.RetrofitFamily
import com.dafay.demo.lib.base.utils.ApplicationUtils
import com.dafay.demo.lib.base.utils.NetworkUtils
import com.example.demo.meetphoto.data.http.UnsplashService
import com.example.demo.meetphoto.data.model.Photo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.dafay.demo.lib.base.utils.error

/**
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */

class PhotosViewModel : BaseViewModel() {


    private val PAGE_COUNT = 30
    private var curPageIndex = 1

    val refreshPhotosLiveData = MutableLiveData<LiveDataPack<List<Photo>>>()
    val loadMorePhotosLiveData = MutableLiveData<LiveDataPack<List<Photo>>>()


    fun refreshPhotos() {
        curPageIndex = 1
        queryPhotos(curPageIndex, PAGE_COUNT, refreshPhotosLiveData)
    }

    fun loadMorePhotos() {
        queryPhotos(curPageIndex, PAGE_COUNT, loadMorePhotosLiveData)
    }

    private fun queryPhotos(pageIndex: Int, pageCount: Int, liveData: MutableLiveData<LiveDataPack<List<Photo>>>) {
        if (liveData.value?.state == RequestState.START) {
            return
        }

        if(!NetworkUtils.isConnected(ApplicationUtils.getApp())){
            liveData.postValue(LiveDataPack(RequestState.NO_NETWORK))
            return
        }

        liveData.postValue(LiveDataPack(RequestState.START))
        compositeDisposable.add(
            RetrofitFamily.createService(UnsplashService::class.java)
            .getPhotos("latest", pageIndex, pageCount).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNullOrEmpty()) {
                    liveData.postValue(LiveDataPack(if (pageIndex == 1) RequestState.NO_DATA else RequestState.NO_MORE_DATA))
                    return@subscribe
                }
                curPageIndex = pageIndex + 1
                liveData.postValue(LiveDataPack(RequestState.SUCCESS, it))
            }) { onError: Throwable? ->
                liveData.postValue(LiveDataPack(RequestState.ERROR, onError))
                error("", onError)
            })
    }

}