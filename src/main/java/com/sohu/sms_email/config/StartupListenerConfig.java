package com.sohu.sms_email.config;

import com.sohu.sms_email.timer.ErrorLogSenderTimer;
import com.sohu.sms_email.timer.TimeoutSendSmsTimer;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.config.ZkPathConfigure;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import com.sohu.snscommon.utils.zk.ZkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by Gary Chan on 2016/4/4.
 */
@Service
public class StartupListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ZKConfig zKConfig;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()) {
            ZkPathConfigure.ROOT_NODE = zKConfig.getZkRoot();
            ZkPathConfigure.ZOOKEEPER_TIMEOUT = Integer.parseInt(zKConfig.getTimeout());
            ZkPathConfigure.ZOOKEEPER_SERVERS = zKConfig.getZkUrls();
            ZkPathConfigure.ZOOKEEPER_AUTH_USER = zKConfig.getUserName();
            ZkPathConfigure.ZOOKEEPER_AUTH_PASSWORD = zKConfig.getPasswd();

            ZkUtils zk = new ZkUtils();
            try {
                zk.connect(ZkPathConfigure.ZOOKEEPER_SERVERS, ZkPathConfigure.ZOOKEEPER_AUTH_USER,
                        ZkPathConfigure.ZOOKEEPER_AUTH_PASSWORD, ZkPathConfigure.ZOOKEEPER_TIMEOUT);

                String errorLogConfig = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/errorlog_email_config"));
                String monitorUrls = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/monitor_urls"));
                String timeoutConfig = new String(zk.getData(ZkPathConfigure.ROOT_NODE + "/sns_monitor/timeout_config"));

                ErrorLogSenderTimer.initEnv(errorLogConfig, monitorUrls);
                TimeoutSendSmsTimer.initEnv(timeoutConfig);

            } catch (Exception e) {
                LOGGER.errorLog(ModuleEnum.MONITOR_SERVICE, "startUpListener", null, null, e);
                e.printStackTrace();
            }



        }
    }
}
