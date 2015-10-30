package com.sohu.sms_email.timer;

import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.bucket.SmsErrorLogBucket;
import com.sohu.sms_email.model.EmailDetail;
import com.sohu.sms_email.model.SmsCount;
import com.sohu.snscommon.utils.EmailUtil;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gary on 2015/10/22.
 */
@Component
public class SmsEmailTimer {

    private static final String PHONE = "13121556477";
    private static final String MSG_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，一共出现%d个错误信息，详情请查收邮件。";
    private static final String EMAIL_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，详情如下：<br><br>";
    private static final String SUBJECT = "服务器错误提醒";
    private static final String[] TO = {"morganyang@sohu-inc.com", "shouqinchen@sohu-inc.com", "guoqingwang@sohu-inc.com", "shousongyang@sohu-inc.com", "jinyingshi@sohu-inc.com", "xuemingzhang@sohu-inc.com"};
    private static boolean isProcess = false;

    @Scheduled(cron = "0 0/5 * * * ? ")
    //@Scheduled(cron = "0/30 * * * * ? ")
    public void sendSmsAndEmail() {
        System.out.println("sendErrorLogBySmsAndEmail timer ...... time : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(true == isProcess) {
            return;
        } else {
            isProcess = true;
        }
        try {
            ConcurrentHashMap<String, SmsCount> smsMap = SmsErrorLogBucket.exchange();
            ConcurrentHashMap<String, EmailDetail> emailMap = EmailErrorLogBucket.exchange();

            Set<Map.Entry<String, SmsCount>> smsEntry = smsMap.entrySet();
            Set<Map.Entry<String, EmailDetail>> emailEntry = emailMap.entrySet();
            int smsInstanceNum = 0, smsErrorNum = 0, emailInstanceNum = 0;
            String emailErrorDetail = "";
            for(Map.Entry<String, SmsCount> entry : smsEntry) {
                SmsCount smsCount = entry.getValue();
                smsInstanceNum += smsCount.getInstanceNum();
                smsErrorNum += smsCount.getErrorNum();
            }
            for(Map.Entry<String, EmailDetail> entry : emailEntry) {
                EmailDetail emailDetail = entry.getValue();
                emailInstanceNum += emailDetail.getInstanceNum();
                emailErrorDetail += emailDetail.getErrorDetail();
            }

            if(0 != smsInstanceNum) {
                String msg = String.format(MSG_TEMPLATE, smsInstanceNum, smsErrorNum);
                //SMS.sendMessage(PHONE, msg);
            }

            if(0 != emailInstanceNum) {
                String text = String.format(EMAIL_TEMPLATE, emailInstanceNum) + emailErrorDetail;
                EmailUtil.sendHtmlEmail(SUBJECT, text, TO);
            }
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null);
            smsMap.clear();
            emailMap.clear();
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null, e);
        } finally {
            isProcess = false;
        }
    }
}
