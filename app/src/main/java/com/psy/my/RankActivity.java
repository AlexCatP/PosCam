/**
 * RankActivity.java
 *P币排行榜界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.psy.util.Common;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import java.util.ArrayList;
import java.util.HashMap;
import cn.xdu.poscam.R;
public class RankActivity extends Activity implements View.OnClickListener {
    private ImageView imgBack;
    private LinearLayout loading;
    private LinearLayout mainContent;
    private ArrayList<HashMap<String, Object>> rankLists;
    private ListView lv;
    private ImageView myHead;
    private TextView myPb, myName;
    public  TextView rankTxt;
    private ArrayList<HashMap<String, Object>> ArrayListHashMap;
    private HashMap<String, Object> hashMap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    loadFail(msg.obj.toString());
                    break;
                case 1:
                    ArrayList<HashMap<String, Object>> rankLists =
                            (ArrayList<HashMap<String, Object>>) msg.obj;
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
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
                    loadData(rankLists);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.rank_pose);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        imgBack.setOnClickListener(this);
        loading = (LinearLayout) findViewById(R.id.loading);
        mainContent = (LinearLayout) findViewById(R.id.mainContent);
        myHead = (ImageView) findViewById(R.id.myHead);
        myPb = (TextView) findViewById(R.id.myPb);
        myName = (TextView) findViewById(R.id.myName);
        rankTxt = (TextView) findViewById(R.id.rankTxt);
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
        mainContent.setVisibility(View.GONE);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initData();
    }
    private void loadFail(String str) {
        Common.display(RankActivity.this, str);
        loading.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
    }
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    rankLists = HttpHelper.AnalysisUserInfo2(postData());
                    if (rankLists != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = rankLists;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = -1;
                        int flag = Common.isNetworkAvailable(RankActivity.this);
                        if (flag==0){
                            msg.obj = "请开启手机网络";
                        }else {
                            msg.obj = "暂无排行榜数据";
                        }
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void loadData(final ArrayList<HashMap<String, Object>> rankLists) {
        loading.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        lv = (ListView) findViewById(R.id.list);
        lv.setCacheColorHint(0);
        getData(rankLists);
        RankAdapter adapter = new RankAdapter(RankActivity.this, ArrayListHashMap,
                R.layout.rank_item_pose, new String[]{"rankNum", "userName", "userPb"},
                new int[]{R.id.rankNum, R.id.userName, R.id.userPb});
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }
    private void getData(ArrayList<HashMap<String, Object>> rankLists) {
        ArrayListHashMap = new ArrayList<>();
        for (int i = 0; i < rankLists.size(); i++) {// list
            hashMap = new HashMap<>();
            hashMap.put("userName", rankLists.get(i).get("username"));
            hashMap.put("userPb", rankLists.get(i).get("userpb"));
            hashMap.put("rank_pose",i+1);
            if (hashMap.get("userName").toString().equals(Common.userPose.getUserName()))
            rankTxt.setText("排名 "+hashMap.get("rank_pose"));
            ArrayListHashMap.add(hashMap);
        }
    }
    public String postData() throws Exception {
        return HttpHelper.postData(URL.FIND_ALL_USERS_URL, null, null);
    }
    @Override
    public void onClick(View arg0) {
        if (arg0 == imgBack) {
            Intent intent = new Intent();
            finish();
            intent.setClass(RankActivity.this, UserActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            finish();
            intent.setClass(RankActivity.this, UserActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
