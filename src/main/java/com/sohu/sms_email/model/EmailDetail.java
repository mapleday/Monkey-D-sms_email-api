package com.sohu.sms_email.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gary on 2015/10/22.
 */
public class EmailDetail {

    private AtomicInteger instanceNum;
    private StringBuffer errorDetail;

    public EmailDetail() {
    }

    public EmailDetail(int instanceNum, String errorDetail) {
        this.instanceNum = new AtomicInteger(instanceNum);
        this.errorDetail = new StringBuffer(errorDetail);
    }

    public int getInstanceNum() {
        return instanceNum.get();
    }

    public void addInstanceNum(int instanceNum) {
        this.instanceNum.addAndGet(instanceNum);
    }

    public String getErrorDetail() {
        return errorDetail.toString();
    }

    public void addErrorDetail(String errorDetail) {
        this.errorDetail.append(errorDetail);
    }
}
