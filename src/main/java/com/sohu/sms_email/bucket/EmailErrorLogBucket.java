package com.sohu.sms_email.bucket;

import com.google.common.base.Strings;
import com.sohu.sms_email.model.EmailDetail;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收ErrorLog统计信息数据的双缓冲
 * Created by Gary on 2015/10/19
 */
public class EmailErrorLogBucket {
    /**
     * 存放数据的alpha桶
     */
    private static final ConcurrentHashMap<String, EmailDetail> bucketAlpha = new ConcurrentHashMap<String, EmailDetail>();
    /**
     * 存放数据的beta桶
     */
    private static final ConcurrentHashMap<String,EmailDetail> bucketBeta = new ConcurrentHashMap<String, EmailDetail>();

    /**
     * 正在工作中的桶
     */
    private static ConcurrentHashMap<String,EmailDetail> bucket = bucketAlpha;

    /**
     * 切换桶
     * @return
     */
    public static ConcurrentHashMap<String,EmailDetail> exchange() {
        ConcurrentHashMap<String, EmailDetail> lastBucket = bucket;
        if(bucket == bucketAlpha){
            bucket = bucketBeta;
        } else {
            bucket = bucketAlpha;
        }
        return lastBucket;
    }

    private static ConcurrentHashMap<String,EmailDetail> getBucket() {
        return bucket;
    }

    /**
     * 向桶中插入数据
     * @param key
     * @param instanceNum
     * @param errorDetail
     */
    public static void insertData(String key, int instanceNum, String errorDetail) {
        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        ConcurrentHashMap<String, EmailDetail> b = getBucket();
        EmailDetail emailDetail  = b.get(key);
        if (null != emailDetail) {
            emailDetail.addInstanceNum(instanceNum);
            emailDetail.addErrorDetail(errorDetail);
        } else {
            synchronized (EmailErrorLogBucket.class) {
                if(null != b.get(key)) {
                    insertData(key, instanceNum, errorDetail);
                } else {
                    EmailDetail temp = new EmailDetail(instanceNum, errorDetail);
                    b.put(key, temp);
                }
            }
        }
    }
}
