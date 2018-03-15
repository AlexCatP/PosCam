package com.psy.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.psy.util.Common;
import com.psy.util.HttpHelper;
import com.psy.util.SquareLayout;
import com.psy.util.URL;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;


public class PosPicAdapter extends BaseAdapter {
    private int resource;
    private List<HashMap<String, Object>> data;
    private Context context;
    private ViewHolder holder = null;
    private LayoutInflater inflater;
    private DisplayImageOptions options;

    public PosPicAdapter(Context context, List<HashMap<String, Object>> data,
                         int resource, String[] from, int[] to) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.resource = resource;
        this.context = context;
        // 使用ImageLoader加载网络图片
        options = new DisplayImageOptions.Builder()//
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)//图片大小刚好满足控件尺寸
                .showImageForEmptyUri(R.drawable.head) //
                .showImageOnFail(R.drawable.head) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.posPic = (ImageView) convertView.findViewById(R.id.gvImg);
            holder.item = (SquareLayout) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final String path =
                "http://" + data.get(position).get("pospic").toString();




        //防止图片闪烁
        final String tag = (String) holder.posPic.getTag();



        if (tag==null||!tag.equals(path)) {
            holder.posPic.setTag(path);
            com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                    .displayImage(path, holder.posPic, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder.posPic.setTag(path);//确保下载完成再打tag.
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }


        final String localPath = Common.FULL_IMG_CACHE_PATH + path.hashCode();

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String updatePb =
                            postData(data.get(position).get("posid").toString(),((int) data.get(position).get("pospb") + 1)+"");

                    if (TakePhoto.pic_status == TakePhoto.PIC_FOR_SELECT) {
                        if (HttpHelper.getCode(updatePb) == 100) {
                            Activity activity = (Activity) context;
                            Common.fragParamName = "image_path";
                            Common.fragParam = localPath;
                            Intent intent = new Intent();
                            intent.putExtra(  Common.fragParamName, Common.fragParam);
                            intent.setClass(activity,TakePhoto.class);
                           activity.startActivity(intent);
                        } else Common.display(context, "错误信息：UPDATE_PB_BY_PID_01");
                    }
                    if (TakePhoto.pic_status == TakePhoto.PIC_FOR_DETAIL) {
                        if (HttpHelper.getCode(updatePb) == 100) {
                        Intent intent = new Intent();
                        Activity activity = (Activity) context;
                        activity.finish();
                        intent.putExtra("posId", data.get(position).get("posid").toString());
                        intent.setClass(activity, DetailActivity.class);
                        activity.startActivity(intent);
                        } else Common.display(context, "错误信息：UPDATE_PB_BY_PID_02");
                    }
                    if (TakePhoto.pic_status == TakePhoto.PIC_ON_CAMERA) {
                        if (localPath != null) {
                            if (HttpHelper.getCode(updatePb) == 100) {
                                TakePhoto.gridView.setVisibility(View.GONE);
                                TakePhoto.resultll.setVisibility(View.GONE);
                                TakePhoto.setZoomImg(localPath);
                            } else Common.display(context, "错误信息：UPDATE_PB_BY_PID_03");
                        }
                    }

                }
                catch (JSONException e){
                    Common.display(context, "服务器错误:JSONException");
                }
                catch (Exception e){
                    Common.display(context, "服务器错误:Exception");
                }

            }







        });


        return convertView;

    }

    public String postData(String posid,String pospb) throws Exception {
        HashMap<String, String> paramHM = new HashMap<>();
        paramHM.put("pos_id", posid);
        paramHM.put("pos_pb", pospb);
        return HttpHelper.postData(URL.UPDATE_PB_BY_PID, paramHM, null);
    }



    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return super.isEnabled(position);
    }

    private final class ViewHolder {
        public ImageView posPic;
        public SquareLayout item;
    }

}
