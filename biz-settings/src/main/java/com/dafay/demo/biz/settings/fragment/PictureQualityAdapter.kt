package com.dafay.demo.biz.settings.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dafay.demo.biz.settings.PhotoQuality
import com.dafay.demo.biz.settings.PhotoQualityType
import com.dafay.demo.biz.settings.databinding.ItemLanguageBinding
import com.dafay.demo.lib.base.ui.adapter.BaseAdapter
import com.google.android.material.color.MaterialColors

/**
 * @Des
 * @Author lipengfei
 * @Date 2024/1/17
 */
class PictureQualityAdapter : BaseAdapter<PhotoQuality> {
    var onItemClickListener: PhotoQualityViewHolder.OnItemClickListener? = null
    var curPhotoQualityType: PhotoQualityType = PhotoQualityType.REGULAR

    constructor(passPhotoQualityType: PhotoQualityType) : super() {
        this.curPhotoQualityType = passPhotoQualityType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoQualityViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PhotoQualityViewHolder).onBindViewHolder(position, datas[position], onItemClickListener, curPhotoQualityType)
    }


    class PhotoQualityViewHolder : RecyclerView.ViewHolder {

        val binding: ItemLanguageBinding

        constructor(itemView: ItemLanguageBinding) : super(itemView.root) {
            binding = itemView
        }

        fun onBindViewHolder(
            position: Int,
            photoQuality: PhotoQuality,
            onItemClickListener: OnItemClickListener?,
            selectedPhotoQualityType: PhotoQualityType
        ) {
            binding.uvItem.binding.apply {
                tvTitle.text =binding.root.context.getString(photoQuality.qualityType.nameResId)
                tvDes.visibility = View.GONE
                mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                mcvCard.setOnClickListener {
                    onItemClickListener?.onClickItem(it, position, photoQuality)
                }
                if (selectedPhotoQualityType == photoQuality.qualityType) {
                    ivIcon.visibility = View.VISIBLE
                    mcvCard.setCardBackgroundColor(MaterialColors.getColor(mcvCard.context,com.google.android.material.R.attr.colorSurfaceContainerHigh, this::class.java.getCanonicalName()))
                } else {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        interface OnItemClickListener {
            fun onClickItem(view: View, position: Int, photoQuality: PhotoQuality)
        }
    }
}