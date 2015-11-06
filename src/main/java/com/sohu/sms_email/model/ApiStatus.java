package com.sohu.sms_email.model;

import java.util.List;

/**
 * Created by Gary on 2015/11/6.
 */
public class ApiStatus {

    private List<ApiStatusCount> apiStatus;

    public List<ApiStatusCount> getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(List<ApiStatusCount> apiStatus) {
        this.apiStatus = apiStatus;
    }
}
