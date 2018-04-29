package com.lhj.vehiclesystem.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseFragment;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.TagItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.adapter.MyCommonNavigatorAdapter;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Songzhihang on 2017/10/6.
 * 首页fragment
 */
public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    private static final int GET_TAGS_OK = 0x01;
    private static final int GET_TAGS_FAIL = 0x02;
    private MagicIndicator magicIndicator;
    private LinearLayout fhl_ll_state;
    private TextView fhl_tv_city;
    private TextView fhl_tv_time;
    private ViewPager fhl_viewpager;
    private List<TagItem> tagItems = new ArrayList<>();
    private int curIndex = 0;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FrameLayout fhl_ll_time_filter;
    private View fhl_bg_filter;
    private RelativeLayout fhl_rl_all;
    private RelativeLayout fhl_rl_today;
    private RelativeLayout fhl_rl_week;
    private RelativeLayout fhl_rl_month;
    private AnimatorSet filterViewHideAnimator;
    private AnimatorSet filterViewShowAnimator;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_TAGS_OK:
                    setData();
                    break;
                case GET_TAGS_FAIL:
                    TagItem tagItem = new TagItem();
                    tagItem.setOrder(0);
                    tagItem.setTypeId(0);
                    tagItem.setValue("全部");
                    tagItems.add(tagItem);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected View getLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_home_layout, container, false);
    }

    @Override
    protected void initView(View view) {
        magicIndicator = (MagicIndicator) contentView.findViewById(R.id.fhl_magic_indicator);
        fhl_ll_state = (LinearLayout) contentView.findViewById(R.id.fhl_ll_state);
        fhl_tv_city = (TextView) contentView.findViewById(R.id.fhl_tv_city);
        fhl_tv_time = (TextView) contentView.findViewById(R.id.fhl_tv_time);
        fhl_viewpager = (ViewPager) contentView.findViewById(R.id.fhl_viewpager);
        MyUtil.setCompoundDrawables(fhl_tv_city, MyUtil.toDip(14), MyUtil.toDip(14), 2, R.drawable.icon_refresh);
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == Constant.TYPE_BISSINESS) {
            fhl_ll_state.setVisibility(View.GONE);
            return;
        }
        initTimeFilterView();
//        baiduMapUtils = BaiduMapUtils.getInstance(new MapView(context));
        getLocation();
    }

    @Override
    protected void initData() {
        getTags();
    }

    private void setData() {
        fragments.clear();
        for (int i = 0; i < tagItems.size(); i++) {
            Bundle bundle = new Bundle();
            VehicleListFragment ticketListFragment = new VehicleListFragment();
            bundle.putString("typeId", tagItems.get(i).getTypeId() + "");
            ticketListFragment.setArguments(bundle);
            fragments.add(ticketListFragment);
        }
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new MyCommonNavigatorAdapter(tagItems, fhl_viewpager, curIndex));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, fhl_viewpager);
        fhl_viewpager.setAdapter(new MyFragmentAdapter(getChildFragmentManager()));
        fhl_viewpager.setCurrentItem(curIndex);
    }

    @Override
    protected void initEvent() {
        fhl_tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAction(fhl_tv_time);
            }
        });
        fhl_tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void getLocation() {
//        baiduMapUtils.getLocation(new BaiduMapUtils.LocationListener() {
//            @Override
//            public void getLocationSuccess(BDLocation location) {
//                String city = "重新定位";
//                if (location != null) {
//                    city = !TextUtils.isEmpty(location.getCity()) ? location.getCity() : city;
//                }
//                fhl_tv_city.setText(city);
//            }
//        });
    }

    private void viewAction(TextView view) {
        Boolean tag = !(Boolean) view.getTag();
        int res = R.drawable.icon_arrow_up;
        if (tag) {
            res = R.drawable.icon_arrow_up;
            filterViewShowAnimator.start();
            fhl_bg_filter.setVisibility(View.VISIBLE);
        } else {
            res = R.drawable.icon_arrow_down;
            filterViewHideAnimator.start();
            fhl_bg_filter.setVisibility(View.GONE);
        }
        MyUtil.setCompoundDrawables(view, MyUtil.toDip(9), MyUtil.toDip(9), 2, res);
        view.setTag(tag);
    }

    private void initTimeFilterView() {
        fhl_ll_time_filter = (FrameLayout) contentView.findViewById(R.id.fhl_ll_time_filter);
        fhl_bg_filter = (View) contentView.findViewById(R.id.fhl_bg_filter);
        fhl_rl_all = (RelativeLayout) contentView.findViewById(R.id.fhl_rl_all);
        fhl_rl_today = (RelativeLayout) contentView.findViewById(R.id.fhl_rl_today);
        fhl_rl_week = (RelativeLayout) contentView.findViewById(R.id.fhl_rl_week);
        fhl_rl_month = (RelativeLayout) contentView.findViewById(R.id.fhl_rl_month);
        fhl_tv_time.setTag(false);
        fhl_bg_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAction(fhl_tv_time);
            }
        });
        fhl_bg_filter.post(new Runnable() {
            @Override
            public void run() {
                initAnimator();
            }
        });
    }

    //初始化动画
    private void initAnimator() {
        int durtime = 300;
        int height = MyUtil.toDip(160);
        ObjectAnimator translationYIn = ObjectAnimator.ofFloat(fhl_ll_time_filter, "translationY", -height, 0);
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(fhl_bg_filter, "alpha", 0, 0.5f);
        filterViewShowAnimator = new AnimatorSet();
        filterViewShowAnimator.play(translationYIn).with(alphaIn);
        filterViewShowAnimator.setDuration(durtime);
        filterViewShowAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fhl_ll_time_filter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator translationYOut = ObjectAnimator.ofFloat(fhl_ll_time_filter, "translationY", 0, -height);
        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(fhl_bg_filter, "alpha", 0.5f, 0);
        filterViewHideAnimator = new AnimatorSet();
        filterViewHideAnimator.play(translationYOut).with(alphaOut);
        filterViewHideAnimator.setDuration(durtime);
        filterViewHideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fhl_ll_time_filter.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    class MyFragmentAdapter extends FragmentPagerAdapter {


        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 返回需要展示的fragment
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /**
         * 返回需要展示的fangment数量
         *
         * @return
         */
        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * 获取类型
     */
    private void getTags() {
        tagItems.clear();
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        Map<String, String> params = new HashMap<>();
        ProgressDialogUtil.showProgressDialog(context, false);
        httpHelp.httpRequest("get", Constant.GET_TAGS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (object.getResult().equals("success")) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(object.getData());
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TagItem tagItem = (new Gson()).fromJson(jsonArray.get(i).toString(), TagItem.class);
                                tagItems.add(tagItem);
                            }
                        }
                        handler.sendEmptyMessage(GET_TAGS_OK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(GET_TAGS_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(GET_TAGS_FAIL);
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                handler.sendEmptyMessage(GET_TAGS_OK);
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }
}