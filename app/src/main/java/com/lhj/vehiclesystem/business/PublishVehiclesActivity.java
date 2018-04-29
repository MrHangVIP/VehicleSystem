package com.lhj.vehiclesystem.business;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lhj.vehiclesystem.BaseActivity;
import com.lhj.vehiclesystem.R;
import com.lhj.vehiclesystem.api.OkHttpHelp;
import com.lhj.vehiclesystem.bean.ResultItem;
import com.lhj.vehiclesystem.bean.TagItem;
import com.lhj.vehiclesystem.listener.ResponseListener;
import com.lhj.vehiclesystem.model.UploadImageModel;
import com.lhj.vehiclesystem.ui.widget.WheelViewDialog;
import com.lhj.vehiclesystem.util.Constant;
import com.lhj.vehiclesystem.util.FileUtils;
import com.lhj.vehiclesystem.util.MyUtil;
import com.lhj.vehiclesystem.util.ProgressDialogUtil;
import com.lhj.vehiclesystem.util.SelectPhotoTools;
import com.lhj.vehiclesystem.util.SpfUtil;
import com.lhj.vehiclesystem.util.StringUtils;
import com.lhj.vehiclesystem.util.WheelUtil;
import com.lhj.vehiclesystem.util.WheelViewDialogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布页面
 * Created by Songzhihang on 2018/3/11.
 */
public class PublishVehiclesActivity extends BaseActivity {
    private static final String TAG = "PublishVehiclesActivity";
    private static final int GET_TAGS_OK = 0x01;
    private static final int GET_TAGS_FAIL = 0x02;

    private List<String> titleDataList = new ArrayList<>();
    private List<String> dirverLevels = new ArrayList<>();
    private List<TagItem> tagItems = new ArrayList<>();
    private WheelViewDialog typeDialog;
    private WheelViewDialog dirverLevelsDialog;
    private ImageView aptl_iv_add;
    private EditText aptl_rt_brief;
    private EditText aptl_rt_title;
    private EditText aptl_rt_price;
    private EditText aptl_rt_identity;
    private TextView aptl_tv_level;
    private Uri photoUri;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_TAGS_OK:
                    showTypeSelectDialog();
                    break;
                case GET_TAGS_FAIL:
                    titleDataList.add("全部");
                    TagItem tagItem = new TagItem();
                    tagItem.setOrder(0);
                    tagItem.setTypeId(0);
                    tagItem.setValue("全部");
                    tagItems.add(tagItem);
                    break;
                case Constant.IMAGE_UPLOAD_OK:
                    ResultItem object = (ResultItem) msg.obj;
                    String headUrl = "";
                    try {
                        JSONArray jsonArray = new JSONArray(object.getData());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            headUrl = new Gson().fromJson(jsonArray.get(i).toString(), String.class);
                        }
                        submitAction(headUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MyUtil.MyLogE(TAG, e.toString());
                        ProgressDialogUtil.dismissProgressdialog();
                    }
                    break;

                case Constant.IMAGE_UPLOAD_FAIL:
                    ProgressDialogUtil.dismissProgressdialog();
                    toast("图片上传失败！，请重新发布");
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void setView() {
        setContentView(R.layout.activity_publish_vehicles_layout);
    }

    @Override
    protected void findViews() {
        aptl_rt_title = (EditText) findViewById(R.id.aptl_rt_title);
        aptl_rt_brief = (EditText) findViewById(R.id.aptl_rt_brief);
        aptl_rt_price = (EditText) findViewById(R.id.aptl_rt_price);
        aptl_rt_identity = (EditText) findViewById(R.id.aptl_rt_identity);
        aptl_tv_level = (TextView) findViewById(R.id.aptl_tv_level);
        aptl_iv_add = (ImageView) findViewById(R.id.aptl_iv_add);
    }

    @Override
    protected void initData() {
        getTags();
        toolbarTitle.setTextColor(Color.parseColor("#333333"));
        MyUtil.setCompoundDrawables(toolbarTitle, MyUtil.toDip(10), MyUtil.toDip(10), 2, R.drawable.icon_arrow_down);
        toolbarTitle.setCompoundDrawablePadding(MyUtil.toDip(5));
        toolbar.inflateMenu(R.menu.publish_submit_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                submitAction("");
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (photoUri != null) {
            return;
        }
    }

    @Override
    protected void setListener() {
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeSelectDialog();
            }
        });
        aptl_tv_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDriverLevels();
            }
        });

        aptl_iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });
    }

    private void showTypeSelectDialog() {
        //弹框提示
        if (typeDialog != null) {
            typeDialog.show();
            return;
        }
        final WheelUtil typeWheelUtil = new WheelUtil(mContext);
        typeWheelUtil.setDatas(titleDataList.toArray());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinkedHashMap<View, LinearLayout.LayoutParams> map = new LinkedHashMap<>();
        map.put(typeWheelUtil.getWheelView(), params);
        typeDialog = WheelViewDialogUtil.showWheelViewDialog(PublishVehiclesActivity.this, "类型选择", new WheelViewDialog.DialogSubmitListener() {
            @Override
            public void onSubmitClick(View v) {
                toolbarTitle.setText(typeWheelUtil.getItems()[typeWheelUtil.getWheelView().getCurrentItem()].toString());
                toolbarTitle.setTag(tagItems.get(typeWheelUtil.getWheelView().getCurrentItem()).getTypeId());
                typeDialog.dismiss();
            }
        }, map);
        typeDialog.setCancelable(false);
    }

    private void showDriverLevels() {
        //弹框提示
        if (dirverLevelsDialog != null) {
            dirverLevelsDialog.show();
            return;
        }
        dirverLevels.clear();
        //驾驶证种类：A1、A2、A3、B1、B2、C1、C2、C3、C4
        dirverLevels.add("A1");
        dirverLevels.add("A2");
        dirverLevels.add("A3");
        dirverLevels.add("B1");
        dirverLevels.add("B2");
        dirverLevels.add("C1");
        dirverLevels.add("C2");
        dirverLevels.add("C3");
        dirverLevels.add("C4");
        final WheelUtil typeWheelUtil = new WheelUtil(mContext);
        typeWheelUtil.setDatas(dirverLevels.toArray());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinkedHashMap<View, LinearLayout.LayoutParams> map = new LinkedHashMap<>();
        map.put(typeWheelUtil.getWheelView(), params);
        dirverLevelsDialog = WheelViewDialogUtil.showWheelViewDialog(PublishVehiclesActivity.this, "驾照等级选择", new WheelViewDialog.DialogSubmitListener() {
            @Override
            public void onSubmitClick(View v) {
                aptl_tv_level.setText(typeWheelUtil.getItems()[typeWheelUtil.getWheelView().getCurrentItem()].toString());
                dirverLevelsDialog.dismiss();
            }
        }, map);
        dirverLevelsDialog.setCancelable(false);
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
            photoUri = FileUtils.getUriByFileDirAndFileName(this,Constant.SystemPicture.SAVE_DIRECTORY, Constant.SystemPicture.SAVE_PIC_NAME);
        } catch (IOException e) {
            toast("创建文件失败");
            return;
        }
        SelectPhotoTools.openDialog(this, photoUri);
    }

    /**
     * 获取类型
     */
    private void getTags() {
        titleDataList.clear();
        tagItems.clear();
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        //登录操作
        Map<String, String> params = new HashMap<>();
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("get", Constant.GET_TAGS, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (object.getResult().equals("success")) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(object.getData());
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TagItem tagItem = (new Gson()).fromJson(jsonArray.get(i).toString(), TagItem.class);
                                titleDataList.add(tagItem.getValue());
                                tagItems.add(tagItem);
                            }
                        }
                        handler.sendEmptyMessage(GET_TAGS_OK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(GET_TAGS_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(GET_TAGS_FAIL);
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                handler.sendEmptyMessage(GET_TAGS_OK);
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    /**
     * 发表
     */
    private void submitAction(String imageUrl) {
        String name = aptl_rt_title.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("标题不能为空！");
            return;
        }
        String brief = aptl_rt_brief.getText().toString();
        if (TextUtils.isEmpty(brief)) {
            toast("简介不能为空！");
            return;
        }
        String price = aptl_rt_price.getText().toString();
        if (TextUtils.isEmpty(price)) {
            toast("价格不能为空");
            return;
        }
        String identity = aptl_rt_identity.getText().toString();
        if (StringUtils.isCar(identity)) {
            toast("请输入正确车牌号");
            return;
        }
        String level = aptl_tv_level.getText().toString();
        if (TextUtils.isEmpty(level)) {
            toast("准驾等级不能为空");
            return;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            File file = FileUtils.getFileByUri(PublishVehiclesActivity.this, photoUri);
            if (file == null) {
                toast("请添加图片");
                return;
            }
            imageUpload(file);
            return;
        }
        //发布
        Map<String, String> params = new HashMap<>();
        params.put("identity", identity);
        params.put("businessId", SpfUtil.getString(Constant.BISINESS_ID, ""));
        params.put("name", name);
        params.put("typeId",toolbarTitle.getTag().toString());
        params.put("biref", brief);
        params.put("indexpicurl", imageUrl);
        params.put("price", price);
        params.put("typeTitle", toolbarTitle.getText().toString());
        params.put("level", aptl_tv_level.getText().toString());
        OkHttpHelp<ResultItem> httpHelp = OkHttpHelp.getInstance();
        ProgressDialogUtil.showProgressDialog(mContext, false);
        httpHelp.httpRequest("post", Constant.ADD_VEHICLE_INFO_URL, params, new ResponseListener<ResultItem>() {
            @Override
            public void onSuccess(ResultItem object) {
                ProgressDialogUtil.dismissProgressdialog();
                if (!object.getResult().equals("fail")) {
                    toast("新车发布成功");
                    finish();
                } else {
                    if (TextUtils.equals("exist", object.getData())) {
                        toast("插入失败，车牌号已经存在");
                    } else {
                        toast("插入失败，服务器错误");
                    }
                }
            }

            @Override
            public void onFailed(String message) {
                ProgressDialogUtil.dismissProgressdialog();
                toast("网络连接失败");
            }

            @Override
            public Class<ResultItem> getEntityClass() {
                return ResultItem.class;
            }
        });
    }

    private void imageUpload(File path) {
        ProgressDialogUtil.showProgressDialog(mContext, true);
        List<String> pathList = new ArrayList<String>();
        pathList.add(path.getAbsolutePath());
        UploadImageModel uploadImageModel = new UploadImageModel(pathList, handler);
        uploadImageModel.imageUpload();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.SystemPicture.PHOTO_REQUEST_TAKEPHOTO: // 拍照
                break;
            case Constant.SystemPicture.PHOTO_REQUEST_GALLERY://相册获取
                if (data == null)
                    return;
                photoUri = data.getData();
                break;
        }
        File file = FileUtils.getFileByUri(PublishVehiclesActivity.this, photoUri);
        MyUtil.MyLogE(TAG, file.toString());
        Glide.with(mContext).load(file).into(aptl_iv_add);
    }
}
