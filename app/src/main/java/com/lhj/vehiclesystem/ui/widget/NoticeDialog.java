/******************************************************************************
 * @project kanfangbao2
 * @brief
 * @author fmh19
 * @module com.yipai.realestate.ui.dialog
 * @date 2016/5/19
 * @version 0.1
 * @history v0.1, 2016/5/19, by fmh19
 * <p/>
 * Copyright (C) 2016 Yipai.
 ******************************************************************************/
package com.lhj.vehiclesystem.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.adapter.NoticeListAdapter;
import com.lhj.vehiclesystem.ui.widget.recyclerview.RecyclerViewLayout;
import com.lhj.vehiclesystem.util.MyUtil;

/**
 * 购票通知对话框
 */
public class NoticeDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "NoticeDialog";
    private RecyclerViewLayout list;
    BaseActivity mActivity;
    private Context mContext;

    public NoticeDialog(Activity activity) {
        super(activity, R.style.TimeDialog);
        this.mActivity = (BaseActivity) activity;
        mContext = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_notice, null);
        list = (RecyclerViewLayout) view.findViewById(R.id.list);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        initData();
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = MyUtil.toDip(400);
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.AnimButton);
        setContentView(view);
    }

    void initData() {
        list.setAdapter(new NoticeListAdapter(mContext));
        list.setBackgroundColor(Color.WHITE);
        list.setListLoadCall(null);
        list.setPullLoadEnable(false);
        list.setPullRefreshEnable(false);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setEmpty_tip(mContext.getResources().getString(R.string.no_data));
        list.showData(true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
