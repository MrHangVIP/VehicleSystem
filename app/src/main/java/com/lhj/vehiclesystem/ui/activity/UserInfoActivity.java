package com.lhj.vehiclesystem.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.UserItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.widget.WheelViewDialog;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;
import com.lhj.vehiclesystem.util.WheelUtil;
import com.lhj.vehiclesystem.util.WheelViewDialogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人信息页面
 * Created by Songzhihang on 2018/4/8.
 */
public class UserInfoActivity extends BaseActivity {
    private static final String TAG = "UserInfoActivity";

    private WheelViewDialog dirverLevelsDialog;
    private List<String> dirverLevels = new ArrayList<>();
    private EditText aful_et_true_name;
    private RadioButton aful_rb_man;
    private RadioButton aful_rb_woman;
    private EditText aful_tv_birth;
    private EditText aful_et_licence;
    private TextView aful_tv_level;
    private Button aful_bt_submit;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_finish_userinfo_layout);
    }

    @Override
    protected void findViews() {
        aful_et_true_name = (EditText) findViewById(R.id.aful_et_true_name);
        aful_rb_man = (RadioButton) findViewById(R.id.aful_rb_man);
        aful_rb_woman = (RadioButton) findViewById(R.id.aful_rb_woman);
        aful_tv_birth = (EditText) findViewById(R.id.aful_tv_birth);
        aful_et_licence = (EditText) findViewById(R.id.aful_et_licence);
        aful_tv_level = (TextView) findViewById(R.id.aful_tv_level);
        aful_bt_submit = (Button) findViewById(R.id.aful_bt_submit);
        aful_bt_submit.setText("修改");
        aful_et_true_name.setEnabled(false);
        aful_et_true_name.setFocusable(false);
        aful_et_licence.setEnabled(false);
        aful_et_licence.setFocusable(false);
    }

    @Override
    protected void initData() {
        setTitle("个人信息");
        UserItem userItem = BaseApplication.getAPPInstance().getmUser();
        if (!TextUtils.isEmpty(userItem.getTrueName())) {
            aful_et_true_name.setText(userItem.getTrueName());
        }
        if (TextUtils.equals("女", userItem.getSex())) {
            aful_rb_woman.setChecked(true);
        }
        if (!TextUtils.isEmpty(userItem.getAge())) {
            aful_tv_birth.setText(userItem.getAge());
        }
        if (!TextUtils.isEmpty(userItem.getLicence())) {
            aful_et_licence.setText(userItem.getLicence());
        }
        if (!TextUtils.isEmpty(userItem.getType())) {
            aful_tv_level.setText(userItem.getType());
        }
    }

    @Override
    protected void setListener() {
        aful_tv_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDriverLevels();
            }
        });

        aful_bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAction();
            }
        });
    }

    private void showDriverLevels() {
        //弹框提示
        if (dirverLevelsDialog != null) {
            dirverLevelsDialog.show();
            return;
        }
        dirverLevels.clear();
        //驾驶证种类：A1、A2、A3、B1、B2、C1、C2、C3、C4
        dirverLevels.add("A1");
        dirverLevels.add("A2");
        dirverLevels.add("A3");
        dirverLevels.add("B1");
        dirverLevels.add("B2");
        dirverLevels.add("C1");
        dirverLevels.add("C2");
        dirverLevels.add("C3");
        dirverLevels.add("C4");
        final WheelUtil typeWheelUtil = new WheelUtil(mContext);
        typeWheelUtil.setDatas(dirverLevels.toArray());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinkedHashMap<View, LinearLayout.LayoutParams> map = new LinkedHashMap<>();
        map.put(typeWheelUtil.getWheelView(), params);
        dirverLevelsDialog = WheelViewDialogUtil.showWheelViewDialog(UserInfoActivity.this, "驾照等级选择", new WheelViewDialog.DialogSubmitListener() {
            @Override
            public void onSubmitClick(View v) {
                aful_tv_level.setText(typeWheelUtil.getItems()[typeWheelUtil.getWheelView().getCurrentItem()].toString());
                dirverLevelsDialog.dismiss();
            }
        }, map);
        dirverLevelsDialog.setCancelable(false);
    }

    private void finishAction() {
        String trueName = aful_et_true_name.getText().toString();
        if (TextUtils.isEmpty(trueName)) {
            toast("请填写名字！");
            return;
        }
        String licence = aful_et_licence.getText().toString();
        if (TextUtils.isEmpty(licence) || licence.length() < 18) {
            toast("请填写正确驾驶证号或身份证！");
            return;
        }
        String birth = aful_tv_birth.getText().toString();
        if (TextUtils.isEmpty(birth)) {
            toast("请输入出生年月！");
            return;
        }
        String sex = aful_rb_man.isChecked() ? "男" : "女";
        String level = aful_tv_level.getText().toString();
        ProgressDialogUtil.showProgressDialog(mContext, true);
        Map<String, String> params = new HashMap<>();
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("userPhone", SpfUtil.getString(Constant.LOGIN_USERPHONE, ""));
        params.put("token", SpfUtil.getString(Constant.TOKEN, ""));
        params.put("trueName", trueName);
        params.put("sex", sex);
        params.put("licence", licence);
        params.put("birth", birth);
        params.put("level", level);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.FINISH_USER_INFO_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (object.getResult().equals("success")) {
                    //修改成功
                    toast("修改成功！");
                    finish();
                } else {
                    toast("修改失败！");
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
