package com.sohu.sms_email.service;

/**
 * Created by Gary on 2015/10/22.
 */
public interface SmsErrorLogService {
    void handleSmsErrorLog(int instanceNum, int errorNum);
    void handleTimeoutCount(String timeoutCount);
}
