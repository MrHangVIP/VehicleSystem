package com.lhj.vehiclesystem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String Date_Format_1="yyyy-MM-dd HH:mm:ss";
	public static final String Date_Format_2="yyyy-MM-dd HH:mm";
	public static final String Date_Format_3="yyyy-MM-dd";
	public static final String TIME_Format_1="HH:mm:ss";
	public static final String TIME_Format_2="HH:mm";

	private static SimpleDateFormat sdf = null;

	/**
	 * 获取系统时间 格式为："yyyy-MM-dd HH:mm:ss " 
	 */
	public static String getCurrentDate() {
		Date d = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	/**
	 * 获取系统时间 格式为："yyyy-MM-dd HH:mm:ss "
	 */
	public static String getCurrentDate(String format) {
		Date d = new Date();
		sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 *  时间戳转换成字符窜
	 *   */
	public static String getDateToString(long time) {
		Date d = new Date(time);
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	/**
	 * 将字符串转为时间戳
	 * */
	public static long getStringToDate(String time) {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime()/1000;
	}

	/**
	 * 日期字符串转换时间
	 * @param time
	 * @param format 当前格式
     * @return
     */
	public static long getStringToDate(String time, String format) {
		sdf = new SimpleDateFormat(format);
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
	
}
