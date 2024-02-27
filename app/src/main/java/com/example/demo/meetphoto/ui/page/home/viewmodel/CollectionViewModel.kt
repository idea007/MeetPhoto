package com.example.demo.meetphoto.ui.page.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dafay.demo.lib.base.deprecaped.base.model.LiveDataPack
import com.dafay.demo.lib.base.deprecaped.base.model.RequestState
import com.dafay.demo.lib.base.net.RetrofitFamily
import com.example.demo.meetphoto.data.http.UnsplashService
import com.example.demo.meetphoto.data.model.CollectionInfo
import com.example.demo.meetphoto.data.model.PAGE
import com.example.demo.meetphoto.data.model.Photo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.dafay.demo.lib.base.utils.error

/**
 * @ClassName CollectionViewModel
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:50
 */
class CollectionViewModel : BaseViewModel() {

    private var curPageIndex = 1

    val refreshCollectionsLiveData = MutableLiveData<LiveDataPack<List<CollectionInfo>>>()
    val loadMoreCollectionsLiveData = MutableLiveData<LiveDataPack<List<CollectionInfo>>>()

    fun refreshCollections() {
        curPageIndex = 1
        queryCollections(curPageIndex, PAGE.PAGE_SIZE_THIRTY, refreshCollectionsLiveData)
    }

    fun loadMoreCollections() {
        queryCollections(curPageIndex, PAGE.PAGE_SIZE_THIRTY, loadMoreCollectionsLiveData)
    }

    private fun queryCollections(pageIndex: Int, pageCount: Int, liveData: MutableLiveData<LiveDataPack<List<CollectionInfo>>>) {
        if (liveData.value?.state == RequestState.START) {
            return
        }
        liveData.postValue(LiveDataPack(RequestState.START))
        compositeDisposable.add(
            RetrofitFamily.createService(UnsplashService::class.java)
            .getCollections(pageIndex, pageCount).subscribeOn(Schedulers.io())
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




    val refreshCollectionPhotosLiveData = MutableLiveData<LiveDataPack<List<Photo>>>()
    val loadMoreCollectionPhotosLiveData = MutableLiveData<LiveDataPack<List<Photo>>>()


    fun refreshCollectionPhotos(id: String) {
        curPageIndex = 1
        queryCollectionPhotos(id, curPageIndex,  PAGE.PAGE_SIZE_THIRTY, refreshCollectionPhotosLiveData)
    }

    fun loadMoreCollectionPhotos(id: String) {
        queryCollectionPhotos(id, curPageIndex, PAGE.PAGE_SIZE_THIRTY, loadMoreCollectionPhotosLiveData)
    }

    private fun queryCollectionPhotos(id: String, pageIndex: Int, pageCount: Int, liveData: MutableLiveData<LiveDataPack<List<Photo>>>) {
        if (liveData.value?.state == RequestState.START) {
            return
        }
        liveData.postValue(LiveDataPack(RequestState.START))
        compositeDisposable.add(RetrofitFamily.createService(UnsplashService::class.java)
            .getCollectionPhotos(id, pageIndex, pageCount).subscribeOn(Schedulers.io())
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