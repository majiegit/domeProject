package com.hx.util;

import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {
    private static final Logger logger = LoggerFactory.getLogger(DateTool.class);
    public static final String datefmt2 = "yyyy-MM-dd HH:mm:ss";

    public static final String datefmt3 = "yyyyMMddHHmmss";

    /**
     * 根据参数中的Cron表达式，计算任务下次执行时间，获取任务需要休眠的毫秒数
     * @param cron
     * @return
     */
    /*public static long getSleepTimeLong(String cron){
        Long timeLong = 0L;
        try {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
            Date currDate=new Date();
            Date nextExecDate = cronSequenceGenerator.next(currDate);
            timeLong=nextExecDate.getTime()-currDate.getTime();
        } catch (Exception e) {
            logger.error("根据Cron计算休眠时间出现错误："+e.toString()+",Cron:"+cron);
            timeLong=getDefaultSleepTimeLong(cron);
        }
        return timeLong;
    }
    public  static long getDefaultSleepTimeLong(String cron){
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
        Date currDate=new Date();
        Date nextExecDate = cronSequenceGenerator.next(currDate);
        Long timeLong=nextExecDate.getTime()-currDate.getTime();
        return timeLong;
    }*/
    public static long getSleepTimeLong(String cron){
        Long timeLong = 1000*10L;// 10s
        try {
        	CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        	cronTriggerImpl.setCronExpression(cron);
            Date currDate=new Date();
            cronTriggerImpl.setStartTime(currDate);

            Date nextExecDate = cronTriggerImpl.getFireTimeAfter(currDate);
            timeLong=nextExecDate.getTime()-currDate.getTime();
        } catch (Exception e) {
            logger.error("根据Cron计算休眠时间出现错误："+e.toString()+",Cron:"+cron);
            timeLong=getDefaultSleepTimeLong(cron);
        }
        return timeLong;
    }
    public  static long getDefaultSleepTimeLong(String cron){
        Long timeLong = 1000*10L;// 10s
        try {
        	CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        	cronTriggerImpl.setCronExpression(cron);
            Date currDate=new Date();
            cronTriggerImpl.setStartTime(currDate);
            Date nextExecDate = cronTriggerImpl.getNextFireTime();
            timeLong=nextExecDate.getTime()-currDate.getTime();
        } catch (Exception e) {
            logger.error("根据Cron计算休眠时间出现错误："+e.toString()+",Cron:"+cron);
            timeLong=getDefaultSleepTimeLong(cron);
        }
        return timeLong;
    }

    /**
     * 根据cron表达式计算上次执行的时间
     * @param cron
     * @param timeFormat
     * @return
     */
    public  static String getlastTime(String cron,String timeFormat){
        String lastTime=null;
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            Date time0 = cronTriggerImpl.getStartTime();
            Date time1 = cronTriggerImpl.getFireTimeAfter(time0);
            Date time2 = cronTriggerImpl.getFireTimeAfter(time1);
            Date time3 = cronTriggerImpl.getFireTimeAfter(time2);
            long last = time1.getTime() -(time3.getTime() -time2.getTime());
            SimpleDateFormat ft = new SimpleDateFormat(timeFormat);
            lastTime=  ft.format(new Date(last));
        } catch (Exception e) {
            logger.error("根据Cron计算上次执行时间出现错误："+e.toString()+",Cron:"+cron);
        }
        return lastTime;
    }
    /**
     * 根据cron表达式计算  时间间隔
     */
    public  static long getinterval(String cron){
        long interval=0;
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            Date time0 = cronTriggerImpl.getStartTime();
            Date time1 = cronTriggerImpl.getFireTimeAfter(time0);
            Date time2 = cronTriggerImpl.getFireTimeAfter(time1);
            Date time3 = cronTriggerImpl.getFireTimeAfter(time2);
            interval = time3.getTime() -time2.getTime();
        } catch (Exception e) {
            logger.error("根据Cron计算时间间隔出现错误："+e.toString()+",Cron:"+cron);
        }
        return interval;
    }

    /**
	 * 世界时转北京时
	 */
	public static Date UTCToCST(Long UTCStr) throws Exception {
		Date date = new Date(UTCStr);
//    SimpleDateFormat sdf = new SimpleDateFormat(format);
//    date = sdf.parse(UTCStr);
		System.out.println("世界時: " + date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
		// calendar.getTime() 返回的是Date类型，也可以使用calendar.getTimeInMillis()获取时间戳
		System.out.println("北京时间: " + calendar.getTime());
		return calendar.getTime();
	}

	/*
	 * 北京时转世界时
	 */
	public static Date CSTToUTC(Long CSTStr) throws Exception {
		Date date = new Date(CSTStr);
//    SimpleDateFormat sdf = new SimpleDateFormat(format);
//    date = sdf.parse(CSTStr);
		System.out.println("北京时: " + date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - 8);
		// calendar.getTime() 返回的是Date类型，也可以使用calendar.getTimeInMillis()获取时间戳
		System.out.println("世界時: " + calendar.getTime());
		return calendar.getTime();
	}

	public static String Date2Str(Date date, String fmt) throws Exception {
		SimpleDateFormat sd = new SimpleDateFormat(fmt);
		return sd.format(date);
	}


	public static Date Str2Date(String str, String fmt) throws Exception {
		SimpleDateFormat sd = new SimpleDateFormat(fmt);
		return sd.parse(str);
	}
    public static String formateDate(Date date, String fmt) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat(fmt);
        return sd.format(date);
    }
}
