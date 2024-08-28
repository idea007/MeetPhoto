package com.dafay.demo.biz.settings.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.dafay.demo.biz.settings.databinding.LayoutCommonBottomSheetItemUniteViewBinding

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/7
 * <a href=" ">相关链接</a>
 */
class BottomSheetCommonItemUniteView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private var _binding: LayoutCommonBottomSheetItemUniteViewBinding? = null
    val binding get() = _binding!!


    init {
        _binding = LayoutCommonBottomSheetItemUniteViewBinding.inflate(LayoutInflater.from(context), this, true)
        initViews()
    }

    private fun initViews() {

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}