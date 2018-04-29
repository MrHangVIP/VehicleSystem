package com.lhj.vehiclesystem.ui.widget.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.widget.roundedimageview.RoundedImageView;
import com.lhj.vehiclesystem.util.ImageLoaderUtil;
import com.lhj.vehiclesystem.util.MyUtil;

/**
 * RecyclerView 的 ViewHolder
 * 待完善
 *
 * @author sunleilei
 * @date 2016/12/22 下午7:11.
 */

public class RVBaseViewHolder extends RecyclerView.ViewHolder {
    public View bottom_line;
    public View top_line;
    public View itemView;
    public SparseArray<View> mView;

    public RVBaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        mView = new SparseArray<>();
    }

    public <T extends View> T retrieveView(int id) {
        View view = mView.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mView.put(id, view);
        }
        return (T) view;
    }

    public RVBaseViewHolder setTextView(int id, String text) {
        TextView textView = retrieveView(id);
        if (null != textView) {
            textView.setText(text);
        }
        return this;
    }

    public RVBaseViewHolder setTextView(int id, String text, int color) {
        TextView textView = retrieveView(id);
        textView.setText(text);
        textView.setTextColor(color);
        return this;
    }

    public RVBaseViewHolder setTexColor(int id, int color) {
        TextView textView = retrieveView(id);
        if (null != textView) {
            textView.setTextColor(color);
        }
        return this;
    }

    /**
     * @param id
     * @param url
     * @param width
     * @param height
     * @return 默认图为 loading_50
     */
    public RVBaseViewHolder setImageView(int id, String url, int width, int height) {
        return setImageView(id, url, width, height, R.drawable.app_logo);
    }

    /**
     * @param id
     * @param url
     * @param width
     * @param height
     * @param default_loading_pic
     * @return
     */
    public RVBaseViewHolder setImageView(int id, String url, int width, int height, int default_loading_pic) {
        ImageView imageView = retrieveView(id);
        if (null != imageView && null != itemView) {
            ImageLoaderUtil.loadingImg(itemView.getContext(), url, imageView, default_loading_pic,
                    width, height);
        }
        return this;
    }

    /**
     * @param id          资源id
     * @param url         图片地址
     * @param urlWidth    图片原始宽
     * @param urlHeight   图片原始高度
     * @param showWidth   需要展示的宽度
     * @param showHeight  需要展示的高度
     * @param default_pic 默认图   传 0 则为全局默认图ImageLoaderUtil.loading_400
     * @return
     */
    public RVBaseViewHolder setImageView(int id, String url, int urlWidth,
                                         int urlHeight, int showWidth, int showHeight, int default_pic) {
//        if (itemView == null) {
//            return this;
//        }
//        ImageView imageView = retrieveView(id);
//        if (null == imageView) {
//            return this;
//        }
//        if (0 == default_pic) {
//            default_pic = ImageLoaderUtil.loading_400;
//        }
//        if (TextUtils.isEmpty(url) || MyUtil.isActivityRun(itemView.getContext())) {
//            imageView.setImageResource(default_pic);
//            return this;
//        }
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put(ImageLoaderUtil.IMAGE_URL, url);
//        map.put(ImageLoaderUtil.PLEASE_HOLDER, default_pic);
//        map.put(ImageLoaderUtil.ORIGINAL_WIDTH_HEIGHT, (urlWidth > 0 ? urlWidth : 0) + ","
//                + (urlHeight > 0 ? urlHeight : 0));
//        map.put(ImageLoaderUtil.WIDTH_HEIGHT, showWidth + "," + showHeight);
//        ImageLoaderUtil.loadingImg(itemView.getContext(), imageView, map, null);
        return this;
    }

    /**
     * RoundImageView
     *
     * @param id
     * @param url
     * @param width
     * @param height
     * @return
     */
    public RVBaseViewHolder setRoundImageView(int id, String url, int width, int height) {
        RoundedImageView imageView = retrieveView(id);
        if (null != imageView && null != itemView) {
            ImageLoaderUtil.loadingImg(itemView.getContext(), url, imageView,
                    width, height);
        }
        return this;
    }

    /**
     * 设置ImageResource
     *
     * @param id
     * @param resource
     * @return
     */
    public RVBaseViewHolder setImageResource(int id, int resource) {
        ImageView imageView = retrieveView(id);
        if (null != imageView && null != itemView) {
            imageView.setImageResource(resource);
        }
        return this;
    }

    /**
     * 设置布局的显示状态
     *
     * @param id       R.id.
     * @param visiable View.VISIBLE | View.GONE | View.INVISIBLE
     * @return
     */
    public RVBaseViewHolder setVisibility(int id, int visiable) {
        View view = retrieveView(id);
        MyUtil.setVisibility(view, visiable);
        return this;
    }

    public RVBaseViewHolder setVisibiity(int id, boolean visiable) {
        View view = retrieveView(id);
        if (view != null)
            view.setVisibility(visiable ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置布局的显示状态
     *
     * @param id   R.id.
     * @param show true  |  false
     * @return
     */
    public RVBaseViewHolder setVisibility(int id, boolean show) {
        View view = retrieveView(id);
        MyUtil.setVisibility(view, show ? View.VISIBLE : View.GONE);
        return this;
    }

    public RVBaseViewHolder setVisibility(boolean visiable, Integer... ids) {
        for (int i = 0; i < ids.length; i++) {
            View view = retrieveView(ids[i]);
            if (view != null) {
                view.setVisibility(visiable ? View.VISIBLE : View.GONE);
            }
        }
        return this;
    }

    /**
     * checkBox 选中状态
     *
     * @param id
     * @param check
     * @return
     */
    public RVBaseViewHolder checkBox(int id, boolean check) {
        CheckBox checkBox = retrieveView(id);
        if (null != checkBox) {
            checkBox.setChecked(check);
        }
        return this;
    }

    /**
     * 设置监听
     *
     * @param id
     * @param onClickEffectiveListener
     * @return
     */
    public RVBaseViewHolder setOnClickListener(int id, View.OnClickListener onClickEffectiveListener) {
        View view = retrieveView(id);
        if (null != view) {
            view.setOnClickListener(onClickEffectiveListener);
        }
        return this;
    }

}
