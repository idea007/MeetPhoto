package com.dafay.demo.lib.base.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val datas = ArrayList<T>()

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemCount(): Int {
        return datas.size
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun setDatas(newDatas: List<T>?) {
        datas.clear()
        if (!newDatas.isNullOrEmpty()) {
            datas.addAll(newDatas)
        }
        notifyDataSetChanged()
    }

    open fun addData(newData: T) {
        val insertRangeStart = itemCount
        datas.add(newData)
        notifyItemRangeInserted(insertRangeStart, 1)
    }

    open fun insertDatas(index: Int, newDatas: List<T>) {
        datas.addAll(index, newDatas)
        notifyItemRangeInserted(index, newDatas.size)
    }

    open fun addDatas(newDatas: List<T>) {
        val insertRangeStart = itemCount
        datas.addAll(newDatas)
        notifyItemRangeInserted(insertRangeStart, newDatas.size)
    }

    fun swapItem(fromPosition: Int, toPosition: Int) {
        val temp: T = datas[fromPosition]
        datas[fromPosition] = datas[toPosition]
        datas[toPosition] = temp
        notifyItemMoved(fromPosition, toPosition)
    }
}