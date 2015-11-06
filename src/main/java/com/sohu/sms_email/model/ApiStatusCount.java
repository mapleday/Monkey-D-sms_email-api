package com.sohu.sms_email.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Gary on 2015/11/6.
 */
public class ApiStatusCount {
    private String methodName;
    private String pathName;
    private AtomicLong useCount;
    private AtomicLong timeOutCount;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Long getUseCount() {
        return useCount.get();
    }

    public void addUseCount(Long useCount) {
        this.useCount.getAndAdd(useCount);
    }

    public Long getTimeOutCount() {
        return timeOutCount.get();
    }

    public void addTimeOutCount(Long timeOutCount) {
        this.timeOutCount.getAndAdd(timeOutCount);
    }

    public void setUseCount(Long useCount) {
        this.useCount = new AtomicLong(useCount);
    }

    public void setTimeOutCount(Long timeOutCount) {
        this.timeOutCount = new AtomicLong(timeOutCount);
    }
}
