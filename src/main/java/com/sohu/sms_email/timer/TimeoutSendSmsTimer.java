package com.sohu.sms_email.timer;

import com.sohu.sms_email.bucket.TimeoutBucket;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.SMS;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Gary on 2015/11/12.
 * 发超时短信
 */
@Component
public class TimeoutSendSmsTimer {

    @Value("#{properties[receive_timeout_person]}")
    private String receiver;

    @Value("#{properties[min_times]}")
    private String minTimes;

    @Value("#{properties[special_interface]}")
    private String specialInters;

    private Integer minTime;
    private Map<String, Integer> specialInterfaces;

    /**
     * 正在处理中，不允许再进行处理
     */
    private boolean isProcess = false;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void process() {
        initEnv();
        if(true == isProcess) {
            return;
        } else {
            isProcess = true;
        }
        ConcurrentHashMap<String, AtomicLong> smsMap = TimeoutBucket.exchange();
        System.out.println("sendTimeoutCountBySms timer ...... time : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ,bucket:" + smsMap.size());
        try {
            if(null == smsMap || smsMap.size() == 0) {
                return;
            }
            StringBuilder smsSb = new StringBuilder();
            Iterator<Map.Entry<String, AtomicLong>> iter = smsMap.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry<String, AtomicLong> entry = iter.next();
                System.out.println("rawCount :" + entry.getKey() + ":" +entry.getValue().get());

                //特殊接口次数，单独配置
                if(specialInterfaces.containsKey(entry.getKey())) {
                    if(entry.getValue().get() >= specialInterfaces.get(entry.getKey())) {
                        System.out.println("specialCount :" + entry.getKey() + ":" + entry.getValue().get());
                        smsSb.append(entry.getKey()).append(":").append(entry.getValue().get()).append("次, ");
                    }
                    continue;
                }

                //统一的最小阈值处理
                if(entry.getValue().get() >= minTime) {
                    System.out.println("count :" + entry.getKey() + ":" + entry.getValue().get());
                    smsSb.append(entry.getKey()).append(":").append(entry.getValue().get()).append("次, ");
                }
            }

            //拼接字符串，并发送短信
            if(0 != smsSb.length()) {
                //newsInterface此字符，短信无法发送，必须干掉
                String sms = smsSb.insert(0, "超时提醒:").substring(0, smsSb.length()-2).replaceAll("newsInterface", "newsInter");
                boolean isSuccess = SMS.sendMessage(receiver, sms);
                if(isSuccess) {
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSmsTimeout", sms, "receiver:"+receiver);
                } else {
                    LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSmsTimeout", sms+"——"+receiver, null, new Exception("sendSmsTimeout failed!"));
                    System.out.println("send message failed!" + new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss").format(new Date()));
                }
            }
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSmsTimeoutError", null, null, e);
        } finally {
            smsMap.clear();
            isProcess = false;
        }
    }



    /**
     * 解析发送短信相关参数
     */
    private void initEnv() {
        if(null == minTime) {
            try {
                minTime = Integer.parseInt(minTimes.trim());
            } catch (Exception e) {
                minTime = 10;
            }
        }

        if(null == specialInterfaces) {
            specialInterfaces = new HashMap<String, Integer>();
            String[] arr = specialInters.split(",");
            for(String str : arr) {
                String[] pair = str.split(":");
                specialInterfaces.put(pair[0].trim(), Integer.parseInt(pair[1].trim()));
            }
        }
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMinTimes() {
        return minTimes;
    }

    public void setMinTimes(String minTimes) {
        this.minTimes = minTimes;
    }

    public String getSpecialInters() {
        return specialInters;
    }

    public void setSpecialInters(String specialInters) {
        this.specialInters = specialInters;
    }
}
