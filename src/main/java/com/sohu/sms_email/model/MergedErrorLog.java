package com.sohu.sms_email.model;

/**
 * Created by Gary on 2015/10/28.
 */
public class MergedErrorLog {

    private ErrorLog errorLog;
    private StringBuilder params = new StringBuilder();
    private int times = 0;

    public ErrorLog getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    public StringBuilder getParams() {
        return params;
    }

    public void addParams(String param) {
        this.params.append(param+"<br>");
    }

    public int getTimes() {
        return times;
    }

    public void addTimes(int times) {
        this.times += times;
    }
}
