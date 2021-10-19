package com.varanegar.vaslibrary.enums;

/**
 * Created by A.Jafarzadeh on 11/12/2018.
 */

public enum NewCustomerConfigType {
    Hidden("2"),
    Necessary("1"),
    Unnecessary("0");

    private final String configTypeText;

    NewCustomerConfigType(final String configTypeText) {
         this.configTypeText = configTypeText;
    }

    @Override
    public String toString() {
        return configTypeText;
    }

}
