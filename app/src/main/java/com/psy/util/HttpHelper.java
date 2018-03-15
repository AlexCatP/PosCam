package com.psy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.psy.model.YouTuTag;

//import org.apache.http.entity.ContentType;


public class HttpHelper {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码


	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url    发送请求的URL
	 * @param params 请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendGet(String url, String params) {
		String str = "";
		try {
			String urlPath = null;
			if (params==null)
				urlPath = url;
			else
				urlPath = url + "?" + params;
			System.out.println(urlPath);
			HttpGet request = new HttpGet(urlPath);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			request.addHeader("charset",HTTP.UTF_8);

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
			// 设置连接超时
			HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
			// 设置请求超时
			request.setParams(httpParams);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(request);
			HttpEntity responseEntity = response.getEntity();

			InputStreamReader isr = new InputStreamReader(
					responseEntity.getContent());

			// 使用缓冲一行行的读入，加速InputStreamReader的速度
			BufferedReader buffer = new BufferedReader(isr);
			String inputLine = null;

			StringBuilder resultData = new StringBuilder("");
			while ((inputLine = buffer.readLine()) != null) {
				resultData.append(inputLine);
				resultData.append("\n");
			}
			buffer.close();
			isr.close();
			str = resultData.toString();
			System.out.println(str);
			if(str.charAt(0)=='<')
				str = null;
			return str;
		} catch (Exception e) {
			str = " 发送GET请求出现异常：" + e;
			e.printStackTrace();
			return null;
		}
	}


	public static int getCode(String jsonStr) throws JSONException {
		int code = 0;
		if (jsonStr == null) {
			code = 0;
		} else {
			JSONObject jsonObject = new JSONObject(jsonStr);
			code = jsonObject.getInt("code");
		}
		return code;
	}

	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;

	}

	/**
	 * 同时传送文件和字符串
	 *
	 * @param url
	 * @param paramHM
	 * @param fileHM
	 */

	public static String postData(String url, HashMap<String, String>
			paramHM, HashMap<String, String> fileHM) throws Exception {

		ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
		HttpClient client = new DefaultHttpClient();// 开启一个客户端 HTTP 请求
		HttpPost post = new HttpPost(url);//创建 HTTP POST 请求
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setCharset(Charset.forName(HTTP.UTF_8));//设置请求的编码格式
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式

		if (paramHM != null) {
			Iterator iter = paramHM.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				StringBody stringBody =
						new StringBody(val.toString(), contentType);
				builder.
						addPart(key.toString(),
								stringBody);
			}
		}

		if (fileHM != null) {
			Iterator iter1 = fileHM.entrySet().iterator();
			while (iter1.hasNext()) {
				Map.Entry entry = (Map.Entry) iter1.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				builder.addBinaryBody(key.toString(),
						new File(val.toString()));
			}
		}

		HttpEntity entity = builder.build();// 生成 HTTP POST 实体
		post.setEntity(entity);//设置请求参数
		HttpResponse response = client.execute(post);// 发起请求 并返回请求的响应
		String res = EntityUtils.toString(response.getEntity());
		System.out.println(res);
		return  res;
	}


	// 把一个url的网络图片变成一个本地的BitMap
	public static Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;


	}

	public static ArrayList<YouTuTag> getTags(JSONObject jsonObject) {
		ArrayList<YouTuTag> tags = new ArrayList<>();
		YouTuTag youTuTag;
		if (jsonObject == null) {
			youTuTag = new YouTuTag();
			youTuTag.setTagName("error");
			youTuTag.setTagConfidence(0);
			tags.add(youTuTag);
		} else {
			try {
				if (jsonObject.getInt("errorcode") == 0
						&& jsonObject.getString("errormsg").equals("OK")) {
					JSONArray jsonArray = jsonObject.getJSONArray("tags");

					for (int i = 0; i < jsonArray.length(); i++) {
						youTuTag = new YouTuTag();
						youTuTag.setTagName(jsonArray.getJSONObject(i).getString("tag_name"));
						youTuTag.setTagConfidence(jsonArray.getJSONObject(i).getInt("tag_confidence"));
						tags.add( youTuTag);

					}
				}
				else {
					youTuTag = new YouTuTag();
					youTuTag.setTagName("error");
					youTuTag.setTagConfidence(0);
					tags.add(youTuTag);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return tags;
	}

	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static HashMap<String, Object> AnalysisUid(
			String jsonStr) throws JSONException {

		if (getCode(jsonStr) == 100) {
			JSONObject jsonObject = new JSONObject(jsonStr);
			int uid = jsonObject.getInt("obj");
			HashMap<String, Object> hm = new HashMap<>();
			hm.put("userid", uid);
			return hm;
		} else {
			return null;
		}

	}
	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static HashMap<String, Object> AnalysisUserInfo(
			String jsonStr) throws JSONException {

		if (getCode(jsonStr) == 100) {

			JSONObject jsonObject = new JSONObject(jsonStr);
			String objStr = jsonObject.getString("obj");
			JSONObject obj = new JSONObject(objStr);
			HashMap<String, Object> hm = new HashMap<>();

			hm.put("userid", obj.getInt("userId"));
			hm.put("username", obj
					.getString("userName"));
			hm.put("userphone", obj
					.getString("userPhone"));
			hm.put("userpb", obj
					.getInt("userPb"));
			hm.put("taskpic_url", obj
					.getString("userPicUrl"));
			hm.put("password", obj
					.getString("passWord"));
			return hm;
		} else {
			return null;
		}

	}

	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static HashMap<String, Object> AnalysisSinglePos(
			String jsonStr) throws JSONException {

		if (getCode(jsonStr) == 100) {

			JSONObject jsonObject = new JSONObject(jsonStr);
			String objStr = jsonObject.getString("obj");
			JSONObject obj = new JSONObject(objStr);
			HashMap<String, Object> hm = new HashMap<>();

			hm.put("posid", obj
					.getInt("postId"));
			hm.put("typeid",obj
					.getInt("typeId"));
			hm.put("tags",obj
					.getString("tags"));
			hm.put("userid",obj
					.getInt("userId"));
			hm.put("pospb", obj
					.getInt("posePb"));
			hm.put("posname", obj
					.getString("poseName"));
			hm.put("pos_pic_url", obj
					.getString("posPicUrl"));
			hm.put("poscontent", obj
					.getString("posContent"));

			return hm;
		} else {
			return null;
		}

	}


	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static  ArrayList<HashMap<String, Object>> AnalysisUserInfo2(
			String jsonStr) throws JSONException {
		ArrayList<JSONObject> jsonArrayList = TOJsonArray(jsonStr);
		if (jsonArrayList == null) {
			return null;
		} else {

			ArrayList<HashMap<String, Object>> pArrayList = new ArrayList<>();

			for (int i = 0; i < jsonArrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("userid", jsonArrayList.get(i)
						.getInt("userId"));
				hm.put("username",jsonArrayList.get(i)
						.getString("userName"));
				hm.put("userphone",jsonArrayList.get(i)
						.getString("userPhone"));
				hm.put("userpb",jsonArrayList.get(i)
						.getInt("userPb"));
				hm.put("taskpic_url", jsonArrayList.get(i)
						.getString("userPicUrl"));
				hm.put("password", jsonArrayList.get(i)
						.getString("passWord"));
				pArrayList.add(hm);

			}
			return pArrayList;


		}


	}

	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<HashMap<String, Object>> AnalysisPosInfo(
			String jsonStr ) throws JSONException {
		ArrayList<JSONObject> jsonArrayList = TOJsonArray(jsonStr);
		if (jsonArrayList == null) {
			return null;
		} else {

			ArrayList<HashMap<String, Object>> pArrayList = new ArrayList<>();

			for (int i = 0; i < jsonArrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("posid", jsonArrayList.get(i)
						.getInt("postId"));
				hm.put("typeid",jsonArrayList.get(i)
						.getInt("typeId"));
				hm.put("tags",jsonArrayList.get(i)
						.getString("tags"));
				hm.put("pospb",jsonArrayList.get(i)
						.getInt("posePb"));
				hm.put("posname", jsonArrayList.get(i)
						.getString("poseName"));
				hm.put("pos_pic_url", jsonArrayList.get(i)
						.getString("posPicUrl"));
				hm.put("poscontent", jsonArrayList.get(i)
						.getString("posContent"));
				hm.put("userid",jsonArrayList.get(i)
						.getInt("userId"));
				pArrayList.add(hm);

			}
			return pArrayList;


		}
	}

	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<HashMap<String, Object>> AnalysisTagInfo(
			String jsonStr ) throws JSONException {
		ArrayList<JSONObject> jsonArrayList = TOJsonArray(jsonStr);
		if (jsonArrayList == null) {
			return null;
		} else {

			ArrayList<HashMap<String, Object>> pArrayList = new ArrayList<>();

			for (int i = 0; i < jsonArrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("tagid", jsonArrayList.get(i)
						.getInt("tagId"));
				hm.put("tag",jsonArrayList.get(i)
						.getString("tag"));
				pArrayList.add(hm);

			}
			return pArrayList;


		}
	}



	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<HashMap<String, Object>> AnalysisPosInfo2(
			String jsonStr ) throws JSONException {
		ArrayList<JSONObject> jsonArrayList = TOJsonArray2(jsonStr);
		if (jsonArrayList == null) {
			return null;
		} else {
			ArrayList<HashMap<String, Object>> pArrayList = new ArrayList<>();
			for (int i = 0; i < jsonArrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("posid", jsonArrayList.get(i)
						.getInt("postId"));
				hm.put("typeid",jsonArrayList.get(i)
						.getInt("typeId"));
				hm.put("tags",jsonArrayList.get(i)
						.getString("tags"));
				hm.put("pospb",jsonArrayList.get(i)
						.getInt("posePb"));
				hm.put("posname", jsonArrayList.get(i)
						.getString("poseName"));
				hm.put("pos_pic_url", jsonArrayList.get(i)
						.getString("posPicUrl"));
				hm.put("poscontent", jsonArrayList.get(i)
						.getString("posContent"));
				hm.put("userid",jsonArrayList.get(i)
						.getInt("userId"));
				pArrayList.add(hm);

			}
			return pArrayList;


		}
	}



	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<JSONObject> TOJsonArray(
			String jsonStr) throws JSONException {
		if (jsonStr == null) {
			return null;
		} else {
			JSONObject jsonObject = new JSONObject(jsonStr);
			if (jsonObject.getInt("code") == 100) {
				JSONArray jsonArray = jsonObject.getJSONArray("obj");

				ArrayList<JSONObject> pArrayList = new ArrayList<>();
				for (int i = 0; i < jsonArray.length(); i++) {
					pArrayList.add(jsonArray.getJSONObject(i));
				}

				return pArrayList;

			} else
				return null;
		}
	}

	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<JSONObject> TOJsonArray2(
			String jsonStr) throws JSONException {
		if (jsonStr == null) {
			return null;
		} else {
			JSONArray jsonArray = new JSONArray(jsonStr);
			ArrayList<JSONObject> pArrayList = new ArrayList<>();
			for (int i = 0; i < jsonArray.length(); i++) {
				pArrayList.add(jsonArray.getJSONObject(i));
			}
			return pArrayList;
		}
	}


}

