package com.example.demo.meetphoto.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.example.demo.meetphoto.databinding.LayoutPromptUniteViewBinding

/**
 * @Des 提示页面
 * @Author m1studio
 * @Date 2024/1/7
 * <a href=" ">相关链接</a>
 */
class PromptUniteView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private var _binding: LayoutPromptUniteViewBinding? = null
    val binding get() = _binding!!


    init {
        _binding = LayoutPromptUniteViewBinding.inflate(LayoutInflater.from(context), this, true)
        initViews()
    }

    private fun initViews() {

    }

    fun setTipImgAndTipText(imgRes: Int?, text: String?) {
        imgRes?.let {
            binding.ivTipImg.setImageResource(it)
        }
        text?.let {
            binding.tvTipText.text = it
        }
    }

    fun setButton(text: String, func: (() -> Unit)? = null) {
        binding.btnClick.visibility = View.VISIBLE
        binding.btnClick.text = text
        binding.btnClick.setOnClickListener {
            func?.invoke()
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}