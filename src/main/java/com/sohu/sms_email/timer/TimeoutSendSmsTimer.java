package com.sohu.sms_email.timer;

import com.sohu.sms_email.bucket.TimeoutBucket;
import com.sohu.sms_email.utils.DateUtils;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.SMS;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Gary on 2015/11/12.
 * 发超时短信
 */
@Component
public class TimeoutSendSmsTimer {

    private static String phoneTo;

    private static Integer minTimes;

    private static Map<String, Integer> specialInterfaces;

    /**
     * 正在处理中，不允许再进行处理
     */
    private boolean isProcess = false;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void process() {
        if(true == isProcess) {
            return;
        } else {
            isProcess = true;
        }
        ConcurrentHashMap<String, AtomicLong> smsMap = TimeoutBucket.exchange();
        System.out.println("sendTimeoutCountBySms timer ...... time : " + DateUtils.getCurrentTime() + " ,bucket:" + smsMap.size());
        try {
            if(null == smsMap || smsMap.isEmpty()) {
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
                if(entry.getValue().get() >= minTimes) {
                    System.out.println("count :" + entry.getKey() + ":" + entry.getValue().get());
                    smsSb.append(entry.getKey()).append(":").append(entry.getValue().get()).append("次, ");
                }
            }

            //拼接字符串，并发送短信
            if(0 != smsSb.length()) {
                //newsInterface此字符，短信无法发送，必须干掉
                String sms = smsSb.insert(0, "超时提醒:").substring(0, smsSb.length()-2).replaceAll("newsInterface", "newsInter");
                boolean isSuccess = SMS.sendMessage(phoneTo, sms);
                if(isSuccess) {
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSmsTimeout", sms, "receiver:"+phoneTo);
                } else {
                    LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSmsTimeout", sms+"——"+phoneTo, null, new Exception("sendSmsTimeout failed!"));
                    System.out.println("send message failed!" + DateUtils.getCurrentTime());
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
    public static void initEnv(String timeoutConfig) {

        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

        Map<String, Object> timeoutConfigMap = jsonMapper.fromJson(timeoutConfig, HashMap.class);

        phoneTo = (String) timeoutConfigMap.get("phone_to");
        minTimes = (Integer) timeoutConfigMap.get("min_alert_times");
        specialInterfaces = (Map<String, Integer>) timeoutConfigMap.get("special_interfaces");
    }
}
