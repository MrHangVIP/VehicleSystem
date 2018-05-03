package com.lhj.vehiclesystem.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.BusinessItem;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.UserItem;
import com.lhj.vehiclesystem.business.PublishVehiclesActivity;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.fragment.HomeFragment;
import com.lhj.vehiclesystem.ui.fragment.UserCenterFragment;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Songzhihang on 2017/10/6.
 * 首页tabActivity
 */
public class MainTabActivity extends BaseActivity {
    private static final String TAG = "MainTabActivity";
    private FrameLayout contentFL;
    private LinearLayout tabLL;
    private LinearLayout aml_ll_left;
    private LinearLayout aml_ll_right;
    private ImageView aml_iv_left;
    private ImageView aml_iv_right;
    private View aml_iv_add;
    private ImageView aml_iv_publish;
    private HomeFragment homeFragment;
    private UserCenterFragment userCenterFragment;
    private  long curtime;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_maintab_layout);
    }

    @Override
    protected void findViews() {
        contentFL = (FrameLayout) findViewById(R.id.aml_fl_content);
        tabLL = (LinearLayout) findViewById(R.id.aml_ll_tab_layout);
        aml_ll_left = (LinearLayout) findViewById(R.id.aml_ll_left);
        aml_ll_right = (LinearLayout) findViewById(R.id.aml_ll_right);
        aml_iv_left = (ImageView) findViewById(R.id.aml_iv_left);
        aml_iv_right = (ImageView) findViewById(R.id.aml_iv_right);
        aml_iv_add = (View) findViewById(R.id.aml_iv_add);
        aml_iv_publish = (ImageView) findViewById(R.id.aml_iv_publish);
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 1) == Constant.TYPE_BISSINESS) {
            aml_iv_add.setVisibility(View.VISIBLE);
            aml_iv_publish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        replaceFragment(homeFragment);
    }

    @Override
    protected void setListener() {
        aml_ll_right.setOnClickListener(this);
        aml_ll_left.setOnClickListener(this);
        aml_iv_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToNext(PublishVehiclesActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SpfUtil.getBoolean(Constant.IS_LOGIN, false)) {//已登陆获取用户信息
            if (Constant.TYPE_BISSINESS == SpfUtil.getInt(Constant.LOGIN_TYPE, 0)) {
                businessLogin();
            } else {
                getUserData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.aml_ll_left) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }
            replaceIcon(0);
            replaceFragment(homeFragment);
        }
        if (v.getId() == R.id.aml_ll_right) {
            if (userCenterFragment == null) {
                userCenterFragment = new UserCenterFragment();
            }
            replaceIcon(1);
            replaceFragment(userCenterFragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.aml_fl_content, fragment);
        transaction.commit();
    }


    private void replaceIcon(int positon) {
        if (positon == 0) {
            aml_iv_left.setImageResource(R.drawable.icon_home_press);
            aml_iv_right.setImageResource(R.drawable.icon_mine_normal);
        } else {
            aml_iv_left.setImageResource(R.drawable.icon_home_normal);
            aml_iv_right.setImageResource(R.drawable.icon_mine_press);
        }
    }

    private void businessLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("email", SpfUtil.getString(Constant.BISINESS_EMAIL, ""));
        params.put("passWord", SpfUtil.getString(Constant.BISINESS_PASSWORD, ""));
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.LOGIN_URL_BUSINESS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (!object.getResult().equals("fail")) {
                    JSONObject userJson = null;
                    try {
                        userJson = new JSONObject(object.getData());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (userJson == null) {
                        toast("请重新登录");
                        goToNext(LoginActivity.class);
                        return;
                    }
                    BusinessItem businessItem = (new Gson()).fromJson(userJson.toString(), BusinessItem.class);
                    BaseApplication.getAPPInstance().setBusinessUser(businessItem);
                    SpfUtil.saveBoolean(Constant.IS_LOGIN, true);
                    SpfUtil.saveString(Constant.BISINESS_ID, businessItem.getBusinessId());
                    SpfUtil.saveString(Constant.BISINESS_EMAIL, businessItem.getEmail());
                } else {
                    toast("请重新登录");
                    goToNext(LoginActivity.class);
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                goToNext(LoginActivity.class);
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    private void getUserData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", SpfUtil.getString(Constant.TOKEN, ""));
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("userPhone", SpfUtil.getString(Constant.LOGIN_USERPHONE, ""));
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("", Constant.GET_USER_URL, params, new ResponseListener<ResultItem>() {
                    @Override
                    public void onSuccess(ResultItem object) {
                        ProgressDialogUtil.dismissProgressdialog();
                        if ("fail".equals(object.getResult())) {
                            if ("token error".equals(object.getData())) {
                                toast("token失效,请重新登录");
                                tokenError();
                            }
                        } else {
                            JSONObject userJson = null;
                            try {
                                userJson = new JSONObject(object.getData());
                                UserItem userItem = (new Gson()).fromJson(userJson.toString(), UserItem.class);
                                BaseApplication.getAPPInstance().setmUser(userItem);
                                userInfoCheck(userItem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailed(String message) {
                        ProgressDialogUtil.dismissProgressdialog();
                    }

                    @Override
                    public Class getEntityClass() {
                        return ResultItem.class;
                    }
                }

        );
    }

    /**
     * 用户信息完善校验
     */
    private void userInfoCheck(UserItem userItem) {
        if (userItem != null) {
            if (TextUtils.isEmpty(userItem.getLicence())
                    || TextUtils.isEmpty(userItem.getTrueName())
                    || TextUtils.isEmpty(userItem.getType())) {
                jumpToNext(FinishUserInfoActivity.class);
            }
        }
    }

    @Override
    public void onBackPressed() {
        long time= System.currentTimeMillis();
        if(time-curtime<=3000){//3秒内重复点击退出
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }else{
            curtime=time;
            toast("再点击一次退出");
        }
    }
}
