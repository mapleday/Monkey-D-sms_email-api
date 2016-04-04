package com.sohu.sms_email.timer;

import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.model.ErrorLog;
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

    private static String[] mailTo;
    private static String mailSubject;
    private static String stackTraceUrl;

    private static final String EMAIL_TEMPLATE = "你好，过去的5分钟共有%d台服务器实例出现错误，详情如下：</b></div><br>";
    private static boolean isProcess = false;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void sendSmsAndEmail() {

        System.out.println("sendErrorLogBySmsAndEmail timer ...... time : " + DateUtils.getCurrentTime());

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
                                    stackTraceUrl + entry.getValue().getErrorLog().genParams(),
                                    entry.getValue().getTimes()));
                }
                emailContentBuffer.append("</table>");
            }

            bucket.clear();

            //发送错误提醒邮件
            String text = EmailStringFormatUtils.getEmailHead() + String.format(EMAIL_TEMPLATE, emailInstanceNum) +
                    emailContentBuffer.toString() + "</td> </tr> </table> </body> </html>";

            if(null != mailTo || 0 != mailTo.length) {
                try {
                    //发送邮件可能失败，会导致后面map无法清理，内存暴走 2016年2月26日 16:01:38
                    EmailUtil.sendHtmlEmail(mailSubject, text, mailTo);
                } catch (Exception e) {
                    EmailUtil.sendHtmlEmail(mailSubject, e.getMessage(), mailTo);
                    LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail.sendMail", null, null, e);
                }
                LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySms", mailTo.toString(), mailSubject);
            }
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogBySmsAndEmail", null, null, e);
        } finally {
            bucket.clear();
            isProcess = false;
        }
    }

    public static void initEnv(String errorLogConfig, String monitorUrls) {

        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

        Map<String, Object> errorLogMap = jsonMapper.fromJson(errorLogConfig, HashMap.class);
        Map<String, String> urls = jsonMapper.fromJson(monitorUrls, HashMap.class);

        List<String> emails = (List<String>) errorLogMap.get("mail_to");
        mailTo = new String[emails.size()];
        for(int i=0; i<mailTo.length; i++) {
            mailTo[i] = emails.get(i);
        }

        mailSubject = (String) errorLogMap.get("mail_subject");
        stackTraceUrl = urls.get("stackTrace_base_url");
    }
}
