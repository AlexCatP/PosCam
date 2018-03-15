/**
 * RegActivity.java
 *用户注册界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.psy.db.DBServer;
import com.psy.util.Common;
import com.psy.util.EncodeAndDecode;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import java.util.HashMap;
import cn.xdu.poscam.R;
public class RegActivity extends Activity implements OnClickListener {
    private EditText edUserName, edPhone, edPassword1, edPassword2;
    private Button btnReg;
    private TextView msgName, msgPhone, msgPw1, msgPw2;
    private LinearLayout docGray, docRed;
    private ImageView imgBack;
    private String md5pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(RegActivity.this);
        this.setContentView(R.layout.reg);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        edPhone = (EditText) findViewById(R.id.userPhone);
        edPassword1 = (EditText) findViewById(R.id.pw1);
        edPassword2 = (EditText) findViewById(R.id.pw2);
        edUserName = (EditText) findViewById(R.id.userName);
        btnReg = (Button) findViewById(R.id.btnReg);
        btnReg = (Button) findViewById(R.id.btnReg);
        msgName = (TextView) findViewById(R.id.msgName);
        msgPw1 = (TextView) findViewById(R.id.msgPwd1);
        msgPw2 = (TextView) findViewById(R.id.msgPwd2);
        msgPhone = (TextView) findViewById(R.id.msgPhone);
        docGray = (LinearLayout) findViewById(R.id.docGray);
        docRed = (LinearLayout) findViewById(R.id.docRed);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        imgBack.setOnClickListener(this);
        btnReg.setOnClickListener(this);
        docGray.setOnClickListener(this);
        docRed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == docGray) {
            docGray.setVisibility(View.GONE);
            docRed.setVisibility(View.VISIBLE);
        }
        if (v == docRed) {
            docGray.setVisibility(View.VISIBLE);
            docRed.setVisibility(View.GONE);
        }
        if (v == imgBack) {
            Intent intent = new Intent();
            finish();
            intent.setClass(RegActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        if (v == btnReg) {
            boolean isConnectWS = true;
            if (!edPassword1.getText().toString().matches("[a-zA-Z\\d+]{6,16}")) {
                msgPw1.setText("密码应该为6-16位字母或数字组合");
                isConnectWS = false;
            } else
                msgPw1.setText("");
            if (!edPassword1.getText().toString().equals(edPassword2.getText().toString())) {
                msgPw2.setText("与上次输入密码不同");
                isConnectWS = false;
            } else
                msgPw2.setText("");

            if (!edUserName.getText().toString().matches("[a-zA-Z\\d+]{6,16}")) {
                msgName.setText("用户名应该为6-16位字母或数字组合");
                isConnectWS = false;
            } else
                msgName.setText("");
            if (!edPhone.getText().toString().matches("1[1-9]{1}[0-9]{9}")) {
                msgPhone.setText("手机号码格式错误");
                isConnectWS = false;
            } else
                msgPhone.setText("");
            int flag = Common.isNetworkAvailable(RegActivity.this);
            if (flag == 0) {
                Common.display(RegActivity.this, "请开启手机网络");
                isConnectWS = false;
            }
            if (docGray.getVisibility() == View.VISIBLE) {
                Common.display(getApplicationContext(), "请阅读并同意注册协议");
                isConnectWS = false;
            }
            if (!isConnectWS)
                return;
            md5pw = EncodeAndDecode.getMD5Str(edPassword2.getText().toString());
            try {
                String jsonStr = postData();
                if (HttpHelper.getCode(jsonStr) == 100) {
                    DBServer dbServer = new DBServer(this);
                    dbServer.updateUser(edUserName.getText().toString(), md5pw);
                    Common.display(RegActivity.this, "注册成功");
                    Intent intent1 = new Intent();
                    finish();
                    intent1.setClass(RegActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (HttpHelper.getCode(jsonStr) == 101) {
                    Common.display(RegActivity.this, "该用户名已注册");
                } else if (HttpHelper.getCode(jsonStr) == 102) {
                    Common.display(RegActivity.this, "该手机号已注册");
                } else {
                    Common.display(RegActivity.this, "注册失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Common.display(RegActivity.this, "服务器出错，请稍后再试");
            }

        }
    }
    public String postData() throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("username", edUserName.getText().toString());
        paramHM.put("userphone", edPhone.getText().toString());
        paramHM.put("password", md5pw);
        paramHM.put("userpb", "0");
        paramHM.put("taskpic_url", "119.29.245.167:8089/head.png");
        return HttpHelper.postData(URL.REG_URL, paramHM, null);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            finish();
            intent.setClass(RegActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
