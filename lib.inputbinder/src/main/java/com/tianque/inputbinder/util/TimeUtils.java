package com.tianque.inputbinder.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeUtils {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }


    public static SimpleDateFormat getSimpleDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    /**
     * 获得当前时间  按照格式：formatString
     *
     * @return
     */
    public static String getSimpleDate(String formatString) {
        return new SimpleDateFormat(formatString, Locale.getDefault()).format(new Date());
    }

    /**
     * 获得当前时间  按照格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getSimpleDate() {
        return getSimpleDateFormat().format(new Date());
    }

    /**
     * 获得当前时间  按照格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getSimpleDate(Date date) {
        return getSimpleDateFormat().format(date);
    }

    /**
     * 获得2个时间的间隔时间 单位：毫秒
     *
     * @param old_date 旧时间
     * @param new_date 新时间
     * @return
     */
    private static long getIntervalTime(Date old_date, Date new_date) {
        long second = 0;
        try {
            // 得到间隔秒数
            second = (new_date.getTime() - old_date.getTime());
        } catch (Exception e) {
            return 0;
        }
        return second;
    }

    /**
     * 获得2个时间的间隔时间 单位：毫秒
     *
     * @param oldTime 旧时间
     * @param newTime 新时间
     * @return
     */
    public static long getIntervalTime(String oldTime, String newTime) throws ParseException {
        return getIntervalTime(getDateFromString(oldTime), getDateFromString(newTime));
    }

    /**
     * 根据时间字符串得到日期
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date getDateFromString(String time) throws ParseException {
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        return simpleDateFormat.parse(time);
    }

    /**
     * 获取指定时间到当前时间的间隔 单位：毫秒
     *
     * @param oldTime 指定时间
     * @return
     * @throws ParseException
     */
    public static long getIntervalTime(String oldTime) throws ParseException {
        return getIntervalTime(simpleDateFormat.parse(oldTime), new Date());
    }

    /**
     * 比较时间大小、先后，
     * time1比time2晚返回1
     * time1比time2早返回-1
     * 一样返回0
     *
     * @param time1
     * @param time2
     * @return
     * @throws ParseException
     */
    public static int dateTimeCompara(String time1, String time2)
            throws ParseException {
        Date dt1 = simpleDateFormat.parse(time1);
        Date dt2 = simpleDateFormat.parse(time2);
        if (dt1.getTime() > dt2.getTime()) {
//			System.out.println("dt1 在dt2前");
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
//			System.out.println("dt1在dt2后");
            return -1;
        } else {
            return 0;
        }
    }

    public static String getDateAsString(String dateStr, String format) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }

        Date date = null;
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat(format, Locale.getDefault());
            date = sFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getDateAsString(date, format);
    }

    public static String getDateAsString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sFormat = new SimpleDateFormat(format, Locale.getDefault());

        return sFormat.format(date);
    }

    public static Date getDateFromString(String dateStr, String strformat) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(strformat);
        Date date = format.parse(dateStr);
        return date;
    }

    public static List<Date> getDateFromStrList(List<String> dateStr, String strformat) throws ParseException {
        List<Date> dateList = new ArrayList<>();
        if (dateStr.size() >= 0) {
            for (int i = 0; i < dateStr.size(); i++) {
                String formatstr = dateStr.get(i);
                SimpleDateFormat format = new SimpleDateFormat(strformat);
                Date date = format.parse(formatstr);
                dateList.add(date);
            }
        }
        return dateList;
    }

    public static String getWeekStr(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getWeek(calendar);
    }


    /*获取星期几*/
    public static String getWeek(Calendar calendar) {
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

}
