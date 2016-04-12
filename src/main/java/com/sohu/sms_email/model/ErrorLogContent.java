package com.sohu.sms_email.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gary Chan on 2016/4/12.
 */
public class ErrorLogContent {

    private StringBuilder emailContent = new StringBuilder();
    private AtomicInteger count = new AtomicInteger(1);

    public String getEmailContent() {
        return emailContent.toString();
    }

    public void addEmailContent(StringBuilder emailContent) {
        this.emailContent.append(emailContent);
    }

    public int getCount() {
        return count.get();
    }

    public void addCount(int count) {
        this.count.addAndGet(count);
    }
}
