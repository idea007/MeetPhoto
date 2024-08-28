package com.dafay.demo.biz.settings.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.dafay.demo.biz.settings.Language
import com.dafay.demo.biz.settings.R
import com.dafay.demo.biz.settings.databinding.ItemLanguageBinding
import com.dafay.demo.lib.base.ui.adapter.BaseAdapter
import com.google.android.material.color.MaterialColors

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/17
 */
class LauguageAdapter : BaseAdapter<Language> {
    var currentSelectedCode: String? = null
    var onItemClickListener: LanguageViewHolder.OnItemClickListener? = null

    constructor() : super() {
        currentSelectedCode = LocaleUtils.getLanguageCode(AppCompatDelegate.getApplicationLocales())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LanguageViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LanguageViewHolder).onBindViewHolder(position, datas[position], onItemClickListener, currentSelectedCode)
    }


    class LanguageViewHolder : RecyclerView.ViewHolder {

        val binding: ItemLanguageBinding

        constructor(itemView: ItemLanguageBinding) : super(itemView.root) {
            binding = itemView
        }

        fun onBindViewHolder(position: Int, language: Language, onItemClickListener: OnItemClickListener?, selectedCode: String?) {
            binding.uvItem.binding.apply {
                if (position == 0) {
                    tvTitle.setText(R.string.other_language_system)
                    tvDes.setText(R.string.other_language_system_description)
                } else {
                    tvTitle.text = language.name
                    tvDes.text = language.des
                }
                mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                mcvCard.setOnClickListener {
                    onItemClickListener?.onClickItem(it, position, language)
                }
                if (selectedCode == language.code) {
                    ivIcon.visibility = View.VISIBLE
                    mcvCard.setCardBackgroundColor(MaterialColors.getColor(mcvCard.context,com.google.android.material.R.attr.colorSurfaceContainerHigh, this::class.java.getCanonicalName()))
                } else {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }

        }

        interface OnItemClickListener {
            fun onClickItem(view: View, position: Int, language: Language)
        }
    }

}