package com.dafay.demo.biz.settings.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.dafay.demo.biz.settings.R
import com.dafay.demo.biz.settings.databinding.FragmentBottomSheetSelectDayModeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/16
 * <a href=" ">相关链接</a>
 */
class SelectDarkModeBottomSheetDialogFragment : BottomSheetDialogFragment {

    private lateinit var binding: FragmentBottomSheetSelectDayModeBinding

    private var currentNightMode: Int
    private var modeChangeCallback: ModeChangeCallback? = null

    constructor(defaultNightMode: Int, modeChangeCallback: ModeChangeCallback? = null) : super() {
        this.currentNightMode = defaultNightMode
        this.modeChangeCallback = modeChangeCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentBottomSheetSelectDayModeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setContentView(binding.root)
        initViews()
        updateSelected()
        return bottomSheetDialog
    }

    private fun updateSelected() {
        when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                binding.uvItemDay.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }

                binding.uvItemNight.binding.apply {
                    ivIcon.visibility = View.VISIBLE
                    mcvCard.setCardBackgroundColor(MaterialColors.getColor(mcvCard.context,com.google.android.material.R.attr.colorSurfaceContainerHigh, this::class.java.getCanonicalName()))
                }

                binding.uvItemAuto.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }

            AppCompatDelegate.MODE_NIGHT_NO -> {
                binding.uvItemDay.binding.apply {
                    ivIcon.visibility = View.VISIBLE
                    mcvCard.setCardBackgroundColor(MaterialColors.getColor(mcvCard.context,com.google.android.material.R.attr.colorSurfaceContainerHigh, this::class.java.getCanonicalName()))
                }

                binding.uvItemNight.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }

                binding.uvItemAuto.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }

            else -> {
                binding.uvItemDay.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }

                binding.uvItemNight.binding.apply {
                    ivIcon.visibility = View.INVISIBLE
                    mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
                }

                binding.uvItemAuto.binding.apply {
                    ivIcon.visibility = View.VISIBLE
                    mcvCard.setCardBackgroundColor(MaterialColors.getColor(mcvCard.context,com.google.android.material.R.attr.colorSurfaceContainerHigh, this::class.java.getCanonicalName()))

                }
            }
        }
    }

    private fun initViews() {
        binding.uvItemDay.binding.apply {
            tvTitle.text = getString(R.string.light_mode)
            tvDes.text = getString(R.string.light_mode_des)
            mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
            mcvCard.setOnClickListener {
                currentNightMode = AppCompatDelegate.MODE_NIGHT_NO
                updateSelected()
                modeChangeCallback?.onModeChange(currentNightMode)
                dismiss()
            }
        }

        binding.uvItemNight.binding.apply {
            tvTitle.text = getString(R.string.dark_mode)
            tvDes.text = getString(R.string.dark_mode_des)
            mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
            mcvCard.setOnClickListener {
                currentNightMode = AppCompatDelegate.MODE_NIGHT_YES
                updateSelected()
                modeChangeCallback?.onModeChange(currentNightMode)
                dismiss()
            }
        }

        binding.uvItemAuto.binding.apply {
            tvTitle.text = getString(R.string.follow_system)
            tvDes.text = getString(R.string.follow_system_des)
            mcvCard.setCardBackgroundColor(Color.TRANSPARENT)
            mcvCard.setOnClickListener {
                currentNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                updateSelected()
                modeChangeCallback?.onModeChange(currentNightMode)
                dismiss()
            }
        }
    }


    interface ModeChangeCallback {
        fun onModeChange(mode: Int)
    }
}