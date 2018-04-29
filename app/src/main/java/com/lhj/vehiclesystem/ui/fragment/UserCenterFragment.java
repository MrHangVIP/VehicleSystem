package com.lhj.vehiclesystem.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseApplication;
import com.lhj.vehiclesystem.BaseFragment;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.BusinessItem;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.UserItem;
import com.lhj.vehiclesystem.bean.VehicleRecordItem;
import com.lhj.vehiclesystem.business.ApplyListActivity;
import com.lhj.vehiclesystem.business.PublishVehiclesActivity;
import com.lhj.vehiclesystem.business.UseListActivity;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.model.UploadImageModel;
import com.lhj.vehiclesystem.ui.activity.LoginActivity;
import com.lhj.vehiclesystem.ui.activity.MyApplyRecordActivity;
import com.lhj.vehiclesystem.ui.activity.MyUseRecordActivity;
import com.lhj.vehiclesystem.ui.activity.SettingActivity;
import com.lhj.vehiclesystem.ui.activity.UserInfoActivity;
import com.lhj.vehiclesystem.ui.widget.CircleImageView;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.FileUtils;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SelectPhotoTools;
import com.lhj.vehiclesystem.util.SpfUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Songzhihang on 2017/10/6.
 * 用户中心fragment
 */
public class UserCenterFragment extends BaseFragment {
    private static final String TAG = "UserCenterFragment";
    private ImageView ful_iv_setting;
    private CircleImageView ful_civ_head;
    private TextView ful_tv_nickname;
    private LinearLayout fcl_ll_star;
    private LinearLayout fcl_ll_money;
    private LinearLayout fcl_ll_all_ticket;
    private LinearLayout fcl_ll_all_sell;
    private LinearLayout ful_ll_second;
    private int type;
    private TextView ful_tv_businessId;
    private TextView fcl_tv_first;
    private TextView fcl_tv_second;
    private String first_str = "\n申请列表";
    private String second_str = "\n使用中";
    private String user_first_str = "\n我的申请";
    private String user_second_str = "\n我的用车记录";
    private Uri photoUri;
    private String imageUrl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.IMAGE_UPLOAD_OK) {
                ResultItem object = (ResultItem) msg.obj;
                try {
                    JSONArray jsonArray = new JSONArray(object.getData());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imageUrl = new Gson().fromJson(jsonArray.get(i).toString(), String.class);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MyUtil.MyLogE(TAG, e.toString());
                }
                if (type == Constant.TYPE_BISSINESS) {
                    BaseApplication.getAPPInstance().getBusinessUser().setLogo(imageUrl);
                    updateLogo();
                } else {
                    BaseApplication.getAPPInstance().getmUser().setHeadUrl(imageUrl);
                    updateHeadUrl();
                }
            }
            if (msg.what == Constant.IMAGE_UPLOAD_FAIL) {
                ProgressDialogUtil.dismissProgressdialog();
                toast("网络异常");
            }

        }
    };

    @Override
    protected View getLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_usercenter_layout, container, false);
    }

    @Override
    protected void initView(View view) {
        ful_iv_setting = (ImageView) contentView.findViewById(R.id.ful_iv_setting);
        ful_civ_head = (CircleImageView) contentView.findViewById(R.id.ful_civ_head);
        ful_tv_nickname = (TextView) contentView.findViewById(R.id.ful_tv_nickname);
        ful_tv_businessId = (TextView) contentView.findViewById(R.id.ful_tv_businessId);
        fcl_ll_money = (LinearLayout) contentView.findViewById(R.id.fcl_ll_money);
        fcl_ll_star = (LinearLayout) contentView.findViewById(R.id.fcl_ll_star);
        ful_ll_second = (LinearLayout) contentView.findViewById(R.id.ful_ll_second);
        fcl_tv_first = (TextView) contentView.findViewById(R.id.fcl_tv_first);
        fcl_tv_second = (TextView) contentView.findViewById(R.id.fcl_tv_second);
        fcl_ll_all_ticket = (LinearLayout) contentView.findViewById(R.id.fcl_ll_all_ticket);
        fcl_ll_all_sell = (LinearLayout) contentView.findViewById(R.id.fcl_ll_all_sell);
    }

    @Override
    protected void initData() {
        type = SpfUtil.getInt(Constant.LOGIN_TYPE, 0);
        if (type == Constant.TYPE_BISSINESS) {
            ful_ll_second.setVisibility(View.GONE);
            ful_tv_businessId.setVisibility(View.VISIBLE);
            BusinessItem businessItem = BaseApplication.getAPPInstance().getBusinessUser();
            if (businessItem == null) {
                goLogin();
                return;
            }
            Glide.with(context)
                    .load(Constant.IMAGE_URL+businessItem.getLogo())
                    .placeholder(R.drawable.icon_default_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ful_civ_head);
            ful_tv_businessId.setText("企业号:" + businessItem.getBusinessId());
            ful_tv_nickname.setText(businessItem.getCompanyname());
            getApplyList("0", fcl_tv_first, first_str);
            getApplyList("1", fcl_tv_second, second_str);
        } else {
            UserItem user = BaseApplication.getAPPInstance().getmUser();
            if (user == null) {
                goLogin();
                return;
            }
            Glide.with(context)
                    .load(Constant.IMAGE_URL+user.getHeadUrl())
                    .placeholder(R.drawable.icon_default_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ful_civ_head);
            ful_tv_nickname.setText(user.getTrueName());
            getApplyList(fcl_tv_first, fcl_tv_second);
        }
    }

    @Override
    protected void initEvent() {
        ful_civ_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });
        ful_tv_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToNext(UserInfoActivity.class);
            }
        });
        ful_iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToNext(SettingActivity.class);
            }
        });

        fcl_ll_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        fcl_ll_all_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToNext(PublishVehiclesActivity.class);
            }
        });

        fcl_tv_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == Constant.TYPE_BISSINESS) {
                    jumpToNext(ApplyListActivity.class);
                } else {
                    jumpToNext(MyApplyRecordActivity.class);
                }
            }
        });
        fcl_tv_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == Constant.TYPE_BISSINESS) {
                    jumpToNext(UseListActivity.class);
                } else {
                    jumpToNext(MyUseRecordActivity.class);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void goLogin() {
        jumpToNext(LoginActivity.class);
        getActivity().finish();
    }

    /**
     * 获取申请列表数据
     */
    private void getApplyList(String state, final TextView view, final String text) {
        Map<String, String> params = new HashMap<>();
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("state", state);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        ProgressDialogUtil.showProgressDialog(context, false);
        httpHelp.httpRequest("post", Constant.GET_VEHICLE_RECORDS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if ("fail".equals(object.getResult())) {
                    view.setText(0 + text);
                    return;
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(object.getData());
                    if (jsonArray != null) {
                        view.setText(jsonArray.length() + text);
                    } else {
                        view.setText(0 + text);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setText(0 + text);
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    private void getApplyList(final TextView view1, final TextView view2) {
        Map<String, String> params = new HashMap<>();
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("userId", BaseApplication.getAPPInstance().getmUser().getUserId() + "");
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        ProgressDialogUtil.showProgressDialog(context, false);
        httpHelp.httpRequest("post", Constant.GET_VEHICLE_RECORDS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if ("fail".equals(object.getResult())) {
                    view1.setText(0 + user_first_str);
                    view2.setText(0 + user_second_str);
                    return;
                }
                JSONArray jsonArray = null;
                int first = 0;
                int second = 0;
                try {
                    jsonArray = new JSONArray(object.getData());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VehicleRecordItem vehicleRecordItem = (new Gson()).fromJson(jsonArray.get(i).toString(), VehicleRecordItem.class);
                            String state = vehicleRecordItem.getState();
                            if (TextUtils.equals(state, "1") || TextUtils.equals(state, "3")) {
                                second++;
                            }
                            if (TextUtils.equals(state, "0") || TextUtils.equals(state, "4")) {
                                if (second == 0) {//上次的申请
                                    first = 1;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    view1.setText(first + user_first_str);
                    view2.setText(second + user_second_str);
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    /**
     * 点击头像按钮
     */
    private void changePhoto() {
        if (!FileUtils.isSdCardExist()) {
            toast("没有找到SD卡，请检查SD卡是否存在");
            return;
        }
        try {
            photoUri = FileUtils.getUriByFileDirAndFileName(getActivity(),Constant.SystemPicture.SAVE_DIRECTORY, Constant.SystemPicture.SAVE_PIC_NAME);
        } catch (IOException e) {
            toast("创建文件失败");
            return;
        }
        SelectPhotoTools.openDialog(this, photoUri);
    }

    private void imageUpload(String path) {
        ProgressDialogUtil.showProgressDialog(context, true);
        List<String> pathList = new ArrayList<String>();
        pathList.add(path);
        UploadImageModel uploadImageModel = new UploadImageModel(pathList, mHandler);
        uploadImageModel.imageUpload();
    }

    private void updateHeadUrl() {
        Map<String, String> params = new HashMap<>();
        params.put("token", SpfUtil.getString(Constant.TOKEN, ""));
        params.put("userPhone", SpfUtil.getString(Constant.LOGIN_USERPHONE, ""));
        params.put("businessid", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("headUrl", imageUrl);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("", Constant.UPDATE_HEAD_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if ("fail".equals(object.getResult())) {
                    if ("token error".equals(object.getData())) {
                        toast("token失效,请重新登录");
                        tokenError();
                    }
                } else {
                    MyUtil.MyLogE(TAG, "插入成功！");
                    toast("修改成功");
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                MyUtil.MyLogE(TAG, "连接失败");
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    private void updateLogo() {
        Map<String, String> params = new HashMap<>();
        params.put("email", SpfUtil.getString(Constant.BISINESS_EMAIL, ""));
        params.put("businessid", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("logo", imageUrl);
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        httpHelp.httpRequest("", Constant.UPDATE_BUSINESS_LOGO, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if ("fail".equals(object.getResult())) {
                    toast("更新失败");
                } else {
                    MyUtil.MyLogE(TAG, "插入成功！");
                    toast("修改成功");
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                MyUtil.MyLogE(TAG, "连接失败");
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.SystemPicture.PHOTO_REQUEST_TAKEPHOTO: // 拍照
                SelectPhotoTools.startPhotoZoom(null, UserCenterFragment.this, photoUri, 300);
                break;
            case Constant.SystemPicture.PHOTO_REQUEST_GALLERY://相册获取
                if (data == null)
                    return;
                SelectPhotoTools.startPhotoZoom(null, UserCenterFragment.this, data.getData(), 300);
                break;
            case Constant.SystemPicture.PHOTO_REQUEST_CUT:  //接收处理返回的图片结果
                if (data == null)
                    return;
                File file = FileUtils.getFileByUri(getActivity(), photoUri);
                MyUtil.MyLogE(TAG, file.toString());
                Bitmap bit = data.getExtras().getParcelable("data");
                ful_civ_head.setImageBitmap(bit);
                try {
                    bit.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    MyUtil.MyLogE(TAG, e.toString());
                }
                imageUpload(file.getAbsolutePath());
                break;
        }
    }

}
