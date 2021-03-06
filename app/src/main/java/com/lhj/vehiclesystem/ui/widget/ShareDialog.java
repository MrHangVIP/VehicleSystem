/******************************************************************************
 * @project kanfangbao2
 * @brief
 * @author fmh19
 * @module com.yipai.realestate.ui.dialog
 * @date 2016/5/19
 * @version 0.1
 * @history v0.1, 2016/5/19, by fmh19
 * <p/>
 * Copyright (C) 2016 Yipai.
 ******************************************************************************/
package com.lhj.vehiclesystem.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.bean.ShareItem;
import com.lhj.vehiclesystem.model.ShareModel;


public class ShareDialog extends Dialog implements View.OnClickListener, ShareItem.OnShareClickListener {

    private String TAG = ShareDialog.class.getSimpleName();
    BaseActivity mActivity;
    private Context mContext;
    ShareItem item1, item2, item3, item4, item5;
    Button cancelView;
    private String shareContent = "掌上问卷";
    private UMImage shareImage;
    private BitmapDrawable bitmap = (BitmapDrawable) BaseApplication.getAPPInstance().getResources().getDrawable(R.drawable.app_logo);

    public ShareDialog(Activity activity) {
        super(activity, R.style.TimeDialog);
        this.mActivity = (BaseActivity) activity;
        mContext = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_share, null);
        item1 = (ShareItem) view.findViewById(R.id.item_1);
        item2 = (ShareItem) view.findViewById(R.id.item_2);
        item3 = (ShareItem) view.findViewById(R.id.item_3);
        item4 = (ShareItem) view.findViewById(R.id.item_4);
        item5 = (ShareItem) view.findViewById(R.id.item_5);
        setItems();
        shareImage = new UMImage(mContext, bitmap.getBitmap());
        cancelView = (Button) view.findViewById(R.id.btn_cancel);
        cancelView.setOnClickListener(this);

        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.AnimButton);
        setContentView(view);

    }

    public BitmapDrawable getBitmap() {
        return bitmap;
    }

    public ShareDialog setBitmap(BitmapDrawable bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    private void setItems() {
        item1.setType(ShareItem.JumpTO.WEIXIN);
        item2.setType(ShareItem.JumpTO.WE_FREND);
        item3.setType(ShareItem.JumpTO.QQ_ZONE);
        item4.setType(ShareItem.JumpTO.QQ);
        item5.setType(ShareItem.JumpTO.WEIBO);

        item1.setOnShareListener(this);
        item2.setOnShareListener(this);
        item3.setOnShareListener(this);
        item4.setOnShareListener(this);
        item5.setOnShareListener(this);
    }

    @Override
    public void onClick(View view) {
        ShareModel shareModel = new ShareModel();
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onShareClick(View view, ShareItem.JumpTO type) {

        switch (view.getId()) {
            case R.id.item_1:
//				mActivity.toast("微信");
                new ShareAction((BaseActivity) mContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withText(shareContent)
                        .withMedia(shareImage)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.item_2:
//				mActivity.toast("朋友圈");
                new ShareAction((BaseActivity) mContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withText(shareContent)
                        .withMedia(shareImage)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.item_3:
//				mActivity.toast("空间");
                new ShareAction((BaseActivity) mContext)
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .withText(shareContent)
                        .withMedia(shareImage)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.item_4:
//				mActivity.toast("QQ");
                //目前不支持纯文本，应该是友盟的bug
                new ShareAction((BaseActivity) mContext)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .withText(shareContent)
                        .withMedia(shareImage)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.item_5:
//				mActivity.toast("微博");
                new ShareAction((BaseActivity) mContext)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .withText(shareContent)
                        .withMedia(shareImage)
                        .setCallback(umShareListener)
                        .share();
                break;

        }
        dismiss();
    }

    public ShareDialog setShareContent(String shareContent) {
        this.shareContent = shareContent;
        return this;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(mContext, getPlatform(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, getPlatform(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, getPlatform(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private String getPlatform(SHARE_MEDIA media) {
        String platformStr = "";
        switch (media) {
            case WEIXIN:
                platformStr = "微信";
                break;

            case WEIXIN_CIRCLE:
                platformStr = "朋友圈";
                break;

            case QQ:
                platformStr = "QQ";
                break;

            case QZONE:
                platformStr = "QQ空间";
                break;

            case SINA:
                platformStr = "微博";
                break;
        }
        return platformStr;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap != null && bitmap.getBitmap() != null) {
            bitmap.getBitmap().recycle();
            bitmap = null;
        }
    }
}
