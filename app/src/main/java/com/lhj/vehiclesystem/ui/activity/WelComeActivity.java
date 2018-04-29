package com.lhj.vehiclesystem.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.widget.WelcomeCircularProgress;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.PermissionConstant;
import com.lhj.vehiclesystem.util.PermissionUtil;
import com.lhj.vehiclesystem.util.SpfUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @discription 欢迎页
 * @autor songzhihang
 * @time 2018/2/28  上午10:54
 **/
public class WelComeActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "WelComeActivity";
    private WelcomeCircularProgress ad_jump_progress1;
    private RelativeLayout ad_jump_layout1;
    private Timer mTimer;

    //权限申请
    private PermissionUtil.IPermissionCallBack permissionCallBack;
    private String[] permissions;
    //所有需要的启动权限
    private static String[] allNeedPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    @Override
    protected void setView() {
        setContentView(R.layout.activity_welcome_layout);
    }

    @Override
    protected void findViews() {
        ad_jump_layout1 = (RelativeLayout) findViewById(R.id.ad_jump_layout1);
        ad_jump_progress1 = (WelcomeCircularProgress) findViewById(R.id.ad_jump_progress1);
        ad_jump_progress1.setProgressColor(mContext.getResources().getColor(R.color.theme_color));
        ad_jump_progress1.setBacgroundColor(0x90000000);
        ad_jump_progress1.setMaxProgress(5000);
    }

    @Override
    protected void initData() {
        setWindowTranslucent();
        mTimer = new Timer();
        requestPermission(PermissionConstant.REQUEST_WEB, allNeedPermissions, mInitPermissionCallback);
//        //==================================================================================
    }

    /**
     * 启动权限申请结果回调
     */
    PermissionUtil.IPermissionCallBack mInitPermissionCallback = new PermissionUtil.IPermissionCallBack() {
        @Override
        public void permissionsGranted() {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (ad_jump_progress1.getProgress() < 5000) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ad_jump_progress1.setProgress((int) (ad_jump_progress1.getProgress() + 10));
                            }
                        });
                        return;
                    }
                    goMain();
                }
            }, 0, 10);
        }

        @Override
        public void permissionsDenied() {
            showPermissionDeniedDialog();
        }
    };

    @Override
    protected void setListener() {
        ad_jump_layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SpfUtil.getBoolean(Constant.IS_FIRST, true)) {
            goToNext(MainTabActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }

    private void goMain() {
        //以上条件不符合   则暂停计时器
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        SpfUtil.saveBoolean(Constant.IS_FIRST, true);
        //
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == Constant.TYPE_BISSINESS) {//商家
            //检查是否登录
            if (SpfUtil.getBoolean(Constant.IS_LOGIN, false)) {
                goToNext(MainTabActivity.class);
            } else {
                goToNext(LoginActivity.class);
            }
        }
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == Constant.TYPE_USER) {//用户
            //检查是否登录
            if (SpfUtil.getBoolean(Constant.IS_LOGIN, false)) {
                goToNext(MainTabActivity.class);
            } else {
                goToNext(LoginActivity.class);
            }
        }
        if (SpfUtil.getInt(Constant.LOGIN_TYPE, 0) == 0) {//选择类型页面
            goToNext(TypeSelectActivity.class);
        }
    }

    /**
     * 设置状态栏透明
     */
    private void setWindowTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // 透明状态栏
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 授权回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限
     *
     * @param requestCode
     * @param permissions
     * @param callBack
     */
    public void requestPermission(int requestCode, String[] permissions, PermissionUtil.IPermissionCallBack callBack) {
        this.permissions = permissions;
        permissionCallBack = callBack;
        if (EasyPermissions.hasPermissions(this, permissions)) {
            callBack.permissionsGranted();
        } else {
            EasyPermissions.requestPermissions(this, PermissionConstant.getPermissionText(requestCode), requestCode, permissions);
        }
    }

    /**
     * 同意权限申请
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && this.permissions.length == perms.size()) {
            permissionCallBack.permissionsGranted();
        }
    }

    /**
     * 拒绝权限申请
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (perms != null && perms.size() > 0 && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //用户拒绝 授权，并且选择不再提示后 去设置界面处理
            new AppSettingsDialog.Builder(this).setRequestCode(requestCode).build().show();
        } else {
            if (permissionCallBack != null) {
                permissionCallBack.permissionsDenied();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(getString(R.string.permission_welcome)); //设置内容
        builder.setPositiveButton("继续？", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                requestPermission(PermissionConstant.REQUEST_WEB, allNeedPermissions, mInitPermissionCallback);
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyUtil.restartApplication(mContext, false);
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
}
