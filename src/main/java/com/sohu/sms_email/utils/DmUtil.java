package com.sohu.sms_email.utils;

import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import com.sohu.snscommon.utils.http.HttpClientUtil;
import com.sohu.snscommon.utils.service.SignatureUtil;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by jy on 16-8-24.
 */
public class DmUtil {
    private static final JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
    private static final String dmUrl = "http://dm-crm.sce.sohuno.com/notice/";
    private static final String dmAppkey = "5febf6e78555ba72d9eede8e422bf10a";
    private static final String dmAppid = "10005";
    public static final String DEFALUT_FROM_USERID = "sohu_official@sohu.com";

    /**
     * 发送纯文本私信
     *
     * @param fromUserId 发送者用户id
     * @param toUserId   接受者用户id
     * @param message    消息内容
     * @throws Exception
     */
    public static void sendTextDM(String fromUserId, String toUserId, String message) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("to_user_id", toUserId);
        params.put("from_user_id", fromUserId);
        params.put("type", "0");
        params.put("message", message);
        params.put("appid", dmAppid);
        params.put("flyer", String.valueOf(System.currentTimeMillis()));

        String sig = SignatureUtil.createSig(params, dmAppkey);
        params.put("sig", sig);

        com.sohu.snscommon.utils.http.HttpClientUtil httpClientUtil = HttpClientUtil.create("VideoUtil", "sendTextDM", 3000);

        long startsend = System.currentTimeMillis();
        String result = null;
        for (int i = 0; i < 3; i++) {
            try {
                result = httpClientUtil.postByUtf(dmUrl, params, null);
            } catch (Exception e) {
                LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "VideoUtil.sendTextDM", toUserId, message, e);
            }
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "VideoUtil.sendTextDM", toUserId, result);
            HashMap resultMap = jsonMapper.fromJson(result, HashMap.class);
            if (resultMap != null) {
                Object status = resultMap.get("status");
                if ("200".equals(status.toString())) {
                    break;
                } else {
                    LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "VideoUtil.sendTextDM", toUserId, message);
                }
            } else {
                LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "VideoUtil.sendTextDM", toUserId, "result is null");
            }
        }
        LOGGER.statLog(ModuleEnum.SMS_EMAIL_SERVICE, "VideoUtil.sendTextDM", params.toString(), result, System.currentTimeMillis() - startsend, 0L, 0L);
    }
}
