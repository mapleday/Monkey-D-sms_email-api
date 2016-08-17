package com.sohu.sms_email.utils;

import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import com.sohu.snscommon.utils.http.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jy on 16-8-17.
 * 发送微信
 */
public class WeixinUtil {
    private static final String weixinUrl = "http://mtpc.sohu.com/wmsnotify/wmsnotify4sohu.php";
    private static HttpClientUtil httpClientUtil = HttpClientUtil.create("weixinUtil", "sendMessage", 3000);

    public static boolean sendMessage(String mobile_list, String message_post) {
        try {
            Map<String, String> params = new HashMap();
            params.put("mobile_list", mobile_list);
            params.put("message_post", message_post);
            String result = httpClientUtil.getByUtf(weixinUrl, params);
            System.out.println(result);
            if (result == null) {
                LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", message_post, message_post, new Exception("send weixin null"));
                return false;
            } else if (!result.contains("发送成功")) {
                LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", message_post, message_post, new Exception("send weixin null"));
                return false;
            }
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", mobile_list + "#" + message_post, result);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", message_post, message_post, e);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        boolean test = WeixinUtil.sendMessage("18910556026", "test");
        System.out.println(test);
    }
}
