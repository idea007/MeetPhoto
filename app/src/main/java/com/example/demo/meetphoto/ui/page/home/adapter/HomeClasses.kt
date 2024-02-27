package com.example.demo.meetphoto.ui.page.home.adapter

import com.example.demo.meetphoto.data.model.Photo

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/30
 */

enum class HomeItemType(val value: Int) {
    TYPE_TITLE(0),
    TYPE_CAROUSEL(1),
    TYPE_PHOTO_CARD(2)
}

class HomeItemInfo<T>(val type: HomeItemType, val data: T)


fun List<Photo>?.toHomeItemInfoPhotoList(): List<HomeItemInfo<Photo>> {
    val temp = ArrayList<HomeItemInfo<Photo>>()
    this?.forEach {
        temp.add(HomeItemInfo(HomeItemType.TYPE_PHOTO_CARD, it))
    }
    return temp
}