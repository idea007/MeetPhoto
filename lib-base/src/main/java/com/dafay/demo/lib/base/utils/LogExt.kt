package com.dafay.demo.lib.base.utils

import android.util.Log

/**
 * 为日志添加统一的前缀
 */
private const val PREFIX = "======> "
private val fqcnIgnore = listOf(
    "dalvik.system.VMStack",
    "java.lang.Thread",
    "com.dafay.demo.lib.base.utils.LogExtKt"
)

private fun findClassNameAndLine(): String? {
    return Throwable().stackTrace.first { it.className !in fqcnIgnore }?.let {
        "${it.fileName}:${it.lineNumber}"
    }
}

private fun appendPrefix(message: String): String {
    return PREFIX + "[${Thread.currentThread().name}](${findClassNameAndLine()}) " + message
}

fun Any.println(message: String) {
    kotlin.io.println(appendPrefix(message))
}

fun Any.println(tr: Throwable) {
    kotlin.io.println(appendPrefix(tr.toString()))
}

fun Any.debug(message: String) {
    Log.d(this::class.java.simpleName, appendPrefix(message))
}

fun Any.debug(message: String, tr: Throwable) {
    Log.d(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.error(message: String) {
    Log.e(this::class.java.simpleName, appendPrefix(message))
}

fun Any.error(message: String, tr: Throwable?) {
    Log.e(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.info(message: String) {
    Log.i(this::class.java.simpleName, appendPrefix(message))
}

fun Any.info(message: String, tr: Throwable) {
    Log.i(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.verbose(message: String) {
    Log.v(this::class.java.simpleName, appendPrefix(message))
}

fun Any.verbose(message: String, tr: Throwable) {
    Log.v(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.warn(message: String) {
    Log.w(this::class.java.simpleName, appendPrefix(message))
}

fun Any.warn(message: String, tr: Throwable) {
    Log.w(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.warn(tr: Throwable) {
    Log.w(this::class.java.simpleName, tr)
}

fun Any.wtf(message: String) {
    Log.wtf(this::class.java.simpleName, appendPrefix(message))
}

fun Any.wtf(message: String, tr: Throwable) {
    Log.wtf(this::class.java.simpleName, appendPrefix(message), tr)
}

fun Any.wtf(tr: Throwable) {
    Log.wtf(this::class.java.simpleName, tr)
}