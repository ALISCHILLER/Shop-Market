package com.varanegar.framework.base.logging;


import java.text.DateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by atp on 12/20/2016.
 */
public class DebugTree extends Timber.DebugTree {
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return super.createStackElementTag(element) + "/Line:" + element.getLineNumber() + "  /Time: " + currentDateTimeString + "  ";
    }
}
