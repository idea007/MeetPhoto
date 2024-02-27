package com.dafay.demo.lib.base.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/25
 * <a href=" ">相关链接</a>
 */
object RxBus {

    private val publishSubject : PublishSubject<Any> = PublishSubject.create()

    fun post(any: Any) {
        publishSubject.onNext(any)
    }

    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return publishSubject.ofType(eventType)
    }

    fun toObservable(): Observable<Any> {
        return publishSubject.hide()
    }

    fun hasObservers(): Boolean {
        return publishSubject.hasObservers()
    }
}