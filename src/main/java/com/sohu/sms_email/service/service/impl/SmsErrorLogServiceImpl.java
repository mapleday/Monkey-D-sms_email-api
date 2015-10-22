package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.bucket.SmsErrorLogBucket;
import com.sohu.sms_email.service.SmsErrorLogService;
import org.springframework.stereotype.Component;

/**
 * Created by Gary on 2015/10/22.
 */

@Component("smsErrorLogService")
public class SmsErrorLogServiceImpl implements SmsErrorLogService {

    private static final String KEY = "errorLogCount";

    @Override
    public void handleSmsErrorLog(int instanceNum, int errorNum) {
        SmsErrorLogBucket.insertData(KEY, instanceNum, errorNum);
    }
}
