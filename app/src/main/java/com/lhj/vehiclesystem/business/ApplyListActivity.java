package com.lhj.vehiclesystem.business;

import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.VehicleRecordItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.adapter.VehicleRecordAdapter;
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

/**
 * 申请列表
 * Created by Songzhihang on 2018/4/16.
 */
public class ApplyListActivity extends BaseActivity implements RecyclerDataLoadListener {
    private static final String TAG = "ApplyListActivity";

    private RecyclerViewLayout avrl_rcyl;
    private VehicleRecordAdapter adapter ;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_vehicle_records_layout);
    }

    @Override
    protected void findViews() {
        adapter = new VehicleRecordAdapter(mContext);
        avrl_rcyl = (RecyclerViewLayout) findViewById(R.id.avrl_rcyl);
        avrl_rcyl.setAdapter(adapter);
        avrl_rcyl.setListLoadCall(this);
        avrl_rcyl.setBackgroundColor(Color.parseColor("#F7F7F7"));
        avrl_rcyl.setPullLoadEnable(false);
        avrl_rcyl.setItemAnimator(new DefaultItemAnimator());
        avrl_rcyl.setEmpty_tip(mContext.getResources().getString(R.string.no_data));
        avrl_rcyl.showData(true);
        avrl_rcyl.showLoading();
        avrl_rcyl.onRefresh();
        adapter.setRecyclerViewLayout(avrl_rcyl);
        adapter.setShouldAction(true);
    }

    @Override
    protected void initData() {
        setTitle("申请列表");
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onLoadMore(final RecyclerListener recyclerLayout, final boolean isRefresh) {
        Map<String, String> params = new HashMap<>();
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("state", "0");
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("post", Constant.GET_VEHICLE_RECORDS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                if (isRefresh) {
                    recyclerLayout.getAdapter().clearData();
                }
                if ("fail".equals(object.getResult())) {
                    recyclerLayout.showEmpty();
                    return;
                }
                JSONArray jsonArray = null;
                ArrayList<VehicleRecordItem> list = new ArrayList<>();
                try {
                    jsonArray = new JSONArray(object.getData());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VehicleRecordItem vehicleRecordItem = (new Gson()).fromJson(jsonArray.get(i).toString(), VehicleRecordItem.class);
                            list.add(vehicleRecordItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (list.size() > 0) {
                        recyclerLayout.getAdapter().appendData(list);
                        recyclerLayout.showData(false);
                    } else {
                        recyclerLayout.showEmpty();
                    }
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                recyclerLayout.showFailure();
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }
}
