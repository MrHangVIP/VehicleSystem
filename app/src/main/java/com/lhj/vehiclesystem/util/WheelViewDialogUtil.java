package com.lhj.vehiclesystem.util;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.lhj.vehiclesystem.ui.widget.WheelViewDialog;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @discription 轮转弹框辅助
 * @autor   songzhihang
 * @time   2017/10/18  上午10:21
 **/
public class WheelViewDialogUtil {

    public static WheelViewDialog showWheelViewDialog(Activity activity, String title
            , WheelViewDialog.DialogSubmitListener listener, LinkedHashMap<View, LinearLayout.LayoutParams> map) {
        WheelViewDialog dialog = new WheelViewDialog(activity, title, listener);
        for (Map.Entry entry : map.entrySet()) {
            View view = (View) entry.getKey();
            LinearLayout.LayoutParams params = map.get(view);
            if (params != null) {
                dialog.addContentView(view, params);
            } else {
                dialog.addContentView(view);
            }
        }
        dialog.show();
        return dialog;
    }

    public static WheelViewDialog showWheelViewDialog(Activity activity, String title
            , WheelViewDialog.DialogSubmitListener listener, LinkedHashMap<View, LinearLayout.LayoutParams> map,Boolean iswhow) {
        WheelViewDialog dialog = new WheelViewDialog(activity, title, listener);
        for (Map.Entry entry : map.entrySet()) {
            View view = (View) entry.getKey();
            LinearLayout.LayoutParams params = map.get(view);
            if (params != null) {
                dialog.addContentView(view, params);
            } else {
                dialog.addContentView(view);
            }
        }
        if (iswhow){
            dialog.show();
        }
        return dialog;
    }

}
