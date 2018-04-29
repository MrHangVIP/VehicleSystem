
package com.lhj.vehiclesystem.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.ui.activity.WelComeActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class MyUtil {

    static public String T = "TICKETSELLER";

    static private Handler handler;

    static private final int NUM_OF_THREADS = 20;

    static private Executor executor;

    static {
        createExecutor();
    }

    static void createExecutor() {
        MyUtil.executor = Executors.newFixedThreadPool(NUM_OF_THREADS, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread th = new Thread(r);
                th.setName("2nd Screen BG");
                return th;
            }
        });
    }

    public static void runOnUI(Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(runnable);
    }

    public static void runInBackground(Runnable runnable, boolean forceNewThread) {
        if (forceNewThread || isMain()) {
            executor.execute(runnable);
        } else {
            runnable.run();
        }
    }

    public static void runInBackground(Runnable runnable) {
        runInBackground(runnable, false);
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static boolean isMain() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static <T> void postSuccess(final ResponseListener<T> listener, final T object) {
        if (listener == null)
            return;
        MyUtil.runOnUI(new Runnable() {
            @Override
            public void run() {
//				listener.onSuccess(object);
            }
        });
    }

//	public static void postError(final ErrorListener listener, final ServiceCommandError error) {
//		if (listener == null)
//			return;
//
//		MyUtil.runOnUI(new Runnable() {
//
//			@Override
//			public void run() {
//				listener.onError(error);
//			}
//		});
//	}

    public static byte[] convertIpAddress(int ip) {
        return new byte[]{(byte) (ip & 0xFF), (byte) ((ip >> 8) & 0xFF), (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 24) & 0xFF)};
    }

    public static long getTime() {
        return TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());
    }

    public static InetAddress getIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return null;
        } else {
            byte[] ipAddress = convertIpAddress(ip);
            return InetAddress.getByAddress(ipAddress);
        }
    }

    public static void MyLogV(String msg) {
        MyLogV(null, msg);
    }

    public static void MyLogV(String tag, String msg) {
        if (IsDebug) {
            if (tag == null) {
                tag = T;
            }
            Log.v(tag, msg);
        }
    }

    public static void MyLogD(String msg) {
        MyLogD(null, msg);
    }

    public static void MyLogD(String tag, String msg) {
        if (IsDebug) {
            if (tag == null) {
                tag = T;
            }
            Log.d(null, msg);
        }
    }

    public static void MyLogI(String msg) {
        MyLogI(null, msg);
    }

    public static void MyLogI(String tag, String msg) {
        if (IsDebug) {
            if (tag == null) {
                tag = T;
            }
            Log.i(tag, msg);
        }
    }

    public static void MyLogW(String msg) {
        MyLogW(null, msg);
    }

    public static void MyLogW(String tag, String msg) {
        if (IsDebug) {
            if (tag == null) {
                tag = T;
            }
            Log.w(tag, msg);
        }
    }

    public static void MyLogE(String msg) {
        MyLogE(null, msg);
    }

    public static void MyLogE(String tag, String msg) {
        if (IsDebug) {
            if (tag == null) {
                tag = T;
            }
            Log.e(tag, msg);
        }
    }

    public static void setDebugLog(boolean isdebug) {
        IsDebug = isdebug;
    }

    private static boolean IsDebug = true;

    /**
     * 日志等级
     *
     * @author moram
     */
    public enum LogLevel {
        VERBOUS, DEBUG, INFO, WARING, ERROR,
    }

    public void cancle() {
    }


    /**
     * 获取设备mac，不开wifi的情况下也能获取
     *
     * @return
     */
    public static String getMac() {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return macSerial;
    }

    /**
     * 图片文件转Base64字符串
     *
     * @param path
     * @return
     */
    public static String fileBase64String(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, count);
            }
            fis.close();
            String uploadBuffer = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));  //进行Base64编码
            return uploadBuffer;
        } catch (Exception e) {
            return null;
        }
    }

    //支持小数,更加精确
    public static int toDip(float value) {
        return (int) (value * Constant.getScreenDensity(BaseApplication.getAPPInstance()));
    }

    /**
     * 隐藏软键盘
     *
     * @param v
     */
    public static void hideSoftInput(View v) {
        if (v == null)
            return;
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param v
     */
    public static void showSoftInput(View v) {
        if (v == null)
            return;
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setVisibility(View view, int flag) {
        if (view != null && view.getVisibility() != flag) {
            view.setVisibility(flag);
        }
    }

    /**
     * 设置下左右图片大小
     *
     * @param view
     * @param width
     * @param height
     * @param attr
     */
    public static void setCompoundDrawables(TextView view, int width,
                                            int height, int attr,@DrawableRes int res) {
        Drawable[] drawables = view.getCompoundDrawables();
        Drawable myImage = drawables[attr];
        if(res!=0){
            myImage=view.getResources().getDrawable(res);
        }
        if (myImage == null) {
            return;
        }
        myImage.setBounds(0, 0, width, height);
        switch (attr) {
            case 0:
                view.setCompoundDrawables(myImage, drawables[1], drawables[2],
                        drawables[3]);
                break;
            case 1:
                view.setCompoundDrawables(drawables[0], myImage, drawables[2],
                        drawables[3]);
                break;
            case 2:
                view.setCompoundDrawables(drawables[0], drawables[1], myImage,
                        drawables[3]);
                break;
            case 3:
                view.setCompoundDrawables(drawables[0], drawables[1], drawables[2],
                        myImage);
                break;

        }
    }

    public static Drawable updateDrawableSize(Drawable drawable,
                                              int width, int height) {
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height);
        }
        return drawable;
    }

    /**
     * 判断Activity是否finish
     *
     * @param mContext
     * @return
     */
    public static boolean isActivityRun(Context mContext) {
        if (mContext == null)
            return true;
        boolean isFinishing = false;
        if (mContext instanceof Activity) {
            if (Build.VERSION.SDK_INT < 17) {
                isFinishing = ((Activity) mContext).isFinishing();
            } else {
                isFinishing = ((Activity) mContext).isDestroyed();
            }
        }
        return isFinishing;
    }

    public static void restartApplication(final Context mContext) {
        if (mContext == null)
            return;
        restartApplication(mContext, true);
    }

    /**
     * 强制关闭和重启App
     *
     * @param mContext
     * @param isReStart
     */
    public static void restartApplication(final Context mContext,
                                          final boolean isReStart) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
//        Activity mainActivity = BaseApplication.getAPPInstance();
//        if (!isActivityRun(mainActivity)) {
//            mainActivity.finish();
//        }

        ThreadPoolUtil.executeCachedThread(new Runnable() {
            @Override
            public void run() {
                if (isReStart) {
                    Intent intent = new Intent(
                            mContext,
                            WelComeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    try {
                        if (mContext instanceof Activity) {
                            ((Activity) mContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                        }
                    } catch (Exception e) {
                    }
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }
}