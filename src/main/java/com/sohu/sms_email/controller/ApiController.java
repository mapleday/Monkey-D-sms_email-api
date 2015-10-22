package com.sohu.sms_email.controller;

import com.sohu.sms_email.service.EmailService;
import com.sohu.sms_email.service.SmsService;
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
			return FAILURE;
		}
		smsService.sendSms(phoneNo, msg);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSms", phoneNo+"_"+msg, null);
		return SUCCESS;
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
			return FAILURE;
		}
		String[] mailTo = to.split("\\|");
		if(0 == mailTo.length) {
			return FAILURE;
		}
		emailService.sendSimpleEmail(subject, text, mailTo);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendSimpleEmail", subject+"_"+text+"_"+to, null);
		return SUCCESS;
	}
	
}
