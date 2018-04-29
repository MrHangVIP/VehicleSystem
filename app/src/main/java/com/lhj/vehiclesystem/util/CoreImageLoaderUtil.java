package com.lhj.vehiclesystem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @discription
 * @autor songzhihang
 * @time 2018/2/27  下午2:41
 **/
public class CoreImageLoaderUtil {
    /* -------------------------接口---------------------------- */

    public interface LoadingImageListener {
        /**
         * 图片加载成功
         *
         * @param t
         * @param <T>
         */
        <T> void onResourceReady(T t);

        /**
         * 加载失败
         */
        void onLoadFailed();
    }

    /**
     * ----------------------------加载图片 int---------------------------
     */
    public static void loadingImg(Context mContext, int resouse, ImageView img,
                                  Object placeholder) {
        Glide.clear(img);
        DrawableTypeRequest<Integer> mDrawableTypeRequest = Glide
                .with(mContext).load(resouse);
        if (null != placeholder) {
            if (placeholder instanceof Drawable) {
                mDrawableTypeRequest.placeholder((Drawable) placeholder)
                        .error((Drawable) placeholder);
            } else if (placeholder instanceof Integer) {
                mDrawableTypeRequest.placeholder((Integer) placeholder)
                        .error((Integer) placeholder);
            }
        }
        mDrawableTypeRequest.dontAnimate();
        mDrawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.NONE).into(img);
    }

    /**
     * ----------------------------加载图片 Bitmap---------------------------
     */
    public static void loadingImg(Context mContext, Bitmap bitmap,
                                  ImageView img, Object placeholder) {
        Glide.clear(img);
        DrawableTypeRequest<Bitmap> mDrawableTypeRequest = Glide.with(mContext)
                .load(bitmap);
        if (null != placeholder) {
            if (placeholder instanceof Drawable) {
                mDrawableTypeRequest.placeholder((Drawable) placeholder)
                        .error((Drawable) placeholder);
            } else if (placeholder instanceof Integer) {
                mDrawableTypeRequest.placeholder((Integer) placeholder)
                        .error((Integer) placeholder);
            }
        }
        mDrawableTypeRequest.dontAnimate();
        mDrawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.NONE).into(img);
    }

    /**
     * --------------------------加载图片File--------------------------------------
     */
    public static void loadingImg(Context mContext, File file, ImageView img,
                                  Object placeholder) {
        Glide.clear(img);
        DrawableTypeRequest<File> mDrawableTypeRequest = Glide.with(mContext)
                .load(file);
        if (null != placeholder) {
            if (placeholder instanceof Drawable) {
                mDrawableTypeRequest.placeholder((Drawable) placeholder)
                        .error((Drawable) placeholder);
            } else if (placeholder instanceof Integer) {
                mDrawableTypeRequest.placeholder((Integer) placeholder)
                        .error((Integer) placeholder);
            }
        }
        mDrawableTypeRequest.dontAnimate();
        mDrawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.NONE).into(img);
    }

    /**
     * --------------------------单独下载加载图片--------------------------------------
     */
    public static void loadingImg(Context mContext, String url,
                                  final LoadingImageListener listener, int width, int height) {
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                @SuppressWarnings("rawtypes") GlideAnimation anim) {
                        listener.onResourceReady(bitmap);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        Glide.clear(this);
                        listener.onLoadFailed();
                    }
                });
    }

    /**
     * --------------------------加载网络普通图片-----------------------------------
     */
    public static void loadingImgSimple(Context mContext, String url, ImageView img) {
        Glide.with(mContext).load(url).into(img);
    }

    /**
     * @param mContext
     * @param url
     * @param img
     * @param placeholder_drawable
     * @param listener
     * @param withAnim
     * @param width
     * @param height
     */
    public static void loadingImgWithCommon(final Context mContext,
                                            String url, final ImageView img, final Object placeholder_drawable,
                                            final LoadingImageListener listener, boolean withAnim, int width,
                                            int height, int imageloader_anim) {
        Glide.clear(img);
        DrawableTypeRequest<String> mDrawableTypeRequest = Glide.with(mContext)
                .load(url);
        if (withAnim) {
            mDrawableTypeRequest.animate(imageloader_anim);
        } else {
            mDrawableTypeRequest.dontAnimate();
        }
        if (width != 0 && height != 0) {
            mDrawableTypeRequest.override(width, height);
        }
        if (listener != null) {
            mDrawableTypeRequest
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception arg0, String arg1,
                                                   Target<GlideDrawable> arg2, boolean arg3) {
                            listener.onLoadFailed();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable arg0,
                                                       String arg1, Target<GlideDrawable> arg2,
                                                       boolean arg3, boolean arg4) {
                            try {
                                GlideBitmapDrawable glideBitmapDrawable = (GlideBitmapDrawable) arg0;
                                listener.onResourceReady(glideBitmapDrawable.getBitmap());
                            } catch (Exception e) {
                                Log.e("appplant", e.getMessage());
                            }
                            return false;
                        }
                    });
        } else {
            mDrawableTypeRequest
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception arg0, String arg1,
                                                   Target<GlideDrawable> arg2, boolean arg3) {
                            if (arg0 != null) {
                                Log.e("appplant", arg0.getMessage());
                            }

                            try {
                                if (null != placeholder_drawable) {
                                    if (placeholder_drawable instanceof Integer) {
                                        img.setImageResource((Integer) placeholder_drawable);
                                    } else if (placeholder_drawable instanceof Drawable) {
                                        img.setImageDrawable((Drawable) placeholder_drawable);
                                    }
                                }
                            } catch (OutOfMemoryError e) {
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable arg0,
                                                       String arg1, Target<GlideDrawable> arg2,
                                                       boolean arg3, boolean arg4) {
                            return false;
                        }
                    });
        }

        if (null != placeholder_drawable) {
            if (placeholder_drawable instanceof Drawable) {
                mDrawableTypeRequest.placeholder((Drawable) placeholder_drawable)
                        .error((Drawable) placeholder_drawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            } else if (placeholder_drawable instanceof Integer) {
                mDrawableTypeRequest.placeholder((Integer) placeholder_drawable)
                        .error((Integer) placeholder_drawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            } else {
                mDrawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            }
        } else {
            mDrawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        }
    }

    /**
     * 非wifi环境下 不显示图片
     *
     * @param mContext
     * @param url
     * @param img
     * @param placeholder
     */
    public static void loadingImgOutofWifi(Context mContext, String url,
                                           final ImageView img, Drawable placeholder, int imageloader_anim) {
        Glide.clear(img);
        Glide.with(mContext)
                .using(new StreamModelLoader<String>() {
                    @Override
                    public DataFetcher<InputStream> getResourceFetcher(
                            final String model, int i, int i1) {
                        return new DataFetcher<InputStream>() {
                            @Override
                            public InputStream loadData(Priority priority)
                                    throws Exception {
                                throw new IOException();
                            }

                            @Override
                            public void cleanup() {

                            }

                            @Override
                            public String getId() {
                                return model;
                            }

                            @Override
                            public void cancel() {

                            }
                        };
                    }
                }).load(url).animate(imageloader_anim)
                .placeholder(placeholder).error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
    }

    /**
     * listview 加载圆形图片
     *
     * @param mContext
     * @param url
     * @param imageView 直接ImageView
     * @param radius    半径，如果是圆形，半径即为宽高的一般，只需要圆角，自定义
     */
    public static void loadingCircleImage(final Context mContext, String url,
                                          final ImageView imageView, final int radius,
                                          final Drawable placeholder, final LoadingImageListener listener) {
        Glide.clear(imageView);
        imageView.setImageDrawable(placeholder);
        Glide.with(mContext).load(url).asBitmap().placeholder(placeholder).centerCrop()
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                .create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(radius);
                        imageView.setImageDrawable(circularBitmapDrawable);
                        if (listener != null) {
                            listener.onResourceReady(circularBitmapDrawable);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        imageView.setImageDrawable(placeholder);
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                    }

                });
    }

    /**
     * 清除图片缓存
     *
     * @param mContext
     */
    public static void clearMemory(final Context mContext) {
        Glide.get(mContext).clearMemory();
    }

    public static void clearDiskCache(final Context mContext) {
        Glide.get(mContext).clearDiskCache();
    }

}
