package camerakit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.psy.util.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.xdu.poscam.R;

public class PreviewActivity extends Activity {

    private String path = "";
    private Bitmap bitmap;

    ImageView imageView,btnBack;
    Button button;

//    @BindView(R.id.nativeCaptureResolution)
//    TextView nativeCaptureResolution;
//
//    @BindView(R.id.actualResolution)
//    TextView actualResolution;
//
//    @BindView(R.id.approxUncompressedSize)
//    TextView approxUncompressedSize;
//
//    @BindView(R.id.captureLatency)
//    TextView captureLatency;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        bitmap = ResultHolder.getImage();

        imageView = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.uploadBtn);


        if (bitmap == null) {
            finish();
            return;
        }

        imageView.setImageBitmap(bitmap);


        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showProgressDialog("准备上传...", PreviewActivity.this);
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        ////////////////////////
                        Looper.prepare();
                        if (saveImageToGallery(PreviewActivity.this, bitmap)) {
                            Common.dismissProgressDialog(PreviewActivity.this);
                            Common.display(PreviewActivity.this, "已保存");
                        }else{
                            Common.dismissProgressDialog(PreviewActivity.this);
                            Common.display(PreviewActivity.this, "保存失败");
                        }
                        //Common.picPath = path;
                        finish();
                        Intent i = new Intent();
                        i.putExtra("item", 111);
                        //i.setClass(PreviewActivity.this, AtyMain.class);
                        startActivity(i);
                        Looper.loop();

                        ////////////////////////
                    }

                };
                timer.schedule(task, 100);
            }
        });
    }



    public boolean saveImageToGallery(Context context, Bitmap bmp) {
        boolean isSucceed = false;
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
            isSucceed = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
       if (isSucceed)
           isSucceed = scanPhoto(this, path);

        return isSucceed;
    }

    public boolean scanPhoto(Context ctx, String imgFileName) {
        boolean isSucceed = false;
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
        isSucceed = true;
        return isSucceed;
    }



}
