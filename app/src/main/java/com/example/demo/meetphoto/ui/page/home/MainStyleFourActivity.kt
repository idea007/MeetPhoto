package com.example.demo.meetphoto.ui.page.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dafay.demo.lib.base.deprecaped.base.model.RequestState
import com.dafay.demo.lib.base.ui.widget.RecyclerViewInfiniteScrollListener
import com.dafay.demo.lib.base.utils.RxBus
import com.dafay.demo.lib.base.utils.dp2px
import com.example.demo.biz.base.widgets.GridMarginDecoration
import com.example.demo.meetphoto.R
import com.example.demo.meetphoto.data.model.CollectionInfo
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.databinding.ActivityMainStyleFourBinding
import com.example.demo.meetphoto.ui.base.BaseThemeActivity
import com.example.demo.meetphoto.ui.helper.CommonMessage
import com.example.demo.meetphoto.ui.page.home.adapter.HomeAdapter
import com.example.demo.meetphoto.ui.page.home.adapter.HomeItemInfo
import com.example.demo.meetphoto.ui.page.home.adapter.HomeItemType
import com.example.demo.meetphoto.ui.page.home.adapter.toHomeItemInfoPhotoList
import com.example.demo.meetphoto.ui.page.home.viewmodel.PhotosViewModel
import com.example.demo.meetphoto.ui.page.home.viewmodel.SearchViewModel
import com.example.demo.meetphoto.ui.page.preview.PreviewStyleTwoActivity
import com.example.demo.meetphoto.ui.page.settings.SettingsActivity
import com.google.android.material.color.MaterialColors
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 * https://gist.github.com/RicardAparicio/f41523daaa0edbe0b4399549fff4da3f
 * <a href="https://juejin.cn/post/7145021109564342309"> AppBarLayout 闪烁问题</a>
 */

class MainStyleFourActivity : BaseThemeActivity<ActivityMainStyleFourBinding>() {

    private val photosViewModel: PhotosViewModel by lazy {
        ViewModelProvider(this).get(PhotosViewModel::class.java)
    }
    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var homeAdapter: HomeAdapter
    private val homeItemList = ArrayList<HomeItemInfo<*>>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initViews() {
        super.initViews()
        initToolBar()
        initRecyclerView()
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun initRecyclerView() {
        homeItemList.clear()
        homeAdapter = HomeAdapter()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.rvRecyclerview.addItemDecoration(GridMarginDecoration(4.dp2px, 4.dp2px, 4.dp2px, 4.dp2px))
        binding.rvRecyclerview.layoutManager = staggeredGridLayoutManager
        binding.rvRecyclerview.adapter = homeAdapter
        binding.rvRecyclerview.addOnScrollListener(object : RecyclerViewInfiniteScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore() {
                searchViewModel.loadMorePhotos()
            }
        })

        homeAdapter.onHomeCarouselClickListener = object : HomeAdapter.HomeCarouselViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, parentPosition: Int, childPosition: Int, photo: Photo) {
                PreviewStyleTwoActivity.startActivity(this@MainStyleFourActivity, photo)
            }
        }
        homeAdapter.onHomePhotoCardClickListener = object : HomeAdapter.HomePhotoCardViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, position: Int, photo: Photo) {
                PreviewStyleTwoActivity.startActivity(this@MainStyleFourActivity, photo)
            }
        }

        binding.srlRefresh.setProgressBackgroundColorSchemeColor(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorOnPrimary,
                this::class.java.getCanonicalName()
            )
        )
        binding.srlRefresh.setColorSchemeColors(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorPrimary,
                this::class.java.getCanonicalName()
            )
        )
        binding.srlRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                homeItemList.clear()
                photosViewModel.refreshPhotos()
            }
        })
    }

    @SuppressLint("CheckResult")
    override fun initObserver() {
        super.initObserver()
        RxBus.toObservable(CommonMessage::class.java).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it.type) {
                    CommonMessage.Type.CHANGE_THEME -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            restartToApply()
                        }
                    }
                }
            })

        photosViewModel.refreshPhotosLiveData.observe(this) {
            if (it.state != RequestState.START) {
                searchViewModel.refreshPhotos()
            }
            when {
                it.state == RequestState.SUCCESS -> {
                    val temp = ArrayList<HomeItemInfo<*>>()
                    temp.add(HomeItemInfo<String>(HomeItemType.TYPE_TITLE, getString(R.string.recommend)))
                    temp.add(HomeItemInfo<List<Photo>>(HomeItemType.TYPE_CAROUSEL, it.data))
                    homeItemList.addAll(temp)

                }
            }
        }

        searchViewModel.refreshPhotosLiveData.observe(this) {
            if (it.state != RequestState.START) {
                binding.srlRefresh.setRefreshing(false)
            }
            when {
                it.state == RequestState.SUCCESS -> {
                    binding.uvPrompt.visibility = View.GONE
                    val temp = ArrayList<HomeItemInfo<*>>()
                    temp.add(HomeItemInfo<String>(HomeItemType.TYPE_TITLE, getString(R.string.wallpaper)))
                    temp.addAll(it.data.toHomeItemInfoPhotoList())
                    homeItemList.addAll(temp)
                    homeAdapter.setDatas(homeItemList)
                }

                it.state == RequestState.NO_NETWORK -> {
                    if (homeItemList.size <= 0 && homeAdapter.datas.size<=0) {
                        binding.uvPrompt.setTipImgAndTipText(R.drawable.ic_page_nonet, getString(R.string.tip_no_net))
                        binding.uvPrompt.visibility = View.VISIBLE
                    }
                }

                it.state == RequestState.NO_DATA -> {
                    if (homeItemList.size <= 0 && homeAdapter.datas.size<=0) {
                        binding.uvPrompt.setTipImgAndTipText(R.drawable.ic_page_nodata, getString(R.string.no_data))
                        binding.uvPrompt.visibility = View.VISIBLE
                    }
                }
            }
        }

        searchViewModel.loadMorePhotosLiveData.observe(this) {
            when {
                it.state == RequestState.SUCCESS -> {
                    homeAdapter.addDatas(it.data.toHomeItemInfoPhotoList())
                }
            }
        }
    }

    private fun restartToApply() {
        this.recreate()
    }


    override fun initializeData() {
        super.initializeData()
        photosViewModel.refreshPhotos()
    }
}