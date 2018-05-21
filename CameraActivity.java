package com.hoge.android.factory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hoge.android.factory.base.BaseSimpleActivity;
import com.hoge.android.factory.compwidget.R;
import com.hoge.android.factory.constants.CameraConfig;
import com.hoge.android.factory.images.Bimp;
import com.hoge.android.factory.images.ImageItem;
import com.hoge.android.factory.util.Go2Util;
import com.hoge.android.factory.util.Util;
import com.hoge.android.factory.util.ui.ImageLoaderUtil;
import com.hoge.android.factory.util.ui.ThemeUtil;
import com.hoge.android.factory.variable.Variable;
import com.hoge.android.factory.views.CircuseeAudioViewLayout;
import com.hoge.android.factory.views.CircuseeAudioViewLayout.AudioLayoutListener;
import com.hoge.android.factory.widget.MMAlert;
import com.hoge.android.factory.widget.MMAlert.OnDialogClick;
import com.hoge.android.util.CustomToast;
import com.hoge.android.util.LogUtil;
import com.hoge.android.util.StorageUtils;
import com.hoge.android.util.bitmap.BitmapCompressUtil;
import com.hoge.android.util.rom.PermissionConstant;
import com.hoge.android.util.rom.PermissionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author ray
 * @tip 具体说明及参数使用：CameraConfig.java 此类主要是 拍照,视频/音频录制的集成
 * 其中音频可通过传递参数控制其显示及隐藏（通过boolean参数CameraConfig.camera_show_audio控制）
 * 视频与拍照功能默认显示（未添加控制）
 * <p>
 * <p>
 * 参数说明： 参数名称 类型 具体说明 CameraConfig.camera_with_filter boolean 是否支持滤镜功能
 * CameraConfig.camera_video_max_time int 视频录制的最大时长
 * CameraConfig.camera_show_audio boolean 是否支持音频录制
 * CameraConfig.camera_image_max_num int 默认选择的图片最大值
 * CameraConfig.camera_default_selected int 刚进入此界面 默认展示的界面 0拍照，1视频录制，2音频录制；
 */
@SuppressLint("NewApi")
public class CameraActivity extends BaseSimpleActivity implements
        SurfaceHolder.Callback, Camera.PictureCallback, OnErrorListener,
        OnInfoListener {
    public static Activity instance;
    public static final int AUDIO_LEAST_TIME = 2;// 音频最少要录制 2秒
    public static final int VIDEO_LEAST_TIME = 4;// 视频最少录制 4秒
    private int video_max_time = 10;// 视频录制 最大时长

    private int currentItem;
    private List<View> views = new ArrayList<View>();
    private ViewPagerAdapter adapter;
    private View view1, view2, view3;
    /**
     * 显示的view
     */
    private SurfaceView surfaceView;
    private SurfaceView surfaceView2;
    private com.hoge.android.factory.views.WaveformView audio_waveformView;
    // 布局
    private TextView main_tv, left1_tv, left2_tv, right1_tv, right2_tv;
    private android.support.v4.view.ViewPager viewPager;
    private RelativeLayout camera_preview_title_rl;
    private TextView camera_preview_cancel;
    private ImageView camera_preview_flash;
    private LinearLayout video_title_time_ll;
    private TextView video_title_time_text;
    private ImageView camera_preview_change;
    private HorizontalScrollView camera_pics_Horlayout;
    private LinearLayout camera_pics_layout;
    private RelativeLayout camera_preview_bottom;
    private LinearLayout camera_go_album_ll;
    private TextView video_camera_restart;
    private ImageView video_new_img_start;
    private LinearLayout camera_preview_next_ll;
    private com.hoge.android.factory.views.CircuseeAudioViewLayout audio_play_layout;
    private View camera_preview_title_line;
    // 传递过来的参数======================
    private boolean show_picture;// 是否显示拍照 默认显示
    private boolean show_video;// 是否显示录制视频 默认显示
    private boolean show_audio;// 是否显示音频 默认不显示
    private int images_max_num;// 最大图片数量
    private int default_selected;// 默认选中的版块
    private boolean visit_with_filter;// 是否可支持滤镜
    private String next_className;// 操作完成后要跳转的类名
    private boolean only_attachment;// 是否只支持一种附件
    private boolean only_streamingMedia;// 是否只支持一种流媒体
    // =================end=================
    /**
     * 摄像头参数
     */
    private Parameters mParameters;
    /**
     * 录制视频的类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 摄像头对象
     */
    private Camera mCamera;
    /**
     * 文本属性获取器
     */
    private SharedPreferences mPreferences;
    /**
     * 刷新界面的回调
     */
    private SurfaceHolder mHolder;
    // / 相关路径
    private String video_package_path = Variable.FILE_PATH + "video/";
    private String audio_package_path = Variable.FILE_PATH + "audio/";
    private String video_Path;// 视频完整路径 直接储存视频资源的完整路径（上传使用）
    protected String base_file_path;
    // 音频路径及相关属性
    public final static int AAC = MediaRecorder.AudioEncoder.AAC;// 音频格式
    private MediaRecorder recorder; // 音频使用\
    private String audio_Path;// 音频完整路径;
    private boolean isRecording_audio = false;// 是否正在录制 音频
    private boolean isRecording = false;// 视频界面是否正在录制
    private boolean isPlaying_audio = false;
    private boolean cancel = false;// 这个参数控制 从edit界面跳转后的是否带有音频 视频资源
    private int use_time_runnable_id = 0;// 这个参数 在视频音频录制的时候赋值，0为视频在使用 1为音频在使用
    // 相关监听
    public OrientationListener mOrientationListener; // 相机横竖屏监听
    private int cameraPosition = 1;// /** 1表示后置，0表示前置 */
    private int audio_max_time = 60; // 音频最大时长
    private int cur_mode = 0; // /** 0表示照片，1表示视频，2表示录音 */
    private Camera.AutoFocusCallback myAutoFocusCallback;
    private boolean isLandscape = false;//是否强制横屏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = CameraActivity.this;
        setContentView(R.layout.v_main);
        enabledActionBar(false);
        // 视频 录制时长
        createFiles();// 创建一些必要的文件夹
        getBundleData();
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initViews();// 初始化 界面参数
        initPager();// 初始化 ViewPager
        setListener();// 注册监听
        if (null != tintManager) {
            tintManager.setStatusBarTintEnabled(false);
        }
        currentItem = setDefaultSelected();

        if (!TextUtils.isEmpty(CameraConfig.video_url)) {
            cancel = true;
        }

        if (!TextUtils.isEmpty(CameraConfig.audio_url)) {
            cancel = true;
        }

        myAutoFocusCallback = new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.setOneShotPreviewCallback(null);
                } else {
                }
            }
        };
    }

    @Override
    protected void left2Right() {
    }

    private int setDefaultSelected() {
        if (default_selected > 2) {
            default_selected = 0;
        }
        switch (default_selected) {
            case 0:
                return 0;
            case 1:
                if (show_picture) {
                    if (show_video) {
                        return 1;// 选中视频
                    } else {
                        if (show_audio) {
                            return 1;// 选中音频
                        } else {
                            return 0;// 选中图片
                        }
                    }
                } else {
                    if (show_video && show_audio) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            case 2:
                if (show_picture && show_video && show_audio) {
                    return 2;// 选中音频
                } else {
                    return 0;
                }
        }
        return 0;
    }

    private void getBundleData() {
        isLandscape = bundle.getBoolean(CameraConfig.ISLANDSCAPE, false);
        images_max_num = bundle.getInt(CameraConfig.camera_image_max_num, 10);
        video_max_time = bundle.getInt(CameraConfig.camera_video_max_time, 10);
        audio_max_time = bundle.getInt(CameraConfig.camera_audio_max_time, 60);
        show_picture = bundle
                .getBoolean(CameraConfig.camera_show_picture, true);
        show_video = bundle.getBoolean(CameraConfig.camera_show_video, true);
        show_audio = bundle.getBoolean(CameraConfig.camera_show_audio, false);
        default_selected = bundle.getInt(CameraConfig.camera_default_selected,
                0);
        visit_with_filter = bundle.getBoolean(CameraConfig.camera_with_filter,
                true);
        next_className = bundle.getString(CameraConfig.camera_next_className,
                "");
        only_attachment = bundle.getBoolean(
                CameraConfig.camera_only_attachment, false);
        only_streamingMedia = bundle.getBoolean(
                CameraConfig.camera_only_streamingMedia, true);
    }

    /**
     * 创建必要的文件夹 及一些路径
     */
    private void createFiles() {
        base_file_path = StorageUtils.getPath(mContext);
        File file = new File(base_file_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // 创建文件夹
        File file_video = new File(video_package_path);
        if (!file_video.exists()) {
            file_video.mkdirs();
        }

        File file_audio = new File(audio_package_path);
        if (!file_audio.exists()) {
            file_audio.mkdirs();
        }
    }

    /**
     * @param index 只接收 0（相机界面）， 1（视频界面） 初始化 相机参数
     */
    @SuppressWarnings("deprecation")
    private void init(int index) {
        // 安装一个SurfaceHolder.Callback
        if (index == 0) {
            mHolder = surfaceView.getHolder();
        } else if (index == 1) {
            mHolder = surfaceView2.getHolder();
        }
        if (null != mHolder) {
            // 针对低于3.0的Android
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.addCallback(this);
            mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }

    /**
     * 取得相机实例 注册横竖屏监听
     */
    private void getMyCamera() {
        // 获取Camera实例
        mCamera = getCamera();
        if (mCamera != null) {
            // 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
            if (isLandscape) {
                mCamera.setDisplayOrientation(0);
            } else {
                mCamera.setDisplayOrientation(90);
            }
            mCamera.lock();
            initCameraParameters();
            mOrientationListener = new OrientationListener(mContext);
            mOrientationListener.enable();
        }
    }

    /**
     * 获取摄像头实例
     *
     * @return
     */
    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
        }
        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            stopRecord(true);
        } else if (isRecording_audio) {
            stopRecord_audio(false);
        }
        if (isPlaying_audio) {
            audio_play_layout.pausePlaying();
        }
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != viewPager) {
            viewPager.setCurrentItem(currentItem);
            changeCursor(currentItem);
        }

        if (currentItem == 0 && show_picture) {// 照片界面
            if (Bimp.selectBitmap.size() > 0) {
                camera_pics_layout.removeAllViews();
                for (int i = 0, j = Bimp.selectBitmap.size(); i < j; i++) {
                    addImages2Horlayout(
                            Bimp.selectBitmap.get(i).getImagePath(), i);
                }
            } else {
                Util.setVisibility(camera_pics_Horlayout, View.GONE);
            }
        } else {
            Util.setVisibility(camera_pics_Horlayout, View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != audio_play_layout) {
            audio_play_layout.unregisterReceiver(false);
        }
        if (null != recordingHandler) {
            recordingHandler.removeCallbacks(time_runnable);
            recordingHandler = null;
        }
        releaseCamera();
        if (null != recorder) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (Exception e) {

            }
        }
        mOrientationListener = null;
        resetMediaRecorder();
    }

    private void initPager() {
        if (!show_picture && !show_video && !show_audio) {// 三个都未被开启,默认添加个 图片
            view1 = getLayoutInflater().inflate(R.layout.v_picture, null);
            surfaceView = (SurfaceView) view1
                    .findViewById(R.id.picture_surfaceview);
            views.add(view1);
            surfaceView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                    return false;
                }
            });
        } else {
            if (show_picture) {
                view1 = getLayoutInflater().inflate(R.layout.v_picture, null);
                surfaceView = (SurfaceView) view1
                        .findViewById(R.id.picture_surfaceview);
                views.add(view1);
                surfaceView.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                            }
                        });
                        return false;
                    }
                });
            }
            if (show_video) {
                view2 = getLayoutInflater().inflate(R.layout.v_video, null);
                surfaceView2 = (SurfaceView) view2
                        .findViewById(R.id.video_surfaceview);
                views.add(view2);
                surfaceView2.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                            }
                        });
                        return false;
                    }
                });
            }
            if (show_audio) {
                view3 = getLayoutInflater().inflate(R.layout.v_audio, null);
                audio_waveformView = (com.hoge.android.factory.views.WaveformView) view3
                        .findViewById(R.id.audio_waveformView);
                views.add(view3);
            }
        }
        if (views.size() > 0) {
            adapter = new ViewPagerAdapter(views);
            viewPager.setAdapter(adapter);
        }
        viewPager.setOffscreenPageLimit(3);
    }

    @SuppressWarnings("deprecation")
    private void setListener() {
        // ViewPager滑动监听器
        viewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRecording || isRecording_audio) {
                    return true;
                }
                return false;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 当滑动式，顶部的imageView是通过animation缓慢的滑动
            @Override
            public void onPageSelected(int arg0) {
                currentItem = arg0;
                changeCursor(currentItem);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        left2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (show_picture && show_video && show_audio) {
                    viewPager.setCurrentItem(0);// 图片
                }
            }
        });

        left1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (show_picture && show_video && show_audio) {
                    if (currentItem == 1) {// 视频
                        viewPager.setCurrentItem(0);// 图片
                    } else if (currentItem == 2) {// 音频
                        viewPager.setCurrentItem(1);// 视频
                    }
                } else {
                    viewPager.setCurrentItem(0);//
                }
            }
        });

        right1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (show_picture && show_video && show_audio) {
                    if (currentItem == 0) {// 图片
                        viewPager.setCurrentItem(1);// 视频
                    } else if (currentItem == 1) {// 视频
                        viewPager.setCurrentItem(2);// 音频
                    }
                } else {
                    viewPager.setCurrentItem(1);//
                }
            }
        });

        right2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (show_picture && show_video && show_audio) {
                    viewPager.setCurrentItem(2);// 图片
                }
            }
        });

        camera_preview_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        setFunctionListener();
    }

    protected void cancel() {
        if (isRecording) {
            stopRecord(true);
        }
        if (isRecording_audio) {
            stopRecord_audio(true);
        }
        if (isPlaying_audio) {
            audio_play_layout.pausePlaying();
        }
        resetMediaRecorder();
        releaseCamera();
        if (!cancel) {
            clearVideo();
            clearAudio();
        }
        Bimp.selectBitmap.clear();
        goBack();
    }

    private void setFunctionListener() {
        video_new_img_start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (currentItem) {
                    case 0:
                        if (show_picture) {
                            getPicture();// 拍照
                        } else {
                            if (show_video) {
                                getVideo();// 视频
                            } else {
                                if (show_audio) {
                                    getAudio();// 视频录制
                                } else {
                                    getPicture();// 拍照
                                }
                            }
                        }
                        break;
                    case 1:// 必显示2个以上控件
                        if (show_picture) {
                            if (show_video) {
                                getVideo();
                            } else {
                                if (show_audio) {
                                    getAudio();
                                }
                            }
                        } else {
                            if (show_video && show_audio) {
                                getAudio();
                            }
                        }
                        break;
                    case 2:// 必显示三个
                        if (show_picture && show_video && show_audio) {
                            getAudio();
                        }
                        break;
                }
            }
        });
        // 去相册选择...
        camera_go_album_ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                final int can_choose_count = images_max_num
                        - camera_pics_layout.getChildCount();
                if (can_choose_count <= 0) {
                    CustomToast.showToast(mContext, "图片选择已达上限",
                            CustomToast.WARM);
                    return;
                }

                if (only_attachment) {// 只是支持一种附件
                    if (!TextUtils.isEmpty(CameraConfig.video_url)
                            || !TextUtils.isEmpty(CameraConfig.audio_url)) {// 并且
                        // 这种附件不是
                        // 图片
                        MMAlert.showAlert(mContext, null, "附件已存在！再次点击将删除",
                                "提示", null, null, new OnDialogClick() {

                                    @Override
                                    public void onOkListener(String content) {
                                        // 构造CountDownTimer对象
                                        clearAudio();
                                        clearVideo();
                                        showNextLayout();
                                        goCameraSelection(can_choose_count);
                                    }

                                    @Override
                                    public void onClickPreListener(EditText et) {

                                    }

                                    @Override
                                    public void onCancelListener(EditText et) {

                                    }
                                }, true);
                        return;
                    }
                }
                goCameraSelection(can_choose_count);
            }
        });

        /** 开启或关闭闪光灯 */
        camera_preview_flash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cameraPosition != 0) {// 前置摄像头的时候不能切换闪光灯
                    if (mParameters != null) {
                        if (mParameters.getFlashMode() != null
                                && mParameters.getFlashMode().equals(
                                Parameters.FLASH_MODE_ON)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                            ThemeUtil.setImageResource(mContext,
                                    camera_preview_flash,
                                    R.drawable.common_image_camera_flash_2x);
                        } else if (mParameters.getFlashMode() != null
                                && mParameters.getFlashMode().equals(
                                Parameters.FLASH_MODE_OFF)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_ON);
                            ThemeUtil
                                    .setImageResource(
                                            mContext,
                                            camera_preview_flash,
                                            R.drawable.common_image_camera_flash_2x_open);
                        }
                        if (mCamera != null) {
                            try {
                                mCamera.setParameters(mParameters);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        /** 摄像头切换 */
        camera_preview_change.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        /** 删除按钮 重置 录制状态 */
        video_camera_restart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != recordingHandler) {
                    recordingHandler.removeCallbacks(time_runnable);
                    recordingHandler = null;
                }
                stopRecord(false);
                if (!TextUtils.isEmpty(CameraConfig.video_url)) {
                    File file = new File(CameraConfig.video_url);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                // cancel = false;
                CameraConfig.video_url = "";
                video_title_time_text.setText("00:00:00");
                recordTime = 0;
                Util.setVisibility(video_camera_restart, View.GONE);
                showNextLayout();
            }
        });

        // 播放监听
        audio_play_layout.setAudioLayoutListener(new AudioLayoutListener() {

            @Override
            public void onProgressChangedListener(long currentPosition) {

            }

            @Override
            public void onCompleteListener() {
                isPlaying_audio = false;
            }

            @Override
            public void isPlaying(boolean isPlaying) {
                isPlaying_audio = isPlaying;
            }
        });

        /** 确认按钮 */
        camera_preview_next_ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecord(true);// 舍弃
                }
                if (isRecording_audio) {
                    stopRecord_audio(false);// 保留
                }
                if (isPlaying_audio) {
                    audio_play_layout.pausePlaying();
                }
                if (null != Bimp.selectBitmap && Bimp.selectBitmap.size() > 0) {
                    for (int i = 0, j = Bimp.selectBitmap.size(); i < j; i++) {
                        CameraConfig.allPictures.add(Bimp.selectBitmap.get(i)
                                .getImagePath());// 新增照片
                    }
                }
                releaseCamera();
                goNext();
                Bimp.selectBitmap.clear();
                goBack();
            }
        });
    }

    private void initViews() {
        left2_tv = (TextView) findViewById(R.id.camera_preview_function_left2);
        left1_tv = (TextView) findViewById(R.id.camera_preview_function_left);
        main_tv = (TextView) findViewById(R.id.camera_preview_function);
        right1_tv = (TextView) findViewById(R.id.camera_preview_function_right);
        right2_tv = (TextView) findViewById(R.id.camera_preview_function_right2);
        viewPager = (android.support.v4.view.ViewPager) findViewById(R.id.viewPager);
        camera_preview_title_rl = (RelativeLayout) findViewById(R.id.camera_preview_title_rl);
        camera_preview_cancel = (TextView) findViewById(R.id.camera_preview_cancel);
        camera_preview_flash = (ImageView) findViewById(R.id.camera_preview_flash);
        video_title_time_ll = (LinearLayout) findViewById(R.id.video_title_time_ll);
        video_title_time_text = (TextView) findViewById(R.id.video_title_time_text);
        camera_preview_change = (ImageView) findViewById(R.id.camera_preview_change);
        camera_pics_Horlayout = (HorizontalScrollView) findViewById(R.id.camera_pics_Horlayout);
        camera_pics_layout = (LinearLayout) findViewById(R.id.camera_pics_layout);
        camera_preview_bottom = (RelativeLayout) findViewById(R.id.camera_preview_bottom);
        camera_go_album_ll = (LinearLayout) findViewById(R.id.camera_go_album_ll);
        video_camera_restart = (TextView) findViewById(R.id.video_camera_restart);
        video_new_img_start = (ImageView) findViewById(R.id.video_new_img_start);
        camera_preview_next_ll = (LinearLayout) findViewById(R.id.camera_preview_next_ll);
        audio_play_layout = (CircuseeAudioViewLayout) findViewById(R.id.audio_play_layout);
        camera_preview_title_line = findViewById(R.id.camera_preview_title_line);
        audio_play_layout.registerReceiver();// 注册广播监听

    }

    /**
     * 执行拍照指令
     */
    private void getPicture() {
        int can_choose_count = images_max_num
                - camera_pics_layout.getChildCount();
        if (can_choose_count <= 0) {
            CustomToast.showToast(mContext, "图片选择已达上限", CustomToast.WARM);
            return;
        }
        if (only_attachment) {
            if (!TextUtils.isEmpty(CameraConfig.video_url)
                    || !TextUtils.isEmpty(CameraConfig.audio_url)) {
                MMAlert.showAlert(mContext, null,
                        getString(R.string.camera_opera), "提示", null, null,
                        new OnDialogClick() {

                            @Override
                            public void onOkListener(String content) {
                                // 构造CountDownTimer对象
                                clearAudio();
                                clearVideo();
                                video_new_img_start.setEnabled(false);
                                camera_preview_next_ll.setEnabled(false);// 生成照片的过程中不可点击下一步
                                showNextLayout();
                                mCamera.takePicture(null, null, null,
                                        CameraActivity.this);
                            }

                            @Override
                            public void onClickPreListener(EditText et) {

                            }

                            @Override
                            public void onCancelListener(EditText et) {

                            }
                        }, true);
                return;
            }
        }
        video_new_img_start.setEnabled(false);
        camera_preview_next_ll.setEnabled(false);
        mCamera.takePicture(null, null, null, CameraActivity.this);
    }

    /**
     * 执行视频录制指令
     */
    private void getVideo() {
        if (only_attachment) {
            if (Bimp.selectBitmap.size() > 0
                    || !TextUtils.isEmpty(CameraConfig.audio_url)) {
                alert(getString(R.string.camera_opera), true, true);
                return;
            }
        } else {// 图片无限制
            if (only_streamingMedia) {
                // TODO 是否只是支持一种流媒体呢？ 具体处理 c此处就要判断 音频 是否存在了
                if (!TextUtils.isEmpty(CameraConfig.audio_url)) {
                    alert(getString(R.string.camera_audio_exist), false, true);
                    return;
                }
            }
        }
        // 视频录制
        if (!TextUtils.isEmpty(CameraConfig.video_url)) {
            alert(getString(R.string.camera_video_exist), false, true);
            return;
        }
        if (!isRecording) {
            // 构造CountDownTimer对象
            use_time_runnable_id = 0;
            recordingHandler = new Handler();
            startRecord();
            recordingHandler.postDelayed(time_runnable, 1000);
        } else {
            if (recordTime < VIDEO_LEAST_TIME) {
                // 是否需要提示当前录制的时间太短
                return;
            }
            stopRecord(false);
        }
    }

    /**
     * @param alertText  弹出框内提示的文案
     * @param clearPhoto 是否需要清除图片
     * @param fromVideo  是否是点击视频 弹出的；； false 为音频
     */
    private void alert(String alertText, final boolean clearPhoto,
                       final boolean fromVideo) {
        MMAlert.showAlert(mContext, null, alertText, "提示", null, null,
                new OnDialogClick() {

                    @Override
                    public void onOkListener(String content) {
                        // 构造CountDownTimer对象
                        clearAudio();
                        clearVideo();
                        if (clearPhoto) {
                            Bimp.selectBitmap.clear();
                            CameraConfig.allPictures.clear();
                            camera_pics_layout.removeAllViews();
                        }
                        Util.setVisibility(video_camera_restart, View.GONE);// 视频的
                        // 重置按钮
                        showNextLayout();
                        if (fromVideo) {
                            use_time_runnable_id = 0;
                        } else {
                            use_time_runnable_id = 1;
                        }
                        // 构造CountDownTimer对象
                        recordingHandler = new Handler();
                        recordingHandler.postDelayed(time_runnable, 1000);
                        if (fromVideo) {
                            startRecord();
                        } else {
                            startRecord_audio(AAC);
                        }
                    }

                    @Override
                    public void onClickPreListener(EditText et) {

                    }

                    @Override
                    public void onCancelListener(EditText et) {

                    }
                }, true);
    }

    /**
     * 执行音频录制指令
     */
    private void getAudio() {
        if (isPlaying_audio) {
            audio_play_layout.pausePlaying();
        }
        if (only_attachment) {
            if (Bimp.selectBitmap.size() > 0
                    || !TextUtils.isEmpty(CameraConfig.video_url)) {
                alert(getString(R.string.camera_opera), true, false);
                return;
            }
        } else {
            if (only_streamingMedia) {
                // TODO 是否只是支持一种流媒体呢？ 具体处理 c此处就要判断 视频 是否存在了
                if (!TextUtils.isEmpty(CameraConfig.video_url)) {
                    alert(getString(R.string.camera_video_exist), false, false);
                    return;
                }
            }
        }
        if (!TextUtils.isEmpty(CameraConfig.audio_url)) {
            alert(getString(R.string.camera_audio_exist), false, false);
            return;
        }
        if (!isRecording_audio) {
            // 构造CountDownTimer对象
            use_time_runnable_id = 1;
            recordingHandler = new Handler();
            recordingHandler.postDelayed(time_runnable, 1000);
            startRecord_audio(AAC);
        } else {
            if (recordTime < AUDIO_LEAST_TIME) {
                // 是否需要提示当前录制的时间太短
                return;
            }
            stopRecord_audio(false);
        }
    }

    /**
     * @param itemId 当前选中的id
     * @return 返回 需要显示的控件id position 0:拍照 1视频 2音频
     */
    private void getSelectedItem(int itemId) {
        switch (itemId) {
            case 0:
                if (show_picture) {
                    Util.setVisibility(left1_tv, View.GONE);
                    Util.setVisibility(left2_tv, View.GONE);
                    main_tv.setText("照片");
                    if (show_video) {
                        Util.setVisibility(right1_tv, View.VISIBLE);
                        right1_tv.setText("视频");
                        if (show_audio) {
                            Util.setVisibility(right2_tv, View.VISIBLE);
                            right2_tv.setText("音频");
                        } else {
                            Util.setVisibility(right2_tv, View.GONE);
                        }
                    } else {
                        Util.setVisibility(right2_tv, View.GONE);
                        if (show_audio) {
                            Util.setVisibility(right1_tv, View.VISIBLE);
                            right1_tv.setText("音频");
                        } else {
                            Util.setVisibility(right1_tv, View.GONE);
                        }
                    }
                    showImageLayout();
                    // return 0;
                } else if (show_video) {// 第一个为视频
                    Util.setVisibility(left1_tv, View.GONE);
                    Util.setVisibility(left2_tv, View.GONE);
                    Util.setVisibility(right2_tv, View.GONE);
                    main_tv.setText("视频");
                    if (show_audio) {
                        Util.setVisibility(right1_tv, View.VISIBLE);
                        right1_tv.setText("音频");
                    } else {
                        Util.setVisibility(right1_tv, View.GONE);
                    }
                    showVideoLayout();
                    // return 1;
                } else if (show_audio) {// 只有音频
                    // 第一个为视频
                    Util.setVisibility(left1_tv, View.GONE);
                    Util.setVisibility(left2_tv, View.GONE);
                    Util.setVisibility(right1_tv, View.GONE);
                    Util.setVisibility(right2_tv, View.GONE);
                    main_tv.setText("音频");
                    showAudioLayout();
                    // return 2;
                } else {// 都不显示 只有默认 拍照
                    // return 0;
                }
                break;
            case 1:
                if (show_picture) {
                    Util.setVisibility(left2_tv, View.GONE);
                    Util.setVisibility(right2_tv, View.GONE);
                    Util.setVisibility(left1_tv, View.VISIBLE);
                    left1_tv.setText("照片");
                    if (show_video) {
                        main_tv.setText("视频");
                        if (show_audio) {
                            Util.setVisibility(right1_tv, View.VISIBLE);
                            right1_tv.setText("音频");
                        } else {
                            Util.setVisibility(right1_tv, View.GONE);
                        }
                        showVideoLayout();
                        // return 1;
                    } else if (show_audio) {
                        Util.setVisibility(right1_tv, View.GONE);
                        main_tv.setText("音频");
                        showAudioLayout();
                        // return 1;
                    }
                    // return 0;
                } else {
                    if (show_video && show_audio) {
                        Util.setVisibility(left2_tv, View.GONE);
                        Util.setVisibility(right2_tv, View.GONE);
                        Util.setVisibility(left1_tv, View.VISIBLE);
                        left1_tv.setText("视频");
                        main_tv.setText("音频");
                        Util.setVisibility(right1_tv, View.GONE);
                        showAudioLayout();
                        // return 2;
                    } else {
                        // return 0;
                    }
                }
                break;
            case 2:// 都显示
                Util.setVisibility(left1_tv, View.VISIBLE);
                Util.setVisibility(left2_tv, View.VISIBLE);
                Util.setVisibility(right1_tv, View.GONE);
                Util.setVisibility(right2_tv, View.GONE);
                main_tv.setText("音频");
                left1_tv.setText("视频");
                left2_tv.setText("照片");
                showAudioLayout();
                // return 2;
                break;
        }
        // return 0;
    }

    protected void changeCursor(int itemId) {
        // TODO 改变游标
        if (isRecording) {
            // 录制过程中 进行切换了...
            stopRecord(true);
        }
        if (isRecording_audio) {// 保存
            if (recordTime < AUDIO_LEAST_TIME) {
                stopRecord_audio(true);
            } else {
                stopRecord_audio(false);
            }
        }
        if (isPlaying_audio) {
            audio_play_layout.pausePlaying();
        }
        getSelectedItem(itemId);
        video_title_time_text.setText("00:00:00");
        showNextLayout();
    }

    /**
     * 是否显示下一步
     */
    private void showNextLayout() {
        if (Bimp.selectBitmap.size() == 0
                && TextUtils.isEmpty(CameraConfig.video_url)
                && TextUtils.isEmpty(CameraConfig.audio_url)) {
            Util.setVisibility(camera_preview_next_ll, View.GONE);
        } else {
            Util.setVisibility(camera_preview_next_ll, View.VISIBLE);
        }
    }

    /**
     * 显示音频相关的布局
     */
    private void showAudioLayout() {
        cur_mode = 2;
        com.hoge.android.factory.util.Util.getHandler(mContext).postDelayed(new Runnable() {

            @Override
            public void run() {
                releaseCamera();
            }
        }, 350);
        // 当前为 音频界面了.. 视频头部的 计时View 将为音频使用
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_2x);
        Util.setVisibility(camera_preview_title_line, View.VISIBLE);
        Util.setVisibility(video_camera_restart, View.GONE);// 视频的 重置
        Util.setVisibility(video_title_time_ll, View.VISIBLE);// 视频时间
        Util.setVisibility(camera_preview_flash, View.GONE);// 闪光灯
        Util.setVisibility(camera_preview_change, View.GONE);// 前置后置 切换
        Util.setVisibility(camera_pics_Horlayout, View.GONE);// 图片布局
        Util.setVisibility(camera_go_album_ll, View.GONE);// 照片的去相册
        if (TextUtils.isEmpty(CameraConfig.audio_url)) {
            Util.setVisibility(audio_play_layout, View.GONE);
        } else {
            Util.setVisibility(audio_play_layout, View.VISIBLE);
        }
        camera_preview_bottom.setBackgroundColor(0xff000000);
        camera_preview_title_rl.setBackgroundColor(0xff000000);
    }

    /**
     * 显示视频相关控件
     */
    private void showVideoLayout() {
        cameraPosition = 1;
        cur_mode = 1;
        com.hoge.android.factory.util.Util.getHandler(mContext).postDelayed(new Runnable() {

            @Override
            public void run() {
                releaseCamera();
                init(1);
                if (null == mCamera) {
                    getMyCamera();
                }
                setStartPreview(mHolder);
            }
        }, 350);
        Util.setVisibility(camera_preview_title_line, View.GONE);
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_2x);
        Util.setVisibility(camera_preview_title_rl, View.VISIBLE);// 头布局
        Util.setVisibility(camera_go_album_ll, View.GONE);// 照片的去相册
        Util.setVisibility(video_title_time_ll, View.VISIBLE);// 视频时间
        Util.setVisibility(camera_preview_flash, View.GONE);// 闪光灯
        Util.setVisibility(audio_play_layout, View.GONE);
        Util.setVisibility(camera_preview_change, View.VISIBLE);// 前置后置 切换
        Util.setVisibility(camera_pics_Horlayout, View.GONE);// 图片布局
        if (!TextUtils.isEmpty(CameraConfig.video_url)) {// 重置 按钮
            video_camera_restart.setVisibility(View.VISIBLE);
        } else {
            Util.setVisibility(video_camera_restart, View.GONE);
        }
        camera_preview_bottom.setBackgroundColor(0x7f000000);
        camera_preview_title_rl.setBackgroundColor(0x7f000000);
    }

    /**
     * 图片拍照相关控件
     */
    private void showImageLayout() {
        cur_mode = 0;
        cameraPosition = 1;
        com.hoge.android.factory.util.Util.getHandler(mContext).postDelayed(new Runnable() {

            @Override
            public void run() {
                releaseCamera();
                init(0);
                if (null == mCamera) {
                    getMyCamera();
                }
                setStartPreview(mHolder);
            }
        }, 350);

        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_shot_2x);
        ThemeUtil.setImageResource(mContext,
                camera_preview_flash,
                R.drawable.common_image_camera_flash_2x);//要把闪光灯的图片重置
        Util.setVisibility(camera_preview_title_line, View.GONE);
        Util.setVisibility(camera_preview_title_rl, View.VISIBLE);// 头布局
        Util.setVisibility(camera_go_album_ll, View.VISIBLE);// 照片的去相册
        Util.setVisibility(video_camera_restart, View.GONE);// 视频的 重置
        Util.setVisibility(video_title_time_ll, View.GONE);// 视频时间
        Util.setVisibility(camera_preview_flash, View.VISIBLE);// 闪光灯
        Util.setVisibility(audio_play_layout, View.GONE);
        Util.setVisibility(camera_preview_change, View.VISIBLE);// 前置后置 切换
        if (camera_pics_layout.getChildCount() > 0) {// 图片布局 选中后的图片
            Util.setVisibility(camera_pics_Horlayout, View.VISIBLE);
        } else {
            Util.setVisibility(camera_pics_Horlayout, View.GONE);
        }
        camera_preview_bottom.setBackgroundColor(0xff000000);
        camera_preview_title_rl.setBackgroundColor(0xff000000);
    }

    private int recordTime = 0;
    private Handler recordingHandler = null;
    Runnable time_runnable = new Runnable() {

        @Override
        public void run() {
            // TODO 定时器
            int time = ++recordTime;
            if (use_time_runnable_id == 0) {// 视频
                if (time < video_max_time) {
                    recordingHandler.postDelayed(time_runnable, 1000);
                    video_title_time_text.setText(transPosition(recordTime));
                } else if (time == video_max_time) {
                    recordingHandler.postDelayed(time_runnable, 1000);
                    video_title_time_text.setText(transPosition(recordTime));
                } else {
                    // video_new_img_start.performClick();
                    stopRecord(false);
                }
            } else if (use_time_runnable_id == 1) {// 音频
                if (recordTime < audio_max_time) {
                    recordingHandler.postDelayed(time_runnable, 1000);
                    video_title_time_text.setText(transPosition(recordTime));
                    if (null != recorder) {
                        int x = recorder.getMaxAmplitude();
                        float f;
                        if (x != 0) {
                            f = (float) (10 * Math.log(x) / Math.log(10));
                            update(f);
                        }
                    }
                } else {
                    stopRecord_audio(false);
                }
            }
        }
    };

    private void update(final float volume) {
        audio_waveformView.post(new Runnable() {
            @Override
            public void run() {
                audio_waveformView.updateAmplitude(volume * 0.1f / 40);
            }
        });
    }

    private void permissionRecord() {
        if (TextUtils.isEmpty(video_Path)) {
            video_Path = video_package_path + System.currentTimeMillis()
                    + ".mp4";
        }
        recordTime = 0;
        if (mCamera == null) {
            getMyCamera();
        }
        // Step 1: Unlock and set camera to MediaRecorder
        try {
            mCamera.unlock();
        } catch (RuntimeException e) {
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
        } else {
            mMediaRecorder.reset();
        }
        // TODO mMediaRecorder参数
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        // before setOutputFormat()
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // before setOutputFormat()
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // ============================================================
        // Step 3: Set output file format
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // ============================================================
        // 设置视频输出的格式和编码
        // after setVideoSource(),after setOutputFormat()
        mMediaRecorder.setAudioEncodingBitRate(44100);
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
//        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        mMediaRecorder.setProfile(camcorderProfile);//构造CamcorderProfile，使用高质量视频录制
        // after after setOutputFormat()
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // after after setOutputFormat()
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // Step 4: Set output file
        mMediaRecorder.setOutputFile(video_Path);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        if (!Build.MODEL.equalsIgnoreCase("MI 6")) {//小米手机单独处理 不做宽高设置
            mMediaRecorder.setVideoSize(bestWidth, bestHeight);//设置拍摄视频的宽高 设置顺序也很重要不然会出错
            Log.e("SZH", "getCurrentNormalizedOrientation:" + Build.MODEL);
        } else {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            Log.e("SZH", "profile:" + profile.videoFrameWidth + "," + profile.videoFrameHeight);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        }
        // 不设置mMediaRecorder.start()会报错
        mMediaRecorder.setOnInfoListener(this);
        // 不设置mMediaRecorder.start()会报错
        mMediaRecorder.setOnErrorListener(this);

        // // 设置保存录像方向
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {//针对横屏添加适配
            int or = mOrientationListener.getCurrentNormalizedOrientation();//获取的数值做过处理,所以下面的判断得反着来
            Log.e("SZH", "getCurrentNormalizedOrientation:" + or);
            if (isLandscape) {//强制横屏的不修改
//                if (or == 0 || or == 180) {
                if (cameraPosition == 1) {//强制横屏的内容就是横屏
                    // orientationHint = 90;
                    // 由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
                    mMediaRecorder.setOrientationHint(0);
                } else if (cameraPosition == 0) {
                    // orientationHint = 270;
                    // 由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
                    mMediaRecorder.setOrientationHint(180);
                }
//                } else {//横屏适配
//                    if (cameraPosition == 1) {
//                        mMediaRecorder.setOrientationHint(90);
//                    } else if (cameraPosition == 0) {
//                        mMediaRecorder.setOrientationHint(270);
//                    }
//                }
            } else {
                if (or == 0 || or == 180) {
                    if (cameraPosition == 1) {
                        // orientationHint = 90;
                        // 由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
                        mMediaRecorder.setOrientationHint(90);
                    } else if (cameraPosition == 0) {
                        // orientationHint = 270;
                        // 由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
                        mMediaRecorder.setOrientationHint(270);
                    }
                } else {//横屏适配
                    if (cameraPosition == 1) {
                        mMediaRecorder.setOrientationHint(0);
                    } else if (cameraPosition == 0) {
                        mMediaRecorder.setOrientationHint(180);
                    }
                }
            }
        }
        // 第6步:根据以上配置准备MediaRecorder
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_recording);
        isRecording = true;
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            recodErrorForVideo(e);
        } catch (IOException e) {
            e.printStackTrace();
            recodErrorForVideo(e);

        }
    }

    /**
     * 开始录制 视频，mMediaRecorder的参数严格按照步骤来
     */
    @SuppressLint("NewApi")
    private void startRecord() {
        requestPermission(PermissionConstant.REQUEST_CAMERAAUDIO, PermissionUtil.getCameraAudioPermission(),
                new PermissionUtil.IPermissionCallBack() {
                    @Override
                    public void permissionsGranted() {
                        permissionRecord();
                    }

                    @Override
                    public void permissionsDenied() {

                    }
                });

    }


    /**
     * 异常处理 视频、
     */
    private void recodErrorForVideo(Exception e) {
        LogUtil.e(e.getMessage());
        isRecording = false;
        CameraConfig.video_url = "";
        if (null != recordingHandler) {
            recordingHandler.removeCallbacks(time_runnable);
            recordingHandler = null;
        }
        stopRecord(true);
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setMessage("该设备暂不支持视频录制");
        builder.setTitle("出错啦");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 异常处理 音频 异常
     */
    private void recodErrorForAudio(Exception e) {
        LogUtil.e(e.getMessage());
        isRecording_audio = false;
        CameraConfig.audio_url = "";
        CameraConfig.audio_time = 0;
        if (null != recordingHandler) {
            recordingHandler.removeCallbacks(time_runnable);
            recordingHandler = null;
        }
        stopRecord_audio(true);
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setMessage("该设备暂不支持录音");
        builder.setTitle("出错啦");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 结束录制 true 强制清除所有视频相关痕迹 false 正常停止录制
     */
    private void stopRecord(boolean CompulsoryRemoval) {
        if (null != recordingHandler) {
            recordingHandler.removeCallbacks(time_runnable);
            recordingHandler = null;
        }
        isRecording = false;
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_2x);
        if (mMediaRecorder != null) {
            try {
                resetMediaRecorder();
                // 没超过3秒就删除录制所有数据
                if (CompulsoryRemoval) {
                    clearVideo();
                    Util.setVisibility(video_camera_restart, View.GONE);
                } else {
                    if (recordTime < VIDEO_LEAST_TIME) {
                        clearVideo();
                        Util.setVisibility(video_camera_restart, View.GONE);
                    } else {
                        CameraConfig.video_url = video_Path;
                        Util.setVisibility(video_camera_restart, View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                clearVideo();
                Util.setVisibility(video_camera_restart, View.GONE);
            }
        }
        showNextLayout();
    }

    /**
     * 清除痕迹
     */
    private void clearVideo() {
        if (!TextUtils.isEmpty(CameraConfig.video_url)) {
            File file = new File(CameraConfig.video_url);
            if (file.exists()) {
                file.delete();
            }
            CameraConfig.video_url = "";
        }
    }

    /**
     * 清除音频痕迹
     */
    private void clearAudio() {
        if (!TextUtils.isEmpty(CameraConfig.audio_url)) {
            File file = new File(CameraConfig.audio_url);
            if (file.exists()) {
                file.delete();
            }
            CameraConfig.audio_url = "";
        }
        CameraConfig.audio_time = 0;
    }

    // 录制音频
    private void startRecord_audio(int recordType) {
        if (TextUtils.isEmpty(audio_Path)) {
            audio_Path = audio_package_path + System.currentTimeMillis()
                    + ".aac";
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        if (recordType == AAC) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        recorder.setOutputFile(audio_Path);
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_recording);
        try {
            isRecording_audio = true;
            recordTime = 0;
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            recodErrorForAudio(e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            recodErrorForAudio(e);
        }
    }

    /**
     * 结束录制 音频 true 强制清除所有音频相关痕迹 false 正常停止录制
     */
    private void stopRecord_audio(boolean CompulsoryRemoval) {
        if (null != recordingHandler) {
            recordingHandler.removeCallbacks(time_runnable);
            recordingHandler = null;
        }
        isRecording_audio = false;
        update(0.0f);// 停止 波动 波动
        ThemeUtil.setImageResource(mContext, video_new_img_start,
                R.drawable.common_image_camera_video_2x);
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                // 没超过3秒就删除录制所有数据
                if (CompulsoryRemoval) {
                    clearAudio();
                    Util.setVisibility(audio_play_layout, View.GONE);
                } else {
                    if (recordTime < AUDIO_LEAST_TIME) {
                        clearAudio();
                        Util.setVisibility(audio_play_layout, View.GONE);
                    } else {
                        CameraConfig.audio_url = audio_Path;
                        CameraConfig.audio_time = recordTime;
                        audio_play_layout.setAudioUrl(CameraConfig.audio_url);
                        Util.setVisibility(audio_play_layout, View.VISIBLE);
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                clearAudio();
                Util.setVisibility(audio_play_layout, View.GONE);
            }
        }
        showNextLayout();
    }

    public class ViewPagerAdapter extends PagerAdapter {

        List<View> viewLists;

        public ViewPagerAdapter(List<View> lists) {
            viewLists = lists;
        }

        // 获得size
        @Override
        public int getCount() {
            return viewLists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 销毁Item
        @Override
        public void destroyItem(View view, int position, Object object) {
            ((ViewPager) view).removeView(viewLists.get(position));
        }

        // 实例化Item
        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView(viewLists.get(position), 0);
            return viewLists.get(position);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
        /*
         * 注释*** ray 2016年3月18日17:20:28 暂时做以下方式处理 监听当前手机的相对位置 手动添加旋转角度，使之预览为竖直状态
		 */
        int or = mOrientationListener.getCurrentNormalizedOrientation();
        int mDisplayOrientation = 0;
        Log.e("SZH", "onPictureTaken: " + or);
        switch (or) {//z这样拍摄的视频角度有问题
            case 0:
                if (cameraPosition == 1) {
                    mDisplayOrientation = 90;
                } else {
                    mDisplayOrientation = 270;
                }
                break;
            case 90:
                mDisplayOrientation = 0;// ok
                break;
            case 180:
                if (cameraPosition == 1) {
                    mDisplayOrientation = 90;
                } else {
                    mDisplayOrientation = 270;
                }
                break;
            case 270:
                mDisplayOrientation = 180;
                break;
        }
        Bitmap bitmap = BitmapCompressUtil.rotateBitmap(
                decodeSampledBitmapFromByte(paramArrayOfByte),
                mDisplayOrientation);
        new MySavePic().execute(bitmap);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
        resetMediaRecorder();
    }

    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    public static boolean getVideoQuality(String quality) {
        return "youtube".equals(quality) || "high".equals(quality);
    }

    /**
     * 设置camera显示取景画面,并预览
     *
     * @param holder
     */
    private void setStartPreview(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 释放Camera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 重置mMediaRecorder
     */
    private void resetMediaRecorder() {
        if (null != mMediaRecorder) {
            // 停止录像，释放camera
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            try {
                mMediaRecorder.stop();
                // 清除recorder配置
                mMediaRecorder.reset();
                // 释放recorder对象
                mMediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder = null;
        }
    }

    /**
     * @param bitmapBytes
     * @return 拍照生成图片
     */
    public static Bitmap decodeSampledBitmapFromByte(byte[] bitmapBytes) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0,
                bitmapBytes.length, options);
        options.inSampleSize = BitmapCompressUtil.calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeByteArray(bitmapBytes, 0,
                bitmapBytes.length, options);
    }

    private class OrientationListener extends OrientationEventListener {

        private int mCurrentNormalizedOrientation;

        public OrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation);
                Log.e("SZH", mCurrentNormalizedOrientation + "");
            }
        }

        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;//正常
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;//顺时针横屏
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;//逆时针横屏
            }

            throw new RuntimeException(
                    "The physics as we know them are no more. Watch out for anomalies.");
        }

        public int getCurrentNormalizedOrientation() {
            return mCurrentNormalizedOrientation;
        }
    }

    private int i = 0;

    // 保持图片到文件中
    protected class MySavePic extends AsyncTask<Bitmap, String, String> {
        Bitmap bitmap = null;
        String filepath;

        @Override
        protected String doInBackground(Bitmap... params) {
            bitmap = params[0];
            i++;
            filepath = base_file_path + new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
                    .format(new Date()) + i + ".jpg";

            File file = new File(filepath);
            try {
                FileOutputStream fout = new FileOutputStream(file.getPath());
                bitmap.compress(CompressFormat.JPEG, 100, fout);
                fout.flush();
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "Handler Fail";
            }
            return "Handler Success";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                addImages2Horlayout(filepath, Bimp.selectBitmap.size());
                ImageItem bean = new ImageItem();
                bean.setImagePath(filepath);
                Bimp.selectBitmap.add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            video_new_img_start.setEnabled(true);// 避免 连续点击
            camera_preview_next_ll.setEnabled(true);
            setStartPreview(mHolder);
        }
    }

    /**
     * 添加图片单张
     */
    private void addImages2Horlayout(String picPath, final int position) {
        if (TextUtils.isEmpty(picPath)) {
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.v_camera_img_layout, null);
        ImageView img = (ImageView) view.findViewById(R.id.camera_shot_pic);
        RelativeLayout.LayoutParams image_params = (LayoutParams) img
                .getLayoutParams();
        image_params.setMargins(Util.dip2px(6), 0, 0, 0);
        image_params.addRule(/* Gravity.CENTER */RelativeLayout.CENTER_VERTICAL,
                RelativeLayout.TRUE);
        img.setLayoutParams(image_params);
        ImageView imgDel = (ImageView) view
                .findViewById(R.id.camera_shot_pic_del);
        ImageLoaderUtil.loadingImg(mContext, new File(picPath), img,
                ImageLoaderUtil.loading_50);

        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (visit_with_filter) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
                        list.add(Bimp.selectBitmap.get(i).getImagePath());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("pics_list", list);
                    bundle.putInt("position", position);
                    bundle.putBoolean("clearContainer", false);
                    Go2Util.goTo(mContext,
                            Go2Util.join(sign, "HotShotImageViewer", null), "",
                            "", bundle);
                }
            }
        });

        imgDel.setVisibility(View.VISIBLE);
        imgDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bimp.selectBitmap.remove(position);
                addAllImages2Horlayout();
            }
        });
        camera_pics_layout.addView(view);
        Util.setVisibility(camera_pics_Horlayout, View.VISIBLE);
        scrollToEnd();
        Util.setVisibility(camera_preview_next_ll, View.VISIBLE);
    }

    /**
     * 添加图片单张
     */
    private void addAllImages2Horlayout() {
        camera_pics_layout.removeAllViews();

        if (Bimp.selectBitmap.size() > 0) {
            for (int i = 0, j = Bimp.selectBitmap.size(); i < j; i++) {
                addImages2Horlayout(Bimp.selectBitmap.get(i).getImagePath(), i);
            }
        } else {
            Util.setVisibility(camera_pics_Horlayout, View.GONE);
            showNextLayout();
        }
    }

    /**
     * > 6 的话滑动到最右边
     */
    private void scrollToEnd() {
        if (null != Bimp.selectBitmap && Bimp.selectBitmap.size() > 5) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                        int left = camera_pics_layout.getMeasuredWidth()
                                - camera_pics_Horlayout.getWidth();
                        if (left < 0) {
                            left = 0;
                        }
                        camera_pics_Horlayout.scrollTo(left, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    int bestWidth = 0, bestHeight = 0;

    /**
     * 初始化摄像头参数
     */
    private void initCameraParameters() {
        // 初始化摄像头参数
        mParameters = mCamera.getParameters();
        Size bestPreviewSize = null;
        Size bestPictureSize = null;
        Size bestVideoSize = null;
        float scale = (float) (Variable.HEIGHT * 1.0 / Variable.WIDTH);// 高宽比
        //参考  http://eyehere.net/2011/android-camera/  取得最合适的预览尺寸
        List<Size> supportedPreviewSizes
                = SupportedSizesReflect.getSupportedPreviewSizes(mParameters);
        List<Size> supportedPictureSizes
                = SupportedSizesReflect.getSupportedPictureSizes(mParameters);
        List<Size> supportedVideoSizes
                = SupportedSizesReflect.getSupportedPictureSizes(mParameters);
        if (null != supportedPreviewSizes && supportedPreviewSizes.size() > 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            bestPreviewSize = getOptimalPreviewSize(
                    supportedPreviewSizes,
                    display.getWidth(),
                    display.getHeight());
            Log.e("SZH", "bestPreviewSize:" + bestPreviewSize.width + "," + bestPreviewSize.height);
            mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        } else {
            mParameters.setPreviewSize(800, (int) (800 / scale));// 预览界面的 宽高
        }
        if (null != supportedPictureSizes && supportedPictureSizes.size() > 0) {
            //2.x
            bestPictureSize = supportedPictureSizes.get(0);
            int maxSize = 1280;
            if (maxSize > 0) {
                for (Size size : supportedPictureSizes) {
                    if (maxSize >= Math.max(size.width, size.height)) {
                        bestPictureSize = size;
                        break;
                    }
                }
            }
            mParameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        } else {
            mParameters.setPictureSize(800, (int) (800 / scale));
        }
        if (null != supportedVideoSizes && supportedVideoSizes.size() > 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            bestVideoSize = getOptimalPreviewSize(
                    supportedVideoSizes,
                    display.getWidth(),
                    display.getHeight());
            bestWidth = bestVideoSize.width;
            bestHeight = bestVideoSize.height;
        } else {
            bestWidth = 0;
            bestHeight = 0;
        }
        Log.e("SZH", "bestVideoSize:" + bestWidth + "," + bestHeight);
        if (Build.MODEL.equalsIgnoreCase("MI 6")) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            Log.e("SZH", "profile:" + profile.videoFrameWidth + "," + profile.videoFrameHeight);
            mParameters.setPreviewSize(profile.videoFrameHeight, profile.videoFrameWidth);
            mParameters.setPictureSize(profile.videoFrameHeight, profile.videoFrameWidth);
        }
//		// 初始化摄像头参数
//		mParameters = mCamera.getParameters();
//		try {
//			bestPreviewSize = determineBestPreviewSize(mParameters);
//			bestPictureSize = determineBestPictureSize(mParameters);
//		} catch (Exception e) {
//		}
//		float scale = (float) (Variable.HEIGHT * 1.0 / Variable.WIDTH);// 高宽比
//
//		// 三星S6 1440*1080
//		// 红米 800*600
//		if (bestPreviewSize == null) {
//			mParameters.setPreviewSize(800, (int) (800 / scale));// 预览界面的 宽高
//			mParameters.setPictureSize(800, (int) (800 / scale));
//		} else {
//			if ((int) (bestPreviewSize.width / scale) > bestPreviewSize.height) {
//				mParameters.setPreviewSize(
//						(int) (bestPreviewSize.height * scale),
//						bestPreviewSize.height);
//			} else {
//				mParameters.setPreviewSize(bestPreviewSize.width,
//						(int) (bestPreviewSize.width / scale));
//			}
//
//		}
////
//		// 设置照片分辨率，注意要在摄像头支持的范围内选择
//		// 三星S6 3984*2988
//		// 红米 3264*2448
//		if (bestPictureSize == null) {
//			mParameters.setPictureSize(800, (int) (800 / scale));
//		} else {
//			if ((int) (bestPictureSize.width / scale) > bestPictureSize.height) {
//				mParameters.setPictureSize(
//						(int) (bestPictureSize.height * scale),
//						bestPictureSize.height);
//			} else {
//				mParameters.setPictureSize(bestPictureSize.width,
//						(int) (bestPictureSize.width / scale));
//			}
//		}

        // 设置聚焦方式

//        if (cur_mode == 0) {
//            if (mParameters.getSupportedFocusModes().contains(
//                    Camera.Parameters.FOCUS_MODE_AUTO)) {
//                mParameters
//                        .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//        } else if (cur_mode == 1) {
//            if (mParameters.getSupportedFocusModes().contains(
//                    Camera.Parameters.FOCUS_MODE_AUTO)) {
//                mParameters
//                        .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//        }
        try {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.autoFocus(myAutoFocusCallback);
        } catch (Exception e) {

        }
        // 设置白平衡参数。
        String whiteBalance = mPreferences.getString(
                "pref_camera_whitebalance_key", "auto");
        if (isSupported(whiteBalance, mParameters.getSupportedWhiteBalance())) {
            mParameters.setWhiteBalance(whiteBalance);
        }

        // 参数设置颜色效果。
        String colorEffect = mPreferences.getString(
                "pref_camera_coloreffect_key", "none");
        if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
            mParameters.setColorEffect(colorEffect);
        }

        try {
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 预览的图片大小
    private Size determineBestPreviewSize(Camera.Parameters parameters) {

        return determineBestSize(parameters.getSupportedPreviewSizes(), 960);
    }

    // 得到图片的大小
    private Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), 1280);
    }

    /**
     * 选择合适的预览尺寸
     *
     * @param sizes 照相机分辨率
     * @author hudebo
     */
    private Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;
        Size size;
        if (sizes != null && sizes.size() > 0) {
            int numOfSizes = sizes.size();
            for (int i = 0; i < numOfSizes; i++) {
                size = sizes.get(i);
                boolean isDesireRatio = (size.width / 4) == (size.height / 3);
                boolean isBetterSize = (bestSize == null)
                        || size.width > bestSize.width;

                if (isDesireRatio && isBetterSize) {
                    bestSize = size;
                }
            }
            if (bestSize == null) {
                return sizes.get(sizes.size() - 1);
            }
        }
        return bestSize;
    }

    /**
     * 切换摄像头
     */
    @SuppressLint("NewApi")
    private void switchCamera() {
        if (isRecording) {
            return;
        }
        // 切换前后摄像头
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            // 得到每一个摄像头的信息
            if (cameraPosition == 1) {
                // 现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // 开启前置
                    // CAMERA_FACING_BACK后置
                    // 前置摄像头时必须关闭闪光灯，不然会报错
                    if (mParameters != null) {
                        if (mParameters.getFlashMode() != null
                                && mParameters.getFlashMode().equals(
                                Parameters.FLASH_MODE_ON)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                            ThemeUtil.setImageResource(mContext,
                                    camera_preview_flash,
                                    R.drawable.common_image_camera_flash_2x);
                        }
                    }

                    // 释放Camera
                    releaseCamera();
                    // 打开当前选中的摄像头
                    mCamera = Camera.open(i);
                    initCameraParameters();
                    if (isLandscape) {
                        mCamera.setDisplayOrientation(0);
                    } else {
                        mCamera.setDisplayOrientation(90);
                    }
                    mCamera.lock();
                    // 通过surfaceview显示取景画面
                    setStartPreview(mHolder);
                    cameraPosition = 0;
                    break;
                }
            } else {
                // 现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    // 释放Camera
                    releaseCamera();
                    // 打开当前选中的摄像头
                    mCamera = Camera.open(i);// 开后置
                    initCameraParameters();
                    if (isLandscape) {
                        mCamera.setDisplayOrientation(0);
                    } else {
                        mCamera.setDisplayOrientation(90);
                    }
                    mCamera.lock();
                    // 通过surfaceview显示取景画面
                    setStartPreview(mHolder);
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    /**
     * @param position
     * @return 返回格式化后的时间
     */
    private String transPosition(long position) {
        long sec = position;
        long min = sec / 60;
        if (sec < 60) {// 60s之内
            if (sec < 10) {
                return "00:00:" + "0" + sec;
            }
            return "00:00:" + sec;
        } else if (sec == 60) {
            return "00:01:" + "00";
        } else if (min < 60) {// 1小时之内
            if ((sec % 60) < 10) {
                if (min < 10) {
                    return "00:" + "0" + min + ":0" + (sec % 60);
                } else {
                    return "00:" + min + ":0" + (sec % 60);
                }
            } else {
                if (min < 10) {
                    return "00:" + "0" + min + ":" + (sec % 60);
                } else {
                    return "00:" + min + ":" + (sec % 60);
                }
            }
        }
        return "00:00:00";
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                cancel();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 跳转到图片选择页面
     *
     * @param can_choose_count
     */
    private void goCameraSelection(int can_choose_count) {
        Bundle bundle = new Bundle();
        bundle.putInt(CameraConfig.camera_image_max_num, can_choose_count);
        bundle.putString(CameraConfig.camera_next_className, next_className);// 类名
        Go2Util.goTo(mContext, Go2Util.join(sign, "CameraSelection", null), "",
                "", bundle);
    }

    /**
     * 进行下一步操作,如果没有下一页面，则发送照片选择广播
     */
    private void goNext() {
        if (!TextUtils.isEmpty(CameraConfig.video_url)
                || Bimp.selectBitmap.size() > 0
                || !TextUtils.isEmpty(CameraConfig.audio_url)) {
            if (TextUtils.isEmpty(next_className)) {
                Intent intent = new Intent(CameraConfig.camera_broadcast_action);
                sendBroadcast(intent);
            } else {
                Go2Util.goTo(mContext,
                        Go2Util.join(sign, next_className, null), "", "", null);
            }
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.e("SZH", "error:" + what);

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }

    //===============2016年4月20日16:58:15  以下为 获取 最合适的预览尺寸的类和方法
    public static class SupportedSizesReflect {
        private static Method Parameters_getSupportedPreviewSizes = null;
        private static Method Parameters_getSupportedPictureSizes = null;
        private static Method Parameters_getSupportedVideoSizes = null;

        static {
            initCompatibility();
        }

        ;

        private static void initCompatibility() {
            try {
                Parameters_getSupportedPreviewSizes = Camera.Parameters.class.getMethod(
                        "getSupportedPreviewSizes", new Class[]{});

                Parameters_getSupportedPictureSizes = Camera.Parameters.class.getMethod(
                        "getSupportedPictureSizes", new Class[]{});

                Parameters_getSupportedVideoSizes = Camera.Parameters.class.getMethod(
                        "getSupportedVideoSizes", new Class[]{});

            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
                Parameters_getSupportedPreviewSizes = Parameters_getSupportedPictureSizes = Parameters_getSupportedVideoSizes = null;
            }
        }

        /**
         * Android 2.1之后有效
         *
         * @param p
         * @return Android1.x返回null
         */
        public static List<Size> getSupportedPreviewSizes(Camera.Parameters p) {
            return getSupportedSizes(p, Parameters_getSupportedPreviewSizes);
        }

        public static List<Size> getSupportedPictureSizes(Camera.Parameters p) {
            return getSupportedSizes(p, Parameters_getSupportedPictureSizes);
        }

        public static List<Size> getSupportedVideoSizes(Camera.Parameters p) {
            return getSupportedSizes(p, Parameters_getSupportedVideoSizes);
        }

        @SuppressWarnings("unchecked")
        private static List<Size> getSupportedSizes(Camera.Parameters p, Method method) {
            try {
                if (method != null) {
                    return (List<Size>) method.invoke(p);
                } else {
                    return null;
                }
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new RuntimeException(ite);
                }
            } catch (IllegalAccessException ie) {
                return null;
            }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 点击聚焦
     *
     * @param autoFocusCallback
     * @return
     */
    public boolean autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        if (null != mCamera) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> supportMode = parameters.getSupportedFocusModes();
            if (supportMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                String focusMode = parameters.getFocusMode();
                if (!Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(parameters);
                }
                if (autoFocusCallback != null) {
                    mCamera.autoFocus(autoFocusCallback);
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public void goBack() {
        if (isLandscape) {
            finish();
        } else {
            super.goBack();
        }
    }
}
