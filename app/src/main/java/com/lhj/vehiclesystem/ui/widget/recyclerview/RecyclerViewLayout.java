package com.lhj.vehiclesystem.ui.widget.recyclerview;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bumptech.glide.Glide;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.widget.XRefreshRecycleView;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.BaseSimpleRecycleAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerDataLoadListener;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerListener;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.MyUtil;


/**
 * 说明：RecycleView封装布局，自带错误页面
 * 作者：wangya
 * 时间：2016/12/22
 * <p>
 * <p>
 * 2017年1月13日17:26:41 张雷修改
 * 1、统一动画效果
 * 2、空数据的展示样式  showEmpty();
 * 3、网络请求失败后的展示效果统一  showFailure();
 */

public class RecyclerViewLayout extends RelativeLayout implements RecyclerListener, XRefreshView.XRefreshViewListener {

    private Context context;
    private XRefreshRecycleView xRefreshRecycleView;
    private RecyclerView recyclerview;
    //空数据的布局
    private RelativeLayout empty_layout;
    private TextView empty_layout_text;
    private ImageView empty_layout_img;
    private String empty_tip;//空数据 界面提示
    //正在加载
    private RelativeLayout loading_layout;
    //error网络错误之类的展示
    private RelativeLayout failure_layout;//网络错误之类的展示
    private TextView mFailureRetryText;//点此重试
    private TextView mFailureSolutionText;//查看解决方案
    private TextView solution_text;//具体的解决方案
    private BaseSimpleRecycleAdapter adapter;
    //
    private View headerView;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerDataLoadListener listLoadCall;

    public RecyclerViewLayout(Context context) {
        super(context);
        initView(context);
    }

    public RecyclerViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecyclerViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 获取当前的RecyclerView对象
     * 可用于 .addOnScrollListener();
     *
     * @return
     */
    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    /**
     * 获取当前封装后的XRefreshRecycleView
     *
     * @return
     */
    public XRefreshRecycleView getxRefreshRecycleView() {
        return xRefreshRecycleView;
    }

    /**
     * 设置layout中recyclerview列表背景色
     * 覆盖了View的setBackgroundColor方法
     * 因为如果给最外层的View设置背景
     * 则当展示emptyView、FailureView、LoadingView时，这些view本身带有背景色，就会造成ui层叠绘制
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        if (xRefreshRecycleView != null) {
            xRefreshRecycleView.setBackgroundColor(color);
        }
    }

    private void initView(Context context) {
        this.context = context;
        xRefreshRecycleView = new XRefreshRecycleView(context);
        xRefreshRecycleView.setXRefreshViewListener(this);
        recyclerview = xRefreshRecycleView.getRecyclerview();
        //loading
        loading_layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recycler_loading_layout, null);
        LinearLayout mRequestLayout = (LinearLayout) loading_layout.findViewById(R.id.request_layout);
        getLoadingGifView(context, mRequestLayout,R.raw.request_layout_gif);
        //failure
        failure_layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.mxu_failure_layout, null);
        mFailureRetryText = (TextView) failure_layout.findViewById(R.id.failure_retry_text);
        mFailureRetryText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mFailureSolutionText = (TextView) failure_layout.findViewById(R.id.failure_solution_text);
        solution_text = (TextView) failure_layout.findViewById(R.id.solution_text);
        //empty
        empty_layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.mxu_empty_layout, null);
        empty_layout_text = (TextView) empty_layout.findViewById(R.id.empty_layout_text);
        empty_layout_img = (ImageView) empty_layout.findViewById(R.id.empty_layout_img);
        //add
        addView(xRefreshRecycleView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(empty_layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(loading_layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(failure_layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        empty_tip =context.getResources().getString(R.string.no_data);
        showLoading();
        setListener();
    }

    private void setListener() {
        //点此重试
        mFailureRetryText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyUtil.setVisibility(empty_layout, View.GONE);
                MyUtil.setVisibility(failure_layout, View.GONE);
                MyUtil.setVisibility(xRefreshRecycleView, View.GONE);
                MyUtil.setVisibility(loading_layout, View.VISIBLE);
               postDelayed(new Runnable() {
                    public void run() {
                        showLoading();
                        onRefresh();
                    }
                }, 500);
            }
        });
        mFailureSolutionText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看解决方案
                MyUtil.setVisibility(mFailureSolutionText, View.GONE);
                MyUtil.setVisibility(solution_text, View.VISIBLE);
            }
        });
    }

    public static void getLoadingGifView(Context mContext,
                                         LinearLayout parentView, int rid) {
        if (parentView == null)
            return;
        ImageView gif = new ImageView(mContext);
        Glide.with(mContext).load(rid).asGif().into(gif);
        LinearLayout.LayoutParams gifpl = new LinearLayout.LayoutParams((int) (Constant.WIDTH * 0.1), (int) (Constant.WIDTH * 0.1));
        parentView.addView(gif, gifpl);
    }

    /**
     * 禁用XrefreshView滑动事件
     * 政企号首页使用
     *
     * @param enable
     */
    public void enableRefresh(boolean enable) {
        xRefreshRecycleView.setEnabled(enable);
    }

    /**
     * 展示内容layout
     */
    public void showSuccess() {
        MyUtil.setVisibility(empty_layout, GONE);
        MyUtil.setVisibility(loading_layout, GONE);
        MyUtil.setVisibility(failure_layout, View.GONE);
        MyUtil.setVisibility(xRefreshRecycleView, VISIBLE);
        empty_layout.setOnClickListener(null);
    }

    /**
     * 显示数据获取失败页面  如网络失败等..
     */
    public void showFailure() {
        stopRefresh();
        MyUtil.setVisibility(mFailureSolutionText, View.VISIBLE);
        MyUtil.setVisibility(solution_text, View.GONE);
        MyUtil.setVisibility(failure_layout, View.VISIBLE);
        MyUtil.setVisibility(empty_layout, GONE);
        MyUtil.setVisibility(loading_layout, GONE);
        MyUtil.setVisibility(xRefreshRecycleView, GONE);
    }

    /**
     * 显示空数据页面
     */
    @Override
    public void showEmpty() {
        stopRefresh();
        empty_layout_text.setText(empty_tip);
        MyUtil.setVisibility(empty_layout, VISIBLE);
        MyUtil.setVisibility(loading_layout, GONE);
        MyUtil.setVisibility(failure_layout, View.GONE);
        MyUtil.setVisibility(xRefreshRecycleView, GONE);
        empty_layout.setOnClickListener(null);
    }

    /**
     * 显示加载中的页面
     */
    public void showLoading() {
        MyUtil.setVisibility(empty_layout, GONE);
        MyUtil.setVisibility(loading_layout, VISIBLE);
        MyUtil.setVisibility(failure_layout, View.GONE);
        MyUtil.setVisibility(xRefreshRecycleView, GONE);
        empty_layout.setOnClickListener(null);
    }


    @Override
    public void onRefresh() {
        if (listLoadCall != null)
            listLoadCall.onLoadMore(this, true);
    }

    @Override
    public void onLoadMore(boolean isSilence) {
        if (listLoadCall != null)
            listLoadCall.onLoadMore(this, false);
    }


    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }

    /**
     * 设置列表空的时候的提示语
     *
     * @param empty_tip
     */
    public void setEmpty_tip(String empty_tip) {
        this.empty_tip = empty_tip;
    }

    /**
     * 设置空图片
     *
     * @param img
     */
    public void setEmpty_Img(@DrawableRes int img) {
        empty_layout_img.setImageResource(img);
    }

    public void setEmptyTipColor(int color) {
        empty_layout_text.setTextColor(color);
    }

    //////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void showData(boolean isHideRefresh) {
        if (recyclerview.getAdapter() == null)
            return;
        //包括头布局
        int childCount = ((BaseSimpleRecycleAdapter) recyclerview.getAdapter()).getAdapterItemCount() + ((BaseSimpleRecycleAdapter) recyclerview.getAdapter()).getStart();
        stopRefresh();
        if (childCount == 0 && !isHideRefresh) {
            showEmpty();
        } else {
            showSuccess();
        }
    }

    @Override
    public BaseSimpleRecycleAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void setListLoadCall(RecyclerDataLoadListener listLoadCall) {
        this.listLoadCall = listLoadCall;
    }

    @Override
    public void stopRefresh() {
        xRefreshRecycleView.stopRefresh();
        xRefreshRecycleView.stopLoadMore();
    }

    @Override
    public void startRefresh() {
        onRefresh();
//        xRefreshRecycleView.startRefresh();
    }

    public View getHeaderView() {
        return headerView;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(BaseSimpleRecycleAdapter adapter) {
        this.adapter = adapter;
        adapter.setHeaderView(headerView, recyclerview);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(context));
        xRefreshRecycleView.getRecyclerview().setAdapter(adapter);
    }

    /**
     * 必须在setAdapter之前执行，且只能调用一次
     *
     * @param headerView
     */
    @Override
    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        if (recyclerview.getAdapter() != null) {
            ((BaseSimpleRecycleAdapter) recyclerview.getAdapter()).setHeaderView(headerView, recyclerview);
        }
    }

    /**
     * 去除头布局
     */
    @Override
    public void removeHeaderView() {
        ((BaseSimpleRecycleAdapter) recyclerview.getAdapter()).removeHeaderView();
    }


    /**
     * 是否可以加载更多
     *
     * @param loadEnable
     */
    @Override
    public void setPullLoadEnable(boolean loadEnable) {
        xRefreshRecycleView.setPullLoadEnable(loadEnable);
    }

    /**
     * 是否可以刷新
     *
     * @param pullRefreshEnable
     */
    @Override
    public void setPullRefreshEnable(boolean pullRefreshEnable) {
        xRefreshRecycleView.setPullRefreshEnable(pullRefreshEnable);
    }


    /**
     * 设置RecycleView的布局类型，线性还是网格类型
     *
     * @param layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        recyclerview.setLayoutManager(layoutManager);
    }


    public <T extends RecyclerView.ItemDecoration> void setDivider(T t) {
        recyclerview.addItemDecoration(t);
    }

    public <T extends RecyclerView.ItemAnimator> void setItemAnimator(T t) {
        recyclerview.setItemAnimator(t);
    }
}
