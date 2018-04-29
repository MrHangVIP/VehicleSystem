package com.lhj.vehiclesystem.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.bean.VehicleItem;
import com.lhj.vehiclesystem.business.BusinessVehicleDetailActivity;
import com.lhj.vehiclesystem.ui.activity.VehicleDetailActivity;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.BaseSimpleRecycleAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.RVBaseViewHolder;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.SpfUtil;

import java.util.ArrayList;

/**
 * @discription 适配器
 * @autor songzhihang
 * @time 2017/10/13  下午3:22
 **/
public class VehicleListAdapter extends BaseSimpleRecycleAdapter<RVBaseViewHolder> {

    private static final String TAG = "VehicleListAdapter";
    private Context mContext;

    public VehicleListAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public RVBaseViewHolder getViewHolder(View view) {
        return new RVBaseViewHolder(view);
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_list_layout, parent, false);
        ImageView itll_iv_image = (ImageView) view.findViewById(R.id.itll_iv_image);
        TextView itll_tv_title = (TextView) view.findViewById(R.id.itll_tv_title);
        TextView itll_tv_duration = (TextView) view.findViewById(R.id.itll_tv_duration);
        TextView itll_tv_price = (TextView) view.findViewById(R.id.itll_tv_price);
        TextView itll_tv_biref = (TextView) view.findViewById(R.id.itll_tv_biref);
        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position, boolean isItem) {
        final VehicleItem item = (VehicleItem) items.get(position);
        Glide.with(mContext)
                .load(Constant.IMAGE_URL + item.getIndexpicurl())
                .placeholder(R.drawable.app_logo)
                .into((ImageView) holder.retrieveView(R.id.itll_iv_image));
        ((TextView) holder.retrieveView(R.id.itll_tv_title)).setText(item.getName());
        ((TextView) holder.retrieveView(R.id.itll_tv_price)).setText(item.getPrice() + " 万元");
        ((TextView) holder.retrieveView(R.id.itll_tv_biref)).setText(item.getBiref() + "");
        ((TextView) holder.retrieveView(R.id.itll_tv_duration)).setText("准驾等级：" + item.getLevel());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("vehicleItem", item);
                if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == Constant.TYPE_BISSINESS) {
                    ((BaseActivity) mContext).jumpToNext(BusinessVehicleDetailActivity.class, bundle);
                } else {
                    ((BaseActivity) mContext).jumpToNext(VehicleDetailActivity.class, bundle);
                }
            }
        });
        super.onBindViewHolder(holder, position, isItem);
    }

    @Override
    public int getAdapterItemCount() {
        return items.size();
    }

    @Override
    public void appendData(ArrayList data) {
        super.appendData(data);
    }
}
