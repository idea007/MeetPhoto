package com.example.demo.meetphoto.ui.page.home.newvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dafay.demo.lib.base.net.Result
import com.example.demo.meetphoto.data.model.PAGE
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.ui.page.home.adapter.HomeItemInfo
import com.example.demo.meetphoto.ui.page.home.adapter.HomeItemType
import com.example.demo.meetphoto.ui.page.home.adapter.toHomeItemInfoPhotoList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/2/27
 */
class NewHomeViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val keyword = "wallpaper"
    private var curPageIndex = 1
    val refreshHomeItemsLiveData = MutableLiveData<Result<List<HomeItemInfo<*>>>>()
    val loadMoreHomeItemsLiveData = MutableLiveData<Result<List<HomeItemInfo<*>>>>()

    fun refresh(recommendTitle: String, wallpaperTitle: String) {
        if (refreshHomeItemsLiveData.value is Result.Loading) {
            return
        }
        refreshHomeItemsLiveData.postValue(Result.Loading)
        curPageIndex = 1
        viewModelScope.launch {
            val latestPhotosDeferred = async { photosRepository.getLatestPhotos(1, PAGE.PAGE_SIZE_TWENTY) }
            val searchPhotosDeferred = async { photosRepository.searchPhotos(keyword, curPageIndex, PAGE.PAGE_SIZE_TWENTY) }
            val latestPhotosResult: Result<List<Photo>?> = latestPhotosDeferred.await()
            val searchPhotosResult = searchPhotosDeferred.await()

            if (searchPhotosResult is Result.Error) {
                refreshHomeItemsLiveData.postValue(searchPhotosResult)
                return@launch
            }
            val temp = ArrayList<HomeItemInfo<*>>()
            if (latestPhotosResult is Result.Success && !latestPhotosResult.value.isNullOrEmpty()) {
                temp.add(HomeItemInfo(HomeItemType.TYPE_TITLE, recommendTitle))
                temp.add(HomeItemInfo(HomeItemType.TYPE_CAROUSEL, latestPhotosResult.value!!))
            }
            if (searchPhotosResult is Result.Success && !searchPhotosResult.value.isNullOrEmpty()) {
                temp.add(HomeItemInfo(HomeItemType.TYPE_TITLE, wallpaperTitle))
                temp.addAll(searchPhotosResult.value.toHomeItemInfoPhotoList())
            }
            curPageIndex++
            refreshHomeItemsLiveData.postValue(Result.Success(temp))
        }
    }

    fun loadMore() {
        if (loadMoreHomeItemsLiveData.value is Result.Loading) {
            return
        }
        loadMoreHomeItemsLiveData.postValue(Result.Loading)
        viewModelScope.launch {
            val searchPhotosResult = photosRepository.searchPhotos(keyword, curPageIndex, PAGE.PAGE_SIZE_TWENTY)
            if (searchPhotosResult is Result.Success) {
                curPageIndex++
                loadMoreHomeItemsLiveData.postValue(Result.Success(searchPhotosResult.value.toHomeItemInfoPhotoList()))
                return@launch
            } else if (searchPhotosResult is Result.Error) {
                loadMoreHomeItemsLiveData.postValue(searchPhotosResult)
            }
        }
    }

}
