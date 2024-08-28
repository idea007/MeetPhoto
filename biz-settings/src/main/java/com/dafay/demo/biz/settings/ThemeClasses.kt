package com.dafay.demo.biz.settings

import android.graphics.Color



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




