package com.varanegar.framework.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class Hardware {
    public static int getNumCores() throws Exception {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        //Get directory containing CPU info
        File dir = new File("/sys/devices/system/cpu/");
        //Filter to only list the devices we care about
        File[] files = dir.listFiles(new CpuFilter());
        //Return the number of cores (virtual CPU devices)
        return files.length;

    }
}
