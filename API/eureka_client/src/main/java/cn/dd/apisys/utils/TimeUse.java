package cn.dd.apisys.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * FileName: TimeUse.java
 * CreateTime: 2020/6/3 16:51.
 * Version: V1.0
 * Author: ChengTao
 * Description: 系统耗时辅助类
 */
public class TimeUse {
    private Long starTime;

    public TimeUse() {
        start();
    }

    public void start() {
        starTime = System.currentTimeMillis();
    }

    public Long end() {
        return System.currentTimeMillis() - starTime;
    }

    public static String getDateStr() {
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.MONTH, -5); //年份减1
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date lastMonth = ca.getTime(); //结果
        //求前一月ca.add(Calendar.MONTH, -1)，
        //return sf.format(lastMonth)+" 00:00:00";
        return "2016-01-01 00:00:00";
    }

    //获取当前时间
    public static String getCurrtntTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(System.currentTimeMillis());
    }
    //获取当前日期
    public static String getCurrtntDate(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(System.currentTimeMillis());
    }
}
