package com.example.demo.meetphoto.ui.page.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @ClassName RecordPageAdapter
 * @Des 烹饪记录 vp2 adapter
 * @Author lipengfei
 * @Date 2023/11/20 13:40
 */
class TopicsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = ArrayList<Fragment>()
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.get(position)
    }

    fun setDatas(newDatas: List<Fragment>) {
        fragments.clear()
        fragments.addAll(newDatas)
        notifyDataSetChanged()
    }
}