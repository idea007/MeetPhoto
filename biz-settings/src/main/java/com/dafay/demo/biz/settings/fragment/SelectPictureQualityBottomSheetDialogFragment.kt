package com.dafay.demo.biz.settings.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dafay.demo.biz.settings.PhotoQuality
import com.dafay.demo.biz.settings.PhotoQualityType
import com.dafay.demo.biz.settings.PrefC
import com.dafay.demo.biz.settings.databinding.FragmentBottomSheetSelectPictureQualityBinding
import com.example.demo.biz.base.storage.sp.SPUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/16
 * <a href=" ">相关链接</a>
 */
class SelectPictureQualityBottomSheetDialogFragment : BottomSheetDialogFragment {

    private lateinit var binding: FragmentBottomSheetSelectPictureQualityBinding
    private lateinit var pictureQualityAdapter: PictureQualityAdapter

    private var callback: QualityChangeCallback? = null

    constructor(callback: QualityChangeCallback?) : super() {
        this.callback = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentBottomSheetSelectPictureQualityBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setContentView(binding.root)
        initViews()
        updateSelected()
        return bottomSheetDialog
    }

    private fun updateSelected() {

    }

    private fun initViews() {
        val temp = SPUtils.findPreference(PrefC.PHOTO_QUALITY_DOWNLAOD, PhotoQualityType.REGULAR.key)
        pictureQualityAdapter = PictureQualityAdapter(PhotoQualityType.from(temp))
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecyclerview.layoutManager = layoutManager
        binding.rvRecyclerview.adapter = pictureQualityAdapter
        pictureQualityAdapter.onItemClickListener = object : PictureQualityAdapter.PhotoQualityViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, position: Int, photoQuality: PhotoQuality) {
                if (SPUtils.findPreference(PrefC.PHOTO_QUALITY_DOWNLAOD, PhotoQualityType.REGULAR.key) != photoQuality.qualityType.key) {
                    pictureQualityAdapter.curPhotoQualityType = photoQuality.qualityType
                    pictureQualityAdapter.notifyDataSetChanged()
                    SPUtils.putPreference(PrefC.PHOTO_QUALITY_DOWNLAOD, photoQuality.qualityType.key)
                    callback?.onQualityChange(photoQuality.qualityType)
                }
                dismiss()
            }
        }

        pictureQualityAdapter.setDatas(PhotoQualityType.values().toList().map {
            PhotoQuality(it)
        })
    }

    interface QualityChangeCallback {
        fun onQualityChange(type: PhotoQualityType)
    }
}