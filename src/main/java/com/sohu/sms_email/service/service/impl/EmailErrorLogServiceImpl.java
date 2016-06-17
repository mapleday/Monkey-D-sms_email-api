package com.sohu.sms_email.service.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Strings;
import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.config.ErrorLogEmailConfig;
import com.sohu.sms_email.model.ErrorLog;
import com.sohu.sms_email.service.EmailErrorLogService;
import com.sohu.sms_email.utils.ZipUtils;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.EmailUtil;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Gary on 2015/10/22.
 */
@Component("emailErrorLogService")
public class EmailErrorLogServiceImpl implements EmailErrorLogService {

    private static JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
    private static JavaType collectionType = jsonMapper.contructCollectionType(LinkedList.class, ErrorLog.class);

    @Override
    public void handleEmailErrorLog(String errorLogs) {

        if(Strings.isNullOrEmpty(errorLogs)) return;
        Map<String, String> map = jsonMapper.fromJson(errorLogs, HashMap.class);
        Set<String> keys = map.keySet();
        for(String key : keys) {
            List<ErrorLog> errorLogsList = jsonMapper.fromJson(map.get(key), collectionType);
            EmailErrorLogBucket.insertData(key, errorLogsList);
        }
    }

    @Override
    public void sendErrorLog(String subject, String errorLog, String emailAddress) throws Exception {
        if(Strings.isNullOrEmpty(errorLog)) {
            errorLog = "";
        } else {
            errorLog = new String(ZipUtils.gunzip(errorLog).getBytes("UTF-8"), "GBK");
        }

        if(Strings.isNullOrEmpty(subject)) {
            subject = "unknown";
        }
        subject = subject + "_服务异常提醒";
        String[] emails = emailAddress.split(",");
        EmailUtil.sendHtmlEmail(subject, ErrorLogEmailConfig.EMAIL_HEAD + errorLog + ErrorLogEmailConfig.EMAIL_TAIL, emails);
    }
}
