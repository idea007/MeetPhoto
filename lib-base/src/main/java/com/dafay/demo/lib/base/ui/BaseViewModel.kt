package com.dafay.demo.lib.base.ui

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/26
 */
open class BaseViewModel : ViewModel() {
    protected val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}