package com.example.demo.meetphoto.ui.page.home.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.dafay.demo.lib.base.ui.adapter.BaseAdapter
import com.example.demo.meetphoto.data.model.CROSS_FADE_DURATION
import com.example.demo.meetphoto.data.model.Photo
import com.example.demo.meetphoto.databinding.ItemHomeCarouselBinding
import com.example.demo.meetphoto.databinding.ItemHomePhotoCardBinding
import com.example.demo.meetphoto.databinding.ItemHomeTitleBinding
import com.example.demo.meetphoto.ui.page.home.view.CarouselAdapter
import com.example.demo.meetphoto.ui.page.home.view.CarouselItemListener
import com.google.android.material.carousel.CarouselLayoutManager


/**
 * @Des
 * @Author lipengfei
 * @Date 2023/11/27 19:05
 */

class HomeAdapter : BaseAdapter<HomeItemInfo<*>>() {

    var onHomePhotoCardClickListener: HomePhotoCardViewHolder.OnItemClickListener? = null
    var onHomeCarouselClickListener: HomeCarouselViewHolder.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HomeItemType.TYPE_TITLE.value -> {
                HomeTitleViewHolder(ItemHomeTitleBinding.inflate(LayoutInflater.from(parent.context)))
            }

            HomeItemType.TYPE_CAROUSEL.value -> {
                HomeCarouselViewHolder(ItemHomeCarouselBinding.inflate(LayoutInflater.from(parent.context)))
            }

           else -> {
                HomePhotoCardViewHolder(ItemHomePhotoCardBinding.inflate(LayoutInflater.from(parent.context)))
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is HomeTitleViewHolder -> {
                val item = datas[position] as HomeItemInfo<String>
                holder.binding.tvTitle.text = item.data
            }

            holder is HomeCarouselViewHolder -> {
                holder.onBindViewHolder(position, (datas[position] as HomeItemInfo<List<Photo>>).data, onHomeCarouselClickListener)
            }

            holder is HomePhotoCardViewHolder -> {
                holder.onBindViewHolder(position, (datas[position] as HomeItemInfo<Photo>).data, onHomePhotoCardClickListener)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return datas[position].type.value
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val type = holder.itemViewType
        if (type == HomeItemType.TYPE_TITLE.value || type == HomeItemType.TYPE_CAROUSEL.value) { //如果类型为头部就占满一行
            val lp = holder.itemView.layoutParams
            if (lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = true
            }
        }
    }

    inner class HomeTitleViewHolder : RecyclerView.ViewHolder {
        val binding: ItemHomeTitleBinding

        constructor(itemView: ItemHomeTitleBinding) : super(itemView.root) {
            binding = itemView
        }
    }

    class HomeCarouselViewHolder : RecyclerView.ViewHolder {
        val binding: ItemHomeCarouselBinding

        constructor(itemView: ItemHomeCarouselBinding) : super(itemView.root) {
            binding = itemView
        }

        fun onBindViewHolder(parentPosition: Int, data: List<Photo>, listener: OnItemClickListener?) {
            binding.rvRecyclerview.apply {
                layoutManager = CarouselLayoutManager()
                isNestedScrollingEnabled = false
                val carouselAdapter = CarouselAdapter(object : CarouselItemListener {
                    override fun onItemClicked(view: View, item: Photo, position: Int) {
                        listener?.onClickItem(view, parentPosition, position, item)
                    }
                })
                adapter = carouselAdapter
                carouselAdapter.submitList(data)
            }
        }

        interface OnItemClickListener {
            fun onClickItem(view: View, parentPosition: Int, childPosition: Int, photo: Photo)
        }

    }

    class HomePhotoCardViewHolder : RecyclerView.ViewHolder {

        val binding: ItemHomePhotoCardBinding

        constructor(itemView: ItemHomePhotoCardBinding) : super(itemView.root) {
            binding = itemView
        }

        fun onBindViewHolder(position: Int, photo: Photo, listener: OnItemClickListener? = null) {

            if(photo.width!=null&&photo.height!=null){
                binding.ivImg.ratio = photo.width / photo.height.toFloat()
            }

            binding.tvTitle.text = photo.user?.name
            binding.tvDes.visibility = if (photo.description.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.tvDes.text = photo.description ?: ""

            Glide.with(binding.ivUser)
                .load(photo.user?.profile_image?.large)
                .into(binding.ivUser)

            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL)
                .dontTransform()

            Glide.with(binding.root.context)
                .load(photo.urls.small)
                .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
                .apply(options)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .into(binding.ivImg)

            binding.mcvCard.setOnClickListener {
                listener?.onClickItem(it, position, photo)
            }
        }

        interface OnItemClickListener {
            fun onClickItem(view: View, position: Int, photo: Photo)
        }
    }

}