/**
 * UploadActivity.java
 *上传照片模块
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.psy.model.YouTuTag;
import com.psy.util.Common;
import com.psy.util.DateTimeHelper;
import com.psy.util.GenericProgressDialog;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import com.youtu.Youtu;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import camerakit.TakePhoto;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.xdu.poscam.R;
public class UploadActivity extends Activity implements View.OnClickListener {
    private ImageView imgBack, posPic;
    private EditText posContent, posTags;
    private TextView posType, posTagsTxt;
    private Button uploadBtn;
    private Bitmap bitmap, bmp;
    private JSONObject respose;
    private ArrayList<YouTuTag> tags;
    private String[] items;
    private String picpath;
    private String localpath;
    private boolean isPicChange = false;
    private LinearLayout poseTypell;
    private static final int REQUEST_CODE_GALLERY = 111;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (msg.obj != null) {
                    dismissProgressDialog();
                    tags = HttpHelper.getTags((JSONObject) msg.obj);
                    String temp = "";
                    for (int i = 0; i < tags.size(); i++) {
                        temp += (tags.get(i).getTagName() + " ");
                    }
                    posTagsTxt.setText(temp + "");
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(UploadActivity.this);
        this.setContentView(R.layout.upload);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        posPic = (ImageView) findViewById(R.id.posPic);
        posTags = (EditText) findViewById(R.id.poseTags);
        posContent = (EditText) findViewById(R.id.posContent);
        posType = (TextView) findViewById(R.id.poseType);
        uploadBtn = (Button) findViewById(R.id.btnUpload);
        uploadBtn.setOnClickListener(this);
        posTagsTxt = (TextView) findViewById(R.id.poseTagsTxt);
        poseTypell = (LinearLayout) findViewById(R.id.poseTypell);
        poseTypell.setOnClickListener(this);
        int flag = Common.isNetworkAvailable(UploadActivity.this);
        if (flag==0){
            Common.display(UploadActivity.this,"请开启手机网络");
        }
        else {
            posPic.setOnClickListener(this);
            imgBack.setOnClickListener(this);
            Intent intent = getIntent();
            String path = intent.getStringExtra("upload");
            if (path != null) {
                localpath = path;
                System.out.println("localpath="+localpath);
                bmp = BitmapFactory.decodeFile(path);
                posPic.setImageBitmap(bmp);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        analysisTag(bmp);
                        Looper.loop();
                    }
                }
                ).start();
            }
        }
    }

    private void showProgressDialog(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog == null) {
                    mAlertDialog = new GenericProgressDialog(
                            UploadActivity.this);
                }
                mAlertDialog.setMessage(msg);
                ((GenericProgressDialog) mAlertDialog)
                        .setProgressVisiable(true);
                mAlertDialog.setCancelable(true);
                mAlertDialog.show();
                mAlertDialog.setCanceledOnTouchOutside(true);
            }
        });
    }
    private void showProgressDialog1(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog == null) {
                    mAlertDialog = new GenericProgressDialog(
                            UploadActivity.this);
                }
                mAlertDialog.setMessage(msg);
                ((GenericProgressDialog) mAlertDialog)
                        .setProgressVisiable(true);
                mAlertDialog.setCancelable(false);
                mAlertDialog.setOnCancelListener(null);
                mAlertDialog.show();
                mAlertDialog.setCanceledOnTouchOutside(false);
            }
        });
    }
    private AlertDialog mAlertDialog;
    private void dismissProgressDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()
                        && !UploadActivity.this.isFinishing()) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            if (localpath != null)
                intent.setClass(UploadActivity.this, PhotoProcessActivity.class);
            else
                intent.setClass(UploadActivity.this, UserActivity.class);
            finish();
            UploadActivity.this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        if (view == imgBack) {
            Intent intent = new Intent();
            if (localpath != null)
                intent.setClass(UploadActivity.this, PhotoProcessActivity.class);
            else
                intent.setClass(UploadActivity.this, UserActivity.class);
            finish();
            UploadActivity.this.startActivity(intent);
        }
        if (view == posPic) {
            //配置功能
            FunctionConfig functionConfig = new FunctionConfig.Builder()
                    .setEnableCrop(true)
                    .setEnableRotate(true)
                    .setCropSquare(true)
                    .setEnablePreview(true)
                    .setEnableEdit(true)//编辑功能
                    .setEnableCrop(true)//裁剪功能
                    .setEnableCamera(false)//相机功能
                    .build();
            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
        }
        if (view == uploadBtn) {
            boolean isConnectWS = true;
            if (posContent.getText().toString().trim().length() > 200 ||
                    posTags.getText().toString().trim().length() > 200) {
                Common.display(UploadActivity.this, "内容不得超过200字");
                isConnectWS = false;
            }
            if (posContent.getText().toString().trim().length() == 0) {
                Common.display(UploadActivity.this, "内容不能为空");
                isConnectWS = false;
            }
            if (posTagsTxt.getText().toString().trim().length() == 0 &&
                    posTags.getText().toString().trim().length() == 0) {
                Common.display(UploadActivity.this, "标签不能为空");
                isConnectWS = false;
            }
            if (picpath == null && localpath == null) {
                Common.display(UploadActivity.this, "图片不能为空");
                isConnectWS = false;
            }
            if (!isConnectWS)
                return;
            try {
                uploadBtn.setBackground(
                        getResources().getDrawable(R.drawable.btn_shape_gray));
                uploadBtn.setText("正在上传...");
                showProgressDialog1("正在上传...");
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            int res = 0;
                            //判断图片是否被修改
                            if (!isPicChange)
                                res = HttpHelper.getCode(postData(localpath));
                            else res = HttpHelper.getCode(postData(picpath));
                            if (res == 100) {
                                String tags = "";
                                if (posTags.getText().toString().trim().length() == 0) {
                                    tags = posTagsTxt.getText().toString().substring(0, posTagsTxt.getText().toString().length() - 1).replace(" ", "_");
                                } else
                                    tags = (posTagsTxt.getText().toString() + posTags.getText().toString()).replace(" ", "_");
                                String[] tagArray = tags.split("_");
                                String tagJson = "";
                                for (int i = 0; i < tagArray.length; i++) {
                                    if(tagArray[i]!=null && tagArray[i].length()!=0) {
                                        System.out.println("pos_tag="+ tagArray[i]+"|");
                                        tagJson = HttpHelper.sendGet(URL.INSERT_TAG, "pos_tag=" + tagArray[i]);
                                    }}
                                if (HttpHelper.getCode(tagJson) == 100 || HttpHelper.getCode(tagJson) == 102) {
                                    Looper.prepare();
                                    Common.display(UploadActivity.this, "上传成功");
                                    dismissProgressDialog();
                                    Intent inte = new Intent();
                                    finish();
                                    inte.setClass(UploadActivity.this, UserActivity.class);
                                    startActivity(inte);
                                    Looper.loop();
                                } else {
                                    Looper.prepare();
                                    Common.display(UploadActivity.this, "上传失败:INSERT_TAG");
                                    dismissProgressDialog();
                                    Looper.loop();
                                }
                            } else {
                                Looper.prepare();
                                Common.display(UploadActivity.this, "上传失败:INSERT_POS");
                                dismissProgressDialog();
                                Looper.loop();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Common.display(UploadActivity.this, "服务器出错，请稍后再试");
                            dismissProgressDialog();
                            Looper.loop();
                        }
                    }
                };
                timer.schedule(task, 100);
            } catch (Exception e) {
                e.printStackTrace();
                Common.display(UploadActivity.this, "服务器出错，请稍后再试");
            }
        }
        if (view == poseTypell) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
            if (TakePhoto.frontStatus == 1) {
                items = Common.SELF_POS_TYPE;
            } else {
                items = Common.POS_TYPE;
            }
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    posType.setText(items[which]);
                }
            });
            builder.show();
        }
    }
    public String postData(String path) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("poscontent", posContent.getText().toString());
        String tags = "";
        if (posTags.getText().toString().trim().length() == 0) {
            tags = posTagsTxt.getText().toString().substring(0, posTagsTxt.getText().toString().length() - 1).replace(" ", "_");
        } else
            tags = (posTagsTxt.getText().toString() + posTags.getText().toString()).replace(" ", "_");
        paramHM.put("tags", tags);
        paramHM.put("userid", Common.userId + "");
        paramHM.put("typeid", Common.type2int(posType.getText().toString()) + "");
        paramHM.put("posepb", "0");
        paramHM.put("posename", DateTimeHelper.getCurrentDateTime());
        HashMap<String, String> fileHM = new HashMap<>();
        fileHM.put("pospicurl", path);
        System.out.println(paramHM + " " + fileHM);
        return HttpHelper.postData(URL.INSERT_POS, paramHM, fileHM);
    }
    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            Log.v("onHanlderSuccess", "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            for (PhotoInfo info : resultList) {
                switch (reqeustCode) {
                    case REQUEST_CODE_GALLERY:
                        picpath = info.getPhotoPath();
                        bitmap = BitmapFactory.decodeFile(info.getPhotoPath());
                        posPic.setImageBitmap(bitmap);
                        isPicChange = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                analysisTag(bitmap);
                                Looper.loop();}
                        }).start();
                        break;
                }
            }
        }
        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(UploadActivity.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    private void analysisTag(Bitmap bitmap){
        Youtu faceYoutu = new Youtu(Common.APP_ID,
                Common.SECRET_ID, Common.SECRET_KEY,
                Youtu.API_YOUTU_END_POINT);
        try {
            showProgressDialog("分析标签中");
            respose = faceYoutu.ImageTag(bitmap);
        } catch (KeyManagementException e) {
            dismissProgressDialog();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            dismissProgressDialog();
            e.printStackTrace();
        } catch (IOException e) {
            dismissProgressDialog();
            e.printStackTrace();
        } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = respose;
        handler.sendMessage(msg);
        System.out.println(respose + "");
    }
}