package com.lhj.vehiclesystem.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.lhj.vehiclesystem.util.LoginUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SpfUtil;
import com.lhj.vehiclesystem.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @discription 注册
 * @autor songzhihang
 * @time 2018/2/28  下午5:07
 **/
public class RegistActivity extends BaseActivity {

    private static final String TAG = "RegisterStyle";
    private int opration_type = 1;//发送验证码和注册2个页面
    private LinearLayout contentView;
    //注册输入手机号页面
    private EditText login4_rscl_et_userphone;
    private ImageView login4_rscl_iv_pass_delete;
    private Button login4_rscl_bt_send;
    //注册输入验证码和密码页面
    private EditText login4_rnl_et_phone;
    private EditText login4_rnl_et_businessId;
    private EditText login4_rnl_et_password1;
    private ImageView login4_rnl_iv_show;
    private EditText login4_rnl_et_password2;
    private Button login4_rnl_bt_register;
    private String businessId;

    @Override
    protected void setView() {
        mContext = this;
        login_activities.add(this);
        if (bundle != null) {
            opration_type = bundle.getInt(Constant.OPRATION_TYPE, 1);
            businessId = bundle.getString("businessId", "");
        }
        initView();
        setContentView(contentView);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initData() {
        setTitle(R.string.login4_register);
        toolbarTitle.setTextColor(Color.parseColor("#333333"));
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.inflateMenu(R.menu.login_cancle_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goBack();
                return true;
            }
        });
    }

    protected boolean showBackDrawable() {
        return false;
    }

    private void initView() {
        if (opration_type == 1) {//发送验证码
            contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_register_layout, null);
            login4_rscl_et_userphone = (EditText) contentView.findViewById(R.id.login4_rscl_et_userphone);
            login4_rscl_iv_pass_delete = (ImageView) contentView.findViewById(R.id.login4_rscl_iv_pass_delete);
            login4_rscl_bt_send = (Button) contentView.findViewById(R.id.login4_rscl_bt_send);
        } else {
            contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_register_next_layout, null);
            login4_rnl_et_businessId = (EditText) contentView.findViewById(R.id.login4_rnl_et_businessId);
            login4_rnl_et_phone = (EditText) contentView.findViewById(R.id.login4_rnl_et_phone);
            login4_rnl_et_password1 = (EditText) contentView.findViewById(R.id.login4_rnl_et_password1);
            login4_rnl_iv_show = (ImageView) contentView.findViewById(R.id.login4_rnl_iv_show);
            login4_rnl_et_password2 = (EditText) contentView.findViewById(R.id.login4_rnl_et_password2);
            login4_rnl_bt_register = (Button) contentView.findViewById(R.id.login4_rnl_bt_register);
            login4_rnl_et_businessId.setText(businessId);
        }
    }

    protected void setListener() {
        if (opration_type == 1) {
            login4_rscl_et_userphone.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean isEmpty = !TextUtils.isEmpty(login4_rscl_et_userphone.getText().toString());
                    if (isEmpty) {
                        login4_rscl_iv_pass_delete.setVisibility(View.VISIBLE);
                    } else {
                        login4_rscl_iv_pass_delete.setVisibility(View.GONE);
                    }
                    login4_rscl_bt_send.setEnabled(isEmpty);
                }
            });

            login4_rscl_et_userphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && !TextUtils.isEmpty(login4_rscl_et_userphone.getText())) {
                        login4_rscl_iv_pass_delete.setVisibility(View.VISIBLE);
                    } else {
                        login4_rscl_iv_pass_delete.setVisibility(View.GONE);
                    }
                }
            });
            login4_rscl_iv_pass_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login4_rscl_et_userphone.setText("");
                }
            });

            login4_rscl_bt_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (login4_rscl_bt_send.isEnabled()) {//检测手机号是否合理,手机号是否绑定,跳转到注册
                        String businessId = login4_rscl_et_userphone.getText().toString();
                        checkBusinessId(businessId);
                    }
                }
            });
        } else {
            LoginUtil.listenEditView(login4_rnl_bt_register, login4_rnl_et_phone, login4_rnl_et_businessId, login4_rnl_et_password1, login4_rnl_et_password2);
            login4_rnl_iv_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean ishow = login4_rnl_iv_show.getTag() != null ? (boolean) login4_rnl_iv_show.getTag() : true;
                    if (ishow) {
                        login4_rnl_et_password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        login4_rnl_et_password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        login4_rnl_iv_show.setImageResource(R.drawable.login4_icon_hide_pass);
                    } else {
                        login4_rnl_et_password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        login4_rnl_et_password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        login4_rnl_iv_show.setImageResource(R.drawable.login4_icon_show_pass);
                    }
                    login4_rnl_iv_show.setTag(!ishow);
                }
            });

            login4_rnl_bt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!login4_rnl_bt_register.isEnabled())
                        return;
                    if (!StringUtils.isMobile(login4_rnl_et_phone.getText() + "")) {
                        toast("请输入正确手机号!");
                        return;
                    }
                    if (login4_rnl_et_password1.getText().length()<8) {
                        toast("密码不能低于8位!");
                        return;
                    }
                    if (!TextUtils.equals(login4_rnl_et_password1.getText(), login4_rnl_et_password2.getText())) {
                        toast("请重新输入密码");
                        login4_rnl_et_password1.setText("");
                        login4_rnl_et_password2.setText("");
                        return;
                    }
                    onRegistAction(businessId, login4_rnl_et_password1.getText().toString(), login4_rnl_et_phone.getText().toString());
                }
            });
        }
    }


    /**
     * @param businessId
     * @param userPass
     * @param userPhone
     */
    private void onRegistAction(final String businessId, final String userPass, final String userPhone) {
        ProgressDialogUtil.showProgressDialog(mContext, true);
        Map<String, String> params = new HashMap<>();
        params.put("businessId", businessId);
        params.put("userPhone", userPhone);
        params.put("userPass", userPass);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.REGIST_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                if (!object.getResult().equals("fail")) {
                    SpfUtil.saveString(Constant.BISINESS_ID, businessId);
                    SpfUtil.saveString(Constant.LOGIN_USERPHONE, userPhone);
                    SpfUtil.saveString(Constant.BISINESS_PASSWORD, userPass);
                    SpfUtil.saveInteger(Constant.LOGIN_TYPE, Constant.TYPE_USER);
                    userLogin(userPhone, userPass, businessId);
                } else {
                    ProgressDialogUtil.dismissProgressdialog();
                    if (object.getData() != null && object.getData().equals("exist")) {
                        toast("该手机已经注册请登录");
                    } else {
                        toast("注册失败");
                    }
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

    /**
     * 企业id检测
     *
     * @param businessId
     */
    private void checkBusinessId(final String businessId) {
        ProgressDialogUtil.showProgressDialog(mContext, true);
        Map<String, String> params = new HashMap<>();
        params.put("businessId", businessId);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.BUSINESSID_CHECK_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (!object.getResult().equals("fail")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.OPRATION_TYPE, 0);
                    bundle.putString("businessId", businessId);
                    jumpToNext(RegistActivity.class, bundle);
                } else {
                    toast("企业号不存在或错误，请联系企业管理员！");
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
                    clearLogins();
                    goToNext(MainTabActivity.class);
                } else {
                    toast("用户名或密码错误");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login_activities.remove(this);
    }

    public void goBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
