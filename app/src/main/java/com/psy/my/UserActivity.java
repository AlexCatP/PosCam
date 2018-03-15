/**
 * UserActivity.java
 *用户信息界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.psy.model.UserPose;
import com.psy.util.Common;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import org.json.JSONException;

import android.os.Handler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;
public class UserActivity extends Activity implements View.OnClickListener {
    private ImageView btnBackCam, btnSetting, uploadBtn,addBtn,rankBtn;
    private ImageView myHead;
    private TextView myPb, myName;
    private GridView gridView;
    private LinearLayout loading, content, logoutll;
    private ArrayList<HashMap<String, Object>> picLists;
    private ArrayList<HashMap<String, Object>> ArrayListHashMap;
    private HashMap<String, Object> hashMap;
    private HashMap<String, Object> userHM;
    private int pb = 0;
    private RelativeLayout hideAdd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    hideAdd.setVisibility(View.GONE);
                    ArrayList<HashMap<String, Object>> picLists =
                            (ArrayList<HashMap<String, Object>>) msg.obj;
                    DisplayImageOptions options = new DisplayImageOptions.Builder()//
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.EXACTLY)//图片大小刚好满足控件尺寸
                            .showImageForEmptyUri(R.drawable.userphoto)
                            .showImageOnFail(R.drawable.userphoto)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(
                            "http://" + Common.userPose.getUserPicUrl(), myHead, options);
                    myPb.setText("P币 " + Common.userPose.getPb());
                    myName.setText(Common.userPose.getUserName() + "");
                    loadData(picLists);
                    break;
                case -1:
                    hideAdd.setVisibility(View.VISIBLE);
                    loadFail(msg.obj.toString());
                    DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.EXACTLY)//图片大小刚好满足控件尺寸
                            .showImageForEmptyUri(R.drawable.userphoto)
                            .showImageOnFail(R.drawable.userphoto)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(
                            "http://" + Common.userPose.getUserPicUrl(), myHead, options1);
                    myPb.setText("P币 " + Common.userPose.getPb());
                    myName.setText(Common.userPose.getUserName() + "");
                    break;
                case 0:
                    hideAdd.setVisibility(View.GONE);
                    btnSetting.setEnabled(false);
                    loadFail(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TakePhoto.pic_status = TakePhoto.PIC_FOR_DETAIL;
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(UserActivity.this);
        this.setContentView(R.layout.my_pose);
        //严苛模式
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        loading = (LinearLayout) findViewById(R.id.loading);
        content = (LinearLayout) findViewById(R.id.content);
        uploadBtn = (ImageView) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(this);
        addBtn = (ImageView) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);
        btnBackCam = (ImageView) findViewById(R.id.btnBackCam);
        btnBackCam.setOnClickListener(this);
        btnSetting = (ImageView) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);
        myHead = (ImageView) findViewById(R.id.myHead);
        myPb = (TextView) findViewById(R.id.myPb);
        myName = (TextView) findViewById(R.id.myName);
        hideAdd = (RelativeLayout) findViewById(R.id.hideAdd);
        rankBtn = (ImageView) findViewById(R.id.rankBtn);
        rankBtn.setOnClickListener(this);
    }

    private void loadFail(String str) {
        Common.display(UserActivity.this, str);
        loading.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loading.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initData();
    }
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    userHM = HttpHelper.AnalysisUserInfo(postData(URL.FIND_U_BY_UID_URL));
                    picLists =
                            HttpHelper.AnalysisPosInfo(
                                    HttpHelper.sendGet(URL.FIND_POS_BY_ID_UID, "uid=" + userHM.get("userid")));
                    if (userHM != null && picLists != null) {
                        for (int i = 0; i < picLists.size(); i++) {
                            pb += (int) picLists.get(i).get("pospb");
                        }
                        System.out.println("pb=" + pb);
                        postData(URL.UPDATE_PB_BY_UID_URL, pb + "");
                        setUserInfo(userHM);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = picLists;
                        handler.sendMessage(msg);
                    } else if (userHM != null && picLists == null) {
                        setUserInfo(userHM);
                        Message msg = handler.obtainMessage();
                        msg.what = -1;
                        msg.obj = "暂未上传pose";
                        handler.sendMessage(msg);
                    } else {
                        int flag = Common.isNetworkAvailable(UserActivity.this);
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        if (flag==0){
                            msg.obj = "请开启手机网络";
                        }else {
                            msg.obj = "未加载到数据";
                        }
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void setUserInfo(HashMap<String, Object> userHM) {
        UserPose userPose = new UserPose();
        userPose.setUserPicUrl(userHM.get("taskpic_url").toString());
        userPose.setUserphone(userHM.get("userphone").toString());
        userPose.setPb((int) userHM.get("userpb"));
        userPose.setUserName(userHM.get("username").toString());
        userPose.setUserID((int) userHM.get("userid"));
        Common.userPose = userPose;
    }
    private void loadData(final ArrayList<HashMap<String, Object>> posLists) {
        loading.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        gridView = (GridView) findViewById(R.id.gview);
        getData(posLists);
        Collections.reverse(ArrayListHashMap);
        PosPicAdapter adapter = new PosPicAdapter(UserActivity.this, ArrayListHashMap,
                R.layout.gird_item, new String[]{"pospic"},
                new int[]{R.id.gvImg});
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }
    public String postData(String url) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        System.out.println("Common.userId=" + Common.userId);
        paramHM.put("userid", Common.userId + "");
        return HttpHelper.postData(url, paramHM, null);
    }
    public String postData(String url, String pb) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        System.out.println("Common.userId=" + Common.userId);
        paramHM.put("userid", Common.userId + "");
        paramHM.put("userpb", pb);
        return HttpHelper.postData(url, paramHM, null);
    }
    private void getData(ArrayList<HashMap<String, Object>> posLists) {
        ArrayListHashMap = new ArrayList<>();
        for (int i = 0; i < posLists.size(); i++) {// list
            hashMap = new HashMap<>();
            hashMap.put("posid", posLists.get(i).get("posid"));
            hashMap.put("typeid", posLists.get(i).get("typeid"));
            hashMap.put("tags", posLists.get(i).get("tags"));
            hashMap.put("pospb", posLists.get(i).get("pospb"));
            hashMap.put("posname", posLists.get(i).get("posname"));
            hashMap.put("pospic", posLists.get(i).get("pos_pic_url"));
            hashMap.put("poscontent", posLists.get(i).get("poscontent"));
            ArrayListHashMap.add(hashMap);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TakePhoto.bmp1 = null;
            Common.bitmap = null;
            Common.fragParamName = null;
            Common.fragParam = null;
            startActivity(new Intent().setClass(UserActivity.this, TakePhoto.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackCam:
                TakePhoto.bmp1 = null;
                Common.bitmap = null;
                Common.fragParamName = null;
                Common.fragParam = null;
                startActivity(new Intent().setClass(UserActivity.this, TakePhoto.class));
                finish();
                break;
            case R.id.btnSetting:
                Intent i = new Intent();
                i.setClass(UserActivity.this, SettingActivity.class);
                String path =
                        "http://" + Common.userPose.getUserPicUrl();
                String localPath = Common.FULL_IMG_CACHE_PATH + path.hashCode();
                i.putExtra("image_path", localPath);
                finish();
                UserActivity.this.startActivity(i);
                break;
            case R.id.uploadBtn:
            case R.id.addBtn:
                Intent intent1 = new Intent();
                intent1.setClass(UserActivity.this, UploadActivity.class);
                finish();
                UserActivity.this.startActivity(intent1);
                break;
            case R.id.rankBtn:
                Intent intent2 = new Intent();
                intent2.setClass(UserActivity.this, RankActivity.class);
                finish();
                UserActivity.this.startActivity(intent2);
                break;
        }
    }
}

