package com.example.demo.meetphoto.ui.helper

import com.example.demo.meetphoto.data.model.Language

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/25
 */
class LanguageHelper {
    val languageList = ArrayList<Language>().apply {
        add(Language(null, "跟随系统", "跟随系统"))
        add(Language("en", "English", "english"))
        add(Language("zh-CN", "中文", "中文"))
    }

    fun getLanguageList(): List<Language> {
        return ArrayList<Language>().apply {
            add(Language(null, "跟随系统", "跟随系统"))
            add(Language("en", "English", "english"))
            add(Language("zh-CN", "中文", "中文"))
        }
    }
}