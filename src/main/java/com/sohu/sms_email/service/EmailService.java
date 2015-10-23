package com.sohu.sms_email.service;

/**
 * Created by Gary on 2015/10/21.
 */
public interface EmailService {
    void sendSimpleEmail(String subject, String text, String[] to);
    void sendHtmlEmail(String subject, String text, String[] to);
}
