package com.sohu.sms_email.utils;

import com.alibaba.druid.util.StringUtils;
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
    /**
     * 微信地址
     */
    private static final String weixinUrl = "http://mtpc.sohu.com/wmsnotify/wmsnotify4sohu.php";
    /**
     * httpclient
     */
    private static HttpClientUtil httpClientUtil = HttpClientUtil.create("weixinUtil", "sendMessage", 3000);


    /**
     * 发送微信
     *
     * @param mobileList  手机号列表 逗号分割
     * @param messagePost 发送内容
     * @return ture 发送成功
     */
    public static boolean sendMessage(String mobileList, String messagePost) {
        if (StringUtils.isEmpty(mobileList)) {
            return false;
        }
        String[] mobileArr = mobileList.split(",");
        for (String mobile : mobileArr) {
            for (int i = 0; i < 3; i++) {
                boolean success = send(mobile, messagePost);
                if (success) {
                    break;
                }
            }
        }
        return true;
    }


    /**
     * 发送微信
     *
     * @param mobile  一个手机号
     * @param content 发送内容
     * @return ture 发送成功
     */
    private static boolean send(String mobile, String content) {
        try {
            Map<String, String> params = new HashMap();
            params.put("mobile_list", mobile);
            params.put("message_post", content);
            String result = httpClientUtil.getByUtf(weixinUrl, params);
            System.out.println(result);
            if (result == null) {
                LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", mobile, content, new Exception("send weixin null"));
                return false;
            } else if (!result.contains("发送成功")) {
                LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", mobile, content, new Exception("send weixin null"));
                return false;
            }
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", mobile + "#" + content, result);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "WeixinUtil.sendMessage", mobile, content, e);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        boolean test = WeixinUtil.sendMessage("18511871276,18910556026", "test2");
        System.out.println(test);
    }
}
