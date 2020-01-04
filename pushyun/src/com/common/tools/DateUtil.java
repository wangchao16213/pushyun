package com.common.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * 日期处理工具类<br/>
 * @author 
 * @see 
 */
public class DateUtil {
	
	public static String getTimeDesc(Date dateTimeOld, Date dateTimeNew){   
		double second=DateUtil.minuSecond(dateTimeOld, dateTimeNew);
		return DateUtil.getTimeDesc(Double.valueOf(second).intValue());
	}
	
	public static String getTimeDesc(int second){   
        int h = 0;   
        int d = 0;   
        int s = 0;   
        int temp = second%3600;   
             if(second>3600){   
               h= second/3600;   
                    if(temp!=0){   
               if(temp>60){   
               d = temp/60;   
            if(temp%60!=0){   
               s = temp%60;   
            }   
            }else{   
               s = temp;   
            }   
           }   
          }else{   
              d = second/60;   
           if(second%60!=0){   
              s = second%60;   
           }   
          }   

         return h+"时"+d+"分"+s+"秒";   
       } 
	
	
	/**
	 * 
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param type   类型：getToday 得到今天的开始时间和结束时间， delToday 去掉今天之后的开始时间和结束时间
	 * @return 返回Date数组，data[0] 表示开始时间， data[1] 表示结束时间
	 */
	public static Date[] getSeparateDate(Date startTime,Date endTime,String type) {
		Date[] date=new Date[2];//data[0] 表示开始时间， data[1] 表示结束时间
		Date startToday=DateUtil.stringToDate(DateUtil.getDate(new Date())+" 00:00:00");
		Date endToday=DateUtil.stringToDate(DateUtil.getDate(new Date())+" 23:59:59");
		Calendar calendar = Calendar.getInstance(); // 获取当前日历
		calendar.setTime(new Date());
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
		Date endYestoday=DateUtil.stringToDate(DateUtil.getDate(calendar.getTime())+" 23:59:59");
		if(type.equals("getToday")){
			if(startTime.before(startToday)){//开始时间在今天之前
				if(endTime.before(startToday)){//结束时间在今天之前
					return null;
				}else{//结束时间包括了今天
					date[0]=startToday;
					date[1]=endTime;
				}
			}else {//开始时间在今天之后（可能包括今天）
				date[0]=startTime;
				date[1]=endTime;
			}
			if(startTime.getTime()<=startToday.getTime()&&startTime.getTime()>=endToday.getTime()){//开始时间在今天
				date[0]=startTime;
				date[1]=endToday;
			}
			if(startTime.after(endToday)){//开始时间在今天之后
				return null;
			}
		}else if(type.equals("delToday")){
			if(startTime.before(startToday)){//开始时间在今天之前
				if(endTime.before(startToday)){//结束时间在今天之前
					date[0]=startTime;
					date[1]=endTime;
				}else{//结束时间包括了今天
					date[0]=startTime;
					date[1]=endYestoday;
				}
			}else {//开始时间在今天之后（可能包括今天）
				return null;
			}
			if(startTime.getTime()<=startToday.getTime()&&startTime.getTime()>=endToday.getTime()){//开始时间在今天
				return null;
			}
			if(startTime.after(endToday)){//开始时间在今天之后
				return null;
			}
		}
		return date;
	}
	
	
	/**
	 * 把字符串格式化为日期yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static Date stringToDate(String date) {
		if(StringUtils.isBlank(date)){
			return null;
		}
		Date obj = null;
		SimpleDateFormat sdfOLD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			obj = sdfOLD.parse(date);
		} catch (ParseException ex) {
		}
		return obj;
	}
	/**
	 * 把字符串格式化为日期yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static Date stringToDate2(String date) {
		if(StringUtils.isBlank(date)){
			return null;
		}
		Date obj = new Date();
		SimpleDateFormat sdfOLD = new SimpleDateFormat("yyyy-MM-dd");
		try {
			obj = sdfOLD.parse(date);
		} catch (ParseException ex) {
		}
		return obj;
	}
	public static Date stringToDate(String date,String format) {
		if(StringUtils.isBlank(date)){
			return null;
		}
		if(StringUtils.isBlank(format)){
			format="yyyy-MM-dd HH:mm:ss";
		}
		Date obj = new Date();
		SimpleDateFormat sdfOLD = new SimpleDateFormat(format);
		try {
			obj = sdfOLD.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * 把日期转换为yyyy-MM-dd HH:mm:ss 格式
	 * 
	 * @param d
	 * @return
	 */
	public static String getDateTime(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d);
	}
	
	public static String getDateTime(Date d,String format) {
		if(d==null){
			return "";
		}
		if(StringUtils.isBlank(format)){
			format="yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d);
	}
	
	/**
	 * 把日期转换为yyyy-MM-dd 格式
	 * @param d
	 * @return
	 */
	public static String getDate(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d);
	}
	
	public static String getYear(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d).split("-")[0];
	}
	
	public static String getMonth(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d).split("-")[1];
	}
	
	public static String getDay(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d).split("-")[2];
	}
	
	/**
	 * 把日期转换为yyyyMMddHHmmss 格式
	 * 
	 * @param d
	 * @return
	 */
	public static String getNoDateTime(Date d) {
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); //设置时区 
		return sdf.format(d);
	}
/**
 * 得到计算之后的时间
 * @param old
 * @param virtualTiem
 * @return
 */
	public static Date getDate(Date old, int virtualTiem) {
		Date dateOld = null;
		try {
			dateOld = old;
			Calendar celendar = Calendar.getInstance(); // 当时的日期和时间
			celendar.setTime(dateOld);
			int secondOld = celendar.get(Calendar.SECOND); // 取出“秒”数
			secondOld = secondOld + virtualTiem; // 将秒＋有效时间
			celendar.set(Calendar.SECOND, secondOld); // 将“小时”数设置回去
			dateOld = celendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateOld;
	}
	
	public static Date getDate(Date old, int virtualTiem,int unit) {
		Date dateOld = null;
		try {
			dateOld = old;
			Calendar celendar = Calendar.getInstance(); // 当时的日期和时间
			celendar.setTime(dateOld);
			int secondOld = celendar.get(unit); // 
			secondOld = secondOld + virtualTiem; // 
			celendar.set(unit, secondOld); //
			dateOld = celendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateOld;
	}
	 /**
	 * 功能:格式化日期(20070529180124 或者20070529)
	 * 2007121201010045
	 * 20070529180124
	 * @param servicetime
	 * @return 日期标准格式(2007-05-29 18:01:24)
	 */
	public static String getFormatDate(String servicetime) {
		StringBuffer sb = new StringBuffer("");
		 if(servicetime.indexOf("-")!=-1){
		    String[] str=servicetime.split("-");
		    sb.append(str[0]);// 年
		    sb.append("-");
		    if(str[1].length()==1){
			 sb.append("0"+str[1]);// 月
		    }else{
			 sb.append(str[1]);// 月
		    }
		    sb.append("-");
		    if(str[2].length()==1){
			 sb.append("0"+str[2]);// 日
		    }else{
			 sb.append(str[2]);//日
		    }
		    return sb.toString();
		}else if (servicetime.length() == 14) {// 格式为20070529180124
			sb.append(servicetime.substring(0, 4));// 年
			sb.append("-");
			sb.append(servicetime.substring(4, 6));// 月
			sb.append("-");
			sb.append(servicetime.substring(6, 8));// 日
			sb.append(" ");
			sb.append(servicetime.substring(8, 10));// 小时
			sb.append(":");
			sb.append(servicetime.substring(10, 12));// 分
			sb.append(":");
			sb.append(servicetime.substring(12, 14));// 分
			return sb.toString();
		} else if (servicetime.length() == 8) {// 格式为20070529
			sb.append(servicetime.substring(0, 4));// 年
			sb.append("-");
			sb.append(servicetime.substring(4, 6));// 月
			sb.append("-");
			sb.append(servicetime.substring(6, 8));// 日
			return sb.toString();
		} else{
			return null;
		}
	}
	public static int minuSecond(String dateTimeOld, String dateTimeNew) {
		int iResult = 0;
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateNew = null;
		Date dateOld = null;
		try {
			dateOld = formater.parse(dateTimeOld);
			dateNew = formater.parse(dateTimeNew);
			long oldTime = dateOld.getTime();
			long newTime = dateNew.getTime();
			iResult = (int) (newTime - oldTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iResult;
	}
	
	/***
	 * 返回二个时间的秒数
	 * @param dateTimeOld
	 * @param dateTimeNew
	 * @return
	 */
	public static double minuSecond(Date dateTimeOld, Date dateTimeNew) {
		double iResult = 0;
		try {
			long oldTime = dateTimeOld.getTime();
			long newTime = dateTimeNew.getTime();
			iResult =(newTime - oldTime)*0.001;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iResult;
	}
	
	 public static void main(String[] args) {
		 System.out.println( DateUtil.getDate(new Date(), -60));
		
	 }
}
