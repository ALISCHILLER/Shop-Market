package com.varanegar.vaslibrary.enums;

/**
 * Created by Asal on 13/07/2017.
 */

public enum PrizeCalcTypeValue {
    DiscountTable("c756e50e-b5a5-4181-a0ea-d8f08b1b9fdc", "محاسبه از جدول تخفیفات و جوایز"),
    Free("4cc866f9-eb19-4c76-8a41-f8207104cdbf", "آزاد");

    private String value;
    private String desc;

    PrizeCalcTypeValue(String value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String descc) {
        this.desc = desc;
    }
    public String getValue() { return value;  }
    public void setValue(String value) { this.value = value; }
}
