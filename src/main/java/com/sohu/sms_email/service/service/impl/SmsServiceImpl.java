package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.service.SmsService;
import com.sohu.snscommon.utils.SMS;
import org.springframework.stereotype.Component;

/**
 * Created by Gary on 2015/10/21.
 */
@Component("smsService")
public class SmsServiceImpl implements SmsService {

    public boolean sendSms(String phoneNo, String msg) {
        return SMS.sendMessage(phoneNo, msg);
    }
}
