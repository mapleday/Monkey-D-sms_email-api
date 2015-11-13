package com.sohu.sms_email.service;

/**
 * Created by Gary on 2015/10/22.
 */
public interface EmailErrorLogService {
    void handleEmailErrorLog(int instanceNum, String errorDetail);
}
