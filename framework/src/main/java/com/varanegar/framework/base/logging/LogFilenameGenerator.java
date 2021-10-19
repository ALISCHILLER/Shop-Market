package com.varanegar.framework.base.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by A.Torabi on 10/4/2017.
 */

public class LogFilenameGenerator implements FileNameGenerator {
    @Override
    public String generateFileName(long time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)) + ".log";
    }
}
