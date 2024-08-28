package com.dafay.demo.biz.settings.helper


/**
 * Created by idea on 2018/6/12.
 */
open class BaseMessage {

    // Map 存储数据
    private var ps: MutableMap<String, Any>? = null

    fun put(key: String?, value: Any): BaseMessage {
        if (ps == null) {
            ps = HashMap()
        }

        if (key != null) {
            ps?.put(key, value)
        }
        return this
    }

    operator fun get(key: String?): Any? {
        return if (ps == null || key.isNullOrBlank()) {
            null
        } else ps?.get(key)
    }

}
