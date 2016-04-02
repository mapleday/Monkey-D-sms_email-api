package com.sohu.sms_email.bucket;

import com.google.common.base.Strings;
import com.sohu.sms_email.model.ErrorLog;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收ErrorLog统计信息数据的双缓冲
 * Created by Gary on 2015/10/19
 */
public class EmailErrorLogBucket {
    /**
     * 存放数据的alpha桶
     */
    private static final ConcurrentHashMap<String, List<ErrorLog>> bucketAlpha = new ConcurrentHashMap<String, List<ErrorLog>>();
    /**
     * 存放数据的beta桶
     */
    private static final ConcurrentHashMap<String,List<ErrorLog>> bucketBeta = new ConcurrentHashMap<String, List<ErrorLog>>();

    /**
     * 正在工作中的桶
     */
    private static ConcurrentHashMap<String,List<ErrorLog>> bucket = bucketAlpha;

    /**
     * 切换桶
     * @return
     */
    public static ConcurrentHashMap<String,List<ErrorLog>> exchange() {
        ConcurrentHashMap<String, List<ErrorLog>> lastBucket = bucket;
        if(bucket == bucketAlpha){
            bucket = bucketBeta;
        } else {
            bucket = bucketAlpha;
        }
        return lastBucket;
    }

    private static ConcurrentHashMap<String,List<ErrorLog>> getBucket() {
        return bucket;
    }

    /**
     * 向桶中插入数据
     * @param key
     * @param key
     * @param errorLogs
     */
    public static void insertData(String key, List<ErrorLog> errorLogs) {
        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        ConcurrentHashMap<String, List<ErrorLog>> b = getBucket();
        List<ErrorLog> errorsList = b.get(key);
        if (null != errorsList) {
            errorsList.addAll(errorLogs);
        } else {
            synchronized (EmailErrorLogBucket.class) {
                if(null != b.get(key)) {
                    insertData(key, errorLogs);
                } else {
                    b.put(key, errorLogs);
                }
            }
        }
    }
}
