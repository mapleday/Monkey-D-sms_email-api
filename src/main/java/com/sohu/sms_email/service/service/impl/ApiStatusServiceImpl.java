package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.bucket.ApiStatusBucket;
import com.sohu.sms_email.model.ApiStatus;
import com.sohu.sms_email.model.ApiStatusCount;
import com.sohu.sms_email.service.ApiStatusService;
import com.sohu.sns.common.utils.json.JsonMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Gary on 2015/11/6.
 */

@Component
public class ApiStatusServiceImpl implements ApiStatusService {
    private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
    @Override
    public void handle(String apiStatus) {
        List<ApiStatusCount> list = jsonMapper.fromJson(apiStatus, ApiStatus.class).getApiStatus();
        for(ApiStatusCount apiStatusCount : list) {
            ApiStatusBucket.insertData(apiStatusCount);
        }
    }
}
