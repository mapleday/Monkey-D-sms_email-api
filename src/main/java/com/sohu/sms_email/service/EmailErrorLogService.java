package com.sohu.sms_email.service;

/**
 * Created by Gary on 2015/10/22.
 */
public interface EmailErrorLogService {
    void handleEmailErrorLog(String errorLogs);
    void sendErrorLog(String subject, String errorLog, String emailAddress) throws Exception;
}
