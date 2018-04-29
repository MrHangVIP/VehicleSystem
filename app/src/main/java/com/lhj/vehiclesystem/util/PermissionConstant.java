package com.lhj.vehiclesystem.util;

import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.R;


/**
 * @author lixurong
 * @descripton 运行时权限常量
 * @date 2017/11/6
 */
public class PermissionConstant {
    public static final int REQUEST_CAMERA = 0x0000;
    public static final int REQUEST_AUDIO = 0x0001;
    public static final int REQUEST_PHONE = 0x0003;
    public static final int REQUEST_CALL = 0x0004;
    public static final int REQUEST_SEND_SMS = 0x0005;
    public static final int REQUEST_RECEIVE_SMS = 0x0006;
    public static final int REQUEST_STORAGE = 0x0007;
    public static final int REQUEST_LOCATION = 0x0008;
    public static final int REQUEST_CALENDAR = 0x0009;
    public static final int REQUEST_CONTACTS = 0x00010;
    public static final int REQUEST_WEB = 0x0011;
    public static final int REQUEST_CAMERAAUDIO = 0x0012;

    public static String getPermissionText(int requestCode) {
        String permissionText = "";
        switch (requestCode) {
            case REQUEST_CAMERA:
                permissionText = getString(R.string.permission_camera);
                break;
            case REQUEST_AUDIO:
                permissionText = getString(R.string.permission_audio);
                break;
            case REQUEST_PHONE:
                permissionText = getString(R.string.permission_phone);
                break;
            case REQUEST_CALL:
                permissionText = getString(R.string.permission_call);
                break;
            case REQUEST_SEND_SMS:
                permissionText = getString(R.string.permission_msg);
                break;
            case REQUEST_RECEIVE_SMS:
                permissionText = getString(R.string.permission_sms);
                break;
            case REQUEST_STORAGE:
                permissionText = getString(R.string.permission_storge);
                break;
            case REQUEST_LOCATION:
                permissionText = getString(R.string.permission_location);
                break;
            case REQUEST_CALENDAR:
                permissionText = getString(R.string.permission_calendar);
                break;
            case REQUEST_CONTACTS:
                permissionText = getString(R.string.permission_contacts);
                break;
            case REQUEST_WEB:
                permissionText = getString(R.string.permission_web);
                break;
            case REQUEST_CAMERAAUDIO:
                permissionText = getString(R.string.permission_camera_audio);
                break;
            default:
//                permissionText = getString(R.string.permission_default);
                break;
        }
        return permissionText;
    }
    
    private static String getString(int id){
       return BaseApplication.getAPPInstance().getString(id);
    }
}
