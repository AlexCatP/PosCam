/**
 * LoginActivity.java
 *用户登录界面
 * @author 	Peng Shiyao
 */
package com.psy.my;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.psy.db.DBServer;
import com.psy.model.UserPose;
import com.psy.util.Common;
import com.psy.util.EncodeAndDecode;
import com.psy.util.HttpHelper;
import com.psy.util.URL;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;
public class LoginActivity extends Activity implements OnClickListener {
    private EditText edLoginname, edPassword;
    private Button btnLogin, btnLoginGray;
    private TextView msgLoginame, msgPw, regText, forgerPw;
    private ImageView imgBack;
    private DBServer dbServer;
    private ArrayList<UserPose> userAL;
    private String md5pw;
    String intentParam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(LoginActivity.this);
        setContentView(R.layout.login);
        // 严苛模式
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        edPassword = (EditText) findViewById(R.id.pw);
        edLoginname = (EditText) findViewById(R.id.loginName);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLoginGray = (Button) findViewById(R.id.btnLoginGray);
        msgLoginame = (TextView) findViewById(R.id.msgName);
        msgPw = (TextView) findViewById(R.id.msgPwd);
        regText = (TextView) findViewById(R.id.textReg);
        forgerPw = (TextView) findViewById(R.id.forgetPw);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        imgBack.setOnClickListener(this);
        regText.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        forgerPw.setOnClickListener(this);
        dbServer = new DBServer(this);
        userAL = new ArrayList<>();
        userAL = dbServer.findUser();
        Intent intent = getIntent();
        intentParam = intent.getStringExtra("extra");
        if (!userAL.get(0).getLoginName().equals("")
                && !userAL.get(0).getPassword().equals("")) {
            if (intentParam != null && intentParam.equals("logout")) {
                edLoginname.setText(userAL.get(0).getLoginName());
                edPassword.setText(userAL.get(0).getPassword());
                dbServer = new DBServer(this);
                dbServer.updateUser(-1, "", "");
                btnLogin.setVisibility(View.VISIBLE);
                btnLoginGray.setVisibility(View.GONE);
            } else if (intentParam != null && intentParam.equals("reset")) {
                edLoginname.setText(userAL.get(0).getLoginName());
                edPassword.setText("");
                btnLogin.setVisibility(View.GONE);
                btnLoginGray.setVisibility(View.VISIBLE);
            } else if (intentParam != null &&
                    intentParam.split("_")[0].equals("upload")) {
                try {
                    String json = postData(userAL.get(0).getLoginName(), userAL.get(0).getPassword());
                    HashMap<String, Object> userHM = HttpHelper.AnalysisUid(json);
                    if (userHM != null) {
                        Common.userId = (int) userHM.get("userid");
                        Intent intent11 = new Intent();
                        finish();
                        intent11.putExtra("upload", intentParam.split("_")[1]);
                        intent11.setClass(LoginActivity.this, UploadActivity.class);
                        startActivity(intent11);
                    } else {
                        Common.display(LoginActivity.this, "获取用户信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int flag = Common.isNetworkAvailable(this);
                if (flag == 0) {
                    Common.display(this, "请开启手机网络");
                } else {
                    try {
                        String json = postData(userAL.get(0).getLoginName(), userAL.get(0).getPassword());
                        HashMap<String, Object> userHM = HttpHelper.AnalysisUid(json);
                        Common.userId = (int) userHM.get("userid");
                        Intent intent1 = new Intent();
                        finish();
                        intent1.setClass(LoginActivity.this, UserActivity.class);
                        startActivity(intent1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.display(LoginActivity.this, "服务器异常，请稍后再试");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Common.display(LoginActivity.this, "服务器异常，请稍后再试");
                    }
                }
            }
        }
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (edPassword.getText().toString().trim().equals("")) {
                    msgPw.setText("密码不能为空");
                    btnLogin.setVisibility(View.GONE);
                    btnLoginGray.setVisibility(View.VISIBLE);
                } else {
                    msgPw.setText("");
                    btnLogin.setVisibility(View.VISIBLE);
                    btnLoginGray.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                msgPw.setText("");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edLoginname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (edLoginname.getText().toString().trim().length() == 0) {

                    msgLoginame.setText("输入不能为空");
                    btnLogin.setVisibility(View.GONE);
                    btnLoginGray.setVisibility(View.VISIBLE);
                } else {
                    msgLoginame.setText("");
                    btnLogin.setVisibility(View.VISIBLE);
                    btnLoginGray.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                msgPw.setText("");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            TakePhoto.bmp1 = null;
            Common.bitmap = null;
            finish();
            startActivity(new Intent().setClass(LoginActivity.this, TakePhoto.class));
        }
        if (v == forgerPw) {
            Common.display(LoginActivity.this, "敬请期待");
        }
        if (v == regText) {
            Intent intent = new Intent();
            finish();
            intent.setClass(LoginActivity.this, RegActivity.class);
            startActivity(intent);
        }
        if (v == btnLogin) {
            int flag = Common.isNetworkAvailable(this);
            if (flag == 0) {
                Common.display(this, "请开启手机网络");
            } else {
                if (!edLoginname.getText().toString().equals("")
                        && !edPassword.getText().toString().equals("")) {
                    try {
                        if (intentParam != null && intentParam.equals("logout")) {
                            if (!edLoginname.getText().toString().equals(userAL.get(0).getLoginName())
                                    || !edPassword.getText().toString().equals(userAL.get(0).getPassword()))
                            //判断用户是否修改了文本框
                            {
                                md5pw = EncodeAndDecode.getMD5Str(edPassword.getText().toString());
                                String json = postData(edLoginname.getText().toString(), md5pw);
                                HashMap<String, Object> userHM = HttpHelper.AnalysisUid(json);
                                if (userHM != null) {
                                    Common.userId = (int) userHM.get("userid");
                                    dbServer = new DBServer(this);
                                    dbServer.updateUser(Common.userId,
                                            edLoginname.getText().toString(), md5pw);

                                    Intent intent = new Intent();
                                    finish();
                                    intent.setClass(LoginActivity.this, UserActivity.class);
                                    startActivity(intent);
                                } else {
                                    Common.display(LoginActivity.this, "登录失败,用户名或密码错误");
                                }
                            } else {
                                Intent intent = new Intent();
                                finish();
                                intent.setClass(LoginActivity.this, UserActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            md5pw = EncodeAndDecode.getMD5Str(edPassword.getText().toString());
                            String json = postData(edLoginname.getText().toString(), md5pw);
                            HashMap<String, Object> userHM = HttpHelper.AnalysisUid(json);
                            if (userHM != null) {
                                Common.userId = (int) userHM.get("userid");
                                dbServer = new DBServer(this);
                                dbServer.updateUser(Common.userId, edLoginname.getText().toString(), md5pw);

                                Intent intent = new Intent();
                                finish();
                                intent.setClass(LoginActivity.this, UserActivity.class);
                                startActivity(intent);
                            } else {
                                Common.display(LoginActivity.this, "登录失败,用户名或密码错误");
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Common.display(LoginActivity.this, "登录失败");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Common.display(LoginActivity.this, "登录失败");
                        e.printStackTrace();
                    } catch (Exception e) {
                        Common.display(LoginActivity.this, "登录失败");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public String postData(String ln, String pw) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("username", ln);
        paramHM.put("password", pw);
        String result = HttpHelper.postData(URL.LOGIN_URL, paramHM, null);
        return result;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TakePhoto.bmp1 = null;
            Common.bitmap = null;
            Common.fragParamName = null;
            Common.fragParam = null;
            startActivity(new Intent().setClass(LoginActivity.this, TakePhoto.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

