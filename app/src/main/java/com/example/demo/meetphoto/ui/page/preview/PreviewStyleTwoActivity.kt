package com.example.demo.meetphoto.ui.page.preview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.dafay.demo.lib.base.ui.widget.FullZoomImageView
import com.dafay.demo.lib.base.ui.widget.KtElasticDragDismissFrameLayout
import com.dafay.demo.lib.base.utils.ScreenUtils
import com.dafay.demo.lib.base.utils.VibratorManager
import com.dafay.demo.lib.base.utils.debug
import com.example.demo.biz.base.storage.sp.SPUtils
import com.example.demo.meetphoto.R
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.data.model.PhotoQualityType
import com.example.demo.meetphoto.data.model.PhotoQualityType.*
import com.example.demo.meetphoto.data.model.PrefC
import com.example.demo.meetphoto.data.model.UrlsInfo
import com.example.demo.meetphoto.databinding.ActivityPreviewStyleTwoBinding
import com.example.demo.meetphoto.ui.base.BaseThemeActivity
import com.example.demo.meetphoto.ui.page.preview.fragment.PhotoInfoBottomSheetDialogFragment
import com.example.demo.meetphoto.ui.page.preview.viewmodel.PreviewViewModel
import com.example.demo.meetphoto.ui.page.webview.WebViewActivity
import com.example.demo.meetphoto.ui.view.dismissLoadingExt
import com.example.demo.meetphoto.ui.view.showLoadingExt
import com.example.demo.meetphoto.utils.HandlerSingleton
import com.example.demo.meetphoto.utils.StatusBarUtils
import com.example.demo.meetphoto.utils.saveToAlbum
import java.util.concurrent.Executors


class PreviewStyleTwoActivity : BaseThemeActivity<ActivityPreviewStyleTwoBinding>() {
    companion object {
        fun startActivity(activity: Activity, photo: Photo) {
            val intent = Intent(activity, PreviewStyleTwoActivity::class.java)
            intent.putExtra("photo", photo)
            activity.startActivity(intent)
        }
    }

    lateinit var passPhoto: Photo

    private val executor = Executors.newSingleThreadExecutor()

    private var preRawOffset = 0f

    private val previewViewModel: PreviewViewModel by lazy {
        ViewModelProvider(this).get(PreviewViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_open_link, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_link -> {
                WebViewActivity.startActivity(this, Uri.parse(passPhoto.links?.html))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun resolveIntent(intent: Intent?) {
        super.resolveIntent(intent)
        val thum = intent?.getParcelableExtra<Photo>("photo")
        if (thum == null) {
            finish()
            return
        }
        passPhoto = thum
    }

    @SuppressLint("CommitTransaction")
    override fun initViews(savedInstanceState: Bundle?) {
        binding.flDragDismiss.addListener(object : KtElasticDragDismissFrameLayout.SystemChromeFader(this@PreviewStyleTwoActivity) {
            override fun onDrag(elasticOffset: Float, elasticOffsetPixels: Float, rawOffset: Float, rawOffsetPixels: Float) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels)
                debug("rawOffset=${rawOffset}" + " rawOffsetPixels=${rawOffsetPixels}")
                if ((rawOffset >= 1 && preRawOffset < 1) || (preRawOffset >= 1 && rawOffset < 1)) {
                    preRawOffset = rawOffset
                    VibratorManager.heavyClick()
                }
            }

            override fun onDragDismissed() {
                super.onDragDismissed()
            }
        })
        initStatusBar()
        initToolBar()
        initImageZoom()
        loadImage()
    }

    private fun initStatusBar() {
        StatusBarUtils.transparentStatusBar(this)
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.translationY = StatusBarUtils.getStatusBarHeight(this).toFloat()

        Glide.with(binding.ivUser)
            .load(passPhoto.user?.profile_image?.large)
            .into(binding.ivUser)
        binding.tvUserName.text = passPhoto.user?.name
    }

    private fun initImageZoom() {
        var screenWidth = ScreenUtils.getScreenWidth().toFloat()
        var screenHeight = ScreenUtils.getScreenHeight().toFloat() + StatusBarUtils.getStatusBarHeight(this)

        var screenRatio = screenWidth / screenHeight
        var imageRatio = (passPhoto.width ?: 0) / (passPhoto.height ?: 0).toFloat()
        var tempMidZoom = 2.0f
        if (imageRatio > screenRatio) {
            tempMidZoom = imageRatio / screenRatio
        } else {
            tempMidZoom = screenRatio / imageRatio
        }
        binding.zivImg.setMinScale(1f)
        binding.zivImg.setMaxScale(tempMidZoom * 2f)
        binding.zivImg.setMidScale(tempMidZoom)
    }

    private fun loadImage() {
        // We don't want Glide to crop or resize our image
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(Target.SIZE_ORIGINAL)
            .dontTransform()

        val thumbRequest: RequestBuilder<Drawable> = Glide.with(binding.zivImg)
            .load(passPhoto.urls.small)
            .apply(options)

        Glide.with(binding.zivImg)
            .load(passPhoto.urls.regular)
            .apply(options)
            .thumbnail(thumbRequest)
            .into(binding.zivImg)
    }

    override fun initObserver() {
        super.initObserver()
        previewViewModel.photoLiveData.observe(this) {
            passPhoto = it
        }
    }

    override fun bindListener() {
        super.bindListener()

        binding.ivUser.setOnClickListener {
            val uri = Uri.parse(passPhoto.user?.links?.html)
            WebViewActivity.startActivity(this@PreviewStyleTwoActivity, uri)
        }

        binding.zivImg.setOnViewTapListener(object : FullZoomImageView.OnViewTapListener {
            override fun onViewTap(view: View?, x: Float, y: Float) {
                VibratorManager.click()
                if (binding.rlControl.visibility == View.VISIBLE) {
                    binding.rlControl.visibility = View.GONE
                } else {
                    binding.rlControl.visibility = View.VISIBLE
                }
            }

            override fun onLongClick(view: View?) {
            }

        })

        binding.mcvCardInfo.setOnClickListener {
            PhotoInfoBottomSheetDialogFragment(passPhoto, object : PhotoInfoBottomSheetDialogFragment.OnClickListener {
                override fun onUserClick() {
                    val uri = Uri.parse(passPhoto.user?.links?.html)
                    WebViewActivity.startActivity(this@PreviewStyleTwoActivity, uri)
                }

                override fun onApplyClick() {
                    setWallpaper()
                }

                override fun onDownloadClick() {
                    download()
                }
            }).show(supportFragmentManager, null)
        }
        binding.mcvCardPreview.setOnClickListener {
            setWallpaper()
        }

        binding.mcvCardDownload.setOnClickListener {
            download()
        }
    }

    private fun download() {
        showLoadingExt(getString(R.string.downloading))
        Glide.with(this).asBitmap().load(downloadUrl(passPhoto.urls))
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                    dismissLoadingExt()
                    Toast.makeText(this@PreviewStyleTwoActivity, getString(R.string.image_saved_failed), Toast.LENGTH_SHORT)
                        .show()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    previewViewModel.photoDownload(passPhoto.id)
                    saveFrameToAlbum(resource)
                }
            })
    }

    private fun downloadUrl(urls: UrlsInfo): String? {
        val qualityType = PhotoQualityType.from(SPUtils.findPreference<String>(PrefC.PHOTO_QUALITY_DOWNLAOD, REGULAR.key))
        when (qualityType) {
            RAW -> return urls.raw
            FULL -> return urls.full
            REGULAR -> return urls.regular
            SMALL -> return urls.small
            THUMB -> return urls.thumb
        }
    }

    private fun setWallpaper() {
        showLoadingExt(getString(R.string.setting_up))
        // 方法二：从 glide 获取 bitmap
        Glide.with(this).asBitmap().load(passPhoto.urls.full)
            .listener(object:RequestListener<Bitmap>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                    dismissLoadingExt()
                    Toast.makeText(this@PreviewStyleTwoActivity, getString(R.string.wallpaper_settings_failed), Toast.LENGTH_SHORT)
                        .show()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                previewViewModel.photoDownload(passPhoto.id)
                executor.execute({
                    WallpaperManager.getInstance(this@PreviewStyleTwoActivity).setBitmap(resource)
                    HandlerSingleton.mainHandler.post {
                        dismissLoadingExt()
                        Toast.makeText(this@PreviewStyleTwoActivity, getString(R.string.wallpaper_settings_success), Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        })
    }

    override fun initializeData() {
        super.initializeData()
        previewViewModel.getPhotoDetail(passPhoto.id)
    }

    private fun saveFrameToAlbum(resource: Bitmap) {
        val fileName = getFileName(passPhoto.urls.full ?: "")
        executor.execute({
            resource.saveToAlbum(this, fileName = fileName, null)
            HandlerSingleton.mainHandler.post({
                dismissLoadingExt()
                Toast.makeText(this, getString(R.string.image_saved_success), Toast.LENGTH_SHORT).show()
            })
        })
    }

    fun getFileName(url: String): String {
        var name = url.split("?")[0]
        name = name.replace("https://images.unsplash.com/", "")
        var fm = url.split("?")[1].split("&").filter { it.startsWith("fm=") }[0].replace("fm=", "")
        return name + "." + fm
    }
}