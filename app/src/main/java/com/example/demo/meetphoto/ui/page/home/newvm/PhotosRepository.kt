package com.example.demo.meetphoto.ui.page.home.newvm

import com.dafay.demo.lib.base.net.RetrofitFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import com.dafay.demo.lib.base.net.Result
import com.dafay.demo.lib.base.net.safeApiCall
import com.example.demo.meetphoto.data.http.UnsplashService
import com.example.demo.meetphoto.data.model.Photo
import kotlinx.coroutines.CoroutineDispatcher

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/2/21
 */
class PhotosRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    /**
     * 查找图片
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    suspend fun searchPhotos(keyword: String, page: Int, pageSize: Int): Result<List<Photo>?> {
        return safeApiCall(dispatcher) {
            RetrofitFamily.createService(UnsplashService::class.java).searchPhotos1(keyword, page, pageSize).results
        }
    }

    /**
     * 获取最新的图片
     * @param page
     * @param pageSize
     * @return
     */
    suspend fun getLatestPhotos(page: Int, pageSize: Int): Result<List<Photo>?> {
        return safeApiCall(dispatcher) {
            RetrofitFamily.createService(UnsplashService::class.java).getPhotos1(page, pageSize)
        }
    }
}