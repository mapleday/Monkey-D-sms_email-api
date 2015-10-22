package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.service.EmailErrorLogService;
import org.springframework.stereotype.Component;

/**
 * Created by Gary on 2015/10/22.
 */
@Component("emailErrorLogService")
public class EmailErrorLogServiceImpl implements EmailErrorLogService {

    private static final String KEY = "emailErrorLog";

    @Override
    public void handleEmailErrorLog(int instanceNum, String errorDetail) {
        EmailErrorLogBucket.insertData(KEY, instanceNum, errorDetail);
    }
}
