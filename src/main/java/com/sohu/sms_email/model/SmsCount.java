package com.sohu.sms_email.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gary on 2015/10/22.
 */
public class SmsCount implements Serializable {

    private AtomicInteger instanceNum;
    private AtomicInteger errorNum;

    public SmsCount(){};

    public SmsCount(int instanceNum, int errorNum) {
        this.instanceNum = new AtomicInteger(instanceNum);
        this.errorNum = new AtomicInteger(errorNum);
    }

    public int getInstanceNum() {
        return instanceNum.get();
    }

    public void addInstanceNum(int instanceNum) {
        this.instanceNum.addAndGet(instanceNum);
    }

    public int getErrorNum() {
        return errorNum.get();
    }

    public void addErrorNum(int errorNum) {
        this.errorNum.addAndGet(errorNum);
    }
}
