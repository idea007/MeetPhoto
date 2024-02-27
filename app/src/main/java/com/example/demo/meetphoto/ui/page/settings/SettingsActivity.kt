package com.example.demo.meetphoto.ui.page.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dafay.demo.lib.base.utils.AppUtils
import com.dafay.demo.lib.base.utils.RxBus
import com.dafay.demo.lib.base.utils.VibratorManager
import com.example.demo.biz.base.storage.sp.SPUtils
import com.example.demo.meetphoto.R
import com.example.demo.meetphoto.data.model.COLOR_THEME
import com.example.demo.meetphoto.data.model.ConfigC
import com.example.demo.meetphoto.data.model.ExtraC
import com.example.demo.meetphoto.data.model.PhotoQualityType
import com.example.demo.meetphoto.data.model.PrefC
import com.example.demo.meetphoto.databinding.ActivitySettingsBinding
import com.example.demo.meetphoto.ui.base.BaseThemeActivity
import com.example.demo.meetphoto.ui.helper.CacheHelper
import com.example.demo.meetphoto.ui.helper.CommonMessage
import com.example.demo.meetphoto.ui.page.settings.fragment.SelectDarkModeBottomSheetDialogFragment
import com.example.demo.meetphoto.ui.page.settings.fragment.SelectLanguageBottomSheetDialogFragment
import com.example.demo.meetphoto.ui.page.settings.fragment.SelectPictureQualityBottomSheetDialogFragment
import com.example.demo.meetphoto.ui.view.SelectionCardView
import com.example.demo.meetphoto.ui.page.webview.WebViewActivity
import com.example.demo.meetphoto.utils.HandlerSingleton
import com.example.demo.meetphoto.utils.ResUtil
import com.example.demo.meetphoto.utils.UiUtil
import com.example.demo.meetphoto.utils.ViewUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.utilities.Hct
import com.google.android.material.divider.MaterialDivider


class SettingsActivity : BaseThemeActivity<ActivitySettingsBinding>() {

    private val settingsViewModel: SettingsViewModel by lazy {
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundleInstanceState = intent.getBundleExtra(ExtraC.SETTINGS_INSTANCE_BOUND)
        super.onCreate(bundleInstanceState ?: savedInstanceState)
    }

    override fun initViews() {
        super.initViews()
        initScrollViewPosition()
        initToolBar()
        initItems()
    }

    override fun initObserver() {
        super.initObserver()
        settingsViewModel.isVibratorTurnedOnLiveData.observe(this) {
            binding.itemVibrator.binding.smSwitch.isChecked = it
        }

        settingsViewModel.totalSizeLiveData.observe(this) {
            binding.itemClearCache.binding.tvDes.text = it
        }
    }

    override fun initializeData() {
        super.initializeData()
        settingsViewModel.isVibratorTurnedOnLiveData.postValue(SPUtils.findPreference(PrefC.VIBRATOR_STATE, false))
        settingsViewModel.queryTotalCache()
    }

    private fun initScrollViewPosition() {
        binding.root.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@SettingsActivity.getIntent().getBundleExtra(ExtraC.SETTINGS_INSTANCE_BOUND)?.let {
                    val themeScrollX = it.getInt(ExtraC.SETTINGS_COLOR_THEME_SCROLL_X, 0)
                    val settingsScrollY = it.getInt(ExtraC.SETTINGS_SCROLL_Y)
                    binding.svMoreColorTheme.scrollTo(themeScrollX, 0)
                    binding.svSettings.scrollTo(0, settingsScrollY)
                }
                binding.root.getViewTreeObserver().removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true)//设置返回键可用
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initItems() {
        // 风格设置栏
        binding.itemSettingsOption.apply {
            binding.tvTitle.text = getString(R.string.settings_options)
            val textColor = ResUtil.getColorAttr(context, com.google.android.material.R.attr.colorPrimary)
            binding.tvTitle.setTextColor(textColor)
            binding.tvDes.visibility = View.GONE
            binding.ivIcon.visibility = View.VISIBLE
            binding.ivIcon.setImageResource(R.drawable.vector_manufacturing_24dp)
        }

        // item language
        binding.itemLanguage.apply {
            binding.tvTitle.text = getString(R.string.language)
            binding.tvDes.text = getString(R.string.language_name)
            binding.ivIcon.setImageResource(R.drawable.vector_language_24dp)
            binding.mcvCard.setOnClickListener {
                SelectLanguageBottomSheetDialogFragment().show(supportFragmentManager, null)
            }
        }

        // item dark mode
        binding.itemDarkMode.apply {
            binding.tvTitle.text = getString(R.string.dark_mode)
            updateCurrentMode(AppCompatDelegate.getDefaultNightMode())
            binding.mcvCard.setOnClickListener {
                SelectDarkModeBottomSheetDialogFragment(AppCompatDelegate.getDefaultNightMode(),
                    object : SelectDarkModeBottomSheetDialogFragment.ModeChangeCallback {
                        override fun onModeChange(mode: Int) {
                            updateCurrentMode(mode)
                            SPUtils.putPreference(PrefC.DARK_MODE, mode)
                            when (mode) {
                                AppCompatDelegate.MODE_NIGHT_NO -> {
                                    AppCompatDelegate.setDefaultNightMode(mode)
                                }

                                AppCompatDelegate.MODE_NIGHT_YES -> {
                                    AppCompatDelegate.setDefaultNightMode(mode)
                                }

                                else -> {
                                    AppCompatDelegate.setDefaultNightMode(mode)
                                }
                            }
                        }
                    }).show(supportFragmentManager, null)
            }
        }

        // item color theme
        binding.itemTheme.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_format_paint_24dp)
            binding.tvTitle.text = getString(R.string.color_theme)
            var colorTheme = COLOR_THEME.from(SPUtils.findPreference(PrefC.COLOR_THEME, ""))
            binding.tvDes.text = context.getString(colorTheme.nameResId)
        }

        setUpThemeSelection()

        // item Vibrator
        binding.itemVibrator.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_edgesensor_low_24dp)
            binding.tvTitle.text = getString(R.string.vibrator_mode)
            binding.smSwitch.visibility = View.VISIBLE
            updateVibratorState()
            binding.smSwitch.setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    SPUtils.putPreference(PrefC.VIBRATOR_STATE, isChecked)
                    VibratorManager.setEnabled(isChecked)
                    updateVibratorState()
                }
            })
            binding.mcvCard.setOnClickListener {
                if (VibratorManager.areSystemHapticsTurnedOn(this@SettingsActivity)) {
                    binding.smSwitch.isChecked = !binding.smSwitch.isChecked
                }
            }
            binding.mdDivider.visibility = View.VISIBLE
        }

        // 下载栏
        binding.itemDownloadOption.apply {
            binding.ivIcon.visibility = View.VISIBLE
            binding.ivIcon.setImageResource(R.drawable.vector_settings_download_24dp)
            binding.tvTitle.text = getString(R.string.download_options)
            binding.tvTitle.setTextColor(ResUtil.getColorAttr(context, com.google.android.material.R.attr.colorPrimary))
            binding.tvDes.visibility = View.GONE
        }

        binding.itemDownload.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_imagesmode_24dp)
            binding.tvTitle.text = context.getString(R.string.picture_quality)
            val photoQualityType = PhotoQualityType.from(SPUtils.findPreference(PrefC.PHOTO_QUALITY_DOWNLAOD, PhotoQualityType.REGULAR.key))
            binding.tvDes.text = getString(photoQualityType.nameResId)
            binding.mcvCard.setOnClickListener {
                SelectPictureQualityBottomSheetDialogFragment(object : SelectPictureQualityBottomSheetDialogFragment.QualityChangeCallback {
                    override fun onQualityChange(type: PhotoQualityType) {
                        binding.tvDes.text = getString(type.nameResId)
                    }
                }).show(supportFragmentManager, null)
            }
        }

        binding.itemClearCache.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_autorenew_24dp)
            binding.tvTitle.text = context.getString(R.string.clear_cache)
            binding.mdDivider.visibility = View.VISIBLE
            binding.mcvCard.setOnClickListener {
                settingsViewModel.clearCache()
            }
        }

        binding.itemMoreOption.apply {
            binding.tvTitle.text = getString(R.string.app_info_options)
            val textColor = ResUtil.getColorAttr(context, com.google.android.material.R.attr.colorPrimary)
            binding.tvTitle.setTextColor(textColor)
            binding.tvDes.visibility = View.GONE
            binding.ivIcon.visibility = View.VISIBLE
            binding.ivIcon.setImageResource(R.drawable.vector_settings_info_24dp)
        }

        binding.itemVersion.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_tag_24dp)
            binding.tvTitle.text = context.getString(R.string.app_version)
            binding.tvDes.text = AppUtils.getAppVersionName(this@SettingsActivity)
            binding.mcvCard.setOnClickListener { }
        }

        binding.itemDeveloper.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_person_24dp)
            binding.tvTitle.text = context.getString(R.string.developer)
            binding.tvDes.text = "dafay"
            binding.mcvCard.setOnClickListener {}
        }

        binding.itemOtherApp.apply {
            binding.tvTitle.text = "开发者别的 App"
            binding.tvDes.text = "google play"
            binding.ivIcon.setImageResource(R.drawable.vector_storefront_24dp)
            binding.mcvCard.setOnClickListener {}
        }

        binding.itemAgreement.apply {
            binding.tvTitle.text = "用户协议"
            binding.tvDes.visibility = View.GONE
            binding.ivIcon.setImageResource(R.drawable.vector_developer_guide_24dp)
            binding.mcvCard.setOnClickListener {}
        }

        binding.itemPrivacyPolicy.apply {
            binding.ivIcon.setImageResource(R.drawable.vector_security_24dp)
            binding.tvTitle.text = context.getString(R.string.privacy_policy)
            binding.tvDes.visibility = View.GONE
            binding.mcvCard.setOnClickListener {
                val uri = Uri.parse(ConfigC.PRIVACY_POLICY)
                WebViewActivity.startActivity(this@SettingsActivity, uri)
            }
        }

    }

    private fun updateVibratorState() {
        binding.itemVibrator.apply {
            binding.tvDes.text =
                if (SPUtils.findPreference(PrefC.VIBRATOR_STATE, false)) getString(R.string.turned_on) else getString(R.string.turned_off)
            if (!VibratorManager.areSystemHapticsTurnedOn(this@SettingsActivity)) {
                binding.tvDes.text = getString(R.string.system_haptics_turnedoff)
                binding.smSwitch.isEnabled = false
            } else {
                binding.smSwitch.isEnabled = true
            }
        }

    }

    // 重启应用
    fun restartToApply(delay: Long) {
        // 重启之前 scrollView 滚动的位置
        val bundle = Bundle().apply {
            putInt(ExtraC.SETTINGS_SCROLL_Y, binding.svSettings.scrollY)
            putInt(ExtraC.SETTINGS_COLOR_THEME_SCROLL_X, binding.svMoreColorTheme.scrollX)
        }
        HandlerSingleton.mainHandler.postDelayed({
            onSaveInstanceState(bundle)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                finish()
            }
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(ExtraC.SETTINGS_INSTANCE_BOUND, bundle)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                finish()
            }
            if (UiUtil.areAnimationsEnabled(this)) {
                overridePendingTransition(R.anim.fade_in_restart, R.anim.fade_out_restart)
            } else {
                overridePendingTransition(-1, -1)
            }
        }, delay)
    }

    @SuppressLint("RestrictedApi")
    private fun getColorRole(@ColorInt color: Int, @IntRange(from = 0, to = 100) tone: Int): Int {
        val hctColor = Hct.fromInt(color)
        hctColor.tone = tone.toDouble()
        return hctColor.toInt()
    }

    @SuppressLint("RestrictedApi")
    private fun setUpThemeSelection() {
        val hasDynamic = DynamicColors.isDynamicColorAvailable()
        val container: ViewGroup = binding.linearOtherThemeContainer
        for (i in (if (hasDynamic) 0 else 1) until COLOR_THEME.values().size) {
            var colorTheme = COLOR_THEME.values()[i]
            val card = SelectionCardView(this)
            card.setScrimEnabled(false, false)
            var color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && i == 0) {
                ContextCompat.getColor(
                    this,
                    if (UiUtil.isDarkModeActive(this)) android.R.color.system_accent1_700 else android.R.color.system_accent1_100
                )
            } else {
                getColorRole(colorTheme.color, 70)
            }
            card.setCardBackgroundColor(color)
            card.setOnClickListener { v ->
                if (!card.isChecked()) {
                    card.startCheckedIcon()
                    ViewUtil.uncheckAllChildren(container)
                    card.setChecked(true)
                    SPUtils.putPreference(PrefC.COLOR_THEME, colorTheme.key)
                    RxBus.post(CommonMessage(CommonMessage.Type.CHANGE_THEME))
                    restartToApply(100)
                }
            }
            val selectedColorThemeKey: String = SPUtils.findPreference(PrefC.COLOR_THEME, COLOR_THEME.DYNAMIC.key)
            var isSelected = selectedColorThemeKey == colorTheme.key
            card.setChecked(isSelected)
            container.addView(card)
            if (hasDynamic && i == 0) {
                val divider = MaterialDivider(this)
                val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    UiUtil.dpToPx(this, 1f), UiUtil.dpToPx(this, 40f)
                )
                var marginLeft: Int
                var marginRight: Int
                if (UiUtil.isLayoutRtl(this)) {
                    marginLeft = UiUtil.dpToPx(this, 8f)
                    marginRight = UiUtil.dpToPx(this, 4f)
                } else {
                    marginLeft = UiUtil.dpToPx(this, 4f)
                    marginRight = UiUtil.dpToPx(this, 8f)
                }
                layoutParams.setMargins(marginLeft, 0, marginRight, 0)
                layoutParams.gravity = Gravity.CENTER_VERTICAL
                divider.layoutParams = layoutParams
                container.addView(divider)
            }
        }
    }

    private fun updateCurrentMode(mode: Int) {
        binding.itemDarkMode.binding.apply {
            when {
                mode == AppCompatDelegate.MODE_NIGHT_NO -> {
                    tvDes.text = getString(R.string.current_mode_night_no)
                }

                mode == AppCompatDelegate.MODE_NIGHT_YES -> {
                    tvDes.text = getString(R.string.current_mode_night_yes)
                }

                else -> {
                    tvDes.text = getString(R.string.current_mode_follow_system)
                }
            }
        }
    }
}