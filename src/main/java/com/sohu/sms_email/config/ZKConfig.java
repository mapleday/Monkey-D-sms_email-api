package com.sohu.sms_email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * zk配置类
 * Created by Gary Chan on 2016/4/4.
 */

@Component
public class ZKConfig {

    @Value("#{properties['sce.zk.root']}")
    private String zkRoot;

    @Value("#{properties['sce.zk.url']}")
    private String zkUrls;

    @Value("#{properties['sce.zk.timeout']}")
    private String timeout;

    @Value("#{properties['sce.zk.user']}")
    private String userName;

    @Value("#{properties['sce.zk.passwd']}")
    private String passwd;

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public String getZkUrls() {
        return zkUrls;
    }

    public void setZkUrls(String zkUrls) {
        this.zkUrls = zkUrls;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
