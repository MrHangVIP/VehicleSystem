package com.lhj.vehiclesystem.ui.widget;

import android.content.Context;
import android.graphics.Typeface;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


/**
 * Created by Songzhihang on 2018/2/25.
 * 栏目栏文本样式带加粗效果
 */
public class MyColorTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 1f;
    private float maxScale = 1.25f;
    private float mNormalAlpha = 0.9f;

    public MyColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        setScaleX(maxScale + (mMinScale - maxScale) * leavePercent);//1.25-1
        setScaleY(maxScale + (mMinScale - maxScale) * leavePercent);
        if (leavePercent >= 1.0) {
            setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        setScaleX(mMinScale + (maxScale - mMinScale) * enterPercent);  //1-1.25
        setScaleY(mMinScale + (maxScale - mMinScale) * enterPercent);
        if (enterPercent >= 1.0) {
            setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public void onSelected(int index, int totalCount) {
        setAlpha(1f);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (mNormalAlpha != 0) {
            setAlpha(mNormalAlpha);
        }
    }
}
