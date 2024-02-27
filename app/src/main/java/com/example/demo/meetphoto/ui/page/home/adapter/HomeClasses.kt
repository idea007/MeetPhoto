package com.example.demo.meetphoto.ui.page.home.adapter

import com.example.demo.meetphoto.data.model.CollectionInfo
import com.example.demo.meetphoto.data.model.Photo

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/30
 */

enum class HomeItemType(val value: Int) {
    TYPE_TITLE(0),
    TYPE_CARD(1),
    TYPE_CAROUSEL(2),
    TYPE_PHOTO_CARD(3)
}

class HomeItemInfo<T>(val type: HomeItemType, val data: T)


fun List<CollectionInfo>?.toHomeItemInfoList(): List<HomeItemInfo<CollectionInfo>> {
    val temp = ArrayList<HomeItemInfo<CollectionInfo>>()
    this?.forEach {
        temp.add(HomeItemInfo(HomeItemType.TYPE_CARD, it))
    }
    return temp
}

fun List<Photo>?.toHomeItemInfoPhotoList(): List<HomeItemInfo<Photo>> {
    val temp = ArrayList<HomeItemInfo<Photo>>()
    this?.forEach {
        temp.add(HomeItemInfo(HomeItemType.TYPE_PHOTO_CARD, it))
    }
    return temp
}