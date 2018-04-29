package com.lhj.vehiclesystem.ui.widget.recyclerview.listener;

import android.view.View;

import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.BaseSimpleRecycleAdapter;

/**
 * RecycleViewLayout接口便于使用
 * 创建人：wangya
 * 时间： 2016/12/22  下午3:11
 */
public interface RecyclerListener {
    BaseSimpleRecycleAdapter getAdapter();

    void setListLoadCall(RecyclerDataLoadListener listLoadCall);

    void setPullRefreshEnable(boolean enable);

    void setPullLoadEnable(boolean enable);

    void showData(boolean isHideRefresh);

    void showEmpty();

    void showFailure();

    void stopRefresh();

    void startRefresh();

    void removeHeaderView();

    void setHeaderView(View headerView);

    void showSuccess();
}
