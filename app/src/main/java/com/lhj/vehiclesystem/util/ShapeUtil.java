package com.lhj.vehiclesystem.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 工厂圆角背景产生类
 */
public class ShapeUtil {
    /**
     * 选中后变色
     * @param uncheck
     * @param checked
     * @return
     */
    public static ColorStateList getTabTextColor(int uncheck, int checked) {
        int[][] states = {{-android.R.attr.state_checked},
                {android.R.attr.state_checked}};
        int[] color = {uncheck, checked};
        return new ColorStateList(states, color);
    }

    /**
     * 带圆角的纯色背景
     * @param corner 圆角
     * @param back 背景色
     * @return
     */
    public static Drawable getDrawable(int corner,int back){
        return getRoundDrawable(back,corner,corner,corner,corner);
    }
    /**
     * 带圆角的纯色背景
     * @param back 背景色
     * @return
     */
    public static Drawable getRoundDrawable(int back, int topLeft, int bottomLeft, int topRight, int bottomRight ){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(back);
        //左上，右上，右下，左下
        drawable.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        return drawable;
    }
    /**
     * 带圆角+边框
     * @param corner  圆角
     * @param bgColor  背景色
     * @param strokeWidth  边框宽度
     * @param strokebg 边框色
     * @return
     */
    public static Drawable getDrawable(int corner,int bgColor,int strokeWidth, int strokebg){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(bgColor);
        //左上，右上，右下，左下
        drawable.setCornerRadii(new float[]{corner,corner,corner,corner,corner,corner,corner,corner});
        drawable.setStroke(strokeWidth, strokebg);
        return drawable;
    }

    /**
     * 带圆角+选中变色（默认透明，选中颜色自定义）
     * @param navbarTitleColor
     * @param topLeft
     * @param bottomLeft
     * @param topRight
     * @param bottomRight
     * @return
     */
    //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
    public static Drawable getDrawable(int navbarTitleColor, int topLeft, int bottomLeft, int topRight, int bottomRight) {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal_drawable = new GradientDrawable();
        normal_drawable.setColor(0x00000000);
        normal_drawable.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        normal_drawable.setStroke(MyUtil.toDip(1), navbarTitleColor);

        GradientDrawable checked_drawable = new GradientDrawable();
        checked_drawable.setColor(navbarTitleColor);
        checked_drawable.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        //	checked_drawable.setStroke(Util.toDip(1), navbarBackground);

        bg.addState(new int[]{android.R.attr.state_checked}, checked_drawable);
        bg.addState(new int[]{}, normal_drawable);
        return bg;
    }

    /**
     * 带圆角+选中变色(自定义变色)
     * @param navbarTitleColor
     * @param backColor
     * @param topLeft
     * @param bottomLeft
     * @param topRight
     * @param bottomRight
     * @return
     */
    //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
    public static Drawable getDrawable(int navbarTitleColor,int backColor,int topLeft,int bottomLeft,int topRight,int bottomRight){
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal_drawable = new GradientDrawable();
        normal_drawable.setColor(backColor);
        normal_drawable.setCornerRadii(new float[]{topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft});
        normal_drawable.setStroke(MyUtil.toDip(1), navbarTitleColor);

        GradientDrawable checked_drawable = new GradientDrawable();
        checked_drawable.setColor(navbarTitleColor);
        checked_drawable.setCornerRadii(new float[]{topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft});
        //	checked_drawable.setStroke(Util.toDip(1), navbarBackground);

        bg.addState(new int[] { android.R.attr.state_checked }, checked_drawable);
        bg.addState(new int[] {}, normal_drawable);
        return bg;
    }



    //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
    public static Drawable getDrawable(int navbarTitleColor,int stroke, int corner ) {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal_drawable = new GradientDrawable();
        normal_drawable.setColor(0x00000000);
        normal_drawable.setCornerRadii(new float[]{corner, corner, corner, corner, corner, corner, corner, corner});
        normal_drawable.setStroke(stroke, navbarTitleColor);
        bg.addState(new int[]{}, normal_drawable);
        return bg;
    }

    public static Drawable getShapeBg(int bgcolor, int corner) {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal_drawable = new GradientDrawable();
        normal_drawable.setColor(bgcolor);
        normal_drawable.setCornerRadii(new float[]{corner, corner, corner, corner, corner, corner, corner, corner});
        bg.addState(new int[]{}, normal_drawable);
        return bg;
    }

    public static Drawable getShapeBg(int bgcolor, int l,int t,int r,int b) {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal_drawable = new GradientDrawable();
        normal_drawable.setColor(bgcolor);
        normal_drawable.setCornerRadii(new float[]{l, l, t, t, r, r, b, b});
        bg.addState(new int[]{}, normal_drawable);
        return bg;
    }


}
