package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.bucket.SmsErrorLogBucket;
import com.sohu.sms_email.bucket.TimeoutBucket;
import com.sohu.sms_email.service.SmsErrorLogService;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Gary on 2015/10/22.
 */

@Component("smsErrorLogService")
public class SmsErrorLogServiceImpl implements SmsErrorLogService {

    private static final String KEY = "errorLogCount";
    private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

    /**
     * 发送邮件同时，发送提醒短信
     * 数据仍在处理，但是不发送短信
     * 后期可酌情处理 properties 中配置phone_to=13121556477
     * @param instanceNum
     * @param errorNum
     */
    @Override
    public void handleSmsErrorLog(int instanceNum, int errorNum) {
        SmsErrorLogBucket.insertData(KEY, instanceNum, errorNum);
    }

    @Override
    public void handleTimeoutCount(String timeoutCount) {

        LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "handleTimeoutCount", timeoutCount, null);

        Map<String, Object> contentMap = jsonMapper.fromJson(timeoutCount, HashMap.class);
        if(null == contentMap || 0 == contentMap.size()) {
            return;
        }

        //汇总错误次数
        Iterator<Map.Entry<String, Object>> iter = contentMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String ,Object> entry = iter.next();
            TimeoutBucket.insertData(entry.getKey(), Long.valueOf(entry.getValue().toString()));
        }
    }
}
