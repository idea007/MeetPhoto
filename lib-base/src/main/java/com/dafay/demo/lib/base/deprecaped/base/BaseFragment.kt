package com.dafay.demo.lib.base.deprecaped.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * @ClassName BaseFragment
 * @Des
 * @Author lipengfei
 * @Date 2023/8/11 14:15
 */
open class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onCreate()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onSaveInstanceState()")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onHiddenChanged(${hidden})")
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onCreateView()")
        _binding = inflateBindingWithGeneric(inflater, container, false)
        return binding.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onViewCreated()")
        initViews()
        initObserver()
        bindListener()
        initializeData()
    }

    protected open fun initViews() {}

    protected open fun initObserver() {}

    protected open fun bindListener() {}

    protected open fun initializeData() {}


    override fun onStart() {
        super.onStart()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onDestroy()")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(this.javaClass.simpleName, "${this.javaClass.simpleName} onDetach()")
    }

    open fun getTitle():String{
        return "default"
    }

}