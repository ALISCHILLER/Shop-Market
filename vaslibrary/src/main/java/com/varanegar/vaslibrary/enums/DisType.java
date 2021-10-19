package com.varanegar.vaslibrary.enums;

/**
 * Created by Asal on 13/07/2017.
 */

public enum DisType {
    DisType2(2, ""),
    DisType3(3, ""),
    DisType4(4, "");

    private int code;
    private String desc;
    DisType(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String descc) {
        this.desc = desc;
    }
}
