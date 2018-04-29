package com.lhj.vehiclesystem.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.VehicleItem;
import com.lhj.vehiclesystem.bean.VehicleRecordItem;
import com.lhj.vehiclesystem.business.BusinessVehicleDetailActivity;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.activity.VehicleDetailActivity;
import com.lhj.vehiclesystem.ui.widget.CircleImageView;
import com.lhj.vehiclesystem.ui.widget.recyclerview.RecyclerViewLayout;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.BaseSimpleRecycleAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.RVBaseViewHolder;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @discription 适配器
 * @autor songzhihang
 * @time 2017/10/13  下午3:22
 **/
public class VehicleRecordAdapter extends BaseSimpleRecycleAdapter<RVBaseViewHolder> {

    private static final String TAG = "VehicleRecordAdapter";
    private Context mContext;
    private boolean shouldAction = false;

    private RecyclerViewLayout recyclerViewLayout;

    public VehicleRecordAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public RVBaseViewHolder getViewHolder(View view) {
        return new RVBaseViewHolder(view);
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_record_layout, parent, false);
        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position, boolean isItem) {
        final VehicleRecordItem item = (VehicleRecordItem) items.get(position);
        View view = holder.itemView;
        CircleImageView ivrl_civ_head = (CircleImageView) view.findViewById(R.id.ivrl_civ_head);
        TextView ivrl_tv_name = (TextView) view.findViewById(R.id.ivrl_tv_name);
        TextView ivrl_tv_publishtime = (TextView) view.findViewById(R.id.ivrl_tv_publishtime);
        ImageView ivrl_iv_image = (ImageView) view.findViewById(R.id.ivrl_iv_image);
        TextView ivrl_tv_title = (TextView) view.findViewById(R.id.ivrl_tv_title);
        TextView ivrl_tv_duration = (TextView) view.findViewById(R.id.ivrl_tv_duration);
        TextView ivrl_tv_price = (TextView) view.findViewById(R.id.ivrl_tv_price);
        TextView ivrl_tv_biref = (TextView) view.findViewById(R.id.ivrl_tv_biref);
        TextView ivrl_tv_reason = (TextView) view.findViewById(R.id.ivrl_tv_reason);
        TextView ivrl_tv_startTime = (TextView) view.findViewById(R.id.ivrl_tv_startTime);
        TextView ivrl_tv_finishtime = (TextView) view.findViewById(R.id.ivrl_tv_finishtime);
        ImageView img_state = (ImageView) view.findViewById(R.id.img_state);
        Glide.with(mContext)
                .load(Constant.IMAGE_URL + item.getUserBean().getHeadUrl())
                .placeholder(R.drawable.icon_default_head)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivrl_civ_head);
        ivrl_tv_name.setText(item.getUserBean().getTrueName());
        ivrl_tv_publishtime.setText(item.getCreateTime());
        Glide.with(mContext)
                .load(Constant.IMAGE_URL + item.getVehicleBean().getIndexpicurl())
                .placeholder(R.drawable.app_logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivrl_iv_image);
        ivrl_tv_title.setText(item.getVehicleBean().getName());
        ivrl_tv_price.setText(item.getVehicleBean().getPrice() + " 万元");
        ivrl_tv_biref.setText(item.getVehicleBean().getBiref() + "");
        ivrl_tv_duration.setText("准驾等级：" + item.getVehicleBean().getLevel());
        ivrl_tv_reason.setText("用途：" + item.getMessage());
        ivrl_tv_startTime.setText("开始时间：" + item.getStartTime());
        ivrl_tv_finishtime.setText("结束时间：" + item.getFinishTime());
        String state = item.getState();
        if (TextUtils.equals("0", state)) {
            img_state.setImageResource(R.drawable.state_apply);
        }
        if (TextUtils.equals("1", state)) {
            img_state.setImageResource(R.drawable.state_use);
        }
        if (TextUtils.equals("2", state)) {
            img_state.setImageResource(R.drawable.state_refuse);
        }
        if (TextUtils.equals("3", state)) {
            img_state.setImageResource(R.drawable.state_finish);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shouldAction) {
                    if (TextUtils.equals(item.getState(), "0")) {//申請中
                        //弹框提示同意或者决绝
                        showDialog(item);
                    }
                    if (TextUtils.equals(item.getState(), "1")) {//使用中
                        backDailog(item);//还车
                    }

                }
            }
        });
    }

    @Override
    public int getAdapterItemCount() {
        return items.size();
    }

    @Override
    public void appendData(ArrayList data) {
        super.appendData(data);
    }

    public boolean isShouldAction() {
        return shouldAction;
    }

    public void setShouldAction(boolean shouldAction) {
        this.shouldAction = shouldAction;
    }

    public RecyclerViewLayout getRecyclerViewLayout() {
        return recyclerViewLayout;
    }

    public void setRecyclerViewLayout(RecyclerViewLayout recyclerViewLayout) {
        this.recyclerViewLayout = recyclerViewLayout;
    }

    //还车对话框
    private void backDailog(final VehicleRecordItem item) {
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    设置Title的图标
//                builder.setIcon(R.drawable.ic_launcher);
        //    设置Title的内容
        builder.setTitle("提示");
        //    设置Content来显示一个信息
        builder.setMessage("是否还车");
        builder.setCancelable(true);
        //    设置一个PositiveButton
        builder.setPositiveButton("还车", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onSubmitAction("3", item.getId(), item.getVehicleId());
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //    显示出该对话框
        builder.show();
    }

    private void showDialog(final VehicleRecordItem item) {
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    设置Title的图标
//                builder.setIcon(R.drawable.ic_launcher);
        //    设置Title的内容
        builder.setTitle("提示");
        //    设置Content来显示一个信息
        builder.setMessage("是否同意申请");
        builder.setCancelable(true);
        //    设置一个PositiveButton
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onSubmitAction("1", item.getId(), item.getVehicleId());
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onSubmitAction("2", item.getId(), item.getVehicleId());
            }
        });
        //    显示出该对话框
        builder.show();
    }

    private void onSubmitAction(String state, int id, int vehicleId) {
        Map<String, String> params = new HashMap<>();
        params.put("state", state);
        params.put("vehicleId", vehicleId + "");
        params.put("id", id + "");
        ProgressDialogUtil.showProgressDialog(mContext, true);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("post", Constant.UPDATE_VEHICLE_RECORDS_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (object.getResult().equals("success")) {
                    //修改成功
                    Toast.makeText(mContext, "操作成功！", Toast.LENGTH_LONG).show();
                    if (recyclerViewLayout != null) {
                        recyclerViewLayout.onRefresh();
                    }
                } else {
                    Toast.makeText(mContext, "操作失败，请稍后重试！", Toast.LENGTH_LONG).show();
                }
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
