package com.sohu.sms_email.bucket;

import com.google.common.base.Strings;
import com.sohu.sms_email.model.SmsCount;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收ErrorLog统计信息数据的双缓冲
 * Created by Gary on 2015/10/19
 */
public class SmsErrorLogBucket {
    /**
     * 存放数据的alpha桶
     */
    private static final ConcurrentHashMap<String, SmsCount> bucketAlpha = new ConcurrentHashMap<String, SmsCount>();
    /**
     * 存放数据的beta桶
     */
    private static final ConcurrentHashMap<String,SmsCount> bucketBeta = new ConcurrentHashMap<String, SmsCount>();

    /**
     * 正在工作中的桶
     */
    private static ConcurrentHashMap<String,SmsCount> bucket = bucketAlpha;

    /**
     * 切换桶
     * @return
     */
    public static ConcurrentHashMap<String,SmsCount> exchange() {
        ConcurrentHashMap<String, SmsCount> lastBucket = bucket;
        if(bucket == bucketAlpha){
            bucket = bucketBeta;
        } else {
            bucket = bucketAlpha;
        }
        return lastBucket;
    }

    private static ConcurrentHashMap<String,SmsCount> getBucket() {
        return bucket;
    }

    /**
     * 向桶中插入数据
     * @param key
     * @param instanceNum
     * @param errorNum
     */
    public static void insertData(String key, int instanceNum, int errorNum) {
        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        ConcurrentHashMap<String, SmsCount> b = getBucket();
        SmsCount smsCount  = b.get(key);
        if (null != smsCount) {
            smsCount.addInstanceNum(instanceNum);
            smsCount.addErrorNum(errorNum);
        } else {
            synchronized (SmsErrorLogBucket.class) {
                if(null != b.get(key)) {
                    insertData(key, instanceNum, errorNum);
                } else {
                    SmsCount temp = new SmsCount(instanceNum, errorNum);
                    b.put(key, temp);
                }
            }
        }
    }
}
