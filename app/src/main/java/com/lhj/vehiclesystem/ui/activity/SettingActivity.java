package com.lhj.vehiclesystem.ui.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.SpfUtil;

/**
 * Created by Songzhihang on 2018/3/3.
 */
public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    private TextView asl_tv_loginout;
    private LinearLayout asl_ll_clear_catch;
    private LinearLayout asl_ll_aboutus;
    private LinearLayout asl_ll_update;
    private LinearLayout asl_ll_change_type;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_setting_layout);
    }

    @Override
    protected void findViews() {

        asl_ll_clear_catch = (LinearLayout) findViewById(R.id.asl_ll_clear_catch);
        asl_ll_aboutus = (LinearLayout) findViewById(R.id.asl_ll_aboutus);
        asl_ll_update = (LinearLayout) findViewById(R.id.asl_ll_update);
        asl_ll_change_type = (LinearLayout) findViewById(R.id.asl_ll_change_type);
        asl_tv_loginout = (TextView) findViewById(R.id.asl_tv_loginout);
    }

    @Override
    protected void initData() {
        setTitle("设置");
        toolbar.setBackgroundColor(Color.WHITE);
        if (SpfUtil.getBoolean(Constant.IS_LOGIN, false)) {//已登陆获取用户信息
            asl_tv_loginout.setVisibility(View.VISIBLE);
        } else {
            asl_tv_loginout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        asl_ll_clear_catch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast("缓存已清除！");
            }
        });

        asl_ll_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToNext(AboutUsActivity.class);
            }
        });

        asl_ll_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast("当前已经是最新版本！");
            }
        });

        asl_ll_change_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpfUtil.clearAll();
                goToNext(TypeSelectActivity.class);
                clearAllActivity();
            }
        });

        asl_tv_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpfUtil.clearAll();
//        BaseApplication.getAPPInstance().setmUser(null);
//        jumpToNext(LoginActivity.class);
            }
        });
    }
}
