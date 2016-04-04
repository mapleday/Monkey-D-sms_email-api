package com.sohu.sms_email.enums;

/**
 * Created by Gary Chan on 2016/4/4.
 */
public enum  CodeEnums {

    SUCCESS(0, "success"),
    FAILED(1, "failed"),
    PARAMS_ERROR(2, "params_error");

    private int type;
    private String name;

    private CodeEnums(int type, String name){
        this.type = type;
        this.name = name;
    }

    public static CodeEnums typeof(int type){
        switch (type){
            case 0 : return SUCCESS;
            case 1 : return FAILED;
        }
        return null;
    }

    public int getType() {
        return type;
    }
    public String getName() {
        return name;
    }
}
