package com.dafay.demo.biz.settings


object ConfigC {
    val PRIVACY_POLICY: String = "https://sites.google.com/view/meetphoto-privacy-policy/%E9%A6%96%E9%A1%B5"
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



/**
 * intent extra
 */
object ExtraC {
    const val SETTINGS_INSTANCE_BOUND = "settings_instance_bound"
    const val SETTINGS_COLOR_THEME_SCROLL_X = "settings_color_theme_scroll_x"
    const val SETTINGS_SCROLL_Y = "settings_scroll_y"
}
