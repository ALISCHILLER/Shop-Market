package com.varanegar.vaslibrary.manager.sysconfigmanager;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class BackOfficeType {
    private final String name;

    private BackOfficeType(String name) {
        this.name = name;
    }

    public static final BackOfficeType Varanegar = new BackOfficeType("varanegar");
    public static final BackOfficeType Rastak = new BackOfficeType("vnlite");
    public static final BackOfficeType ThirdParty = new BackOfficeType("THIRDPARTY");

    public String getName() {
        return name;
    }
}
