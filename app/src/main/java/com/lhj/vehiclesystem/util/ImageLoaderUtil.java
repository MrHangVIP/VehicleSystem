package com.lhj.vehiclesystem.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.ui.widget.CircleImageView;
import com.lhj.vehiclesystem.ui.widget.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.HashMap;

/**
 * @discription
 * @autor songzhihang
 * @time 2018/2/27  上午11:35
 **/
//public class ImageLoaderUtil {
//
//    public static void loadingImg(Context mContext, String url, ImageView view, int width, int height) {
//        Glide.with(mContext).load(url).into(view);
//    }
//
//    public static void loadingImg(Context mContext, String url, ImageView view, int defaultId, int width, int height) {
//        Glide.with(mContext).load(url).placeholder(defaultId).into(view);
//    }
//}


/**
 * 描述：Glide图片加载框架，需要修改框架配置在GlideConfiguration里面修改
 * (占位图：placeholder；是否显示动画withAnim；是否默认加载图片loadPic
 * ；请求宽高：width、height；原来的宽高：orwidth、orheight；)
 * 作者：wangya
 * 时间：15/10/17 下午4:02
 */
public class ImageLoaderUtil {
    /**
     * 图片链接(必要)
     */
    public static String IMAGE_URL = "imageUrl";
    /**
     * 请求宽高，英文逗号分隔(必要)
     */
    public static String WIDTH_HEIGHT = "width_height";
    /**
     * 原来的宽高，英文逗号分隔(必要)
     */
    public static String ORIGINAL_WIDTH_HEIGHT = "original_width_height";
    /**
     * 占位图（非必要，除非是头像）
     */
    public static String PLEASE_HOLDER = "placeholder";
    /**
     * 是否显示动画（非必要，默认是显示动画的）
     */
    public static String WITH_ANIM = "withAnim";
    /**
     * 是否默认加载图片(非必要，如果为true，则不受非wifi下不加载图片限制)
     */
    public static String LOAD_PIC = "loadPic";

    /* --------------------------默认图片-------------------------- */
    public static int loading_50 = R.drawable.app_logo;
    public static int loading_400 = R.drawable.app_logo;

	/* --------------------------加载图片 int------------------------------------- */

    public static void loadingImg(Context mContext, int resouse, ImageView img,
                                  int placeholder) {
        if (MyUtil.isActivityRun(mContext)) {
            return;
        }
        Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
        CoreImageLoaderUtil.loadingImg(mContext, resouse, img, placeholder_drawable);
    }

    /* ----------------------------加载图片 Bitmap--------------------------- */
    public static void loadingImg(Context mContext, Bitmap bitmap,
                                  ImageView img, int placeholder) {
        if (MyUtil.isActivityRun(mContext)) {
            return;
        }
        Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
        CoreImageLoaderUtil.loadingImg(mContext, bitmap, img, placeholder_drawable);
    }

    /* --------------------------加载图片File-------------------------------------- */
    public static void loadingImg(Context mContext, File file, ImageView img,
                                  int placeholder) {
        if (MyUtil.isActivityRun(mContext)) {
            return;
        }
        Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
        CoreImageLoaderUtil.loadingImg(mContext, file, img, placeholder_drawable);
    }

    /* --------------------------单独下载加载图片-------------------------------------- */
    public static void loadingImg(Context mContext, String url,
                                  final CoreImageLoaderUtil.LoadingImageListener listener) {
        if (MyUtil.isActivityRun(mContext)) {
            return;
        }
        CoreImageLoaderUtil.loadingImg(mContext, url, listener, MyUtil.toDip(200), MyUtil.toDip(200));
    }

    /* --------------------------加载网络普通图片----------------------------------- */
    public static void loadingImg(Context mContext, String url, ImageView img) {
        loadingImg(mContext, url, img, loading_50, null, true, false, 0, 0);
    }

    public static void loadingImg(Context mContext, String url, ImageView img,
                                  int width, int height) {
        loadingImg(mContext, url, img, loading_50, null, true, false, width,
                height);
    }

    public static void loadingImg(Context mContext, String url, ImageView img,
                                  int placeholder) {
        loadingImg(mContext, url, img, placeholder, null, true, false, 0, 0);
    }

    public static void loadingImg(Context mContext, String url, ImageView img,
                                  int placeholder, int width, int height) {
        loadingImg(mContext, url, img, placeholder, null, true, false, width,
                height);
    }

    public static void loadingImg(Context mContext, String url, ImageView img,
                                  CoreImageLoaderUtil.LoadingImageListener listener, int width, int height) {
        loadingImg(mContext, url, img, loading_50, listener, true, false,
                width, height);
    }

    public static void loadingImg(Context mContext, String url, ImageView img,
                                  int placeholder, CoreImageLoaderUtil.LoadingImageListener listener, int width,
                                  int height) {
        loadingImg(mContext, url, img, placeholder, listener, true, false,
                width, height);
    }

    public static void loadingImgWithOutAnim(Context mContext, String url,
                                             ImageView img) {
        loadingImg(mContext, url, img, loading_50, null, false, false, 0, 0);
    }

    public static void loadingImgWithOutAnim(Context mContext, String url,
                                             ImageView img, int width, int height) {
        loadingImg(mContext, url, img, loading_50, null, false, false, width,
                height);
    }

    public static void loadingImgWithOutAnim(Context mContext, String url,
                                             ImageView img, int placeholder) {
        loadingImg(mContext, url, img, placeholder, null, false, false, 0, 0);
    }

    public static void loadingImgWithOutAnim(Context mContext, String url,
                                             ImageView img, int placeholder, int width, int height) {
        loadingImg(mContext, url, img, placeholder, null, false, false, width,
                height);
    }

    /**
     * 统一处理图片加载，带属性，带自定义监听，动画开关
     *
     * @param <T>
     * @param url
     * @param img
     * @param placeholder
     * @param listener
     * @param withAnim
     * @param loadPic     : 在非wifi,设置了nowifi no pic 的情况下，手动点击查看图片，还是需要加载图片的
     */
    public static <T> void loadingImg(Context mContext, String url,
                                      final ImageView img, int placeholder,
                                      final CoreImageLoaderUtil.LoadingImageListener listener, boolean withAnim,
                                      boolean loadPic, int width, int height) {
        if (MyUtil.isActivityRun(mContext) || TextUtils.isEmpty(url)) {
            img.setImageDrawable(mContext.getResources().getDrawable(placeholder));
            return;
        }
        if (placeholder == 0) {
            placeholder = ImageLoaderUtil.loading_50;
        }
//        if (width != 0) {
//            if (height != 0) {
//                url = MyUtil.getImageUrlByWidthHeight(url, width, height);
//            } else {
//                url = Util.getImageUrlByWidthHeight(url, width);
//            }
//        }
//        loadPic = !MyUtil.isWifiActive(mContext) && Variable.NO_WIFI_NO_PIC ? loadPic
//                : true;
        if (!loadPic) {
            loadingImgOutofWifi(mContext, url, img, placeholder);
        } else {
            if (img instanceof RoundedImageView || img instanceof CircleImageView) {
                loadingImgWithSpecial(mContext, url, img, placeholder);
            } else {
                loadingImgWithCommon(mContext, url, img, placeholder, listener,
                        withAnim, width, height);
            }
        }
    }

    /**
     * 不带非wifi下不加载图片功能
     *
     * @param mContext
     * @param url
     * @param img
     * @param placeholder
     * @param listener
     * @param withAnim
     * @param width
     * @param height
     * @param <T>
     */
    public static <T> void loadingImg(Context mContext, String url,
                                      final ImageView img, int placeholder,
                                      final CoreImageLoaderUtil.LoadingImageListener listener, boolean withAnim,
                                      int width, int height) {
        if (MyUtil.isActivityRun(mContext) || TextUtils.isEmpty(url)) {
            img.setImageDrawable(mContext.getResources().getDrawable(placeholder));
            return;
        }
        if (placeholder == 0) {
            placeholder = ImageLoaderUtil.loading_50;
        }
//        if (width != 0) {
//            if (height != 0) {
//                url = Util.getImageUrlByWidthHeight(url, width, height);
//            } else {
//                url = Util.getImageUrlByWidthHeight(url, width);
//            }
//        }

        if (img instanceof RoundedImageView || img instanceof CircleImageView) {
            loadingImgWithSpecial(mContext, url, img, placeholder);
        } else {
            loadingImgWithCommon(mContext, url, img, placeholder, listener,
                    withAnim, width, height);
        }
    }

    /**
     * 统一处理图片加载，带属性，带自定义监听，动画开关
     *
     * @param mContext
     * @param map      (占位图：placeholder；是否显示动画withAnim；是否默认加载图片loadPic；请求宽高：width、
     *                 height；原来的宽高：orwidth、orheight；)
     * @param listener
     */
    public static <T> void loadingImg(Context mContext, final ImageView img,
                                      HashMap<String, Object> map, final CoreImageLoaderUtil.LoadingImageListener listener) {
        int placeholder = (Integer) getMapValue(map, PLEASE_HOLDER, loading_50);
        boolean withAnim = (Boolean) getMapValue(map, WITH_ANIM, true);
        boolean loadPic = (Boolean) getMapValue(map, LOAD_PIC, false);
        String width_height = (String) getMapValue(map, WIDTH_HEIGHT, null);
        int width = 0;
        int height = 0;
//        if (!TextUtils.isEmpty(width_height)) {
//            if (width_height.contains(",")) {
//                String[] width_height_arr = width_height.split(",");
//                width = ConvertUtils.toInt(width_height_arr[0]);
//                height = ConvertUtils.toInt(width_height_arr[1]);
//            } else {
//                width = ConvertUtils.toInt(width_height);
//            }
//        }
        String url = getImageUrlByWidthHeight(map, width, height);
        if (MyUtil.isActivityRun(mContext) || TextUtils.isEmpty(url)) {
            img.setImageDrawable(mContext.getResources().getDrawable(placeholder));
            return;
        }
        if (placeholder == 0) {
            placeholder = loading_50;
        }
//        loadPic = !Util.isWifiActive(mContext) && Variable.NO_WIFI_NO_PIC ? loadPic
//                : true;
        if (!loadPic) {
            loadingImgOutofWifi(mContext, url, img, placeholder);
        } else {
            if (img instanceof RoundedImageView || img instanceof CircleImageView) {
                loadingImgWithSpecial(mContext, url, img, placeholder);
            } else {
                loadingImgWithCommon(mContext, url, img, placeholder, listener,
                        withAnim, width, height);
            }

        }
    }

    /**
     * 拼接图片请求参数
     *
     * @param url         图片链接
     * @param placeholder 占位图
     * @param withAnim    是否显示动画
     * @param loadPic     是否默认加载图片
     * @param width       请求的宽
     * @param height      请求图片的高
     * @param or_width    接口返回的宽
     * @param or_height   接口返回的高
     * @return
     */
    public static HashMap<String, Object> setImageLoadMap(String url,
                                                          int placeholder, boolean withAnim, boolean loadPic, int width,
                                                          int height, int or_width, int or_height) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(IMAGE_URL, url);
        map.put(WIDTH_HEIGHT, width + "," + height);
        map.put(ORIGINAL_WIDTH_HEIGHT, or_width + "," + or_height);
        map.put(PLEASE_HOLDER, placeholder);
        map.put(WITH_ANIM, withAnim);
        map.put(LOAD_PIC, loadPic);
        return map;
    }

    /**
     * 拼接图片请求参数
     *
     * @param url         图片链接
     * @param placeholder 占位图
     * @param width       请求的宽
     * @param height      请求图片的高
     * @param or_width    接口返回的宽
     * @param or_height   接口返回的高
     * @return
     */
    public static HashMap<String, Object> setImageLoadMap(String url,
                                                          int placeholder, int width, int height, int or_width, int or_height) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(IMAGE_URL, url);
        map.put(WIDTH_HEIGHT, width + "," + height);
        map.put(ORIGINAL_WIDTH_HEIGHT, or_width + "," + or_height);
        map.put(PLEASE_HOLDER, placeholder);
        return map;
    }

    /**
     * 根据图片宽高来拼接图片路径
     *
     * @param map
     * @param width
     * @param height
     * @return
     */
    public static String getImageUrlByWidthHeight(HashMap<String, Object> map,
                                                  int width, int height) {
        String url = (String) getMapValue(map, IMAGE_URL, null);
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        // 判断是不是七牛的图片
        if (url.contains("{$hgPicSizeStart}")
                && url.contains("{$hgPicSizeEnd}")) {
            url = url.replace("{$hgPicSizeStart}", "").replace(
                    "{$hgPicSizeEnd}", "");
            String width_height = (String) getMapValue(map, WIDTH_HEIGHT, null);
            String or_width_height = (String) getMapValue(map,
                    ORIGINAL_WIDTH_HEIGHT, null);
            int or_width = 0;
            int or_height = 0;
//            if (!TextUtils.isEmpty(or_width_height)) {
//                if (or_width_height.contains(",")) {
//                    String[] or_width_height_arr = or_width_height.split(",");
//                    or_width = ConvertUtils.toInt(or_width_height_arr[0]);
//                    or_height = ConvertUtils.toInt(or_width_height_arr[1]);
//                } else {
//                    or_width = ConvertUtils.toInt(width_height);
//                }
//            }
            if (or_width != 0 && width != 0 && height == 0) {
                height = width * or_height / or_width;
            }
            url = url.replace("{$hgPicSizeWidth}", width + "").replace(
                    "{$hgPicSizeHeight}", height + "");
        } else {
//            if (width != 0) {
//                if (height != 0) {
//                    url = Util.getImageUrlByWidthHeight(url, width, height);
//                } else {
//                    url = Util.getImageUrlByWidthHeight(url, width);
//                }
//            }
        }
        return url;
    }

    private static Object getMapValue(HashMap<String, Object> map, String key,
                                      Object defaultvalue) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultvalue;
    }

    /**
     * 加载自定义的ImageView
     *
     * @param mContext
     * @param url
     * @param img
     * @param placeholder
     */
    private static void loadingImgWithSpecial(final Context mContext,
                                              String url, final ImageView img, final int placeholder) {
        img.setImageDrawable(mContext.getResources().getDrawable(placeholder));
        loadingImg(mContext, url, new CoreImageLoaderUtil.LoadingImageListener() {

            @Override
            public <T> void onResourceReady(T t) {
                img.setImageBitmap((Bitmap) t);
            }

            @Override
            public void onLoadFailed() {
                img.setImageDrawable(mContext.getResources().getDrawable(placeholder));
            }

        });
    }

    /**
     * @param mContext
     * @param url
     * @param img
     * @param placeholder
     * @param listener
     * @param withAnim
     * @param width
     * @param height
     */
    private static void loadingImgWithCommon(final Context mContext,
                                             String url, final ImageView img, final int placeholder,
                                             final CoreImageLoaderUtil.LoadingImageListener listener, boolean withAnim, int width,
                                             int height) {
        if (null == img) {
            return;
        }
        Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
        ;
        CoreImageLoaderUtil.loadingImgWithCommon(mContext, url, img, placeholder_drawable, listener, withAnim, width, height, R.anim.imageloader_anim);
    }

    /**
     * 非wifi环境下 不显示图片
     *
     * @param mContext
     * @param url
     * @param img
     * @param placeholder
     */
    private static void loadingImgOutofWifi(Context mContext, String url,
                                            final ImageView img, int placeholder) {
//        if (Util.isActivityRun(mContext)) {
//            return;
//        }
//        LogUtil.e("-------------------loadingImgOutofWifi--------------------------");
//        Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);;
//        CoreImageLoaderUtil.loadingImgOutofWifi(mContext, url, img, placeholder_drawable, R.anim.imageloader_anim);
    }

    /**
     * listview 加载圆形图片
     *
     * @param mContext
     * @param url
     * @param imageView 直接ImageView
     * @param width
     * @param height
     * @param radius    半径，如果是圆形，半径即为宽高的一般，只需要圆角，自定义
     */
    public static void loadingCircleImage(final Context mContext, String url,
                                          final ImageView imageView, int width, int height, final int radius,
                                          int placeholder) {
        if (placeholder == 0) {
            placeholder = loading_50;
        }
        final Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
//        if (width != 0) {
//            if (height != 0) {
//                url = Util.getImageUrlByWidthHeight(url, width, height);
//            } else {
//                url = Util.getImageUrlByWidthHeight(url, width);
//            }
//        }
        CoreImageLoaderUtil.loadingCircleImage(mContext, url, imageView, radius, placeholder_drawable, null);
    }

    public static void loadingCircleImage(final Context mContext, String url,
                                          final ImageView imageView, int width, int height, final int radius,
                                          int placeholder, final CoreImageLoaderUtil.LoadingImageListener listener) {
        if (placeholder == 0) {
            placeholder = loading_50;
        }
        final Drawable placeholder_drawable = mContext.getResources().getDrawable(placeholder);
//        if (width != 0) {
//            if (height != 0) {
//                url = Util.getImageUrlByWidthHeight(url, width, height);
//            } else {
//                url = Util.getImageUrlByWidthHeight(url, width);
//            }
//        }
        CoreImageLoaderUtil.loadingCircleImage(mContext, url, imageView, radius, placeholder_drawable, listener);
    }


    /**
     * 清除图片缓存
     *
     * @param mContext
     */
    public static void clearImageCache(final Context mContext) {
        if (MyUtil.isActivityRun(mContext)) {
            return;
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                CoreImageLoaderUtil.clearMemory(mContext);
            }
        });
        ThreadPoolUtil.executeCachedThread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                CoreImageLoaderUtil.clearDiskCache(mContext);
                Looper.loop();
            }
        });
    }
}

