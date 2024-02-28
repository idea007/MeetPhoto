package com.example.demo.meetphoto.ui.page.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.net.Result
import com.dafay.demo.lib.base.ui.widget.RecyclerViewInfiniteScrollListener
import com.dafay.demo.lib.base.ui.widgets.itemdecoration.GridMarginDecoration
import com.dafay.demo.lib.base.utils.RxBus
import com.dafay.demo.lib.base.utils.dp2px
import com.example.demo.meetphoto.R
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.databinding.ActivityMainBinding
import com.example.demo.meetphoto.ui.base.BaseThemeActivity
import com.example.demo.meetphoto.ui.helper.CommonMessage
import com.example.demo.meetphoto.ui.page.home.adapter.HomeAdapter
import com.example.demo.meetphoto.ui.page.home.newvm.NewHomeViewModel
import com.example.demo.meetphoto.ui.page.home.newvm.PhotosRepository
import com.example.demo.meetphoto.ui.page.preview.PreviewActivity
import com.example.demo.meetphoto.ui.page.settings.SettingsActivity
import com.google.android.material.color.MaterialColors
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 * https://gist.github.com/RicardAparicio/f41523daaa0edbe0b4399549fff4da3f
 * <a href="https://juejin.cn/post/7145021109564342309"> AppBarLayout 闪烁问题</a>
 */
class MainActivity : BaseThemeActivity(R.layout.activity_main) {

    override val binding: ActivityMainBinding by viewBinding()

    private val viewModel by lazy { NewHomeViewModel(PhotosRepository()) }

    private lateinit var homeAdapter: HomeAdapter

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
        homeAdapter = HomeAdapter()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.rvRecyclerview.addItemDecoration(GridMarginDecoration(4.dp2px, 4.dp2px, 4.dp2px, 4.dp2px))
        binding.rvRecyclerview.layoutManager = staggeredGridLayoutManager
        binding.rvRecyclerview.adapter = homeAdapter
        binding.rvRecyclerview.addOnScrollListener(object : RecyclerViewInfiniteScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore() {
                viewModel.loadMore()
            }
        })

        homeAdapter.onHomeCarouselClickListener = object : HomeAdapter.HomeCarouselViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, parentPosition: Int, childPosition: Int, photo: Photo) {
                PreviewActivity.startActivity(this@MainActivity, photo)
            }
        }
        homeAdapter.onHomePhotoCardClickListener = object : HomeAdapter.HomePhotoCardViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, position: Int, photo: Photo) {
                PreviewActivity.startActivity(this@MainActivity, photo)
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
                viewModel.refresh(getString(R.string.recommend), getString(R.string.wallpaper))
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
                            this.recreate()
                        }
                    }
                }
            })

        viewModel.refreshHomeItemsLiveData.observe(this) {
            if (!(it is Result.Loading)) {
                binding.srlRefresh.setRefreshing(false)
            }
            when (it) {
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.uvPrompt.visibility = View.GONE
                    if (it.value.isEmpty()) {
                        binding.uvPrompt.setTipImgAndTipText(R.drawable.ic_page_nodata, getString(R.string.no_data))
                        binding.uvPrompt.visibility = View.VISIBLE
                    } else {
                        homeAdapter.setDatas(it.value)
                    }
                }

                is Result.NetworkError -> {
                    binding.uvPrompt.setTipImgAndTipText(R.drawable.ic_page_nonet, getString(R.string.tip_no_net))
                    binding.uvPrompt.visibility = View.VISIBLE
                }

                else -> {
                    binding.uvPrompt.setTipImgAndTipText(R.drawable.ic_page_nodata, getString(R.string.no_data))
                    binding.uvPrompt.visibility = View.VISIBLE
                }

            }
        }

        viewModel.loadMoreHomeItemsLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    homeAdapter.addDatas(it.value)
                }
                else -> {
                }
            }
        }
    }


    override fun initializeData() {
        super.initializeData()
        viewModel.refresh(getString(R.string.recommend), getString(R.string.wallpaper))
    }
}