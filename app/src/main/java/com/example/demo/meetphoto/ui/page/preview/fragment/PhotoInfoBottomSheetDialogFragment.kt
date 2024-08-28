package com.example.demo.meetphoto.ui.page.preview.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.databinding.FragmentBottomSheetPhotoInfoBinding
import com.dafay.demo.biz.settings.NumberUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/16
 * <a href=" ">相关链接</a>
 */
class PhotoInfoBottomSheetDialogFragment : BottomSheetDialogFragment {

    private lateinit var binding: FragmentBottomSheetPhotoInfoBinding
    private val passPhoto: Photo
    private val listener: OnClickListener?

    constructor(passPhoto: Photo, listener: OnClickListener?) : super() {
        this.passPhoto = passPhoto
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentBottomSheetPhotoInfoBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setContentView(binding.root)
        initViews()
        return bottomSheetDialog
    }


    private fun initViews() {
        Glide.with(binding.ivUser)
            .load(passPhoto.user?.profile_image?.large)
            .into(binding.ivUser)
        binding.tvUserName.text = passPhoto.user?.name

        binding.tvLikes.text = if ((passPhoto.likes ?: 0) > 0) com.dafay.demo.biz.settings.NumberUtils.amountConversion((passPhoto.likes ?: 0) + 0.0) else "Unknow"
        binding.tvDownloads.text =
            if ((passPhoto.downloads ?: 0) > 0) com.dafay.demo.biz.settings.NumberUtils.amountConversion((passPhoto.downloads ?: 0) + 0.0) else "Unknow"
        binding.tvViews.text = if ((passPhoto.views ?: 0) > 0) com.dafay.demo.biz.settings.NumberUtils.amountConversion((passPhoto.views ?: 0) + 0.0) else "Unknow"
        if (passPhoto.description.isNullOrEmpty()) {
            binding.tvDes.visibility = View.GONE
            binding.mdDividerDes.visibility = View.GONE
        } else {
            binding.tvDes.text = passPhoto.description
        }
        binding.btnSetWallpaper.setOnClickListener {
            dismiss()
            listener?.onApplyClick()
        }

        binding.ivUser.setOnClickListener {
            dismiss()
            listener?.onUserClick()
        }
        binding.btnDownload.setOnClickListener {
            dismiss()
            listener?.onDownloadClick()
        }
        if (passPhoto.exif?.model.isNullOrEmpty()) {
            binding.llCamera.visibility = View.GONE
        } else {
            binding.tvCameraMode.text = passPhoto.exif?.model ?: "Unknow"
        }

        var sb = StringBuilder()
        if (!passPhoto.exif?.focal_length.isNullOrEmpty()) {
            sb.append("${passPhoto.exif?.focal_length}mm ")
        }
        if (!passPhoto.exif?.aperture.isNullOrEmpty()) {
            sb.append("f/${passPhoto.exif?.aperture} ")
        }
        if (!passPhoto.exif?.exposure_time.isNullOrEmpty()) {
            sb.append("${passPhoto.exif?.exposure_time}s ")
        }
        if ((passPhoto.exif?.iso ?: 0) > 0) {
            sb.append("ISO ${passPhoto.exif?.iso}")
        }
        if (sb.toString().isNullOrEmpty()) {
            binding.llParam.visibility = View.GONE
        } else {
            binding.tvParam.text = if (sb.toString().length > 0) sb.toString() else "Unknow"
        }
        binding.tvDay.text = passPhoto.created_at
        val location = passPhoto.location?.name ?: passPhoto.location?.city
        if (location.isNullOrEmpty()) {
            binding.llLocation.visibility = View.GONE
        } else {
            binding.tvLocation.text = location
        }
    }

    interface OnClickListener {
        fun onUserClick()
        fun onApplyClick()
        fun onDownloadClick()
    }
}