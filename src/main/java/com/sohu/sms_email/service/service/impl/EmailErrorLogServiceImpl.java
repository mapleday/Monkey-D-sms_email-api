package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.bucket.EmailErrorLogBucket;
import com.sohu.sms_email.bucket.TimeoutBucket;
import com.sohu.sms_email.service.EmailErrorLogService;
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
@Component("emailErrorLogService")
public class EmailErrorLogServiceImpl implements EmailErrorLogService {

    private static final String KEY = "emailErrorLog";
    private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

    @Override
    public void handleEmailErrorLog(int instanceNum, String errorDetail) {
        EmailErrorLogBucket.insertData(KEY, instanceNum, errorDetail);
    }

    @Override
    public void handleTimeoutCount(String timeoutCount) {

        LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "handleTimeoutCount", timeoutCount, null);

        Map<String, Long> contentMap = jsonMapper.fromJson(timeoutCount, HashMap.class);
        Iterator<Map.Entry<String, Long>> iter = contentMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String ,Long> entry = iter.next();
            TimeoutBucket.insertData(entry.getKey(), entry.getValue());
        }

    }
}
