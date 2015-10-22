package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.service.EmailService;
import com.sohu.snscommon.utils.EmailUtil;
import org.springframework.stereotype.Component;

/**
 * Created by Gary on 2015/10/21.
 */
@Component("emailService")
public class EmailServiceImpl implements EmailService {

    public void sendSimpleEmail(String subject, String text, String[] to) {
        EmailUtil.sendSimpleEmail(subject, text, to);
    }
}
