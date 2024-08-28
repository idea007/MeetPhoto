package com.dafay.demo.biz.settings.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.dafay.demo.biz.settings.databinding.LayoutSettingsItemUniteViewBinding

/**
 * @Des 设置页通用 item
 * @Author m1studio
 * @Date 2024/1/7
 * <a href=" ">相关链接</a>
 */
class SettingsItemUniteView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private var _binding: LayoutSettingsItemUniteViewBinding? = null
    val binding get() = _binding!!


    init {
        _binding = LayoutSettingsItemUniteViewBinding.inflate(LayoutInflater.from(context), this, true)
        initViews()
    }

    private fun initViews() {

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}