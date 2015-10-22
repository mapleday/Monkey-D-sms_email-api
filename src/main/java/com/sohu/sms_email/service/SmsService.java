package com.sohu.sms_email.service;

/**
 * Created by Gary on 2015/10/21.
 */
public interface SmsService {
    void sendSms(String phoneNo, String msg);
}
