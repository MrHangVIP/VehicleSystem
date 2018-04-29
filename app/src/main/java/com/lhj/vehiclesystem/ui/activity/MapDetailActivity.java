package com.lhj.vehiclesystem.ui.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapView;
import com.hoge.android.library.baidumap.BaiduMapUtils;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;

/**
 * 地图详情页
 * Created by Songzhihang on 2018/3/4.
 */
public class MapDetailActivity extends BaseActivity {
    private static final String TAG = "MapDetailActivity";
    private MapView amdl_mapview;
    private BaiduMapUtils baiduMapUtils;
    private ImageView amdl_iv_zoom_out;
    private ImageView amdl_iv_zoom_in;
    private ImageView amdl_iv_location;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_map_detail_layout);
    }

    @Override
    protected void findViews() {
        amdl_mapview=(MapView)findViewById(R.id.amdl_mapview);
        amdl_iv_zoom_out=(ImageView)findViewById(R.id.amdl_iv_zoom_out);
        amdl_iv_zoom_in=(ImageView)findViewById(R.id.amdl_iv_zoom_in);
        amdl_iv_location=(ImageView)findViewById(R.id.amdl_iv_location);
        // 隐藏logo
        View child = amdl_mapview.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initData() {
        setTitle(bundle.getString("title"));
        toolbar.setBackgroundColor(Color.WHITE);
        baiduMapUtils=BaiduMapUtils.getInstance(amdl_mapview);
        getMyLocation();
    }

    @Override
    protected void setListener() {
        amdl_iv_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                baiduMapUtils.mapZoomOut();
            }
        });
        amdl_iv_zoom_out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                baiduMapUtils.mapZoomIn();
            }
        });
        amdl_iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }

        });
    }

    private void getMyLocation(){
        baiduMapUtils.getLocation(new BaiduMapUtils.LocationListener() {
            @Override
            public void getLocationSuccess(BDLocation location) {

            }
        });
    }
}
