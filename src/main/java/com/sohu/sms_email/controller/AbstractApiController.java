package com.sohu.sms_email.controller;

import com.google.common.base.Joiner;
import com.sohu.sms_email.enums.CodeEnums;
import com.sohu.sns.common.utils.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gary Chan on 2016/4/4.
 */
public abstract class AbstractApiController {

    protected static Joiner joiner = Joiner.on(" or ").skipNulls();
    protected static String paramsError = "%s cannot empty!";
    protected static String smsSendFail = "sms send failed ! ";
    protected static String emailSendFail = "email send failed ! ";
    private static JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

    protected String genRetMsg(CodeEnums codeEnums, String detail) {
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put("code", codeEnums.getType());
        map.put("desc", codeEnums.getName());
        map.put("detail", null == detail?"":detail);
        return jsonMapper.toJson(map);
    }
}
