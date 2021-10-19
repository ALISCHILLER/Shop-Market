package com.varanegar.vaslibrary.enums;

/**
 * Created by Asal on 13/07/2017.
 */

public enum BackOfficeTypeValue
    {
        Varanegar("varanegar", "ورانگر"),
        VnLite("vnlite", "رستاک");

        private String value;
        private String desc;

        BackOfficeTypeValue(String value, String desc){
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
