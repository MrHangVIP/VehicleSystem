package com.lhj.vehiclesystem.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.BusinessItem;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.UserItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;
import com.lhj.vehiclesystem.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @discription 登录
 * @autor songzhihang
 * @time 2018/2/28  下午3:57
 **/
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private EditText login4_ll_et_userphone;
    private ImageView login4_ll_iv_delete;
    private EditText login4_ll_et_password;
    private ImageView login4_ll_iv_pass_delete;
    private Button login4_ll_bt_login;
    private TextView login4_ll_tv_forget_pass;
    private TextView login4_ll_tv_register;
    private LinearLayout login4_ll_ll_third_layout;
    private int buttonColor;
    private String last_login_name;
    private int type;
    private RelativeLayout login4_ll_rl_businessId;
    private EditText login4_ll_et_businessId;
    private ImageView login4_ll_iv_businessId_delete;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_login_layout);
    }

    @Override
    protected void findViews() {
        login4_ll_et_userphone = (EditText) findViewById(R.id.login4_ll_et_userphone);
        login4_ll_iv_delete = (ImageView) findViewById(R.id.login4_ll_iv_delete);
        login4_ll_et_password = (EditText) findViewById(R.id.login4_ll_et_password);
        login4_ll_iv_pass_delete = (ImageView) findViewById(R.id.login4_ll_iv_pass_delete);
        login4_ll_rl_businessId = (RelativeLayout) findViewById(R.id.login4_ll_rl_businessId);
        login4_ll_et_businessId = (EditText) findViewById(R.id.login4_ll_et_businessId);
        login4_ll_iv_businessId_delete = (ImageView) findViewById(R.id.login4_ll_iv_businessId_delete);
        login4_ll_bt_login = (Button) findViewById(R.id.login4_ll_bt_login);
        //忘记密码和注册
        login4_ll_tv_forget_pass = (TextView) findViewById(R.id.login4_ll_tv_forget_pass);
        login4_ll_tv_register = (TextView) findViewById(R.id.login4_ll_tv_register);
        //第三方
        login4_ll_ll_third_layout = (LinearLayout) findViewById(R.id.login4_ll_ll_third_layout);
        login4_ll_bt_login.setEnabled(false);
    }

    @Override
    protected void initData() {
        type = SpfUtil.getInt(Constant.LOGIN_TYPE, 0);
        if (type == Constant.TYPE_BISSINESS) {
            setTitle("管理员登录");
            login4_ll_et_userphone.setHint("邮箱");
            login4_ll_tv_forget_pass.setVisibility(View.GONE);
            login4_ll_tv_register.setVisibility(View.GONE);
            login4_ll_rl_businessId.setVisibility(View.GONE);
            login4_ll_et_businessId.setText("ss");
        } else {
            setTitle("员工登录");
        }

        toolbarTitle.setTextColor(Color.parseColor("#333333"));
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.inflateMenu(R.menu.login_cancle_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goToNext(TypeSelectActivity.class);
                return true;
            }
        });
        last_login_name = SpfUtil.getString(Constant.LOGIN_USERPHONE, "");
        buttonColor = Color.parseColor("#DC3C38");
    }

    protected boolean showBackDrawable() {
        return false;
    }

    @Override
    protected void setListener() {
        login4_ll_et_userphone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = !TextUtils.isEmpty(login4_ll_et_userphone.getText().toString());
                if (isEmpty) {
                    login4_ll_iv_delete.setVisibility(View.VISIBLE);
                } else {
                    login4_ll_iv_delete.setVisibility(View.GONE);
                }
                isEmpty = isEmpty && !TextUtils.isEmpty(login4_ll_et_password.getText().toString()) && !TextUtils.isEmpty(login4_ll_et_businessId.getText().toString());
                login4_ll_bt_login.setEnabled(isEmpty);
            }
        });

        login4_ll_et_userphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(login4_ll_et_userphone.getText())) {
                    login4_ll_iv_delete.setVisibility(View.VISIBLE);
                } else {
                    login4_ll_iv_delete.setVisibility(View.GONE);
                }
            }
        });

        login4_ll_et_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = !TextUtils.isEmpty(login4_ll_et_password.getText().toString());
                if (isEmpty) {
                    login4_ll_iv_businessId_delete.setVisibility(View.VISIBLE);
                } else {
                    login4_ll_iv_businessId_delete.setVisibility(View.GONE);
                }
                isEmpty = isEmpty && !TextUtils.isEmpty(login4_ll_et_userphone.getText().toString()) && !TextUtils.isEmpty(login4_ll_et_businessId.getText().toString());
                login4_ll_bt_login.setEnabled(isEmpty);
            }
        });
        login4_ll_et_businessId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = !TextUtils.isEmpty(login4_ll_et_businessId.getText().toString());
                if (isEmpty) {
                    login4_ll_iv_pass_delete.setVisibility(View.VISIBLE);
                } else {
                    login4_ll_iv_pass_delete.setVisibility(View.GONE);
                }
                isEmpty = isEmpty && !TextUtils.isEmpty(login4_ll_et_userphone.getText().toString()) && !TextUtils.isEmpty(login4_ll_et_password.getText().toString());
                login4_ll_bt_login.setEnabled(isEmpty);
            }
        });

        login4_ll_et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(login4_ll_et_password.getText())) {
                    login4_ll_iv_pass_delete.setVisibility(View.VISIBLE);
                } else {
                    login4_ll_iv_pass_delete.setVisibility(View.GONE);
                }
            }
        });

        login4_ll_iv_pass_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login4_ll_et_password.setText("");
            }
        });

        login4_ll_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login4_ll_et_userphone.setText("");
            }
        });

        login4_ll_bt_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!login4_ll_bt_login.isEnabled()) {
                    return;
                }
                MyUtil.hideSoftInput(login4_ll_et_userphone);
                String name = login4_ll_et_userphone.getText().toString();
                String pwd = login4_ll_et_password.getText().toString();
                if (type == Constant.TYPE_BISSINESS) {
                    if (!StringUtils.isEmail(name)) {
                        toast("邮箱格式不对！");
                        loginFail();
                        return;
                    }
                    businessLogin(name, pwd);
                } else {
                    String businessId = login4_ll_et_businessId.getText().toString();
                    if (!StringUtils.isMobile(name)) {
                        toast("请输入正确的手机号");
                        loginFail();
                        return;
                    }
                    if (pwd.length() < 8) {
                        toast("密码长度不能低于8位");
                        loginFail();
                        return;
                    }
                    userLogin(name, pwd, businessId);
                }
            }
        });

        login4_ll_tv_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPwdAction();
            }
        });

        login4_ll_tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAction();
            }
        });
    }

    protected void forgetPwdAction() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.OPRATION_TYPE, 1);
        jumpToNext(ForgetPassWordActivity.class, bundle);
    }

    void registerAction() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.OPRATION_TYPE, 1);
        jumpToNext(RegistActivity.class, bundle);
    }

    private void businessLogin(String email, String passWord) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("passWord", passWord);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.LOGIN_URL_BUSINESS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (!object.getResult().equals("fail")) {
                    toast("登录成功！");
                    JSONObject userJson = null;
                    try {
                        userJson = new JSONObject(object.getData());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BusinessItem businessItem = (new Gson()).fromJson(userJson.toString(), BusinessItem.class);
                    BaseApplication.getAPPInstance().setBusinessUser(businessItem);
                    SpfUtil.saveBoolean(Constant.IS_LOGIN, true);
                    SpfUtil.saveString(Constant.BISINESS_ID, businessItem.getBusinessId());
                    SpfUtil.saveString(Constant.BISINESS_PASSWORD, businessItem.getPassWord());
                    SpfUtil.saveString(Constant.BISINESS_EMAIL, businessItem.getEmail());
                    goToNext(MainTabActivity.class);
                    clearLogins();
                } else {
                    toast("邮箱或者密码错误");
                    loginFail();
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

    private void userLogin(String userPhone, String userPass, String businessId) {
        Map<String, String> params = new HashMap<>();
        params.put("userPhone", userPhone);
        params.put("userPass", userPass);
        params.put("businessId", businessId);
        params.put("MAC", Constant.MacAddress);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.LOGIN_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (!object.getResult().equals("fail")) {
                    toast("登录成功！");
                    SpfUtil.saveString(Constant.TOKEN, object.getResult());
                    JSONObject userJson = null;
                    try {
                        userJson = new JSONObject(object.getData());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UserItem userItem = (new Gson()).fromJson(userJson.toString(), UserItem.class);
                    BaseApplication.getAPPInstance().setmUser(userItem);
                    SpfUtil.saveBoolean(Constant.IS_LOGIN, true);
                    SpfUtil.saveString(Constant.BISINESS_ID, userItem.getBusinessId());
                    SpfUtil.saveString(Constant.LOGIN_USERPHONE, userItem.getUserPhone());
                    goToNext(MainTabActivity.class);
                    clearLogins();
                } else {
                    toast("用户名或密码错误");
                    loginFail();
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

    private void loginFail() {
        login4_ll_et_userphone.setText("");
        login4_ll_et_userphone.requestFocus();
        login4_ll_et_password.setText("");
        login4_ll_et_businessId.setText("");
    }
}
