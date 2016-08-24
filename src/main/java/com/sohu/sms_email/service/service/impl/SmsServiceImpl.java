package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.service.SmsService;
import com.sohu.sms_email.utils.DmUtil;
import com.sohu.sms_email.utils.WeixinUtil;
import org.springframework.stereotype.Component;

/**
 * Created by Gary on 2015/10/21.
 */
@Component("smsService")
public class SmsServiceImpl implements SmsService {

    public boolean sendSms(String phoneNo, String msg) {
        //添加发私信监控 测试发私信是否好用
        DmUtil.sendTextDM(DmUtil.DEFALUT_FROM_USERID, "18910556026@sohu.com", msg);
        return WeixinUtil.sendMessage(phoneNo, msg);
    }
}
