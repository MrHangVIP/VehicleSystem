package com.lhj.vehiclesystem.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.VehicleItem;
import com.lhj.vehiclesystem.bean.VehicleRecordItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.adapter.VehicleListAdapter;
import com.lhj.vehiclesystem.ui.widget.ActionSheetDialog;
import com.lhj.vehiclesystem.ui.widget.NoticeDialog;
import com.lhj.vehiclesystem.ui.widget.ShareDialog;
import com.lhj.vehiclesystem.ui.widget.recyclerview.RecyclerViewLayout;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerDataLoadListener;
import com.lhj.vehiclesystem.ui.widget.recyclerview.listener.RecyclerListener;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author songzhihang
 * @discription 车辆详情页
 * @time 2018/2/27  下午4:05
 **/
public class VehicleDetailActivity extends BaseActivity implements RecyclerDataLoadListener {
    private static final String TAG = "VehicleDetailActivity";
    private ImageView atdl_iv_bg;
    private RecyclerViewLayout atdl_rcy_list;
    private ImageView atdl_iv_star;
    private TextView atdl_tv_action;
    private NoticeDialog noticeDialog;
    private VehicleItem vehicleItem;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_vehicle_detail_layout);
    }

    @Override
    protected void findViews() {
        setWindowTranslucent();
        vehicleItem = (VehicleItem) getIntent().getBundleExtra("bundle").getSerializable("vehicleItem");
        atdl_iv_bg = (ImageView) findViewById(R.id.atdl_iv_bg);
        atdl_rcy_list = (RecyclerViewLayout) findViewById(R.id.atdl_rcy_list);
        atdl_iv_star = (ImageView) findViewById(R.id.atdl_iv_star);
        atdl_tv_action = (TextView) findViewById(R.id.atdl_tv_action);
        initHeadViews();
    }

    private void initHeadViews() {
        View headView = LayoutInflater.from(mContext).inflate(R.layout.layout_vehicle_detail_head, null);
        ImageView ltdh_iv_index = (ImageView) headView.findViewById(R.id.ltdh_iv_index);
        ImageView ltdh_tv_image = (ImageView) headView.findViewById(R.id.ltdh_tv_image);
        TextView ltdh_tv_name = (TextView) headView.findViewById(R.id.ltdh_tv_name);
        TextView ltdh_tv_identify = (TextView) headView.findViewById(R.id.ltdh_tv_identify);
        TextView ltdh_tv_level = (TextView) headView.findViewById(R.id.ltdh_tv_level);
        TextView ltdh_tv_price = (TextView) headView.findViewById(R.id.ltdh_tv_price);
        TextView ltdh_tv_time = (TextView) headView.findViewById(R.id.ltdh_tv_time);
        TextView ltdh_tv_address = (TextView) headView.findViewById(R.id.ltdh_tv_address);
        TextView ltdh_tv_biref = (TextView) headView.findViewById(R.id.ltdh_tv_biref);
        RelativeLayout ltdh_ll_address_layout = (RelativeLayout) headView.findViewById(R.id.ltdh_ll_address_layout);
        RelativeLayout ltdh_ll_notice = (RelativeLayout) headView.findViewById(R.id.ltdh_ll_notice);
        ltdh_ll_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "南京.奥体中心");
            }
        });
        //弹窗
        ltdh_ll_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noticeDialog == null) {
                    noticeDialog = new NoticeDialog(VehicleDetailActivity.this);
                }
                noticeDialog.show();
            }
        });
        atdl_rcy_list.setHeaderView(headView);
        Glide.with(mContext).load(Constant.IMAGE_URL + vehicleItem.getIndexpicurl())
                .placeholder(R.drawable.app_logo)
                .into(ltdh_iv_index);
        Glide.with(mContext).load(Constant.IMAGE_URL + vehicleItem.getIndexpicurl())
                .placeholder(R.drawable.app_logo)
                .into(ltdh_tv_image);
        ltdh_tv_name.setText(vehicleItem.getName());
        ltdh_tv_identify.setText("车牌：" + vehicleItem.getIdentity());
        ltdh_tv_level.setText("准驾等级：" + vehicleItem.getLevel());
        ltdh_tv_biref.setText(vehicleItem.getBiref());
        ltdh_tv_price.setText("¥ " + vehicleItem.getPrice() + "万元");
        if (vehicleItem.getStatusId() != 0) {//不可用
            atdl_tv_action.setText(vehicleItem.getStatusType());
            atdl_tv_action.setEnabled(false);
        }
    }

    @Override
    protected void initData() {
        setTitle("");
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.inflateMenu(R.menu.ticket_detail__share_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                //分享弹窗
//                new ShareDialog((Activity) VehicleDetailActivity.this).
//                        setShareContent("快来看看（" + "title" + "）这个问卷，快来看看吧！")
//                        .show();
                toast("功能暂未开放");
                return true;
            }
        });
        Glide.with(mContext).load(Constant.IMAGE_URL + vehicleItem.getIndexpicurl()).
                bitmapTransform(new BlurTransformation(mContext, 23, 1)). // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                crossFade(1000).
                into(atdl_iv_bg);
        atdl_rcy_list.setAdapter(new VehicleListAdapter(mContext));
        atdl_rcy_list.setBackgroundColor(Color.TRANSPARENT);
        atdl_rcy_list.setListLoadCall(this);
        atdl_rcy_list.setPullLoadEnable(false);
        atdl_rcy_list.setPullRefreshEnable(true);
        atdl_rcy_list.setItemAnimator(new DefaultItemAnimator());
        atdl_rcy_list.setEmpty_tip(mContext.getResources().getString(R.string.no_data));
        atdl_rcy_list.showLoading();
        atdl_rcy_list.onRefresh();
    }

    @Override
    protected void setListener() {
        atdl_iv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atdl_iv_star.setImageResource(R.drawable.icon_star_select);
            }
        });
        atdl_tv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!atdl_tv_action.isEnabled()) {
                    toast("当前车辆不可用");
                    return;
                }
                //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //    设置Title的图标
//                builder.setIcon(R.drawable.ic_launcher);
                //    设置Title的内容
                builder.setTitle("是否申请");
                //    设置Content来显示一个信息
                builder.setMessage("确定申请用车？申请后不能取消。");
                //    设置一个PositiveButton
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApplyList();  //查询当前是否正在申请的车
                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //    显示出该对话框
                builder.show();
            }
        });
    }

    @Override
    public void onLoadMore(RecyclerListener recyclerLayout, boolean isRefresh) {
        getData(isRefresh);
    }

    @Override
    public Drawable setBackIcon() {
        return getDrawableRes(R.drawable.icon_back_white);
    }

    /**
     * 设置状态栏透明
     */
    private void setWindowTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // 透明状态栏
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void getData(final boolean isRefresh) {
        Map<String, String> map = new HashMap<>();
        map.put("typeId", vehicleItem.getTypeId() + "");
        map.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        map.put("vehicleId", vehicleItem.getVehicleId() + "");
        OkHttpHelp<ResultItem> okHttpHelp = OkHttpHelp.getInstance();
        okHttpHelp.httpRequest("", Constant.GET_VEHICLE_DATA_URL, map, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {

                if ("fail".equals(object.getResult())) {
                    atdl_rcy_list.showData(true);
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
                            atdl_rcy_list.getAdapter().clearData();
                        }
                        atdl_rcy_list.getAdapter().appendData(vehicleItems);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        atdl_rcy_list.showData(true);
                    }
                }
            }

            @Override
            public void onFailed(String message) {
                toast("网络错误，请重试！");
                atdl_rcy_list.showData(true);
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    private void getApplyList() {
        Map<String, String> params = new HashMap<>();
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("userId", BaseApplication.getAPPInstance().getmUser().getUserId() + "");
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.GET_VEHICLE_RECORDS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(object.getData());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VehicleRecordItem vehicleRecordItem = (new Gson()).fromJson(jsonArray.get(i).toString(), VehicleRecordItem.class);
                            String state = vehicleRecordItem.getState();
                            if (TextUtils.equals(state, "1") || TextUtils.equals(state, "0")) {
                                ProgressDialogUtil.dismissProgressdialog();
                                toast("您还有未完成的申请或使用中的车辆！请还车后再试！");
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Bundle bundle=new Bundle();
                bundle.putInt("vehicleId",vehicleItem.getVehicleId());
                jumpToNext(ApplyVehicleActivity.class,bundle);
            }
            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
            }
            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }
}
