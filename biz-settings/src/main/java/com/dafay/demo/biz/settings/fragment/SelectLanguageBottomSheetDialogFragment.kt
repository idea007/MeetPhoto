package com.dafay.demo.biz.settings.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dafay.demo.biz.settings.Language
import com.dafay.demo.biz.settings.PrefC
import com.dafay.demo.biz.settings.databinding.FragmentBottomSheetSelectLanguageBinding
import com.example.demo.biz.base.storage.sp.SPUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @Des
 * @Author m1studio
 * @Date 2024/1/16
 * <a href=" ">相关链接</a>
 */
class SelectLanguageBottomSheetDialogFragment : BottomSheetDialogFragment {

    private lateinit var binding: FragmentBottomSheetSelectLanguageBinding
    private lateinit var lauguageAdapter: LauguageAdapter

    val languageList = ArrayList<Language>().apply {
        add(Language(null, "跟随系统", "跟随系统"))
        add(Language("en", "English", "english"))
        add(Language("zh-CN", "中文", "中文"))
    }


    constructor() : super() {
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentBottomSheetSelectLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setContentView(binding.root)
        initViews()
        updateSelected()
        return bottomSheetDialog
    }

    private fun updateSelected() {

    }

    private fun initViews() {
        lauguageAdapter = LauguageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecyclerview.layoutManager = layoutManager
        binding.rvRecyclerview.adapter = lauguageAdapter
        lauguageAdapter.onItemClickListener = object : LauguageAdapter.LanguageViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, position: Int, language: Language) {
                val code: String = language.code?:""
                val previous = AppCompatDelegate.getApplicationLocales()
                val selected = LocaleListCompat.forLanguageTags(code)
                if (previous != selected) {
                    lauguageAdapter.currentSelectedCode = language.code
                    lauguageAdapter.notifyDataSetChanged()
                    dismiss()
                    SPUtils.putPreference(PrefC.LANGUAGE, code)
                    AppCompatDelegate.setApplicationLocales(selected)
                }
            }
        }
        lauguageAdapter.setDatas(languageList)
    }
}