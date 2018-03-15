package camerakit;

/**
 * Created by ppssyyy on 2017-08-10.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.psy.model.Param;
import com.psy.model.PosLib;
import com.psy.model.YouTuTag;
import com.psy.my.LoginActivity;
import com.psy.my.MyActivityManager;
import com.psy.my.PhotoProcessActivity;
import com.psy.my.PosLibActivity;
import com.psy.my.PosPicAdapter;
import com.psy.my.ZoomImageView;
import com.psy.util.BitmapUtil;
import com.psy.util.Common;
import com.psy.util.DataConvert;
import com.psy.util.HttpHelper;
import com.psy.util.URL;
import com.youtu.Youtu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.xdu.poscam.R;


public class TakePhoto extends Activity implements View.OnLayoutChangeListener,View.OnClickListener {


    ViewGroup parent;

    CameraView camera;

    // Capture Mode:

    RadioGroup captureModeRadioGroup;

    // Crop Mode:

    RadioGroup cropModeRadioGroup;

    // Width:

    TextView screenWidth;
    EditText width;
    Button widthUpdate;
    RadioGroup widthModeRadioGroup;

    // Height:

    TextView screenHeight;
    EditText height;
    Button heightUpdate;
    RadioGroup heightModeRadioGroup;

    private int mCameraWidth;
    private int mCameraHeight;

    private ImageButton capturePhoto,toggleCamera,toggleFlash;


    public static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int flashStatus = 0;
    public static int frontStatus = 0;

    /**
     * pose图片
     */
    private static final int REQUEST_CODE = 100;
    private String photo_path;

    private static ZoomImageView zoomImageView;

    private ImageView my,map;

    private static Bitmap bitmap;
    private static String imgURL;

    public static Bitmap bmp1;

    public static LinearLayout resultll;
    private TextView resultTxt;

    JSONObject respose;
    ArrayList<YouTuTag> tags;
    ArrayList<PosLib> poses;
    MyActivityManager man;
    ArrayList<HashMap<String, Object>> arrHM;
    private ArrayList<HashMap<String, Object>> ArrayListHashMap;
    private HashMap<String, Object> hashMap;
    public static GridView gridView;

    private Button mBtnSearch;
    private Button mBtnTakePhoto;
    public static int pic_status;
    public static int PIC_ON_CAMERA = 11;
    public static int PIC_FOR_SELECT = 12;
    public static int PIC_FOR_DETAIL = 13;

    String imgPath, detailImgPath, analysis, analysis1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                tags = HttpHelper.getTags((JSONObject) msg.obj);

                System.out.println("tags=" + tags);
                if (tags.size() == 0) {
                    Common.dismissProgressDialog(TakePhoto.this);
                    Toast.makeText(TakePhoto.this, "图片格式错误或图片破损",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (tags.get(0).getTagName().equals("error")) {
                        Common.dismissProgressDialog(TakePhoto.this);
                        Toast.makeText(TakePhoto.this, "图片格式错误或图片破损",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Common.dismissProgressDialog(TakePhoto.this);
                        poses = new ArrayList<>();
                        Collections.sort(tags);
                        //现在能找到新图片所有的特征向量了，通过json传给服务器
                        //然后服务器返回json结果
                        try {
                            Param param = new Param();
                            param.setTag_id(DataConvert.getParam2(DataConvert.toTagMap(), tags));
                            param.setWeight(DataConvert.getParam3(tags));
                            HashMap<Integer, Double> param3 =
                                    DataConvert.getMergedParam(param.getTag_id(), param.getWeight());

                            String json = postData(DataConvert.toJsonArray(param3));
                            arrHM = HttpHelper.AnalysisPosInfo2(json);

                            if (arrHM != null) {
                                Collections.reverse(arrHM);
                                loadData(arrHM);
                                String temp = "";
                                for (int i = 0; i < tags.size(); i++) {
                                    temp += (tags.get(i).getTagName() + "、");
                                }

                                resultTxt.setText("为您找到适合" + "\""
                                        + temp.substring(0, temp.length() - 1) + "\"场景的pose");
                                if (gridView.getVisibility() == View.VISIBLE
                                        && resultll.getVisibility() == View.VISIBLE) {
                                    gridView.setVisibility(View.GONE);
                                    resultll.setVisibility(View.GONE);
                                } else {
                                    gridView.setVisibility(View.VISIBLE);
                                    resultll.setVisibility(View.VISIBLE);
                                }
                                if (imgURL != null) {
                                    setZoomImg(imgURL);
                                }
                            } else {
                                Common.display(TakePhoto.this, "分析失败");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Common.display(TakePhoto.this, "服务器错误，请稍后再试");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Common.display(TakePhoto.this, "服务器错误，请稍后再试");
                        }

                    }
                }

            }

            if (msg.what == -1) {
                loadFail(msg.obj.toString());
            }

        }
    };

    public String postData(String param) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("json", param);
        return HttpHelper.postData(URL.GET_RESULT, paramHM, null);
    }


    private void loadFail(String str) {
        Common.dismissProgressDialog(TakePhoto.this);
        Common.display(TakePhoto.this, str);
    }



    @Override
    protected void onResume() {
        super.onResume();
        camera.start();

        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("poscam_sp", MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
        flashStatus = sharedPreferences.getInt("flash", 0);
        frontStatus = sharedPreferences.getInt("front", 0);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (ContextCompat.checkSelfPermission(TakePhoto.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePhoto.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_main);

        man = MyActivityManager.getInstance();
        man.pushOneActivity(TakePhoto.this);

        parent = (ViewGroup) findViewById(R.id.camera_activity_main);
        camera = (CameraView) findViewById(R.id.camera);
        captureModeRadioGroup = (RadioGroup) findViewById(R.id.captureModeRadioGroup);
        cropModeRadioGroup = (RadioGroup) findViewById(R.id.cropModeRadioGroup);
        screenWidth = (TextView) findViewById(R.id.screenWidth);
        width = (EditText) findViewById(R.id.width);
        widthUpdate = (Button) findViewById(R.id.widthUpdate);
        widthModeRadioGroup = (RadioGroup) findViewById(R.id.widthModeRadioGroup);
        screenHeight = (TextView) findViewById(R.id.screenHeight);
        height = (EditText) findViewById(R.id.height);
        heightUpdate = (Button) findViewById(R.id.heightUpdate);
        heightModeRadioGroup = (RadioGroup) findViewById(R.id.heightModeRadioGroup);

        mBtnSearch = (Button) findViewById(R.id.search);
        mBtnTakePhoto = (Button) findViewById(R.id.takephoto);
        zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);

        resultll = (LinearLayout) findViewById(R.id.resultll);
        resultTxt = (TextView) findViewById(R.id.resultTxt);
        gridView = (GridView) findViewById(R.id.gview);

        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenWidth.setText("屏宽: " + parent.getWidth() + "px");
                screenHeight.setText("屏高: " + parent.getHeight() + "px");
            }
        });

        camera.addOnLayoutChangeListener(this);


        captureModeRadioGroup.setOnCheckedChangeListener(captureModeChangedListener);
        cropModeRadioGroup.setOnCheckedChangeListener(cropModeChangedListener);
        widthModeRadioGroup.setOnCheckedChangeListener(widthModeChangedListener);
        heightModeRadioGroup.setOnCheckedChangeListener(heightModeChangedListener);

        capturePhoto = (ImageButton) findViewById(R.id.capturePhoto);
        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                camera.setCameraListener(new CameraListener() {
                    @Override
                    public void onPictureTaken(byte[] jpeg) {
                        super.onPictureTaken(jpeg);
                        long callbackTime = System.currentTimeMillis();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                        ResultHolder.dispose();
                        ResultHolder.setImage(bitmap);
                        ResultHolder.setNativeCaptureSize(
                                captureModeRadioGroup.getCheckedRadioButtonId() == R.id.modeCaptureStandard ?
                                        camera.getCaptureSize() : camera.getPreviewSize()
                        );
                        ResultHolder.setTimeToCallback(callbackTime - startTime);
                        Common.bitmap = bitmap;
                        Intent intent = new Intent(TakePhoto.this, PhotoProcessActivity.class);
                        startActivity(intent);
                    }
                });
                camera.captureImage();
            }
        });

        widthUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widthUpdate.getAlpha() >= 1) {
                    updateCamera(true, false);
                }
            }
        });

        heightUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heightUpdate.getAlpha() >= 1) {
                    updateCamera(false, true);
                }
            }
        });

        toggleFlash = (ImageButton) findViewById(R.id.toggleFlash);
        toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (camera.toggleFlash()) {
                    case CameraKit.Constants.FLASH_ON:
                        //Toast.makeText(this, "Flash on!", Toast.LENGTH_SHORT).show();
                        break;

                    case CameraKit.Constants.FLASH_OFF:
                        // Toast.makeText(this, "Flash off!", Toast.LENGTH_SHORT).show();
                        break;

                    case CameraKit.Constants.FLASH_AUTO:
                        // Toast.makeText(this, "Flash auto!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        toggleCamera = (ImageButton) findViewById(R.id.toggleCamera);
        toggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (camera.toggleFacing()) {
                    case CameraKit.Constants.FACING_BACK:
                        //Toast.makeText(this, "Switched to back camera!", Toast.LENGTH_SHORT).show();
                        break;

                    case CameraKit.Constants.FACING_FRONT:
                        //Toast.makeText(this, "Switched to front camera!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        Intent intent = getIntent();
        if (intent.hasExtra("image_path") || intent.hasExtra("detail_image_path") || intent.hasExtra("extra")){
            if (zoomImageView!=null){
                imgPath = intent.getStringExtra("image_path");
                detailImgPath = intent.getStringExtra("detail_image_path");
                analysis = intent.getStringExtra("extra");

            }
        }



        if (imgPath != null) {
            setZoomImg(imgPath);
        }
        if (detailImgPath != null) setZoomImg(detailImgPath);

        if (analysis != null) {
            if (analysis.equals("analysis") || analysis.equals("analysis1"))
                imgAnalysis();
        }




    }



    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();
    }



//    @OnClick(R.id.captureVideo)
//    void captureVideo() {
//        camera.setCameraListener(new CameraListener() {
//            @Override
//            public void onVideoTaken(File video) {
//                super.onVideoTaken(video);
//            }
//        });
//
//        camera.startRecordingVideo();
//        camera.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                camera.stopRecordingVideo();
//            }
//        }, 3000);
//    }





    RadioGroup.OnCheckedChangeListener captureModeChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            camera.setMethod(
                    checkedId == R.id.modeCaptureStandard ?
                            CameraKit.Constants.METHOD_STANDARD :
                            CameraKit.Constants.METHOD_STILL
            );

            //Toast.makeText(TakePhoto.this, "Picture capture set to" + (checkedId == R.id.modeCaptureStandard ? " quality!" : " speed!"), Toast.LENGTH_SHORT).show();
        }
    };

    RadioGroup.OnCheckedChangeListener cropModeChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            camera.setCropOutput(
                    checkedId == R.id.modeCropVisible
            );

            //Toast.makeText(TakePhoto.this, "Picture cropping is" + (checkedId == R.id.modeCropVisible ? " on!" : " off!"), Toast.LENGTH_SHORT).show();
        }
    };


    RadioGroup.OnCheckedChangeListener widthModeChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            widthUpdate.setEnabled(checkedId == R.id.widthCustom);
            widthUpdate.setAlpha(checkedId == R.id.widthCustom ? 1f : 0.3f);
            width.clearFocus();
            width.setEnabled(checkedId == R.id.widthCustom);
            width.setAlpha(checkedId == R.id.widthCustom ? 1f : 0.5f);

            updateCamera(true, false);
        }
    };


    RadioGroup.OnCheckedChangeListener heightModeChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            heightUpdate.setEnabled(checkedId == R.id.heightCustom);
            heightUpdate.setAlpha(checkedId == R.id.heightCustom ? 1f : 0.3f);
            height.clearFocus();
            height.setEnabled(checkedId == R.id.heightCustom);
            height.setAlpha(checkedId == R.id.heightCustom ? 1f : 0.5f);

            updateCamera(false, true);
        }
    };

    private void updateCamera(boolean updateWidth, boolean updateHeight) {
        ViewGroup.LayoutParams cameraLayoutParams = camera.getLayoutParams();
        int width = cameraLayoutParams.width;
        int height = cameraLayoutParams.height;

        if (updateWidth) {
            switch (widthModeRadioGroup.getCheckedRadioButtonId()) {
                case R.id.widthCustom:
                    String widthInput = this.width.getText().toString();
                    if (widthInput.length() > 0) {
                        try {
                            width = Integer.valueOf(widthInput);
                        } catch (Exception e) {

                        }
                    }

                    break;

                case R.id.widthWrapContent:
                    width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    break;

                case R.id.widthMatchParent:
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                    break;
            }
        }

        if (updateHeight) {
            switch (heightModeRadioGroup.getCheckedRadioButtonId()) {
                case R.id.heightCustom:
                    String heightInput = this.height.getText().toString();
                    if (heightInput.length() > 0) {
                        try {
                            height = Integer.valueOf(heightInput);
                        } catch (Exception e) {

                        }
                    }
                    break;

                case R.id.heightWrapContent:
                    height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    break;

                case R.id.heightMatchParent:
                    height = parent.getHeight();
                    break;
            }
        }

        cameraLayoutParams.width = width;
        cameraLayoutParams.height = height;

        camera.addOnLayoutChangeListener(this);
        camera.setLayoutParams(cameraLayoutParams);

       // Toast.makeText(this, (updateWidth && updateHeight ? "新尺寸" : updateWidth ? "新宽" : "新高") + " 设置成功!", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "设置成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mCameraWidth = right - left;
        mCameraHeight = bottom - top;

        width.setText(String.valueOf(mCameraWidth));
        height.setText(String.valueOf(mCameraHeight));

        camera.removeOnLayoutChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search: // 智能分析
                mBtnSearch.setSelected(true);
                mBtnTakePhoto.setSelected(false);
                pic_status = PIC_ON_CAMERA;
                editor.commit();

                //配置功能
                FunctionConfig functionConfig = new FunctionConfig.Builder()
                        .setEnableCrop(true)
                        .setEnableRotate(true)
                        .setCropSquare(true)
                        .setEnablePreview(true)
                        .setEnableEdit(false)//编辑功能
                        .setEnableCrop(false)//裁剪功能
                        .setEnableCamera(false)//相机功能
                        .build();

                GalleryFinal.openGallerySingle(REQUEST_CODE, functionConfig, mOnHanlderResultCallback);
                break;

            case R.id.takephoto: // pose库
                mBtnTakePhoto.setSelected(true);
                mBtnSearch.setSelected(false);
                Intent intent = new Intent();
                pic_status = PIC_FOR_SELECT;
                Common.dismissProgressDialog(TakePhoto.this);
                finish();
                intent.setClass(TakePhoto.this, PosLibActivity.class);
                startActivity(intent);

                editor.putInt("flash", flashStatus);
                editor.putInt("front", frontStatus);
                editor.commit();

                break;

            case R.id.my:
                Intent intent1 = new Intent();
                finish();
                intent1.setClass(TakePhoto.this, LoginActivity.class);
                startActivity(intent1);

                editor.putInt("flash", flashStatus);
                editor.putInt("front", frontStatus);
                editor.commit();
                break;
        }
    }


    private void loadData(final ArrayList<HashMap<String, Object>> posLists) {
        getData(posLists);
        Collections.reverse(ArrayListHashMap);
        ArrayList<HashMap<String,Object>> subList = new ArrayList<>();

        for (int i = 0;i<9;i++){
            subList.add(ArrayListHashMap.get(i));
        }

        PosPicAdapter adapter = new PosPicAdapter(TakePhoto.this, subList,
                R.layout.gird_item, new String[]{"pospic"},
                new int[]{R.id.gvImg});

        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
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

    public static void setZoomImg(String imgPath) {
        if (imgPath != null) {
            System.out.println("imgPath=" + imgPath);
            bitmap = BitmapFactory.decodeFile(imgPath);
            if (bitmap != null) {
                zoomImageView.setImageBitmap(bitmap);
                zoomImageView.setAlpha((float) 0.45);
                if (zoomImageView.getVisibility() == View.VISIBLE ||
                        gridView.getVisibility() == View.VISIBLE) {
                    zoomImageView.setVisibility(View.GONE);
                } else {
                    zoomImageView.setVisibility(View.VISIBLE);
                }

                imgURL = imgPath;
            }
        }
    }

    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            Log.v("onHanlderSuccess", "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            for (PhotoInfo info : resultList) {
                switch (reqeustCode) {
                    case REQUEST_CODE:
                        photo_path = info.getPhotoPath();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (photo_path == null) {
                                    Toast.makeText(TakePhoto.this, "图片路径为空", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    // 数据返回
                                    bmp1 = BitmapUtil.decodeSampledBitmapFromFile(
                                            photo_path, 500, 500, TakePhoto.this);
                                    Youtu faceYoutu = new Youtu(Common.APP_ID,
                                            Common.SECRET_ID, Common.SECRET_KEY,
                                            Youtu.API_YOUTU_END_POINT);
                                    try {
                                        Common.showProgressDialog("分析中", TakePhoto.this);
                                        respose = faceYoutu.ImageTag(bmp1);
                                    } catch (KeyManagementException e) {
                                        // TODO Auto-generated catch block
                                        Common.dismissProgressDialog(TakePhoto.this);
                                        e.printStackTrace();
                                    } catch (NoSuchAlgorithmException e) {
                                        // TODO Auto-generated catch block
                                        Common.dismissProgressDialog(TakePhoto.this);
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        Common.dismissProgressDialog(TakePhoto.this);
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Common.dismissProgressDialog(TakePhoto.this);
                                        e.printStackTrace();
                                    }
                                    Message msg = handler.obtainMessage();
                                    int flag = Common.isNetworkAvailable(TakePhoto.this);
                                    if (flag == 0) {
                                        msg.what = -1;
                                        msg.obj = "请开启手机网络";
                                    } else {
                                        msg.what = 1;
                                        msg.obj = respose;
                                    }

                                    handler.sendMessage(msg);
                                    System.out.println(respose + "");
                                }
                                Looper.loop();
                            }
                        }).start();


                        break;

                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(TakePhoto.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();

        }
    };

    public void imgAnalysis() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                Bitmap bmp = BitmapUtil.decodeSampledBitmapFromFile(
                        PhotoProcessActivity.path, 500, 500, TakePhoto.this);


                if (bmp!= null) {
                    Youtu faceYoutu = new Youtu(Common.APP_ID,
                            Common.SECRET_ID, Common.SECRET_KEY,
                            Youtu.API_YOUTU_END_POINT);
                    try {

                        Common.showProgressDialog("分析中", TakePhoto.this);
                        respose = faceYoutu.ImageTag(bmp);
                    } catch (KeyManagementException e) {
                        // TODO Auto-generated catch block
                        Common.dismissProgressDialog( TakePhoto.this);
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        Common.dismissProgressDialog( TakePhoto.this);
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Common.dismissProgressDialog( TakePhoto.this);
                        e.printStackTrace();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Common.dismissProgressDialog( TakePhoto.this);
                        e.printStackTrace();
                    }
                    Message msg = handler.obtainMessage();
                    int flag = Common.isNetworkAvailable( TakePhoto.this);
                    if (flag == 0) {
                        msg.what = -1;
                        msg.obj = "请开启手机网络";
                    } else {
                        msg.what = 1;
                        msg.obj = respose;
                    }
                    handler.sendMessage(msg);
                    System.out.println(respose + "");
                }
                Looper.loop();

            }
        }).start();
    }




    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer tExit = new Timer();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                        hasTask = true;
                    }
                };
                tExit.schedule(task, 2000);

            } else {
                man.finishAllActivity();
                System.exit(0);

            }
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                if (!hasTask) {
                }
            } else {
                man.finishAllActivity();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }



}