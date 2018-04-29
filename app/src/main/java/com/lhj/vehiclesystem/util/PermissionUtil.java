package com.lhj.vehiclesystem.util;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.lhj.vehiclesystem.R;


/**
 * 6.0权限工具类
 * <p/>
 * Created by yangjian on 16/4/19.
 */
public class PermissionUtil {

    public static final int REQUEST_CAMERA = 0x0000;
    public static final int REQUEST_AUDIO = 0x0001;
    public static final int REQUEST_SMS = 0x0002;
    public static final int REQUEST_PHONE = 0x0003;

    public static void requestPermission(Activity activity,
                                         String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }


    /**
     * 直接拨打电话
     *
     * @param activity
     * @param phone
     * @param requestCode
     */
    public static void Call(Activity activity, String phone, int requestCode) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, requestCode);
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.permission_call), Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, requestCode);
            }
            return;
        } else {
            Uri uri = Uri.parse("tel:" + phone);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            activity.startActivity(intent);
        }

    }

    /**
     * 直接发送短信
     *
     * @param activity
     * @param phone
     * @param msg
     * @param requestCode
     */
    public static void SendSms(final Activity activity, String phone, String msg, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, requestCode);
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.permission_sms), Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, requestCode);
            }
        } else {
//            String SENT_SMS_ACTION = "SENT_SMS_ACTION";
//            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent pi = PendingIntent.getActivity(activity, 0, new Intent(), 0);
//            activity.registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context _context, Intent _intent) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
//                            Toast.makeText(activity,
//                                    "短信发送成功", Toast.LENGTH_SHORT)
//                                    .show();
//                            break;
//                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                            Toast.makeText(activity,
//                                    "1", Toast.LENGTH_SHORT)
//                                    .show();
//                            break;
//                        case SmsManager.RESULT_ERROR_RADIO_OFF:
//                            Toast.makeText(activity,
//                                    "2", Toast.LENGTH_SHORT)
//                                    .show();
//                            break;
//                        case SmsManager.RESULT_ERROR_NULL_PDU:
//                            Toast.makeText(activity,
//                                    "3", Toast.LENGTH_SHORT)
//                                    .show();
//                            break;
//                    }
//                }
//            }, new IntentFilter(SENT_SMS_ACTION));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, msg, pi, null);
        }

    }

    /**
     * 检测拍照权限
     *
     * @param context
     * @return
     */
    public static boolean Camera(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                !(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 拍照授权
     *
     * @param activity
     * @param requestCode
     */
    public static void CameraPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, R.string.permission_camera, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                !(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        } else if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
        } else if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }


    /**
     * 检测存储权限
     * READ,WRITE
     *
     * @return
     */
    public static boolean storage(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 授权存储权限
     *
     * @param activity
     * @param requestCode
     */
    public static void storagePermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, R.string.permission_storge, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    /**
     * 检测位置权限
     * FINE,COARSE
     *
     * @param context
     * @return
     */
    public static boolean Location(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 位置授权
     *
     * @param activity
     * @param requestCode
     */
    public static void LocationPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, R.string.permission_location, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
    }

    /**
     * 录音上传
     *
     * @param context
     */
    public static boolean Audio(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) ||
                !(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 录音授权
     *
     * @param activity
     * @param requestCode
     */
    public static void AudioPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, R.string.permission_audio, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) &&
                !(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        } else if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
        } else if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    /**
     * 检测短信权限
     * READ,WRITE,RECEIVE
     *
     * @param context
     * @return
     */
    public static boolean SMS(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 短信授权
     *
     * @param activity
     * @param requestCode
     */
    public static void SMSPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS)) {
            Toast.makeText(activity, R.string.permission_sms, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, requestCode);
        }
    }

    /**
     * 检测日历权限
     * READ,WRITE
     *
     * @param context
     * @return
     */
    public static boolean Calendar(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 日历授权
     *
     * @param activity
     * @param requestCode
     */
    public static void CalendarPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALENDAR)) {
            Toast.makeText(activity, R.string.permission_calendar, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, requestCode);
        }
    }

    /**
     * 检测联系人权限
     * READ,WRITE,ACCOUNTS
     *
     * @param context
     * @return
     */
    public static boolean Contacts(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 联系人授权
     *
     * @param activity
     * @param requestCode
     */
    public static void ContactsPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity, R.string.permission_contacts, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
        }
    }

    /**
     * 检测电话权限
     * CALL_LOG,
     * PHONE_STATE,
     * CALL_PHONE
     *
     * @param context
     * @return
     */
    public static boolean Phone(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 电话授权
     *
     * @param activity
     * @param requestCode
     */
    public static void PhonePermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            Toast.makeText(activity, R.string.permission_phone, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
        }
    }

    /**
     * 检测传感器权限
     * BODY_SENSOR
     *
     * @param context
     * @return
     */
    public static boolean Sensor(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    /**
     * 传感器授权
     *
     * @param activity
     * @param requestCode
     */
    public static void SensorPermission(Activity activity, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BODY_SENSORS)) {
            Toast.makeText(activity, R.string.permission_sensor, Toast.LENGTH_SHORT).show();
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BODY_SENSORS}, requestCode);
        }
    }

    /**
     * 进入程序检测权限
     * 读取手机状态，读sd卡，相机
     *
     * @param context
     * @return
     */
    public static boolean check(Context context) {
        if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 授权
     *
     * @param activity
     * @param requestCode
     */
    public static void getPermission(Activity activity, int requestCode) {
        StringBuilder builder = new StringBuilder();
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            builder.append(Manifest.permission.CAMERA).append(":");
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            builder.append(Manifest.permission.READ_PHONE_STATE).append(":");
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            builder.append(Manifest.permission.WRITE_EXTERNAL_STORAGE).append(":");
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            builder.append(Manifest.permission.ACCESS_FINE_LOCATION).append(":");
        }
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            builder.append(Manifest.permission.READ_EXTERNAL_STORAGE).append(":");
        }
        String promession = builder.toString();
        if (TextUtils.isEmpty(promession)) return;
        String[] promessionArr = promession.substring(0, promession.length() - 1).split(":");
        ActivityCompat.requestPermissions(activity, promessionArr, requestCode);
    }

    public interface IPermissionCallBack {
        void permissionsGranted();

        void permissionsDenied();
    }

}
