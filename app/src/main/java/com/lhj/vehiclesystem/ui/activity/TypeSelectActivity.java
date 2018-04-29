package com.lhj.vehiclesystem.ui.activity;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.SpfUtil;

/**
 * 类型选择页面
 * Created by Songzhihang on 2018/3/11.
 */
public class TypeSelectActivity extends BaseActivity {
    private static final String TAG = "TypeSelectActivity";
    private RadioButton atsl_rb_bussiness;
    private RadioButton atsl_rb_user;
    private TextView atsl_tv_start;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_type_select_layout);
    }

    @Override
    protected void findViews() {
        atsl_rb_bussiness = (RadioButton) findViewById(R.id.atsl_rb_bussiness);
        atsl_rb_user = (RadioButton) findViewById(R.id.atsl_rb_user);
        atsl_tv_start = (TextView) findViewById(R.id.atsl_tv_start);
    }

    protected boolean showBackDrawable() {
        return false;
    }

    @Override
    protected void initData() {
        setTitle("类型选择");
    }

    @Override
    protected void setListener() {
        atsl_tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (atsl_rb_bussiness.isChecked()) {
                    SpfUtil.saveInteger(Constant.LOGIN_TYPE, Constant.TYPE_BISSINESS);
                    goToNext(LoginActivity.class);
                }
                if (atsl_rb_user.isChecked()) {
                    SpfUtil.saveInteger(Constant.LOGIN_TYPE, Constant.TYPE_USER);
                    goToNext(LoginActivity.class);
                }
            }
        });
    }
}
