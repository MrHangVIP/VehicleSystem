package com.lhj.vehiclesystem.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.BaseSimpleRecycleAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.adapter.RVBaseViewHolder;

import java.util.ArrayList;

/**
 * @discription 适配器
 * @autor songzhihang
 * @time 2017/10/13  下午3:22
 **/
public class NoticeListAdapter extends BaseSimpleRecycleAdapter<RVBaseViewHolder> {

    private static final String TAG = "NoticeListAdapter";
    private Context mContext;

    public NoticeListAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public RVBaseViewHolder getViewHolder(View view) {
        return new RVBaseViewHolder(view);
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_notice_layout, parent, false);
        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position, boolean isItem) {
        TextView titleTV = (TextView) holder.retrieveView(R.id.idnl_tv_label);
        TextView contentTv = (TextView) holder.retrieveView(R.id.idnl_tv_content);
        if (position == 0) {
            titleTV.setText("借车须知");
            contentTv.setText("每人，每次只允许申请一辆车。驾照等级必须不低于准驾等级");
        }
        if (position == 1) {
            titleTV.setText("赔偿须知");
            contentTv.setText("车辆为公司财产，使用时如因个人原因造成的损坏或扣分，应由个人维修或消分");
        }
        if (position == 2) {
            titleTV.setText("归还须知");
            contentTv.setText("车辆应该在申请期限内还车，如未按按时归还车辆的人员将根据企业内部制度进行相应的信用分扣除，或者罚款");
        }
    }

    @Override
    public int getAdapterItemCount() {
        return items.size() == 0 ? 3 : items.size();
    }

    @Override
    public void appendData(ArrayList data) {
        super.appendData(data);
    }
}
