package com.lhj.vehiclesystem.ui.widget.refresh;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.bumptech.glide.Glide;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.MyUtil;

/**
 * 描述：
 * 作者： wangya
 * 时间： 16/9/12 20:27
 */
public class XRefreshViewExtendHeader extends LinearLayout implements IHeaderCallBack {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    //
    private int mState = STATE_NORMAL;
    private Context mContext;
    private ImageView mADImageView;
    private LinearLayout mContainer;
    private RelativeLayout mHeaderViewContent;
    private ImageView mArrowImageView;
    //
    private float group;
    private boolean inAnim = false;//正在进行旋转动画
    private boolean inDownAnim = false;//是否正在执行
    private float totalHeight;

    public XRefreshViewExtendHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XRefreshViewExtendHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        mContainer = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.mxu_xlistview_header, this);
        setGravity(Gravity.BOTTOM);

        mADImageView = (ImageView) findViewById(R.id.xlistview_header_adimage);
        mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
        mHeaderViewContent = (RelativeLayout) findViewById(R.id.xlistview_header_content);
        Glide.with(mContext).load(R.raw.mxu_header_sun).asGif().into((ImageView) mArrowImageView);
        //设置半径
        group = 0.5f * Constant.getScreenWidth(mContext);
        initADImage();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = mHeaderViewContent.getMeasuredHeight();
    }

    private void initADImage() {//广告头
//        if (Variable.HAS_REFRESH_AD) {
//            MyUtil.setVisibility(mADImageView, View.VISIBLE);
//            LayoutParams adimgpar = (LayoutParams) mADImageView
//                    .getLayoutParams();
//            adimgpar.height = (int) (Variable.WIDTH * 0.3);
//            mADImageView.setLayoutParams(adimgpar);
//            if (!TextUtils.isEmpty(Variable.PULL_DOWN_AD)) {
//                ImageLoaderUtil.loadingImg(mContext, Variable.PULL_DOWN_AD,
//                        mADImageView, Variable.WIDTH,
//                        (int) (Variable.WIDTH * 0.3));
//            }
//        }
    }

    public void setRefreshTime(long lastRefreshTime) {
    }

    /**
     * hide footer when disable pull refresh
     */
    public void hide() {
        setVisibility(View.GONE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateNormal() {
        mState = STATE_NORMAL;
    }

    @Override
    public void onStateReady() {
        mState = STATE_READY;
    }

    @Override
    public void onStateRefreshing() {
        mState = STATE_REFRESHING;
        //        resetHeight();
    }

    /**
     * 初始化View高度
     */
    private void resetHeight() {
        float height = (int) (80 * Constant.Density);
//        if (Variable.HAS_REFRESH_AD) {
//            height = height + (int) (Variable.WIDTH * 0.3);
//        }
        if (getMeasuredHeight() < height) {
            LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
            lp.height = (int) height;
            mContainer.setLayoutParams(lp);
            invalidate();
        }
    }

    @Override
    public void onStateFinish(final boolean success) {
        if (getMeasuredHeight() < 5) {
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAnimation(totalHeight);
            }
        }, 900);
    }

//    private void startAnim() {
//        if (inAnim) {
//            return;
//        }
//        inAnim = true;
//    }
//
//    private void stopAnim() {
//        inAnim = false;
//    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY, int deltaY) {
        if (offsetY < 0)
            offsetY = 0;
//        if (offsetY == 0) {
//            stopAnim();
//        } else {
//            startAnim();
//        }
        if (offsetY > totalHeight) {
            offsetY = (int) totalHeight;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = offsetY;
        mContainer.setLayoutParams(lp);
        if (totalHeight > group) {
            group = totalHeight;
        }
        switch (mState) {
            case STATE_REFRESHING:
                mArrowImageView.setX(Constant.getScreenWidth(mContext) / 2 - MyUtil.toDip(10));
                mArrowImageView.setY(MyUtil.toDip(10));
                break;
            case STATE_READY:
            case STATE_NORMAL:
            default:
                float y_pos = totalHeight - offsetY + MyUtil.toDip(10);
                mArrowImageView.setY(y_pos < 0 ? totalHeight : y_pos);
                float groupY = group - totalHeight + offsetY;
                double x = Constant.getScreenWidth(mContext) / 2 / 2 - Math.sqrt((group * group) - (groupY * groupY));
                mArrowImageView.setX((float) x - MyUtil.toDip(10));
                break;
        }
    }

    /**
     * 结束动画
     */
    public void finishAnimation(float height) {
        if (inDownAnim) {
            return;
        }
        totalHeight = height;
        if (height > group) {
            group = height;
        }
        inDownAnim = true;
        int count = (int) totalHeight + MyUtil.toDip(10);
        Keyframe[] keyframes = new Keyframe[count];
        final float keyStep = 1f / (float) count;
        float key = keyStep;
        for (int i = 0; i < count; i++) {
            keyframes[i] = Keyframe.ofFloat(key, i + 1 + MyUtil.toDip(10));
            key += keyStep;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("Y", keyframes);
        key = keyStep;
        for (int i = 0; i < count; i++) {
            keyframes[i] = Keyframe.ofFloat(key, getX(i + 1));
            key += keyStep;
        }
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("X", keyframes);
        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(mArrowImageView, pvhY, pvhX).setDuration(800);
        yxBouncer.setInterpolator(new BounceInterpolator());
        yxBouncer.start();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                //复位
                inDownAnim = false;
            }
        }, 1500);
    }

    /**
     * 在结束时  根据垂直方向的位移计算出 水平方向的 x
     *
     * @param totalPercent
     * @return
     */
    private float getX(float totalPercent) {
        float groupY = group - totalPercent;
        double x = Constant.WIDTH / 2 + Math.sqrt((group * group) - (groupY * groupY));
        return (float) x;
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
