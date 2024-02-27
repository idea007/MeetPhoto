package com.dafay.demo.lib.base.deprecaped.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * @ClassName BaseActivity
 * @Des act 基类
 * @Author lipengfei
 * @Date 2023/11/21 16:59
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private val TAG = BaseActivity::class.java.simpleName
    private var _binding: VB? = null
    protected val binding get() = _binding!!


    open override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "======> ${this.javaClass.simpleName} onCreate() ")
        dynamicColorsApplyBeforeInflateView()
        inflateViewBinding()
        resolveIntent(intent)
        initViews(savedInstanceState)
        initViews()
        initObserver()
        bindListener()
        initializeData()
    }

    protected open fun dynamicColorsApplyBeforeInflateView() {
    }

    private fun inflateViewBinding() {
        _binding = inflateBindingWithGeneric(layoutInflater)
        if (_binding == null) {
            Log.wtf(TAG, "_binging is null")
            finish()
            return
        }
        setContentView(binding.root)
    }

    protected open fun resolveIntent(intent: Intent?) {}

    protected open fun initViews(savedInstanceState: Bundle?) {}

    protected open fun initViews() {}

    protected open fun initObserver() {}

    protected open fun bindListener() {}

    protected open fun initializeData() {}

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG, "======> ${this.javaClass.simpleName} onNewIntent() ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "======> ${this.javaClass.simpleName} onResume() ")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "======> ${this.javaClass.simpleName} onPause() ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "======> ${this.javaClass.simpleName} onDestroy() ")
        _binding = null
    }
}