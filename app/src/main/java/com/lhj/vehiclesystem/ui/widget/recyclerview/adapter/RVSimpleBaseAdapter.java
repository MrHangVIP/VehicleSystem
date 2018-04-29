package com.lhj.vehiclesystem.ui.widget.recyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 说明：厚建封装的RecycleAdapter(适用于普通的RecycleView)
 * 作者：wangya
 * 时间：2016/12/22
 */
public abstract class RVSimpleBaseAdapter<VH extends RVBaseViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext;
    protected List items = new ArrayList();
    protected LayoutInflater mInflater;
    protected String tag;

    protected abstract VH createHolder(ViewGroup parent, int viewType);

    protected abstract void bindHolder(VH holder, int position);

    public RVSimpleBaseAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public void appendData(List data) {
        if (data == null)
            return;
        items.addAll(data);
        LinkedHashSet set = new LinkedHashSet(items);
        items = new ArrayList(set);
        notifyDataSetChanged();
    }

    public void appendItem(Object object) {
        if (items.contains(object))
            return;
        items.add(object);
        notifyDataSetChanged();
    }

    public void appendItem(int index, Object object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        items.remove(index);
        notifyDataSetChanged();
    }

    public void removeItem(Object object) {
        items.remove(object);
        notifyDataSetChanged();

    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List getItems() {
        return items;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return createHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        bindHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
