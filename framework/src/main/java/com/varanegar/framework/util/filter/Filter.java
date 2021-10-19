package com.varanegar.framework.util.filter;

/**
 * Created by A.Torabi on 6/19/2017.
 */

public class Filter
{
    public Filter(String name, String value, boolean khafan)
    {
        this.value = value;
        this.name = name;
        this.khafan = khafan;
    }
    public Filter(String name, String value)
    {
        this.value = value;
        this.name = name;
        this.selected = false;
        this.khafan = false;
    }
    public String name;
    public String value;
    public boolean selected;
    public boolean khafan;
}