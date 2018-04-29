package com.lhj.vehiclesystem.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhj.vehiclesystem.R;


/**
 * @discription 轮转dialog
 * @autor songzhihang
 * @time 2017/7/6  下午2:28
 **/
public class WheelViewDialog extends Dialog {
    private static final String TAG = "WheelViewDialog";
    private LinearLayout wdgl_ll_content_layout;
    private Activity activity;
    private String title;
    private TextView wdgl_tv_title;
    private DialogSubmitListener listener;

    public WheelViewDialog(Activity activity, String title, DialogSubmitListener listener) {
        super(activity, R.style.TimeDialog);
        this.activity = activity;
        this.title = title;
        this.listener = listener;
        init(activity);
    }

    void init(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.wheel_dialog_game_layout, null);
        wdgl_ll_content_layout = (LinearLayout) view.findViewById(R.id.wdgl_ll_content_layout);
        wdgl_tv_title = (TextView) view.findViewById(R.id.wdgl_tv_title);
        view.findViewById(R.id.wdgl_tv_confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSubmitClick(v);
                }
            }
        });
        wdgl_tv_title.setText(title);
        setContentView(view);
        Window win = getWindow();
//        win.getDecorView().setPadding(Util.toDip(10), Util.toDip(10), Util.toDip(10), Util.toDip(10));
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = Util.toDip(300);
//        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.AnimButton);
    }

    public TextView setTitle(String title) {
        this.title = title;
        wdgl_tv_title.setText(title);
        return wdgl_tv_title;
    }

    /**
     * 添加view 可多次添加,顺序从左往右
     *
     * @param view
     * @return
     */
    public WheelViewDialog addContentView(View view) {
        if (view == null) {
            return this;
        }
        wdgl_ll_content_layout.addView(view);
        return this;
    }

    /**
     * 添加view 可多次添加,顺序从左往右
     *
     * @param view
     * @return
     */
    public WheelViewDialog addContentView(View view, LinearLayout.LayoutParams params) {
        if (view == null || params == null) {
            return this;
        }
        wdgl_ll_content_layout.addView(view, params);
        return this;
    }


    public interface DialogSubmitListener {
        void onSubmitClick(View v);
    }

}
