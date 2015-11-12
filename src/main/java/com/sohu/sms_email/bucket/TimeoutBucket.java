package com.sohu.sms_email.bucket;

import com.google.common.base.Strings;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 超时桶
 * Created by Gary on 2015/10/19
 */
public class TimeoutBucket {
    /**
     * 存放数据的alpha桶
     */
    private static final ConcurrentHashMap<String, AtomicLong> bucketAlpha = new ConcurrentHashMap<String, AtomicLong>();
    /**
     * 存放数据的beta桶
     */
    private static final ConcurrentHashMap<String,AtomicLong> bucketBeta = new ConcurrentHashMap<String, AtomicLong>();

    /**
     * 正在工作中的桶
     */
    private static ConcurrentHashMap<String,AtomicLong> bucket = bucketAlpha;

    /**
     * 切换桶
     * @return
     */
    public static ConcurrentHashMap<String,AtomicLong> exchange() {
        ConcurrentHashMap<String, AtomicLong> lastBucket = bucket;
        if(bucket == bucketAlpha){
            bucket = bucketBeta;
        } else {
            bucket = bucketAlpha;
        }
        return lastBucket;
    }

    private static ConcurrentHashMap<String,AtomicLong> getBucket() {
        return bucket;
    }

    /**
     * 向桶中插入数据
     * @param
     */
    public static void insertData(String key, Long count) {
        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        ConcurrentHashMap<String, AtomicLong> b = getBucket();
        AtomicLong timeoutCount = b.get(key);
        if (null != timeoutCount) {
            timeoutCount.addAndGet(count);
        } else {
            synchronized (TimeoutBucket.class) {
                if(null != b.get(key)) {
                    insertData(key, count);
                } else {
                    AtomicLong countTemp = new AtomicLong(1);
                    b.put(key, countTemp);
                }
            }
        }
    }
}
