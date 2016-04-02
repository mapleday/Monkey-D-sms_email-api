package com.sohu.sms_email.timer;

import com.google.common.base.Strings;
import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.model.ErrorLog;
import com.sohu.sms_email.model.MergedErrorLog;
import com.sohu.sms_email.utils.EmailStringFormatUtils;
import com.sohu.snscommon.utils.EmailUtil;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gary on 2015/10/22.
 */

@Component
public class ErrorLogSenderTimer {

    @Value("#{properties[mail_to]}")
    private String mailTo = "";

    @Value("#{properties[phone_to]}")
    private String phoneTo = "";

    @Value("#{properties[mail_subject]}")
    private String mailSubject = "";

    private static final String STACKTRACE_URL = "http://sns-monitor-web-test.sohusce.com/queryStackTrace";
    private static final String EMAIL_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，详情如下：</b></div><br>";
    private static boolean isProcess = false;
    private String[] mailAddresses;
    private String[] phoneNumbers;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void sendSmsAndEmail() {

        initEnv();  //解析短信和邮件字符串
        System.out.println("sendErrorLogBySmsAndEmail timer ...... time : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        if(true == isProcess) return;
        else isProcess = true;
        ConcurrentHashMap<String, List<ErrorLog>> bucket = EmailErrorLogBucket.exchange();
        try {
            Set<String> keySet = bucket.keySet();
            int emailInstanceNum = keySet.size();
            if(0 == emailInstanceNum) return;
            StringBuilder emailContentBuffer = new StringBuilder();
            for(String instance : keySet) {
                List<ErrorLog> errorLogs = bucket.get(instance);
                emailContentBuffer.append(EmailStringFormatUtils.formatHead(instance));
                Map<String, MergedErrorLog> map = new HashMap<String, MergedErrorLog>();
                for (ErrorLog errorLog : errorLogs) {
                    String key = errorLog.getKey();
                    if (map.containsKey(key)) {
                        map.get(key).addTimes(1);
                        if (map.get(key).getTimes() <= 10){
                            map.get(key).addParams(errorLog.getParam());
                        }
                    } else {
                        MergedErrorLog mergedErrorLog = new MergedErrorLog();
                        mergedErrorLog.setErrorLog(errorLog);
                        mergedErrorLog.addParams(errorLog.getParam());
                        mergedErrorLog.addTimes(1);
                        map.put(key, mergedErrorLog);
                    }
                }

                Set<Map.Entry<String, MergedErrorLog>> set = map.entrySet();
                for (Map.Entry<String, MergedErrorLog> entry : set) {
                    emailContentBuffer.append(entry.getValue().getErrorLog().warpHtml())
                            .append(EmailStringFormatUtils.formatTail(
                                    entry.getValue().getParams().toString(),
                                    STACKTRACE_URL + entry.getValue().getErrorLog().genParams(),
                                    entry.getValue().getTimes()));
                }
                emailContentBuffer.append("</table>");
            }

            bucket.clear();

            //发送错误提醒邮件
            String text = EmailStringFormatUtils.getEmailHead() + String.format(EMAIL_TEMPLATE, emailInstanceNum) +
                    emailContentBuffer.toString() + "</td> </tr> </table> </body> </html>";

            if(!Strings.isNullOrEmpty(mailTo)) {
                try {
                    //发送邮件可能失败，会导致后面map无法清理，内存暴走 2016年2月26日 16:01:38
                    EmailUtil.sendHtmlEmail(mailSubject, text, mailAddresses);
                } catch (Exception e) {
                    EmailUtil.sendHtmlEmail(mailSubject, e.getMessage(), mailAddresses);
                    LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail.sendMail", null, null, e);
                }
                LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySms", mailTo, mailSubject);
            }
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null, e);
        } finally {
            bucket.clear();
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
