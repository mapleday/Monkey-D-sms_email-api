package com.sohu.sms_email.service.service.impl;

import com.sohu.sms_email.model.PersonInfo;
import com.sohu.sms_email.utils.DateUtils;
import com.sohu.sns.common.utils.json.JsonMapper;
import com.sohu.snscommon.utils.LOGGER;
import com.sohu.snscommon.utils.SMS;
import com.sohu.snscommon.utils.constant.ModuleEnum;
import com.sohu.snscommon.utils.http.HttpClientUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Gary on 2015/12/24.
 */
@Component
public class SelectPersonServiceImpl {

    private static String sms_email_baseUrl = "";
    private static String simpleEmailInterface = "";
    private static String sendSmsInterface = "";
    private static String person_admin_phone = "";
    private static String person_admin_email = "";
    private static List<PersonInfo> dutyPersonInfos;
    private static String dutyContent = "";
    private static String dutyMailSubject = "";
    private static String failSubject = "";
    private static String failContent = "";
    private static Integer flag;

//    @Scheduled(cron = "0 0/5 * * * ? ")
    @Scheduled(cron = "0 0 18 * * ?")
    public void send() throws Exception {
        try {

            PersonInfo personInfo = dutyPersonInfos.get(flag ++);

            /**发送值班提醒邮件和短信**/
            String content = String.format(dutyContent, personInfo.getName());
            String subject = String.format(dutyMailSubject, DateUtils.getCurrentDate());
            StringBuilder emailBuffer = new StringBuilder();
            StringBuilder smsBuffer = new StringBuilder();
            for(PersonInfo p : dutyPersonInfos) {
                if(0 != emailBuffer.length()) {
                    emailBuffer.append("|");
                }
                if(0 != smsBuffer.length()) {
                    smsBuffer.append(",");
                }
                emailBuffer.append(p.getEmail());
                smsBuffer.append(p.getPhone());
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("subject", subject);
            map.put("text", content);
            map.put("to", emailBuffer.toString());
            try {
                HttpClientUtil.getStringByPost(sms_email_baseUrl+simpleEmailInterface, map, null);
            } catch (Exception e) {
                SMS.sendMessage(person_admin_phone, String.format(failContent, personInfo.getName()));
            } finally {
                map.clear();
            }

            map.put("phoneNo", smsBuffer.toString());
            map.put("msg", content);
            try {
                HttpClientUtil.getStringByPost(sms_email_baseUrl+sendSmsInterface, map, null);
            } catch (Exception e) {
                SMS.sendMessage(person_admin_phone, String.format(failContent, personInfo.getName()));
            } finally {
                map.clear();
            }

            System.out.println("值班人：" + personInfo.getName() + "time : " + DateUtils.getCurrentTime());

            if(flag == dutyPersonInfos.size()) {
                flag = 0;
                Collections.shuffle(dutyPersonInfos);
            }

        } catch (Exception e) {
            LOGGER.errorLog(ModuleEnum.MONITOR_SERVICE, "select_person", null, null, e);
            e.printStackTrace();
        }
    }

    /**
     * 初始化值班人员信息
     */
    public static void initEnv(String monitorUrl, String dutyInfo) {
        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
        Map<String, String> urls = jsonMapper.fromJson(monitorUrl, HashMap.class);
        Map<String, String> dutyInfoMap = jsonMapper.fromJson(dutyInfo, HashMap.class);
        sms_email_baseUrl = urls.get("base_url");
        simpleEmailInterface = urls.get("simple_email_interface");
        sendSmsInterface = urls.get("send_sms_interface");
        person_admin_email = dutyInfoMap.get("person_admin_email");
        person_admin_phone = dutyInfoMap.get("person_admin_phone");
        dutyContent = dutyInfoMap.get("duty_content");
        dutyMailSubject = dutyInfoMap.get("mail_subject");
        failSubject = dutyInfoMap.get("fail_subject");
        failContent = dutyInfoMap.get("fail_content");
        String dutyPersonInfo = dutyInfoMap.get("person_info");
        if(null != dutyPersonInfo) {
            String[] arr = dutyPersonInfo.split("\\|");
            dutyPersonInfos = new ArrayList<PersonInfo>();
            for(int i=0; i<arr.length; i++) {
                String[] everyPerson = arr[i].split(",");
                if(3 == everyPerson.length) {
                    dutyPersonInfos.add(new PersonInfo(everyPerson[0], everyPerson[1], everyPerson[2]));
                }
            }
        }
        if(null == flag) {
           flag = new Random().nextInt(dutyPersonInfos.size()-1);
        }
    }
}
