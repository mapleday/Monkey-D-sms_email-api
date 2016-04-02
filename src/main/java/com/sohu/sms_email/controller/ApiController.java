package com.sohu.sms_email.controller;

import com.google.common.base.Strings;
import com.sohu.sms_email.service.*;
import com.sohu.sms_email.utils.ZipUtils;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class ApiController {

	private static final String SUCCESS = "success";
	private static final String FAILURE = "failure";

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
	 * @param phoneNo	电话号码
	 * @param msg	短信内容
	 * @return
	 */
	@RequestMapping("sendSms")
	@ResponseBody
	public String sendSms(@RequestParam("phoneNo") String phoneNo, @RequestParam("msg") String msg){
		if(null == phoneNo || 0 == phoneNo.length() || null == msg) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", phoneNo+"_"+msg, FAILURE, new Exception("phoneNo or msg is empty!!!"));
			return FAILURE;
		}
		boolean isSuccess = smsService.sendSms(phoneNo, msg);
		if(isSuccess) {
			LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", phoneNo+"_"+msg, SUCCESS);
			return SUCCESS;
		} else {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", phoneNo+"_"+msg, FAILURE, new Exception("SMS send Failed!"));
			return FAILURE;
		}
	}

	/**
	 * 发送简单文本邮件，多个收件地址请用“|”分隔
	 * @param subject	主题
	 * @param text	邮件内容
	 * @param to	收件地址
	 * @return
	 */
	@RequestMapping("sendSimpleEmail")
	@ResponseBody
	public String sendSimpleEmail(@RequestParam("subject") String subject, @RequestParam("text") String text, @RequestParam("to") String to) {
		if(null == to || 0 == to.length()) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", subject+"_"+text+"_"+to, FAILURE, new Exception("receiver should not empty!!"));
			return FAILURE;
		}
		String[] mailTo = to.split("\\|");
		if(0 == mailTo.length) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", subject+"_"+text+"_"+to, FAILURE, new Exception("receiver should not empty!!"));
			return FAILURE;
		}
		try {
			emailService.sendSimpleEmail(subject, text, mailTo);
			LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", subject + "_" + text + "_" + to, null);
			return SUCCESS;
		} catch (Exception e) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", subject + "_" + text + "_" + to, FAILURE, e);
			return FAILURE;
		}

	}

	/**
	 * 发送Html邮件，多个收件地址请用“|”分隔
	 * @param subject	主题
	 * @param text	邮件内容
	 * @param to	收件地址
	 * @return
	 */
	@RequestMapping("sendHtmlEmail")
	@ResponseBody
	public String sendHtmlEmail(@RequestParam("subject") String subject, @RequestParam("text") String text, @RequestParam("to") String to) {
		if(null == to || 0 == to.length()) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", subject+"_"+text+"_"+to, FAILURE, new Exception("receiver should not empty!!"));
			return FAILURE;
		}
		String[] mailTo = to.split("\\|");
		if(0 == mailTo.length) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", subject+"_"+text+"_"+to, FAILURE, new Exception("receiver should not empty!!"));
			return FAILURE;
		}
		try {
			emailService.sendHtmlEmail(subject, text, mailTo);
			LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", subject+"_"+text+"_"+to, null);
			return SUCCESS;
		} catch (Exception e) {
			LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", subject + "_" + text + "_" + to, FAILURE, e);
			return FAILURE;
		}
	}

	/**
	 * 收集错误的详细信息，以便发送邮件
	 * @param errorlogs 错误日志
	 * @return
	 */
	@RequestMapping("sendErrorLogEmail")
	@ResponseBody
	public String receiveErrorLogEmail(@RequestParam("errorLogs") String errorlogs) {
        try {
            if(Strings.isNullOrEmpty(errorlogs)) {
                return FAILURE;
            }
			errorlogs = ZipUtils.gunzip(errorlogs);
            emailErrorLogService.handleEmailErrorLog(errorlogs);
        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveErrorLogDetailEmail",errorlogs,"",e);
        } finally {
			LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receieveErrorLogEmail", errorlogs.getBytes().length/(1024.0*1024) + "MB", null);
		}
        return SUCCESS;
	}

	/**
	 * 发送超时短信
	 * @param timeoutCount
	 * @return
	 */
	@RequestMapping("sendTimeoutCount")
	@ResponseBody
	public String receiveTimeoutCount(@RequestParam("timeoutCount") String timeoutCount) {
		if(Strings.isNullOrEmpty(timeoutCount)) {
			return FAILURE;
		}
		smsErrorLogService.handleTimeoutCount(timeoutCount);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveTimeoutCount", timeoutCount, null);
		return SUCCESS;
	}
}
