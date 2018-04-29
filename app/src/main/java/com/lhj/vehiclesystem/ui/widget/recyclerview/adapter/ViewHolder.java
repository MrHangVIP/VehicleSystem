package com.lhj.vehiclesystem.ui.widget.recyclerview.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * @author sunleilei
 * @date 2016/12/23 下午3:48.
 */

public class ViewHolder {
    public static <T extends View> T getView(View contentView,int id) {
        SparseArray<View> mView = (SparseArray<View>) contentView.getTag();
        if (mView == null) {
            mView = new SparseArray<>();
            contentView.setTag(mView);
        }
        View view = mView.get(id);
        if (view == null) {
            view = contentView.findViewById(id);
            mView.put(id,view);
        }
        return (T) view;
    }
}
