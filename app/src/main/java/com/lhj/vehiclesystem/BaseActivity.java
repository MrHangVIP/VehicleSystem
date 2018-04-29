package com.lhj.vehiclesystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.vehiclesystem.ui.activity.LoginActivity;
import com.lhj.vehiclesystem.ui.activity.MainTabActivity;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import java.util.LinkedList;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BaseActivity";

    protected Context mContext;

    private Toast toast;

    /**
     * 管理activity栈空间
     */
    private static LinkedList<BaseActivity> activityList = new LinkedList<BaseActivity>();
    /**
     * 登录相关页面
     */
    protected static LinkedList<BaseActivity> login_activities = new LinkedList<BaseActivity>();
    /**
     * 创建问卷相关页面管理
     */
    protected static LinkedList<BaseActivity> createActivityList = new LinkedList<BaseActivity>();

    protected Toolbar toolbar;

    protected TextView toolbarTitle;
    protected Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        activityList.add(this);
        bundle = getIntent().getBundleExtra("bundle");
        setView();

        setToolbar();

        findViews();

        initData();

        setListener();

    }

    public void cancelAction(View v) {
//        dismiss();
//        cancel();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        if (toolbar != null && toolbarTitle != null && showBackDrawable()) {
            toolbar.setNavigationIcon(setBackIcon());
            toolbar.setNavigationOnClickListener(setBackClick());
        }
    }

    protected boolean showBackDrawable() {
        return true;
    }

    /**
     * @param Res 资源id
     */
    protected void setRight(int Res) {
        toolbar.setRight(Res);
    }

    /**
     * 设置返回按钮图标
     *
     * @return 按钮图标
     */
    public Drawable setBackIcon() {
        return MyUtil.updateDrawableSize(
                getDrawableRes(R.drawable.back_toolbar),
                MyUtil.toDip(30), MyUtil.toDip(30));
    }

    /**
     * 设置返回按钮监听
     *
     * @return 按钮监听
     */
    public View.OnClickListener setBackClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
    }


    protected Drawable getDrawableRes(@DrawableRes int resId) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getDrawable(this, resId);
        } else {
            return getResources().getDrawable(resId);
        }
    }

    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(msg);
                }
                toast.show();
            }
        });
    }

    public void toast(@StringRes int rid) {
        String msg = mContext.getString(rid);
        toast(msg);
    }

    public void clearAll() {
        activityList.clear();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }


    /**
     * 页面切换，含参数
     *
     * @param mClass
     * @param bundle
     */
    public void jumpToNext(Class<?> mClass, Bundle bundle) {
        jumpToNext(mClass, bundle, 0);
    }

    /**
     * 页面切换，含参数
     *
     * @param mClass
     * @param bundle
     */
    protected void jumpToNext(Class<?> mClass, Bundle bundle, @AnimRes int resId) {
        Intent intent = new Intent(this, mClass);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        startActivity(intent);
        if (resId == 0) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_fade_out);
        } else {
            overridePendingTransition(resId, 0);
        }
    }

    /**
     * 页面切换，不含参数
     *
     * @param mClass
     */
    public void jumpToNext(Class<?> mClass) {
        jumpToNext(mClass, null);
    }

    /**
     * 页面切换，不含参数
     *
     * @param mClass
     */
    protected void jumpToNext(Class<?> mClass, @AnimRes int resId) {
        jumpToNext(mClass, null, resId);
    }

    /**
     * 页面切换，含参数，并且将当前页面关闭
     *
     * @param mClass
     * @param bundle
     */
    protected void goToNext(Class<?> mClass, Bundle bundle) {
        jumpToNext(mClass, bundle);
        finish();
    }

    /**
     * 页面切换，含参数，并且将当前页面关闭
     *
     * @param mClass
     * @param bundle
     */
    protected void goToNext(Class<?> mClass, Bundle bundle, @AnimRes int resId) {
        jumpToNext(mClass, bundle, resId);
        finish();
    }

    /**
     * 页面切换，不含参数，并且将原页面关闭
     *
     * @param mClass
     */
    public void goToNext(Class<?> mClass) {
        goToNext(mClass, null, 0);
    }

    /**
     * 页面切换，不含参数，并且将原页面关闭
     *
     * @param mClass
     */
    public void goToNext(Class<?> mClass, @AnimRes int resId) {
        goToNext(mClass, null, resId);
    }

    /**
     * token失效
     */
    public void tokenError() {
        BaseApplication.getAPPInstance().setmUser(null);
        SpfUtil.clearAll();
        if(this instanceof MainTabActivity){
            jumpToNext(LoginActivity.class);
        }else{
            goToNext(LoginActivity.class);
        }
    }

    @Override
    public void onClick(View v) {

    }

    protected abstract void setView();

    protected abstract void findViews();

    protected abstract void initData();

    protected abstract void setListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtil.MyLogE(TAG, "onDestroy");
        activityList.remove(this);
    }

    protected void clearAllActivity() {
        for (BaseActivity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }
    protected void clearLogins() {
        for (BaseActivity activity : login_activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
