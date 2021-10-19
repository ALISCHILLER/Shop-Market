package com.varanegar.vaslibrary.enums;

/**
 * Created by A.Jafarzadeh on 6/15/2017.
 */

public enum EvcType
{
    NON(0, "نامشخص"),
    TOSELL(1, "تبدیل درخواست به فاکتور یا حواله"),
    SELLRETURN(2, "برگشت فاکتور"),
    FOLLOW_ORDER(3, "پیگیری حواله"),
    HOTSALE(4, "فروش گرم"),
    POS(5, "فروش فرشگاهی");

    private int code;
    private String desc;
    EvcType(int code, String desc){
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
