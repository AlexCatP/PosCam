/**
 * PhotoProcessActivity.java
 *照片处理界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.psy.util.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;
public class PhotoProcessActivity extends Activity implements View.OnClickListener {
    private ImageView photoImageView, test;
    public static String path = "";
    private ImageView actionTextView;
    private ImageView btnAnalysis, btnUpload, btnEdit;
    public SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(PhotoProcessActivity.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.photo_activity_pose);
        initView();
        initData();
        sp = getSharedPreferences("flagSP_", 0);
        SharedPreferences.Editor editor1 = sp.edit();
        if (sp.getInt("flag", 0) == 0) {
            editor1.putInt("flag", 1);
            editor1.commit();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(PhotoProcessActivity.this);
            builder1.setMessage("前人种树，后人乘凉。" +
                    "\n您每上传一个pose，都将丰富我们的pose库，" +
                    "上传后您将获得P币奖励，是否上传此pose？");
            builder1.setTitle("提示");
            builder1.setPositiveButton("是", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (Common.bitmap != null) {
                        saveImageToGallery(PhotoProcessActivity.this, Common.bitmap);
                        Common.display(PhotoProcessActivity.this, "已保存到本地相册");
                    }
                    Intent intent = new Intent();
                    intent.setClass(PhotoProcessActivity.this, LoginActivity.class);
                    intent.putExtra("extra", "upload_" + path);
                    startActivity(intent);
                    finish();
                }
            });

            builder1.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder1.create().show();
        }
    }
    private void initData() {
        photoImageView.setImageBitmap(Common.bitmap);
        actionTextView.setOnClickListener(this);
        btnAnalysis.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        test.setOnClickListener(this);
    }
    private void initView() {
        test = (ImageView) findViewById(R.id.test);
        photoImageView = (ImageView) findViewById(R.id.photo_imageview);
        actionTextView = (ImageView) findViewById(R.id.photo_process_action);
        btnAnalysis = (ImageView) findViewById(R.id.btnAnalysis);
        btnUpload = (ImageView) findViewById(R.id.btnUpload);
        btnEdit = (ImageView) findViewById(R.id.btnEdit);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        path = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + fileName;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        scanPhoto(this, path);
    }
    public void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                TakePhoto.bmp1 = null;
                Common.bitmap = null;
                startActivity(new Intent().setClass(PhotoProcessActivity.this, TakePhoto.class));
                finish();
                break;
            case R.id.btnUpload:
                if (Common.bitmap != null) {
                    saveImageToGallery(PhotoProcessActivity.this, Common.bitmap);
                    Common.display(PhotoProcessActivity.this, "已保存到本地相册");
                }
                Intent inten = new Intent();
                inten.setClass(PhotoProcessActivity.this, LoginActivity.class);
                inten.putExtra("extra", "upload_" + path);
                startActivity(inten);
                finish();
                break;
            case R.id.photo_process_action:
                if (Common.bitmap != null) {
                    saveImageToGallery(PhotoProcessActivity.this, Common.bitmap);
                    Common.display(PhotoProcessActivity.this, "已保存到本地相册");
                }
                finish();
                startActivity(new Intent().setClass(PhotoProcessActivity.this, TakePhoto.class));
                break;

            case R.id.btnAnalysis:
                if (Common.bitmap != null) {
                    saveImageToGallery(PhotoProcessActivity.this, Common.bitmap);
                    Common.display(PhotoProcessActivity.this, "已保存到本地相册");
                }
                TakePhoto.pic_status = TakePhoto.PIC_ON_CAMERA;
                Common.fragParamName = "extra";
                Common.fragParam = "analysis";
                Intent i = new Intent();
                i.putExtra( Common.fragParamName, Common.fragParam);
                i.setClass(PhotoProcessActivity.this, TakePhoto.class);
                startActivity(i);
                finish();
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            TakePhoto.bmp1 = null;
            Common.bitmap = null;
            startActivity(new Intent().setClass(PhotoProcessActivity.this, TakePhoto.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
