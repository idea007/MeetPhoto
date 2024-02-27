package com.example.demo.meetphoto.data.http

import com.example.demo.meetphoto.data.model.CollectionInfo
import com.example.demo.meetphoto.data.model.DownloadResponse
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.data.model.SearchResult
import com.example.demo.meetphoto.data.model.Topic
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @ClassName UnsplashService
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 18:59
 */
interface UnsplashService {

    @GET("/search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("order_by") order_by: String = "latest",
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("orientation") orientation: String = "portrait"
    ): Observable<SearchResult>


    @GET("/search/photos")
    suspend fun searchPhotos1(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String = "latest",
        @Query("orientation") orientation: String = "portrait"
    ): SearchResult

    @GET("/photos")
    suspend fun getPhotos1(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String = "latest",
    ): List<Photo>?


    @GET("/photos")
    fun getPhotos(
        @Query("order_by") order_by: String = "latest",
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Observable<List<Photo>>


    @GET("/photos/{id}")
    fun getPhoto(
        @Path("id") id: String,
    ): Observable<Photo>

    @GET("/topics/{id_or_slug}/photos")
    fun getTopicPhotos(
        @Path("id_or_slug") id_or_slug: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Observable<List<Photo>>

    @GET("/topics")
    fun getTopics(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Observable<List<Topic>>


    @GET("/collections")
    fun getCollections(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Observable<List<CollectionInfo>>


    @GET("/collections/{id}/photos")
    fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Observable<List<Photo>>

    //照片下载是，上报
    @GET("/photos/{id}/download")
    fun photoDownload(
        @Path("id") id: String
    ): Observable<DownloadResponse>

}