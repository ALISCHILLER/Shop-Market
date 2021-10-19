package com.varanegar.vaslibrary.manager.locationmanager;

import org.simpleframework.xml.Element;

/**
 * Created by A.Torabi on 8/12/2017.
 */

public class TrackingConfig {
    @Element(name = "name")
    public String name;
    @Element(name = "id")
    public String value;
}
