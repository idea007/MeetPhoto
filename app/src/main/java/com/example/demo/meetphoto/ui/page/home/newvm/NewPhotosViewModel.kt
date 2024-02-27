package com.example.demo.meetphoto.ui.page.home.newvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dafay.demo.lib.base.net.Result
import com.example.demo.meetphoto.data.model.Photo
import kotlinx.coroutines.launch

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/2/27
 */
class NewPhotosViewModel(private val photosRepository: PhotosRepository) : ViewModel() {

    val photoLiveData = MutableLiveData<Result<Photo?>>()

    fun getPhotoDetail(id: String) {
        viewModelScope.launch {
            photoLiveData.postValue(photosRepository.getPhotoDetail(id))
        }
    }


    fun photoDownload(id: String) {
        viewModelScope.launch {
            photosRepository.photoDownload(id)
        }
    }
}
