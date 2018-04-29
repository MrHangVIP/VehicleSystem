package com.lhj.vehiclesystem.ui.activity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.widget.WheelViewDialog;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.DateUtil;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;
import com.lhj.vehiclesystem.util.WheelDateUtil;
import com.lhj.vehiclesystem.util.WheelViewDialogUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 申请用车页面
 * Created by Songzhihang on 2018/4/16.
 */
public class ApplyVehicleActivity extends BaseActivity {
    private static final String TAG = "ApplyVehicleActivity";
    private EditText apvl_et_message;
    private TextView apvl_tv_starttime;
    private TextView apvl_tv_finishTime;
    private TextView apvl_tv_submit;
    private WheelViewDialog timeStartWheelDialog;
    private WheelViewDialog timeFinishWheelDialog;
    private WheelDateUtil wheelDateUtil;
    private WheelDateUtil wheelDateUtil1;
    private int vehicleId;

    private Handler handle = new Handler();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_apply_vehicle_layout);
    }

    @Override
    protected void findViews() {
        apvl_et_message = (EditText) findViewById(R.id.apvl_et_message);
        apvl_tv_starttime = (TextView) findViewById(R.id.apvl_tv_starttime);
        apvl_tv_finishTime = (TextView) findViewById(R.id.apvl_tv_finishTime);
        apvl_tv_submit = (TextView) findViewById(R.id.apvl_tv_submit);
    }

    @Override
    protected void initData() {
        vehicleId = getIntent().getBundleExtra("bundle").getInt("vehicleId");

        setTitle("申请用车");
        apvl_tv_starttime.setText(DateUtil.getCurrentDate(DateUtil.Date_Format_3));
        apvl_tv_finishTime.setText(DateUtil.getCurrentDate(DateUtil.Date_Format_3));
        initTimeDialog();
    }

    @Override
    protected void setListener() {
        apvl_tv_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStartWheelDialog.show();
            }
        });
        apvl_tv_finishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFinishWheelDialog.show();
            }
        });


        apvl_tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitAction();
            }
        });
    }

    private void initTimeDialog() {
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params1.weight = 1;
        params1.gravity = Gravity.RIGHT;
        wheelDateUtil = new WheelDateUtil(mContext);
        wheelDateUtil1 = new WheelDateUtil(mContext);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(MyUtil.toDip(50), ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params3.weight = 1;
        params3.gravity = Gravity.LEFT;
        wheelDateUtil.setCyclic(true);
        wheelDateUtil.textSize = 17;
        wheelDateUtil1.setCyclic(true);
        wheelDateUtil1.textSize = 17;
        LinkedHashMap<View, LinearLayout.LayoutParams> map = new LinkedHashMap<>();
        map.put(wheelDateUtil.getWv_year(), params1);
        map.put(wheelDateUtil.getWv_month(), params2);
        map.put(wheelDateUtil.getWv_day(), params3);
        LinkedHashMap<View, LinearLayout.LayoutParams> map1 = new LinkedHashMap<>();
        map1.put(wheelDateUtil1.getWv_year(), params1);
        map1.put(wheelDateUtil1.getWv_month(), params2);
        map1.put(wheelDateUtil1.getWv_day(), params3);
        timeStartWheelDialog = WheelViewDialogUtil.showWheelViewDialog(ApplyVehicleActivity.this, "选择开始时间", new WheelViewDialog.DialogSubmitListener() {
            @Override
            public void onSubmitClick(View v) {
                String startTime = wheelDateUtil.getYear()
                        + "-" + wheelDateUtil.getMonth() + "-" + wheelDateUtil.getDay();
                apvl_tv_starttime.setText(startTime);
                toast("开始时间已选定");
                timeStartWheelDialog.dismiss();
            }
        }, map, false);
        timeFinishWheelDialog = WheelViewDialogUtil.showWheelViewDialog(ApplyVehicleActivity.this, "选择结束时间", new WheelViewDialog.DialogSubmitListener() {
            @Override
            public void onSubmitClick(View v) {
                String finishTime = wheelDateUtil1.getYear()
                        + "-" + wheelDateUtil1.getMonth() + "-" + wheelDateUtil1.getDay();
                apvl_tv_finishTime.setText(finishTime);
                toast("结束时间已选定");
                timeFinishWheelDialog.dismiss();
            }
        }, map1, false);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelDateUtil.setPicker(year, month, day);
        wheelDateUtil1.setPicker(year, month, day);
    }

    private void onSubmitAction() {
        String message = apvl_et_message.getText().toString();
        if (TextUtils.isEmpty(message)) {
            toast("用车原因不能为空！");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", BaseApplication.getAPPInstance().getmUser().getUserId() + "");
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("message", message);
        params.put("starttime", apvl_tv_starttime.getText().toString());
        params.put("finishtime", apvl_tv_finishTime.getText().toString());
        params.put("vehicleId", vehicleId + "");
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("post", Constant.Add_RECORD_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (object.getResult().equals("success")) {
                    //修改成功
                    toast("申请成功,请等待管理员审核！");
                    apvl_et_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    toast("申请失败！");
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
