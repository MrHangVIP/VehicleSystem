package com.lhj.vehiclesystem.ui.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.callback.IFooterCallBack;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ReflectResourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * 作者： wangya
 * 时间： 16/9/14 11:35
 */
public class XRefreshViewExtendFooter extends LinearLayout implements IFooterCallBack {
    public static Integer[] loadingpics;
    private Context mContext;
    private View mContentView;
    private ImageView mProgressBar;
    private TextView mHintView;
    //    private TextView mClickView;
    private Animation mRotateAnim;
    private boolean showing = true;

    public XRefreshViewExtendFooter(Context context) {
        super(context);
        initView(context);
    }

    public XRefreshViewExtendFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        if (loadingpics == null) {
            List<Integer> list = getLoadingPicPath(mContext, "refresh");
            loadingpics = list.toArray(new Integer[list.size()]);
        }
        ViewGroup moreView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.xrefreshview_custom_footer, this);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, MyUtil.toDip(50)));
        mContentView = moreView.findViewById(R.id.xrefreshview_footer_content);
        mProgressBar = (ImageView) moreView
                .findViewById(R.id.xrefreshview_footer_progressbar);
        mHintView = (TextView) moreView
                .findViewById(R.id.xrefreshview_footer_hint_textview);
        //        mClickView = (TextView) moreView
        //                .findViewById(R.id.xrefreshview_footer_click_textview);
        mProgressBar.setImageResource(loadingpics[loadingpics.length - 1]);

        mRotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setDuration(750);
        mRotateAnim.setFillAfter(false);
        mRotateAnim.setRepeatCount(-1);
    }

    @Override
    public void callWhenNotAutoLoadMore(final XRefreshView xRefreshView) {
        //        mClickView.setText(R.string.xrefreshview_footer_hint_click);
        //        mClickView.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                xRefreshView.notifyLoadMore();
        //            }
        //        });
    }

    @Override
    public void onStateReady() {
        //        LogUtil.d("onStateReady");
        //        mProgressBar.clearAnimation();
        //        mProgressBar.setVisibility(View.GONE);
        //        mHintView.setVisibility(View.GONE);
        //        mClickView.setText(R.string.xrefreshview_footer_hint_ready);
        //        mClickView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateRefreshing() {
        mProgressBar.clearAnimation();
        mProgressBar.startAnimation(mRotateAnim);
        mProgressBar.setVisibility(View.VISIBLE);

        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.xrefreshview_footer_hint_ready);
        //        mClickView.setVisibility(View.GONE);
        show(true);
    }

    @Override
    public void onStateFinish(boolean hideFooter) {
        if (hideFooter) {
            mHintView.setText(R.string.xrefreshview_footer_hint_normal);
        } else {
            //处理数据加载失败时ui显示的逻辑，也可以不处理，看自己的需求
            mHintView.setText(R.string.xrefreshview_footer_hint_fail);
        }
        mProgressBar.clearAnimation();
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        //        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void onReleaseToLoadMore() {
        //        LogUtil.d("onReleaseToLoadMore");
        //        mHintView.setVisibility(View.GONE);
        //        mProgressBar.clearAnimation();
        //        mProgressBar.setVisibility(View.GONE);
        //        mClickView.setVisibility(View.VISIBLE);
        //        mClickView.setText(R.string.xrefreshview_footer_hint_release);
    }


    @Override
    public void onStateComplete() {
        //        LogUtil.d("onStateComplete");
        //        mHintView.setText(R.string.xrefreshview_footer_hint_complete);
        //        mHintView.setVisibility(View.VISIBLE);
        //        mProgressBar.setVisibility(View.GONE);
        //        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void show(final boolean show) {
        MyUtil.MyLogD("show：" + show);
        if (show == showing) {
            return;
        }
        showing = show;
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        lp.height = show ? LayoutParams.WRAP_CONTENT : 0;
        mContentView.setLayoutParams(lp);
    }

    @Override
    public boolean isShowing() {
        return showing;
    }


    @Override
    public int getFooterHeight() {
        return getMeasuredHeight();
    }

    public static List<Integer> getLoadingPicPath(Context context,
                                                  String firstname) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; ; i++) {
            Integer drawableId = 0;
            drawableId = ReflectResourceUtil.getDrawableId(context, firstname + (i < 10 ? "0" : "")
                    + i);
            if (drawableId != 0) {
                list.add(drawableId);
            } else {
                break;
            }
        }
        return list;
    }
}
