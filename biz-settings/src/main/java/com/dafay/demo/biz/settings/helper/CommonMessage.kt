package com.dafay.demo.biz.settings.helper

class CommonMessage(var type: Type) : BaseMessage() {
    enum class Type {
        // 切换主题色
        CHANGE_THEME,
    }
}
