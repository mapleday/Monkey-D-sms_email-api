package com.sohu.sms_email.bucket;

import com.google.common.base.Strings;
import com.sohu.sms_email.model.ApiStatusCount;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gary on 2015/11/6.
 */
public class ApiStatusBucket {
    /**
     * 存放数据的alpha桶
     */
    private static final ConcurrentHashMap<String, ApiStatusCount> bucketAlpha = new ConcurrentHashMap<String, ApiStatusCount>();
    /**
     * 存放数据的beta桶
     */
    private static final ConcurrentHashMap<String,ApiStatusCount> bucketBeta = new ConcurrentHashMap<String, ApiStatusCount>();

    /**
     * 正在工作中的桶
     */
    private static ConcurrentHashMap<String,ApiStatusCount> bucket = bucketAlpha;

    /**
     * 切换桶
     * @return
     */
    public static ConcurrentHashMap<String,ApiStatusCount> exchange() {
        ConcurrentHashMap<String, ApiStatusCount> lastBucket = bucket;
        if(bucket == bucketAlpha){
            bucket = bucketBeta;
        } else {
            bucket = bucketAlpha;
        }
        return lastBucket;
    }

    private static ConcurrentHashMap<String,ApiStatusCount> getBucket() {
        return bucket;
    }

    /**
     * 向桶中插入数据
     * @param apiStatusCount
     */
    public static void insertData(ApiStatusCount apiStatusCount) {
        if(null == apiStatusCount) {
            return;
        }
        String key = apiStatusCount.getMethodName();
        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        ConcurrentHashMap<String, ApiStatusCount> b = getBucket();
        ApiStatusCount apiStatusCountTemp = b.get(key);
        if (null != apiStatusCountTemp) {
            apiStatusCountTemp.addUseCount(apiStatusCount.getUseCount());
            apiStatusCountTemp.addTimeOutCount(apiStatusCount.getTimeOutCount());
        } else {
            synchronized (ApiStatusBucket.class) {
                if(null != b.get(key)) {
                    insertData(apiStatusCount);
                } else {
                    b.put(key, apiStatusCount);
                }
            }
        }
    }
}
