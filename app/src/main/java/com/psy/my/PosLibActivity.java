/**
 * PosLibActivity.java
 *pose库界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.psy.model.PosLib;
import com.psy.util.Common;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;
public class PosLibActivity extends Activity implements View.OnClickListener {
    private GridView gridView;
    private ArrayList<HashMap<String, Object>> ArrayListHashMap;
    private HashMap<String, Object> hashMap;
    private LinearLayout loading, mainContent, frontll, notFrontll,search;
    private ArrayList<HashMap<String, Object>> ah;
    private Button boy, girl, gruop, couple, kid, boyself, girlself,hotSelf,hot;
    private EditText searchEdt;
    private ImageView searchBtn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Common.isVisible)
                search.setVisibility(View.VISIBLE);
            else search.setVisibility(View.GONE);
            switch (msg.what) {
                case -1:
                    loadFail(msg.obj.toString());
                    break;
                case 1:
                    ArrayList<HashMap<String, Object>> ah =
                            (ArrayList<HashMap<String, Object>>) msg.obj;
                    loadData(ah);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(PosLibActivity.this);
        setContentView(R.layout.pos_lib);
        initView();
    }
    public void initView() {
        loading = (LinearLayout) findViewById(R.id.loading);
        mainContent = (LinearLayout) findViewById(R.id.mainContent);
        frontll = (LinearLayout) findViewById(R.id.frontll);
        notFrontll = (LinearLayout) findViewById(R.id.notFrontll);
        boy = (Button) findViewById(R.id.boy);
        girl = (Button) findViewById(R.id.girl);
        couple = (Button) findViewById(R.id.couple);
        gruop = (Button) findViewById(R.id.group);
        kid = (Button) findViewById(R.id.kid);
        girlself = (Button) findViewById(R.id.girlSelf);
        boyself = (Button) findViewById(R.id.boySelf);
        hotSelf = (Button)findViewById(R.id.hotlSelf);
        hot = (Button) findViewById(R.id.hot);
        search = (LinearLayout) findViewById(R.id.search);
        searchEdt = (EditText) findViewById(R.id.searchEdt);
        searchBtn = (ImageView) findViewById(R.id.searchBtn);
        boy.setOnClickListener(this);
        girl.setOnClickListener(this);
        couple.setOnClickListener(this);
        gruop.setOnClickListener(this);
        kid.setOnClickListener(this);
        girlself.setOnClickListener(this);
        boyself.setOnClickListener(this);
        hotSelf.setOnClickListener(this);
        hot.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        if (TakePhoto.frontStatus == 1) {
            frontll.setVisibility(View.VISIBLE);
            notFrontll.setVisibility(View.GONE);
        } else {
            frontll.setVisibility(View.GONE);
            notFrontll.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loading.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (TakePhoto.frontStatus == 1) {
            initData(R.id.hotlSelf);
        } else {
            initData(R.id.hot);
        }
    }
    private void loadFail(String str) {
        loading.setVisibility(View.GONE);
        mainContent.setVisibility(View.GONE);
        Common.display(PosLibActivity.this, str);
    }
    private void initData(final int id) {
        new Thread() {
            @Override
            public void run() {
                try {
                    ah = new ArrayList<>();
                    switch (id) {
                        case R.id.searchBtn:
                            if (searchEdt.getText().toString().trim().length() > 0) {
                                    ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                            URL.FIND_POS_BY_TAG, "tag=" + searchEdt.getText().toString().trim()));
                                if (ah==null){
                                    Message msg = handler.obtainMessage();
                                    msg.what = -1;
                                    msg.obj = "未能搜索到相关pose，请试试其他关键词";
                                    handler.sendMessage(msg);
                                }
                            }else {
                                Message msg = handler.obtainMessage();
                                msg.what = -1;
                                msg.obj = "输入不能为空";
                                handler.sendMessage(msg);
                            }
                            break;
                        case R.id.hot:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_TYPE_NOT12,null));
                            Common.isVisible = true;
                            break;
                        case R.id.hotlSelf:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_TYPE12,null));
                            Common.isVisible = true;
                            break;
                        case R.id.girlSelf:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=1"));
                            Common.isVisible = false;
                            break;

                        case R.id.boySelf:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=2"));
                            Common.isVisible = false;
                            break;
                        case R.id.girl:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=3"));
                            Common.isVisible = false;
                            break;
                        case R.id.boy:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=4"));
                            Common.isVisible = false;
                            break;
                        case R.id.couple:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=5"));
                            Common.isVisible = false;
                            break;
                        case R.id.group:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=6"));
                            Common.isVisible = false;
                            break;
                        case R.id.kid:
                            ah = HttpHelper.AnalysisPosInfo(HttpHelper.sendGet(
                                    URL.FIND_POS_BY_ID_TID, "tid=7"));
                            Common.isVisible = false;
                            break;
                    }
                    if (ah != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = ah;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = -1;
                        int flag = Common.isNetworkAvailable(PosLibActivity.this);
                        if (flag==0){
                            msg.obj = "请开启手机网络";
                        }else {
                            msg.obj = "未加载到图片 :(";
                        }
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void loadData(final ArrayList<HashMap<String, Object>> posLists) {
        loading.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        gridView = (GridView) findViewById(R.id.gview);
        getData(posLists);
        PosPicAdapter adapter = new PosPicAdapter(PosLibActivity.this, ArrayListHashMap,
                R.layout.gird_item, new String[]{"pospic"},
                new int[]{R.id.gvImg});
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }
    private void getData(ArrayList<HashMap<String, Object>> posLists) {
        ArrayListHashMap = new ArrayList<>();
        for (int i = 0; i < posLists.size(); i++) {// list
            hashMap = new HashMap<>();
            hashMap.put("userid", posLists.get(i).get("userid"));
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
    public static ArrayList<PosLib> getPoses(ArrayList<HashMap<String, Object>> ah) {
        PosLib posLib = null;
        ArrayList<PosLib> poes = new ArrayList<>();
        for (int i = 0; i < ah.size(); i++) {
            posLib = new PosLib();
            posLib.setUserId((int) ah.get(i).get("userid"));
            posLib.setPosContent(ah.get(i).get("poscontent").toString());
            posLib.setPosId((int) ah.get(i).get("posid"));
            posLib.setPosName(ah.get(i).get("posname").toString());
            posLib.setTags(ah.get(i).get("tags").toString());
            posLib.setPosUrl(ah.get(i).get("pos_pic_url").toString());
            posLib.setTypeId((int) ah.get(i).get("typeid"));
            posLib.setPosPb((int) ah.get(i).get("pospb"));
            poes.add(posLib);
        }
        return poes;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchBtn:
                initData(R.id.searchBtn);
                break;
            case R.id.hot:
                setBarStyle(hot);
                initData(R.id.hot);
                break;
            case R.id.hotlSelf:
                setBarStyle(hotSelf);
                initData(R.id.hotlSelf);
                break;
            case R.id.girl:
                setBarStyle(girl);
                initData(R.id.girl);
                break;
            case R.id.boy:
                setBarStyle(boy);
                initData(R.id.boy);
                break;
            case R.id.couple:
                setBarStyle(couple);
                initData(R.id.couple);
                break;
            case R.id.group:
                setBarStyle(gruop);
                initData(R.id.group);
                break;
            case R.id.kid:
                setBarStyle(kid);
                initData(R.id.kid);
                break;
            case R.id.girlSelf:
                setBarStyle(girlself);
                initData(R.id.girlSelf);
                break;
            case R.id.boySelf:
                setBarStyle(boyself);
                initData(R.id.boySelf);
                break;
        }
    }
    private void setBarStyle(Button selectedBtn) {
        Button[] buttons = {hot,hotSelf,girlself, boyself, girl, boy,
                couple, gruop, kid};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackgroundColor(getResources().getColor(R.color.white));
            buttons[i].setTextColor(getResources().getColor(R.color.textGray));
            if (buttons[i].equals(selectedBtn)) {
                if (i==0 || i==1)
                    buttons[i].setTextColor(getResources().getColor(R.color.shanZhaRed));
                else
                buttons[i].setTextColor(getResources().getColor(R.color.black));
                buttons[i].setBackgroundColor(getResources().getColor(R.color.bgColor));
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            TakePhoto.bmp1 = null;
            Common.bitmap = null;
            Common.fragParamName = null;
            Common.fragParam = null;
            startActivity(new Intent().setClass(PosLibActivity.this, TakePhoto.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
