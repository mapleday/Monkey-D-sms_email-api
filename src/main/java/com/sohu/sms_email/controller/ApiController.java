package com.sohu.sms_email.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sohu.sms_email.enums.CodeEnums;
import com.sohu.sms_email.service.*;
import com.sohu.sms_email.utils.ZipUtils;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import junit.framework.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class ApiController extends AbstractApiController {

    private static final Joiner JOINER = Joiner.on("_").skipNulls();

    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsErrorLogService smsErrorLogService;
    @Autowired
    private EmailErrorLogService emailErrorLogService;

    /**
     * 发送短信
     *
     * @param phoneNo 电话号码
     * @param msg     短信内容
     * @return
     */
    @RequestMapping("sendSms")
    @ResponseBody
    public String sendSms(@RequestParam("phoneNo") String phoneNo, @RequestParam("msg") String msg) {
        if (Strings.isNullOrEmpty(phoneNo) || Strings.isNullOrEmpty(msg)) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", JOINER.join(phoneNo, msg), CodeEnums.PARAMS_ERROR.getName(), new Exception("phoneNo or msg is empty!!!"));
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, joiner.join("phoneNo", "msg")));
        }
        boolean isSuccess = smsService.sendSms(phoneNo, msg);
        if (isSuccess) {
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", JOINER.join(phoneNo, msg), CodeEnums.SUCCESS.getName());
            return genRetMsg(CodeEnums.SUCCESS, null);
        } else {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", JOINER.join(phoneNo, msg), CodeEnums.FAILED.getName(), new Exception("SMS send Failed!"));
            return genRetMsg(CodeEnums.FAILED, smsSendFail);
        }
    }

    /**
     * 发送简单文本邮件，多个收件地址请用“|”分隔
     *
     * @param subject 主题
     * @param text    邮件内容
     * @param to      收件地址
     * @return
     */
    @RequestMapping("sendSimpleEmail")
    @ResponseBody
    public String sendSimpleEmail(@RequestParam("subject") String subject, @RequestParam("text") String text, @RequestParam("to") String to) {
        if (Strings.isNullOrEmpty(to)) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", JOINER.join(subject, text, to), CodeEnums.PARAMS_ERROR.getName(), new Exception("receiver should not empty!!"));
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "to"));
        }
        String[] mailTo = to.split("\\|");
        if (0 == mailTo.length) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", JOINER.join(subject, text, to), CodeEnums.PARAMS_ERROR.getName(), new Exception("receiver should not empty!!"));
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "to"));
        }
        try {
            emailService.sendSimpleEmail(subject, text, mailTo);
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", JOINER.join(subject, text, to), null);
            return genRetMsg(CodeEnums.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", JOINER.join(subject, text, to), CodeEnums.FAILED.getName(), e);
            return genRetMsg(CodeEnums.FAILED, emailSendFail);
        }

    }

    /**
     * 发送Html邮件，多个收件地址请用“|”分隔
     *
     * @param subject 主题
     * @param text    邮件内容
     * @param to      收件地址
     * @return
     */
    @RequestMapping("sendHtmlEmail")
    @ResponseBody
    public String sendHtmlEmail(@RequestParam("subject") String subject, @RequestParam("text") String text, @RequestParam("to") String to) {
        if (Strings.isNullOrEmpty(to)) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", JOINER.join(subject, text, to), CodeEnums.PARAMS_ERROR.getName(), new Exception("receiver should not empty!!"));
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "to"));
        }
        String[] mailTo = to.split("\\|");
        if (0 == mailTo.length) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", JOINER.join(subject, text, to), CodeEnums.PARAMS_ERROR.getName(), new Exception("receiver should not empty!!"));
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "to"));
        }
        try {
            emailService.sendHtmlEmail(subject, text, mailTo);
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", JOINER.join(subject, text, to), null);
            return genRetMsg(CodeEnums.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", JOINER.join(subject, text, to), CodeEnums.FAILED.getName(), e);
            return genRetMsg(CodeEnums.FAILED, emailSendFail);
        }
    }

    /**
     * 收集错误的详细信息，以便发送邮件
     *
     * @param errorlogs 错误日志
     * @return
     */
    @RequestMapping("sendErrorLogEmail")
    @ResponseBody
    public String receiveErrorLogEmail(@RequestParam("errorLogs") String errorlogs) {
        try {
            if (Strings.isNullOrEmpty(errorlogs)) {
                return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "errorLogs"));
            }
            errorlogs = ZipUtils.gunzip(errorlogs);
            emailErrorLogService.handleEmailErrorLog(errorlogs);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveErrorLogDetailEmail", errorlogs, "", e);
        } finally {
            LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receieveErrorLogEmail", errorlogs.getBytes().length / (1024.0 * 1024) + "MB", null);
        }
        return genRetMsg(CodeEnums.SUCCESS, null);
    }

    /**
     * 发送超时短信
     *
     * @param timeoutCount
     * @return
     */
    @RequestMapping("sendTimeoutCount")
    @ResponseBody
    public String receiveTimeoutCount(@RequestParam("timeoutCount") String timeoutCount) {
        if (Strings.isNullOrEmpty(timeoutCount)) {
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "timeoutCount"));
        }
        smsErrorLogService.handleTimeoutCount(timeoutCount);
        LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveTimeoutCount", timeoutCount, null);
        return genRetMsg(CodeEnums.SUCCESS, null);
    }

    /**
     * 发送错误日志接口
     *
     * @param subject       主题
     * @param error_log     错误日志
     * @param email_address email邮件地址，多个以","分割
     * @return
     */
    @RequestMapping("send_error_log")
    @ResponseBody
    public String sendErrorLogs(@RequestParam("subject") String subject, @RequestParam("error_log") String error_log, @RequestParam("email_address") String email_address) {
        if (Strings.isNullOrEmpty(email_address)) {
            return genRetMsg(CodeEnums.PARAMS_ERROR, String.format(paramsError, "email_address"));
        }
        try {
            emailErrorLogService.sendErrorLog(subject, error_log, email_address);
            return genRetMsg(CodeEnums.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendErrorLogs", subject + email_address, "", e);
            return genRetMsg(CodeEnums.FAILED, emailSendFail);
        }
    }

    /**
     * 发送错误日志接口
     *
     * @param subject       主题
     * @param error_log     错误日志
     * @param email_address email邮件地址，多个以","分割
     * @return
     */
    @RequestMapping("webHealth")
    @ResponseBody
    public String webHealth() {
        return "up";
    }
}
