package com.lhj.vehiclesystem.util;

import android.content.Context;


import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.third.wheelview.NumericWheelViewAdapter;
import com.lhj.vehiclesystem.third.wheelview.OnWheelChangedListener;
import com.lhj.vehiclesystem.third.wheelview.WheelView3;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * 时间的设置 通过穿过来的时间
 *
 * @author hudebo
 */
public class WheelDateUtil implements OnWheelChangedListener {
    private WheelView3 wv_year;
    private WheelView3 wv_month;
    private WheelView3 wv_day;
    private Context context;
    public int textSize;
    public static final int DEFULT_START_YEAR = 1930;
    public static final int DEFULT_END_YEAR = 2100;
    private int startYear = DEFULT_START_YEAR;
    private int endYear = DEFULT_END_YEAR;
    public static DateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    private NumericWheelViewAdapter monthAdapter;
    private NumericWheelViewAdapter dayAdapter;
    private NumericWheelViewAdapter yearAdapter;

    // 添加大小月月份并将其转换为list,方便之后的判断
    String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
    String[] months_little = {"4", "6", "9", "11"};

    public WheelDateUtil(Context context) {
        this.context = context;
        wv_year = new WheelView3(context);
        wv_month = new WheelView3(context);
        wv_day = new WheelView3(context);
        wv_year.setWheelForeground(R.drawable.wheel_dialog_select_bg);
        wv_year.setWheelBackground(R.drawable.wheel_dialog_bg);
        wv_year.setDrawShadows(false);
        wv_month.setWheelForeground(R.drawable.wheel_dialog_select_bg);
        wv_month.setWheelBackground(R.drawable.wheel_dialog_bg);
        wv_month.setDrawShadows(false);
        wv_day.setWheelForeground(R.drawable.wheel_dialog_select_bg);
        wv_day.setWheelBackground(R.drawable.wheel_dialog_bg);
        wv_day.setDrawShadows(false);
    }

    /**
     * @Description: TODO 弹出日期时间选择器
     */
    public void setPicker(int year, int month, int day) {

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        // 年
        yearAdapter = new NumericWheelViewAdapter(context, startYear, endYear);
        wv_year.setViewAdapter(yearAdapter);// 设置"年"的显示数据
        yearAdapter.setLabel("年");// 添加文字
        yearAdapter.setTextSize(textSize);
        wv_year.setVisibleItems(5);
        wv_year.setCurrentItem(year-startYear);// 初始化时显示的数据

        // 月
        monthAdapter = new NumericWheelViewAdapter(context, 1, 12);
        monthAdapter.setLabel("月");
        monthAdapter.setTextSize(textSize);
        wv_month.setViewAdapter(monthAdapter);
        wv_month.setVisibleItems(5);
        wv_month.setCurrentItem(month);
        // 日
        // 判断大小月及是否闰年,用来确定"日"的数据
        dayAdapter = new NumericWheelViewAdapter(context);
        if (list_big.contains(String.valueOf(month + 1))) {
            dayAdapter.updateNumbers(1, 31);
        } else if (list_little.contains(String.valueOf(month + 1))) {
            dayAdapter.updateNumbers(1, 30);
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                dayAdapter.updateNumbers(1, 29);
            else
                dayAdapter.updateNumbers(1, 28);
        }
        dayAdapter.setLabel("日");
        dayAdapter.setTextSize(textSize);
        wv_day.setViewAdapter(dayAdapter);
        wv_day.setVisibleItems(5);
        wv_day.setCurrentItem(day - 1);
        wv_year.addChangingListener(this);
        wv_month.addChangingListener(this);
    }

    /**
     * 带时间最大最小值
     * @param year
     * @param month
     * @param day
     * @param startYear
     * @param endYear
     */
    public void setPicker(int year, int month, int day, int startYear, int endYear) {
        this.startYear=startYear;
        this.endYear=endYear;
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        // 年
        yearAdapter = new NumericWheelViewAdapter(context, startYear, endYear);
        wv_year.setViewAdapter(yearAdapter);// 设置"年"的显示数据
        yearAdapter.setLabel("年");// 添加文字
        yearAdapter.setTextSize(textSize);
        wv_year.setVisibleItems(5);
        wv_year.setCurrentItem(year);// 初始化时显示的数据

        // 月
        monthAdapter = new NumericWheelViewAdapter(context, 1, 12);
        monthAdapter.setLabel("月");
        monthAdapter.setTextSize(textSize);
        wv_month.setViewAdapter(monthAdapter);
        wv_month.setVisibleItems(5);
        wv_month.setCurrentItem(month);
        // 日
        // 判断大小月及是否闰年,用来确定"日"的数据
        dayAdapter = new NumericWheelViewAdapter(context);
        if (list_big.contains(String.valueOf(month + 1))) {
            dayAdapter.updateNumbers(1, 31);
        } else if (list_little.contains(String.valueOf(month + 1))) {
            dayAdapter.updateNumbers(1, 30);
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                dayAdapter.updateNumbers(1, 29);
            else
                dayAdapter.updateNumbers(1, 28);
        }
        dayAdapter.setLabel("日");
        dayAdapter.setTextSize(textSize);
        wv_day.setViewAdapter(dayAdapter);
        wv_day.setVisibleItems(5);
        wv_day.setCurrentItem(day - 1);
        wv_year.addChangingListener(this);
        wv_month.addChangingListener(this);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_day.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_year.setCyclic(cyclic);
    }

    public String getYear() {
        return String.valueOf(yearAdapter.getNumItems()[wv_year.getCurrentItem()]);
    }

    public String getMonth() {
        return String.valueOf(monthAdapter.getNumItems()[wv_month.getCurrentItem()]);
    }

    public String getDay() {
        return String.valueOf(dayAdapter.getNumItems()[wv_day.getCurrentItem()]);
    }

    public WheelView3 getWv_year() {
        return wv_year;
    }

    public WheelView3 getWv_month() {
        return wv_month;
    }

    public WheelView3 getWv_day() {
        return wv_day;
    }


    @Override
    public void onChanged(WheelView3 wheel, int oldValue, int newValue) {
        if (wheel == wv_year || wheel == wv_month) {
            int year_num = wv_year.getCurrentItem() + startYear;
            // 判断大小月及是否闰年,用来确定"日"的数据
            int maxItem = 30;
            if (Arrays.asList(months_big).contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                dayAdapter.updateNumbers(1, 31);
                maxItem = 31;
            } else if (Arrays.asList(months_little).contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                dayAdapter.updateNumbers(1, 30);
                maxItem = 30;
            } else {
                if ((year_num % 4 == 0 && year_num % 100 != 0)
                        || year_num % 400 == 0) {
                    dayAdapter.updateNumbers(1, 29);
                    maxItem = 29;
                } else {
                    dayAdapter.updateNumbers(1, 28);
                    maxItem = 28;
                }
            }
            if (wv_day.getCurrentItem() > maxItem - 1) {
                wv_day.setCurrentItem(maxItem - 1);
            }
        }
    }
}
