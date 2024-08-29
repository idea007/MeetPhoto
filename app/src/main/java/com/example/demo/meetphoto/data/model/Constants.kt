package com.example.demo.meetphoto.data.model

/**
 * 配置参数
 */
object ConfigC {
    val CLIENT_ID="client_id"
    val PRIVACY_POLICY: String = "https://sites.google.com/view/meetphoto-privacy-policy/%E9%A6%96%E9%A1%B5"
    val DEMO_UNSPLASH_CLIENT_ID: String = "dg0yWpXZkFvjaARvvhfIadcU0HnN08nYjoQckbXYcpY"
    val BASEURL_UNSPLASH: String = "https://api.unsplash.com"
}

// sp key
object PrefC {
    val COLOR_THEME: String = "color_theme"
    val LANGUAGE: String = "language"
    val DARK_MODE: String = "dark_mode"
    val VIBRATOR_STATE: String = "vibrator_state"


    val PHOTO_QUALITY_DOWNLAOD: String = "photo_quality_downlaod"
}

// sp default
object DefC {
    val THEME: String = ""
}

object PAGE {
    val PAGE_SIZE_TWENTY = 20
}


/**
 * intent extra
 */
object ExtraC {
    const val PHOTO = "photo"
}



const val CROSS_FADE_DURATION = 350