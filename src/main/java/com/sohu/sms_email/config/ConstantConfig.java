package com.sohu.sms_email.config;

import com.sohu.sms_email.timer.ErrorLogSenderTimer;
import com.sohu.sms_email.timer.TimeoutSendSmsTimer;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.config.ZkPathConfigure;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import com.sohu.snscommon.utils.zk.ZkUtils;
import com.sohucs.org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Gary Chan on 2016/4/12.
 */
@Component
public class ConstantConfig {

    public static Set<String> special_appIds = new HashSet<String>();
    public static String[] mailTo;
    public static String mailSubject;
    public static String stackTraceUrl;

    public static void initEnv(String errorLogConfig, String monitorUrls) {
        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
        Map<String, Object> errorLogMap = jsonMapper.fromJson(errorLogConfig, HashMap.class);
        Map<String, Object> monitorUrlsMap = jsonMapper.fromJson(monitorUrls, HashMap.class);

        List<String> emails = (List<String>) errorLogMap.get("mail_to");
        mailTo = new String[emails.size()];
        for(int i=0; i<mailTo.length; i++) {
            mailTo[i] = emails.get(i);
        }

        mailSubject = (String) errorLogMap.get("mail_subject");

        String specialAppIds = (String) errorLogMap.get("special_appId");
        String[] array = StringUtils.split(specialAppIds, ",");
        special_appIds.clear();
        for(String str : array) {
            special_appIds.add(str);
        }

        stackTraceUrl = (String) monitorUrlsMap.get("stackTrace_base_url");
    }

    @Scheduled(cron = "0 0/6 * * * ? ")
    public void refreshEnv() {
        ZkUtils zk = new ZkUtils();
        try {
            zk.connect(ZkPathConfigure.ZOOKEEPER_SERVERS, ZkPathConfigure.ZOOKEEPER_AUTH_USER,
                    ZkPathConfigure.ZOOKEEPER_AUTH_PASSWORD, ZkPathConfigure.ZOOKEEPER_TIMEOUT);

            String errorLogConfig = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/errorlog_email_config"));
            String monitorUrls = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/monitor_urls"));
            String timeoutConfig = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/timeout_config"));

            initEnv(errorLogConfig, monitorUrls);
            TimeoutSendSmsTimer.initEnv(timeoutConfig);
            zk.close();
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.MONITOR_SERVICE, "ConstantConfig.refreshEnv", null, null, e);
            e.printStackTrace();
        }
    }
}
