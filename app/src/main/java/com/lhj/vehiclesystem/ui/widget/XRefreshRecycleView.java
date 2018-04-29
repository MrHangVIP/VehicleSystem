package com.lhj.vehiclesystem.ui.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.lhj.vehiclesystem.ui.widget.refresh.XRefreshViewExtendHeader;

/**
 * @discription 下拉刷新
 * @autor songzhihang
 * @time 2018/2/26  上午11:02
 **/
public class XRefreshRecycleView extends XRefreshView {
    private RecyclerView recyclerview;

    public XRefreshRecycleView(Context context) {
        super(context);
        initView(context);
    }

    public XRefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        recyclerview = new RecyclerView(context);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        addView(recyclerview, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setPinnedTime(1000);
        setPullRefreshEnable(true);
        setPullLoadEnable(false);
//        setAutoLoadMore(false);//是否静默加载
//        enableRecyclerViewPullUp(false);//设置在被刷新的view滑倒最底部的时候，是否允许被刷新的view继续往上滑动，默认是true
        setCustomHeaderView(new XRefreshViewExtendHeader(context));
    }

    /**
     * 获取recyclerview
     *
     * @return
     */
    public RecyclerView getRecyclerview() {
        return recyclerview;
    }
}