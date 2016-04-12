package com.sohu.sms_email.timer;

import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.config.ConstantConfig;
import com.sohu.sms_email.model.ErrorLog;
import com.sohu.sms_email.model.ErrorLogContent;
import com.sohu.sms_email.model.MergedErrorLog;
import com.sohu.sms_email.utils.DateUtils;
import com.sohu.sms_email.utils.EmailStringFormatUtils;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.EmailUtil;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gary on 2015/10/22.
 */

@Component
public class ErrorLogSenderTimer {

    private static String UNKNOWN_KEY = "all";
    private static final Map<String, ErrorLogContent> SPECIAL_APPID_MONITOR = new HashMap<String, ErrorLogContent>();

    private static final String EMAIL_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，详情如下：</b></div><br>";
    private static boolean isProcess = false;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void sendSmsAndEmail() {

        System.out.println("sendErrorLogByEmail timer ...... time : " + DateUtils.getCurrentTime());

        if(true == isProcess) return;
        else isProcess = true;
        ConcurrentHashMap<String, List<ErrorLog>> bucket = EmailErrorLogBucket.exchange();
        try {
            Set<String> keySet = bucket.keySet();
            if(bucket.isEmpty()) return;
            for(String appIns : keySet) {
                StringBuilder emailBuffer = handleErrorLogs(appIns, bucket.get(appIns));
                String appId = appIns.substring(0, appIns.indexOf("_"));
                if(ConstantConfig.special_appIds.contains(appId)) {
                    if(SPECIAL_APPID_MONITOR.containsKey(appId)) {
                        SPECIAL_APPID_MONITOR.get(appId).addEmailContent(emailBuffer);
                        SPECIAL_APPID_MONITOR.get(appId).addCount(1);
                    } else {
                        ErrorLogContent errorLogContent = new ErrorLogContent();
                        errorLogContent.addEmailContent(emailBuffer);
                        SPECIAL_APPID_MONITOR.put(appId, errorLogContent);
                    }
                } else {
                    if(SPECIAL_APPID_MONITOR.containsKey(UNKNOWN_KEY)) {
                        SPECIAL_APPID_MONITOR.get(UNKNOWN_KEY).addEmailContent(emailBuffer);
                        SPECIAL_APPID_MONITOR.get(UNKNOWN_KEY).addCount(1);
                    } else {
                        ErrorLogContent errorLogContent = new ErrorLogContent();
                        errorLogContent.addEmailContent(emailBuffer);
                        SPECIAL_APPID_MONITOR.put(UNKNOWN_KEY, errorLogContent);
                    }
                }
            }

            bucket.clear();

            Set<String> emailAppId = SPECIAL_APPID_MONITOR.keySet();
            String[] mailTo = ConstantConfig.mailTo;
            for(String appId : emailAppId) {
                //发送错误提醒邮件
                String text = EmailStringFormatUtils.getEmailHead() + String.format(EMAIL_TEMPLATE, SPECIAL_APPID_MONITOR.get(appId).getCount()) +
                        SPECIAL_APPID_MONITOR.get(appId).getEmailContent() + "</td> </tr> </table> </body> </html>";
                String subject = String.format(ConstantConfig.mailSubject, appId+"_");
                if(null != mailTo || 0 != mailTo.length) {
                    try {
                        //发送邮件可能失败，会导致后面map无法清理，内存暴走 2016年2月26日 16:01:38
                        EmailUtil.sendHtmlEmail(subject, text, mailTo);
                    } catch (Exception e) {
                        EmailUtil.sendHtmlEmail(ConstantConfig.mailSubject, e.getMessage(), ConstantConfig.mailTo);
                        LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail.sendMail", null, null, e);
                    }
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySms", mailTo.toString(), subject);
                }
            }

            SPECIAL_APPID_MONITOR.clear();
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null, e);
        } finally {
            bucket.clear();
            SPECIAL_APPID_MONITOR.clear();
            isProcess = false;
        }
    }

    private StringBuilder handleErrorLogs(String appIns, List<ErrorLog> errorLogs) {
        StringBuilder emailBuffer = new StringBuilder();
        emailBuffer.append(EmailStringFormatUtils.formatHead(appIns));
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
            emailBuffer.append(entry.getValue().getErrorLog().warpHtml())
                    .append(EmailStringFormatUtils.formatTail(
                            entry.getValue().getParams().toString(),
                            ConstantConfig.stackTraceUrl + entry.getValue().getErrorLog().genParams(),
                            entry.getValue().getTimes()));
        }
        emailBuffer.append("</table>");
        return emailBuffer;
    }
}
