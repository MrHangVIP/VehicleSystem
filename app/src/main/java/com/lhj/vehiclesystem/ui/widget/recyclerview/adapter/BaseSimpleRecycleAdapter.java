package com.lhj.vehiclesystem.ui.widget.recyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;

import java.util.ArrayList;

/**
 * 说明：厚建封装的RecycleAdapter(适用于有下拉刷新的RecycleView)
 * 作者：wangya
 * 时间：2016/12/22
 */

public class BaseSimpleRecycleAdapter<VH extends RecyclerView.ViewHolder> extends BaseRecyclerAdapter<VH> {
    protected LayoutInflater inflater;
    protected Context mContext;
    protected ArrayList items = new ArrayList();
    protected int selected = -1;

    public BaseSimpleRecycleAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void appendData(ArrayList data) {
        if (data == null)
            return;
        items.addAll(data);
        notifyDataSetChanged();
    }
    public void deleteItemData(Object item) {
        items.remove(item);
        notifyDataSetChanged();
    }
    public void clearData() {
        items.clear();
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    public void removeHeaderView() {
        customHeaderView = null;
        notifyDataSetChanged();
    }

    public ArrayList getItems() {
        return items;
    }


    @Override
    public VH getViewHolder(View view) {
        return null;
    }

    /**
     * 引入布局  设置ViewHolder
     *
     * @param parent
     * @param viewType
     * @param isItem
     * @return
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        return null;
    }

    /**
     * 设置单个view的数据
     *
     * @param holder
     * @param position
     * @param isItem
     */
    @Override
    public void onBindViewHolder(VH holder, int position, boolean isItem) {

    }


    @Override
    public int getAdapterItemCount() {
        return items == null ? 0 : items.size();
    }
}
