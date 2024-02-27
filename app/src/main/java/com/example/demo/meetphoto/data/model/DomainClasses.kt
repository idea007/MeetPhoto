package com.example.demo.meetphoto.data.model

import android.graphics.Color
import android.os.Parcelable
import com.example.demo.meetphoto.R
import kotlinx.parcelize.Parcelize

/**
 * @Des
 * @Author lipengfei
 * @Date 2023/12/29
 */


@Parcelize
data class SearchResult(val total: Int?,val total_pages:Int,val results:List<Photo>) : Parcelable

@Parcelize
data class DownloadResponse(val url: String?) : Parcelable


@Parcelize
data class Topic(val id: String, val slug: String, val title: String) : Parcelable


@Parcelize
data class Photo(
    val id: String,
    val created_at: String?,
    val updated_at: String?,
    val width: Int?,
    val height: Int?,
    val color: String? = "#E0E0E0",
    val blur_hash: String?,
    val views: Int?,
    val downloads: Int?,
    val likes: Int?,
    var liked_by_user: Boolean?,
    val description: String?,
    val alt_description: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tag>?,
    val current_user_collections: List<CollectionInfo>?,
    val sponsorship: Sponsorship?,
    val urls: UrlsInfo,
    val links: Links?,
    val user: User?,
    val statistics: PhotoStatistics?
) : Parcelable

@Parcelize
data class Exif(
    val make: String?,
    val model: String?,
    val exposure_time: String?,
    val aperture: String?,
    val focal_length: String?,
    val iso: Int?
) : Parcelable


@Parcelize
data class Location(
    val name: String?,
    val city: String?,
    val country: String?,
    val position: Position?
) : Parcelable

@Parcelize
data class Position(
    val latitude: Double?,
    val longitude: Double?
) : Parcelable

@Parcelize
data class Sponsorship(
    val sponsor: User?
) : Parcelable

@Parcelize
data class UrlsInfo(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?,
    val small_s3: String?
) : Parcelable


@Parcelize
data class Links(
    val self: String?,
    val html: String?,
    val download: String?,
    val download_location: String?,
) : Parcelable

/**
 * TODO  "links":Object{...},
 *             "profile_image":Object{...},
 *
 * @property id
 * @property updated_at
 * @property username
 * @property name
 * @property first_name
 * @property last_name
 * @property location
 */

@Parcelize
data class User(
    val id: String?,
    val updated_at: String?,
    val username: String?,
    val name: String?,
    val first_name: String?,
    val last_name: String?,
    val location: String?,
    val links: UserLinks?,
    val profile_image: UserProfileImage?,
) : Parcelable


@Parcelize
data class UserProfileImage(val small: String, val medium: String, val large: String) : Parcelable

@Parcelize
data class UserLinks(
    val html: String,
    val photos: String,
    val likes: String,
    val portfolio: String,
    val following: String,
    val followers: String
) : Parcelable

@Parcelize
data class CollectionInfo(
    val id: String,
    val title: String,
    val description: String?,
    val published_at: String?,
    val updated_at: String?,
    val curated: Boolean?,
    val featured: Boolean?,
    val total_photos: Int,
    val private: Boolean?,
    val share_key: String?,
    val tags: List<Tag>?,
    val cover_photo: Photo?,
    val preview_photos: List<Photo>?,
    val user: User?,
    val links: Links?
) : Parcelable

@Parcelize
data class Tag(
    val type: String?,
    val title: String?
) : Parcelable


data class Language(val code: String?, val name: String, val des: String)

data class PhotoQuality(val qualityType: PhotoQualityType)


enum class PhotoQualityType(val key: String,val nameResId:Int) {
    RAW("raw",R.string.raw),
    FULL("full",R.string.full),
    REGULAR("regular",R.string.regular),
    SMALL("samll",R.string.samll),
    THUMB("thumb",R.string.thumb);

    companion object {
        fun from(key: String?): PhotoQualityType = PhotoQualityType.values().firstOrNull { it.key == key } ?: REGULAR
    }
}

enum class COLOR_THEME(val key: String, val color: Int,val nameResId:Int) {
    // 动态主题色
    DYNAMIC("dynamic", Color.TRANSPARENT, R.string.dynamic),

    // 中国色-茶花红
    CHAHUAHONG("chahuahong", Color.parseColor("#ee3f4d"),R.string.chahuahong),

    // 扁豆紫
    BIANDOUZI("biandouzi", Color.parseColor("#a35c8f"),R.string.biandouzi),

    // 魏紫
    WEIZI("weizi", Color.parseColor("#7e1671"),R.string.weizi),

    // 花青
    HUAQING("huaqing", Color.parseColor("#2376b7"),R.string.huaqing),

    // 中国色-荷叶绿
    HEYELV("heyelv", Color.parseColor("#1a6840"),R.string.heyelv);

    companion object {
        fun from(key: String?): COLOR_THEME = COLOR_THEME.values().firstOrNull { it.key == key } ?: DYNAMIC
    }
}




