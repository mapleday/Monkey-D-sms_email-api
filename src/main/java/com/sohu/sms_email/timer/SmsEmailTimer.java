package com.sohu.sms_email.timer;

import com.google.common.base.Strings;
import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.bucket.SmsErrorLogBucket;
import com.sohu.sms_email.model.EmailDetail;
import com.sohu.sms_email.model.SmsCount;
import com.sohu.snscommon.utils.EmailUtil;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("#{properties[mail_to]}")
    private String mailTo = "";

    @Value("#{properties[phone_to]}")
    private String phoneTo = "";

    @Value("#{properties[mail_subject]}")
    private String mailSubject = "";

    private static final String MSG_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，一共出现%d个错误信息，详情请查收邮件。";
    private static final String EMAIL_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，详情如下：</b></div><br>";
    private static final String HTML_HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <html xmlns=\"http://www.w3.org/1999/xhtml\"> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> <title>Demystifying Email Design</title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> </head><body style=\"margin: 0; padding: 0;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr> <td><br><div><b>";
    private static boolean isProcess = false;
    private String[] mailAddresses;
    private String[] phoneNumbers;

    @Scheduled(cron = "0 0/5 * * * ? ")
//    @Scheduled(cron = "0/30 * * * * ? ")
    public void sendSmsAndEmail() {
        initEnv();  //解析短信和邮件字符串
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

/*            //发送错误提醒短信
            if(0 != smsInstanceNum) {
                String msg = String.format(MSG_TEMPLATE, smsInstanceNum, smsErrorNum);
                if(!Strings.isNullOrEmpty(phoneTo)) {
                    SMS.sendGroupMessage(msg, phoneNumbers);
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySms", phoneTo, msg);
                }
            }*/

            //发送错误提醒邮件
            if(0 != emailInstanceNum) {
                String text = HTML_HEAD + String.format(EMAIL_TEMPLATE, emailInstanceNum) + emailErrorDetail + "</td> </tr> </table> </body> </html>";
                if(!Strings.isNullOrEmpty(mailTo)) {
                    EmailUtil.sendHtmlEmail(mailSubject, text, mailAddresses);
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySms", mailTo, mailSubject);
                }
            }
            smsMap.clear();
            emailMap.clear();
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null, e);
        } finally {
            isProcess = false;
        }
    }

    public void initEnv() {
        //第一次调用方法的时候解析一下发送的邮件地址和发送短信的手机号码
        if(null == mailAddresses) {
            mailAddresses = mailTo.split(",");
            for(int i=0; i<mailAddresses.length; i++) {
                mailAddresses[i] = mailAddresses[i].trim();
            }
        }
        if(null == phoneNumbers) {
            phoneNumbers = phoneTo.split(",");
            for(int i=0; i<phoneNumbers.length; i++) {
                phoneNumbers[i] = phoneNumbers[i].trim();
            }
        }
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getPhoneTo() {
        return phoneTo;
    }

    public void setPhoneTo(String phoneTo) {
        this.phoneTo = phoneTo;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

}
