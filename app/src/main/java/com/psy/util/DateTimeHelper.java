package com.psy.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeHelper {

	@SuppressLint("SimpleDateFormat")
	public static String FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static String DATE_FORMAT_TILL_SECOND = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat DATE_TILL_DAY_CURRENT_YEAR = new SimpleDateFormat(
			"MM-dd");
	public static SimpleDateFormat DATE_TILL_DAY_CH = new SimpleDateFormat(
			"yyyy-MM-dd");


	/**
	 * 日期字符串转换为Date
	 * 
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date strToDate(String dateStr, String format) {
		Date date = null;

		if (!TextUtils.isEmpty(dateStr)) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			try {
				date = df.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	// 获取年份
	public static int getYear() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 0);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 日期转换为字符串
	 * 
	 * @param timeStr
	 * @param format
	 * @return
	 */
	public static String dateToString(String timeStr, String format) {
		// 判断是否是今年
		Date date = strToDate(timeStr, format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 如果是今年的话，才取“xx月xx日”日期格式
		if (calendar.get(Calendar.YEAR) == getYear()) {
			return DATE_TILL_DAY_CURRENT_YEAR.format(date);
		}

		return DATE_TILL_DAY_CH.format(date);
	}

	/**
	 * 日期逻辑
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @return
	 */
	public static String timeLogic(String dateStr, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.get(Calendar.DAY_OF_MONTH);
		long now = calendar.getTimeInMillis();
		Date date = strToDate(dateStr, format);
		calendar.setTime(date);
		long past = calendar.getTimeInMillis();

		// 相差的秒数
		long time = (now - past) / 1000;

		StringBuffer sb = new StringBuffer();
		if (time > 0 && time < 60) { // 1小时内
			return sb.append(time + "秒前").toString();
		} else if (time > 60 && time < 3600) {
			return sb.append(time / 60 + "分钟前").toString();
		} else if (time >= 3600 && time < 3600 * 24) {
			return sb.append(time / 3600 + "小时前").toString();
		} else if (time >= 3600 * 24 && time < 3600 * 48) {
			return sb.append("昨天").toString();
		} else if (time >= 3600 * 48 && time < 3600 * 72) {
			return sb.append("前天").toString();
		} else if (time >= 3600 * 72) {
			return dateToString(dateStr, format);
		}
		return dateToString(dateStr, format);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return 当前日期
	 */
	public static String getCurrentDateTime() {
		 Date date=new Date();
		   SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFm.format(date);
	}

	/**
	 * 判断是否白天
	 * @return
	 */
	public static boolean isDay() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<=18) {
			return true;
		}
		else
		return false;
		
	}
}
