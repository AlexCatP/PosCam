package com.psy.util;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.psy.model.Param;
import com.psy.model.PosLib;
import com.psy.model.YouTuTag;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ppssyyy on 2016-08-05.
 */

/**
 * K_Top的参数
 */
public class DataConvert {
    /**
     * 生成tag字典
     * @return
     */
    public static HashMap<String, Integer> toTagMap() {
        HashMap<String, Integer> tagMap = new HashMap<>();
        try {
            String json = HttpHelper.sendGet(URL.FIND_ALL_TAGS,null);
            ArrayList<HashMap<String,Object>> ah =
                    HttpHelper.AnalysisTagInfo(json);
            if (ah!=null){
                for (int i =0;i<ah.size();i++){
                   tagMap.put(ah.get(i).get("tag").toString(),(Integer) ah.get(i).get("tagid"));
                }
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tagMap;
    }

//    /**
//     * 通过txt文件生成tag字典
//     * @param context
//     * @return
//     */
//    public static HashMap<String, Integer> toTagMap(Context context) {
//        InputStream tagIs;
//        HashMap<String, Integer> tagMap = null;
//        try {
//            tagIs = context.getResources().getAssets().open("tag.txt");
//            BufferedReader br = new BufferedReader(new InputStreamReader(tagIs));
//            String temString = null;
//            tagMap = new HashMap<>();
//            int count = 0;
//            while ((temString = br.readLine()) != null) {
//                count++;
//                tagMap.put(temString.trim(), count);
//            }
//            tagIs.close();
//            br.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return tagMap;
//    }

    /**
     * 得到数据库图片tag
     * @param tagMap tag字典
     * @param poses 数据库所有pose对象
     * @return
     */

    public static List<List<Integer>> getParam1(HashMap<String, Integer> tagMap, ArrayList<PosLib> poses) {
        List<List<Integer>> ll = new ArrayList<>();
        List<Integer> listInt;

        for (int i = 0; i < poses.size(); i++) {
            listInt = new ArrayList<>();
            String[] tags = poses.get(i).getTags().split("_");
            for (int j = 0; j < tags.length; j++) {
                listInt.add(tagMap.get(tags[j]));
            }
            ll.add(listInt);
        }
        return ll;
    }

    /**
     * 得到要搜索的图片tag
     * @param tagMap
     * @param tags
     * @return
     */
    public static List<Integer> getParam2(HashMap<String, Integer> tagMap, ArrayList<YouTuTag> tags) {
        List<Integer> listInt = new ArrayList<>();
        //Collections.sort(tags);
        for (int i = 0; i < tags.size(); i++) {

            if(tagMap.get(tags.get(i).getTagName())==null){
                listInt.add(-1);
            }
            else
            listInt.add(
                    tagMap.get(tags.get(i).getTagName()));
        }
        return listInt;
    }

    /**
     * 得到要搜索的图片tag权重 （服务端要传的参数）
     * @param tags
     * @return
     */

    public static List<Double> getParam3(ArrayList<YouTuTag> tags) {
        List<Double> list = new ArrayList<>();
        //Collections.sort(tags);
        for (int i = 0; i < tags.size(); i++) {
            list.add((tags.get(i).getTagConfidence() + 0.0));
        }
        return list;
    }

    public static HashMap<Integer,Double> getMergedParam(List<Integer> getParam2,List<Double> getParam3){
        HashMap<Integer,Double> hm = new HashMap<>();
        for (int i=0;i<getParam2.size();i++) {
            hm.put(getParam2.get(i),getParam3.get(i));
        }
        return hm;
    }

    /**
     * 得到最终结果
     * @param poses
     * @param p
     * @return
     */
    public static ArrayList<PosLib> getResult(ArrayList<PosLib> poses,Integer[] p){
        ArrayList<PosLib> result = new ArrayList<>();
        for (int i=0;i<p.length;i++){
            result.add(poses.get(p[i]));
        }
        return result;
    }

    public static String toJsonArray(HashMap<Integer,Double> param){
        return  JSON.toJSONString(param);
    }

}
