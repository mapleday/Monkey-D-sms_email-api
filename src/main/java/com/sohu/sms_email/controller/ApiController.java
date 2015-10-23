package com.sohu.sms_email.controller;

import com.sohu.sms_email.service.EmailErrorLogService;
import com.sohu.sms_email.service.EmailService;
import com.sohu.sms_email.service.SmsErrorLogService;
import com.sohu.sms_email.service.SmsService;
import com.sohu.sns.common.utils.json.JsonMapper;
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
	private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

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
			return FAILURE;
		}
		String[] mailTo = to.split("\\|");
		if(0 == mailTo.length) {
			return FAILURE;
		}
		emailService.sendHtmlEmail(subject, text, mailTo);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "sendHtmlEmail", subject+"_"+text+"_"+to, null);
		return SUCCESS;
	}

	/**
	 * 收集出现的实例与错误的总数，以便发送短信
	 * @param instanceCount	出现错误的机器实例个数
	 * @param errorCount	出现的错误总数
	 * @return
	 */
	@RequestMapping("sendErrorLogSms")
	@ResponseBody
	public String receiveErrorLogSms(@RequestParam("instanceCount") String instanceCount, @RequestParam("errorCount") String errorCount) {
		if(null == instanceCount || 0 == instanceCount.length() || null == errorCount || 0 == errorCount.length()) {
			return FAILURE;
		}
		int instanceNum = Integer.parseInt(instanceCount);
		int errorNum = Integer.parseInt(errorCount);

		smsErrorLogService.handleSmsErrorLog(instanceNum, errorNum);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveErrorLogSms", "instanceCount:"+instanceCount+", errorCount:"+errorCount, SUCCESS);
		return SUCCESS;
	}

	/**
	 * 收集错误的详细信息，以便发送邮件
	 * @param instanceCount	出现错误的实例的总数
	 * @param errorDetail	错误的详情
	 * @return
	 */
	@RequestMapping("sendErrorLogEmail")
	@ResponseBody
	public String receiveErrorLogEmail(@RequestParam("instanceCount") String instanceCount, @RequestParam("errorDetail") String errorDetail) {
		if(null == instanceCount || 0 == instanceCount.length() || null == errorDetail ) {
			return FAILURE;
		}

		int instanceNum = Integer.parseInt(instanceCount);
		emailErrorLogService.handleEmailErrorLog(instanceNum, errorDetail);
		LOGGER.buziLog(ModuleEnum.SMS_EMAIL_SERVICE, "receiveErrorLogDetailEmail", "instanceCount:" + instanceCount + ", errorDetail:" + errorDetail, SUCCESS);
		return SUCCESS;
	}
}
