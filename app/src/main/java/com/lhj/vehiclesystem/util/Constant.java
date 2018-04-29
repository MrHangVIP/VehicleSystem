package com.lhj.vehiclesystem.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Constant {

    //    public static final String DEFAULT_URL = "http://localhost:8080/LHJBiShe";
    public static final String DEFAULT_URL = "http://192.168.0.105:8080/LHJBiShe";

    public static final String IMAGE_URL = DEFAULT_URL + "/image/";

    public static final String LOGIN_BuSINESS_URL = "/GetBusinessInfo";
    public static final String REGIST_URL = "/RegistUser";
    public static final String LOGIN_URL = "/LoginUser";
    public static final String LOGIN_URL_BUSINESS = "/GetBusinessInfo";
    public static final String GET_USER_URL = "/GetUserInfo";
    public static final String UPDATE_USER_URL = "/UpdateUserInfo";
    public static final String IMAGE_UPLOAD_URL = "/ImageUpload";
    public static final String UPDATE_HEAD_URL = "/UpdateHeadUrl";
    public static final String UPDATE_BUSINESS_LOGO = "/UpdateBusinessLogo";

    public static final String GET_VEHICLE_RECORDS = "/GetVehicleRecordDatas";
    public static final String GET_TAGS = "/GetTags";
    public static final String ADD_VEHICLE_INFO_URL = "/AddVehicleInfo";
    public static final String GET_VEHICLE_DATA_URL = "/GetVehicleData";
    public static final String BUSINESSID_CHECK_URL = "/BusinessIdCheck";
    public static final String FINISH_USER_INFO_URL = "/FinishUserInfo";
    public static final String UPDATE_VEHICLE_STATUS_URL = "/UpdateVehicleStatus";
    public static final String UPDATE_VEHICLE_RECORDS_URL = "/UpdateVehicleRecordState";
    public static final String Add_RECORD_URL = "/AddRecordInfo";


    public static final int IMAGE_UPLOAD_OK = 0x1000;
    public static final int IMAGE_UPLOAD_FAIL = 0x1001;
    /**
     * 类型
     */
    public static final String OPRATION_TYPE = "opration_type";

    //-------本地sharedpreference的key开始----------

    public static final String IS_LOGIN = "is_login";

    public static final String IS_FIRST = "is_first";

    public static final String LOGIN_USERPHONE = "login_userphone";
    public static final String BISINESS_EMAIL = "business_emal";
    public static final String BISINESS_PASSWORD = "business_password";
    public static final String BISINESS_ID = "business_id";

    public static final String TOKEN = "token";

    public static final String LOGIN_TYPE = "login_type";
    public static final int TYPE_USER = 0x100;
    public static final int TYPE_BISSINESS = 0x101;

    //-------本地sharedpreference的key结束----------


    //分隔符
    public static final String MY_SPLIT_STR = "<&;&>";

    public static final String MacAddress = MyUtil.getMac();
    public static int WIDTH = 0;
    public static int HEIGHT = 0;
    public static float Density = 0;

    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        if (WIDTH == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            WIDTH = display.getWidth();
        }
        return WIDTH;
    }

    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        if (HEIGHT == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            HEIGHT = display.getHeight();
        }
        return HEIGHT;
    }

    //获取屏幕的分辨率
    public static float getScreenDensity(Context context) {
        if (Density == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
            Density = dm.density;
        }
        return Density;
    }

    /*****
     * 系统相册（包含有 照相、选择本地图片）
     */
    public class SystemPicture {
        /***
         * 保存到本地的目录
         */
        public static final String SAVE_DIRECTORY = "/TicketSeller";
        /***
         * 保存到本地图片的名字
         */
        public static final String SAVE_PIC_NAME = "head.jpeg";
        /***
         * 标记用户点击了从照相机获取图片  即拍照
         */
        public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
        /***
         * 标记用户点击了从图库中获取图片  即从相册中取
         */
        public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
        /***
         * 返回处理后的图片
         */
        public static final int PHOTO_REQUEST_CUT = 3;// 结果
    }

}
