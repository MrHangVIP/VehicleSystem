package com.lhj.vehiclesystem.ui.widget.recyclerview.listener;


/**
 * 数据接口监听
 * 创建人：wangya
 * 时间： 2016/12/22  下午2:08
 */
public interface RecyclerDataLoadListener {

    void onLoadMore(RecyclerListener recyclerLayout, boolean isRefresh);
}
