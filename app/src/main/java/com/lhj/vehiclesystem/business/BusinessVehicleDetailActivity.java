package com.lhj.vehiclesystem.business;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.bean.VehicleItem;
import com.lhj.vehiclesystem.util.Constant;

/**
 * @author songzhihang
 * @discription 企业车辆详情页
 * @time 2018/2/27  下午4:05
 **/
public class BusinessVehicleDetailActivity extends BaseActivity {
    private static final String TAG = "VehicleDetailActivity";
    private TextView atdl_tv_action;
    private VehicleItem vehicleItem;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_vehicle_detail_business_layout);
    }

    @Override
    protected void findViews() {
        vehicleItem = (VehicleItem) getIntent().getBundleExtra("bundle").getSerializable("vehicleItem");
        atdl_tv_action = (TextView) findViewById(R.id.atdl_tv_action);
        ImageView ltdh_iv_index = (ImageView) findViewById(R.id.ltdh_iv_index);
        ImageView ltdh_tv_image = (ImageView) findViewById(R.id.ltdh_tv_image);
        TextView ltdh_tv_name = (TextView) findViewById(R.id.ltdh_tv_name);
        TextView ltdh_tv_identify = (TextView) findViewById(R.id.ltdh_tv_identify);
        TextView ltdh_tv_level = (TextView) findViewById(R.id.ltdh_tv_level);
        TextView ltdh_tv_price = (TextView) findViewById(R.id.ltdh_tv_price);
        TextView ltdh_tv_biref = (TextView) findViewById(R.id.ltdh_tv_biref);
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
        atdl_tv_action.setText(vehicleItem.getStatusType());
    }

    @Override
    protected void initData() {
        setTitle("车辆详情");
    }

    @Override
    protected void setListener() {
    }
}
