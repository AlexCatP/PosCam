package com.psy.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.psy.model.PosLib;
import com.psy.util.Common;
import com.psy.util.DateTimeHelper;
import com.psy.util.HttpHelper;
import com.psy.util.URL;

import org.json.JSONException;


import java.util.HashMap;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;

public class DetailActivity extends Activity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView posName, posContent, del, userName, posPb;
    private HashMap<String, Object> picInfo;
    private ImageView userHead, posPic;
    private String poslocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(DetailActivity.this);
        this.setContentView(R.layout.pos_detail);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        userHead = (ImageView) findViewById(R.id.userHead);
        posPic = (ImageView) findViewById(R.id.posPic);

        posName = (TextView) findViewById(R.id.posName);//实际上是日期
        posContent = (TextView) findViewById(R.id.posContent);
        del = (TextView) findViewById(R.id.del);
        userName = (TextView) findViewById(R.id.userName);
        posPb = (TextView) findViewById(R.id.posPb);
        imgBack.setOnClickListener(this);
        del.setOnClickListener(this);
        Intent intent = getIntent();
        String posID = intent.getStringExtra("posId");
        posPic.setOnClickListener(this);

        try {

            if (posID != null) {
                picInfo = HttpHelper.AnalysisSinglePos(
                        HttpHelper.sendGet(URL.FIND_POS_BY_ID_PID, "pid=" + posID));
                setPosLib(picInfo);
                userName.setText(Common.userPose.getUserName());


                // 使用ImageLoader加载网络图片
                DisplayImageOptions options = new DisplayImageOptions.Builder()//
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)//图片大小刚好满足控件尺寸
                        .showImageForEmptyUri(R.drawable.head) //
                        .showImageOnFail(R.drawable.head) //
                        .cacheInMemory(true) //
                        .cacheOnDisk(true) //
                        .build();//

                String headpath =
                        "http://" + Common.userPose.getUserPicUrl();
                String picpath =
                        "http://" + Common.pos.getPosUrl();
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(headpath, userHead, options);
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(picpath, posPic, options);

                poslocalPath = Common.FULL_IMG_CACHE_PATH + picpath.hashCode();


                posContent.setText(Common.pos.getPosContent());
                posName.setText(
                        DateTimeHelper.timeLogic(
                                Common.pos.getPosName(),
                                DateTimeHelper.DATE_FORMAT_TILL_SECOND));
                posPb.setText("热度：" + Common.pos.getPosPb());

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Common.display(DetailActivity.this, "服务器错误，请稍后再试");

        }
    }

    public static void setPosLib(HashMap<String, Object> picInfo) {
        PosLib posLib = new PosLib();
        posLib.setPosUrl(picInfo.get("pos_pic_url").toString());
        posLib.setTags(picInfo.get("tags").toString());
        posLib.setTypeId((int) picInfo.get("typeid"));
        posLib.setUserId((int) picInfo.get("userid"));
        posLib.setPosContent(picInfo.get("poscontent").toString());
        posLib.setPosName(picInfo.get("posname").toString());
        posLib.setPosPb((int) picInfo.get("pospb"));
        posLib.setPosId((int) picInfo.get("posid"));
        Common.pos = posLib;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(DetailActivity.this, UserActivity.class);
            finish();
            DetailActivity.this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if (view == imgBack) {
            Intent intent = new Intent();
            intent.setClass(DetailActivity.this, UserActivity.class);
            finish();
            DetailActivity.this.startActivity(intent);
        }
        if (view == posPic) {

            Common.fragParamName = "detail_image_path";
            Common.fragParam = poslocalPath;
            Intent intent = new Intent();
            intent.putExtra(Common.fragParamName, Common.fragParam);
            intent.setClass(DetailActivity.this, TakePhoto.class);
            finish();
            DetailActivity.this.startActivity(intent);

        }
        if (view == del) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailActivity.this);
            builder1.setMessage("确定删除该pose？");
            builder1.setTitle("提示");
            builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {

                        String s = HttpHelper.sendGet(URL.DEL_POS_BY_PID, "postid=" + Common.pos.getPosId());
                        if (HttpHelper.getCode(s) == 100) {
                            Common.display(DetailActivity.this, "删除成功");
                            Intent intent = new Intent();
                            intent.setClass(DetailActivity.this, UserActivity.class);
                            finish();
                            DetailActivity.this.startActivity(intent);
                        } else Common.display(DetailActivity.this, "删除失败");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.display(DetailActivity.this, "服务器出错，请稍后再试");
                    }


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


}

