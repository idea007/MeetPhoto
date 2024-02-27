package com.example.demo.meetphoto.ui.helper

import androidx.annotation.Keep

/**
 * Created by idea on 2018/6/12.
 */

class CommonMessage(var type: Type) : BaseMessage() {

    //为避免重复，这个地名命名请尽可能详细一些
    enum class Type {
        // 切换主题色
        CHANGE_THEME,
    }
}
