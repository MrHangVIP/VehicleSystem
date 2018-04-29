package com.lhj.vehiclesystem.ui.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lhj.vehiclesystem.util.MyUtil;

/**
 * 说明：网格布局的recycleview间距
 * 作者：wangya
 * 时间：2017/2/22
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    /**
     * @param space 传入的值，其单位视为dp
     */
    public SpaceItemDecoration(int space) {
        this.mSpace = MyUtil.toDip(space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = mSpace / 2;
        outRect.right = mSpace / 2;
        outRect.bottom = mSpace;
    }
}
