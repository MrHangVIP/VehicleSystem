package com.lhj.vehiclesystem.util;

import android.content.Context;

import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.third.wheelview.ArrayWheelAdapter;
import com.lhj.vehiclesystem.third.wheelview.OnWheelChangedListener;
import com.lhj.vehiclesystem.third.wheelview.WheelView3;


/**
 * @discription 普通无联动轮转控件
 * @autor songzhihang
 * @time 2017/10/18  上午10:16
 **/
public class WheelUtil<T> implements OnWheelChangedListener {
    private Context context;

    private WheelView3 wheelView;
    private ArrayWheelAdapter adapter;

    private T[] items;

    public WheelUtil(Context context) {
        this.context = context;
        wheelView = new WheelView3(context);
        wheelView.setWheelForeground(R.drawable.wheel_dialog_select_bg);
        wheelView.setWheelBackground(R.drawable.wheel_dialog_bg);
        wheelView.setDrawShadows(false);
        wheelView.setVisibleItems(6);
        adapter = new ArrayWheelAdapter(context, new String[]{});
    }

    public WheelView3 getWheelView() {
        return wheelView;
    }

    public ArrayWheelAdapter setDatas(T[] datas) {
        items = datas;
        adapter = new ArrayWheelAdapter(context, datas);
        adapter.setTextColor(0xFF333333);
        wheelView.setViewAdapter(adapter);
        return adapter;
    }

    public ArrayWheelAdapter setTextSize(int size) {
        adapter.setTextSize(size);
        return adapter;
    }

    public ArrayWheelAdapter setTextColor(int color) {
        adapter.setTextSize(color);
        return adapter;
    }

    public T[] getItems() {
        return items;
    }

    @Override
    public void onChanged(WheelView3 wheel, int oldValue, int newValue) {

    }

}
