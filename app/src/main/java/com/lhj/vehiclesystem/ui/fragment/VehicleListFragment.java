package com.lhj.vehiclesystem.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.VehicleItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.lhj.vehiclesystem.BaseFragment;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.adapter.VehicleListAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.RecyclerViewLayout;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerDataLoadListener;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerListener;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.SpfUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @discription 首页列表fragment
 * @autor songzhihang
 * @time 2017/10/13  下午2:25
 **/
public class VehicleListFragment extends BaseFragment implements RecyclerDataLoadListener {
    private static final String TAG = "QuestionnaireFragment";

    private Banner fcl_banner;
    private RecyclerViewLayout xRefreshRecycleView;

    private List<Integer> images = new ArrayList<>();
    private String type;//栏目类型

    @Override
    protected View getLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_content_layout, container, false);
    }

    @Override
    protected void initView(View view) {
        type = getArguments().getString("typeId");
        xRefreshRecycleView = (RecyclerViewLayout) view.findViewById(R.id.fcl_rv_recycleview);
        xRefreshRecycleView.setAdapter(new VehicleListAdapter(getActivity()));
        xRefreshRecycleView.setListLoadCall(this);
        xRefreshRecycleView.setBackgroundColor(Color.parseColor("#F7F7F7"));
        xRefreshRecycleView.setPullLoadEnable(false);
        xRefreshRecycleView.setItemAnimator(new DefaultItemAnimator());
        xRefreshRecycleView.setEmpty_tip(context.getResources().getString(R.string.no_data));
        xRefreshRecycleView.showData(true);
        xRefreshRecycleView.showLoading();
        xRefreshRecycleView.onRefresh();
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == Constant.TYPE_BISSINESS) {
            return;
        }
        initHeadView();
    }

    @Override
    protected void initData() {

    }

    private void initHeadView() {
        fcl_banner = new Banner(context);
        View headView = LayoutInflater.from(context).inflate(R.layout.banner_head_layout, null);
        fcl_banner = (Banner) headView.findViewById(R.id.bhl_banner);
//        fcl_banner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MyUtil.toDip(100)));
        xRefreshRecycleView.setHeaderView(headView);
        fcl_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        fcl_banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        images.add(R.drawable.glide_one);
        images.add(R.drawable.glide_two);
        images.add(R.drawable.glide_three);
        fcl_banner.setImages(images);
        //设置banner动画效果
        fcl_banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
//        fcl_banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        fcl_banner.isAutoPlay(true);
        //设置轮播时间
        fcl_banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        fcl_banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        fcl_banner.start();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getData(final boolean isRefresh) {
        Map<String, String> map = new HashMap<>();
        map.put("typeId", type);
        map.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        OkHttpHelp<ResultItem> okHttpHelp = OkHttpHelp.getInstance();
        okHttpHelp.httpRequest("", Constant.GET_VEHICLE_DATA_URL, map, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {

                if ("fail".equals(object.getResult())) {
                    if (isRefresh) {
                        xRefreshRecycleView.showFailure();
                    } else {
                        xRefreshRecycleView.setPullLoadEnable(false);
                        xRefreshRecycleView.showData(true);
                    }
                    return;
                } else {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(object.getData());
                        ArrayList<VehicleItem> vehicleItems = new ArrayList<VehicleItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VehicleItem vehicleItem = new Gson().fromJson(jsonArray.get(i).toString(), VehicleItem.class);
                            if (vehicleItem != null) {
                                vehicleItems.add(vehicleItem);
                            }
                        }
                        if (isRefresh) {
                            xRefreshRecycleView.getAdapter().clearData();
                        }
                        xRefreshRecycleView.getAdapter().appendData(vehicleItems);
                        if (xRefreshRecycleView.getAdapter().getAdapterItemCount() == 0) {
                            xRefreshRecycleView.showEmpty();
                        } else {
                            xRefreshRecycleView.showData(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (isRefresh) {
                            xRefreshRecycleView.showEmpty();
                        } else {
                            xRefreshRecycleView.showData(true);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String message) {
                toast("网络错误，请重试！");
                xRefreshRecycleView.showFailure();
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    @Override
    public void onLoadMore(RecyclerListener recyclerLayout, final boolean isRefresh) {
        getData(isRefresh);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */
            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);
        }
    }
}
