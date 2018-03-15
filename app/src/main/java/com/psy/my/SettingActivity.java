/**
 * SettingActivity.java
 *设置界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.psy.util.Common;
import com.psy.util.FileManager;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import camerakit.TakePhoto;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.xdu.poscam.R;
public class SettingActivity extends Activity implements View.OnClickListener {
    private ImageView imgBack, backCamera, myHead;
    private TextView myName, myPhone, myCache;
    private LinearLayout changePw, changeName, changePhone, changeHeadPic, clearCache, logoutll;
    private String picpath;
    private Bitmap bitmap;
    private boolean isPicChange;
    private String localPath;
    private String fileSizeString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(SettingActivity.this);
        this.setContentView(R.layout.setting);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        myHead = (ImageView) findViewById(R.id.myHead);
        backCamera = (ImageView) findViewById(R.id.btnBackCam);
        changePw = (LinearLayout) findViewById(R.id.myChangePw);
        changeName = (LinearLayout) findViewById(R.id.changeNamell);
        changePhone = (LinearLayout) findViewById(R.id.changePhonell);
        changeHeadPic = (LinearLayout) findViewById(R.id.changeHeadll);
        clearCache = (LinearLayout) findViewById(R.id.clearCachell);
        logoutll = (LinearLayout) findViewById(R.id.logoutll);
        myName = (TextView) findViewById(R.id.myName);
        myPhone = (TextView) findViewById(R.id.myPhone);
        myCache = (TextView) findViewById(R.id.myCache);
        int flag = Common.isNetworkAvailable(SettingActivity.this);
        if (flag==0){
           Common.display(SettingActivity.this,"请开启手机网络");
        }else {
            imgBack.setOnClickListener(this);
            backCamera.setOnClickListener(this);
            changePw.setOnClickListener(this);
            changeName.setOnClickListener(this);
            changeHeadPic.setOnClickListener(this);
            changePhone.setOnClickListener(this);
            clearCache.setOnClickListener(this);
            logoutll.setOnClickListener(this);
            if (Common.userPose != null) {
                myName.setText(Common.userPose.getUserName());
                myPhone.setText(Common.userPose.getUserphone().
                        replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
            Intent intent = getIntent();
            localPath = intent.getStringExtra("image_path");
            if (localPath != null) {
                bitmap = BitmapFactory.decodeFile(localPath);
                myHead.setImageBitmap(bitmap);
            } else myHead.setImageResource(R.drawable.userphoto);
            fileSizeString = FileManager
                    .fileSizeCal(new File(Common.FULL_IMG_CACHE_PATH));
            myCache.setText(fileSizeString);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, UserActivity.class);
            finish();
            SettingActivity.this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        if (view == imgBack) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, UserActivity.class);
            finish();
            SettingActivity.this.startActivity(intent);
        }
        if (view == backCamera) {
            //new DefineCameraBasePose().showSample(this);
            startActivity(new Intent().setClass(SettingActivity.this, TakePhoto.class));
            finish();
        }
        if (view == clearCache) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
            builder1.setMessage("确定清除缓存？");
            builder1.setTitle("提示");
            builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (FileManager.delAllFile(Common.FULL_IMG_CACHE_PATH)) {
                        Common.display(SettingActivity.this, "缓存已清空");
                    } else
                        Common.display(SettingActivity.this, "清空缓存失败");
                    fileSizeString = FileManager
                            .fileSizeCal(new File(Common.FULL_IMG_CACHE_PATH));
                    myCache.setText(fileSizeString);
                    dialog.dismiss();
                }
            });
            builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder1.create().show();
        }
        if (view == changeHeadPic) {
            //配置功能
            FunctionConfig functionConfig = new FunctionConfig.Builder()
                    .setEnableCrop(true)
                    .setEnableRotate(true)
                    .setCropSquare(true)
                    .setEnablePreview(true)
                    .setEnableEdit(true)//编辑功能
                    .setEnableCrop(true)//裁剪功能
                    .setEnableCamera(true)//相机功能
                    .setForceCrop(true)
                    .setForceCropEdit(true)
                    .build();
            GalleryFinal.openGallerySingle(1, functionConfig, mOnHanlderResultCallback);
        }
        if (view == changePw) {
            Common.display(SettingActivity.this, "敬请期待");
        }
        if (view == changePhone) {
            Common.display(SettingActivity.this, "敬请期待");
        }
        if (view == logoutll) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
            builder1.setMessage("确定退出当前账号？");
            builder1.setTitle("提示");
            builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    finish();
                    intent.putExtra("extra", "logout");
                    intent.setClass(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder1.create().show();
        }
    }
    public String postData(String url, String name, String picurl) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("username", name);
        HashMap<String, String> fileHM = new HashMap<>();
        fileHM.put("taskpic_url", picurl);
        String result = HttpHelper.postData(url, paramHM, fileHM);
        return result;
    }
    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            Log.v("onHanlderSuccess", "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            for (PhotoInfo info : resultList) {
                switch (reqeustCode) {
                    case 1:
                        picpath = info.getPhotoPath();
                        bitmap = BitmapFactory.decodeFile(info.getPhotoPath());
                        myHead.setImageBitmap(bitmap);
                        isPicChange = true;

                        try {

                            if (picpath != null) {
                                String json =
                                        postData(URL.UPDATE_PIC_BY_NAME_URL, Common.userPose.getUserName(), picpath);
                                if (HttpHelper.getCode(json) == 100) {
                                    Common.display(SettingActivity.this, "修改成功");
                                } else {
                                    if (localPath != null) {
                                        bitmap = BitmapFactory.decodeFile(localPath);
                                        myHead.setImageBitmap(bitmap);
                                    } else {
                                        myHead.setImageResource(R.drawable.userphoto);
                                        Common.display(SettingActivity.this, "服务器错误，请稍后再试");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Common.display(SettingActivity.this, "服务器错误，请稍后再试");
                            if (localPath != null) {
                                bitmap = BitmapFactory.decodeFile(localPath);
                                myHead.setImageBitmap(bitmap);
                            } else {
                                myHead.setImageResource(R.drawable.userphoto);
                            }

                        }
                        break;
                }
            }
        }
        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(SettingActivity.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();
        }
    };
}

