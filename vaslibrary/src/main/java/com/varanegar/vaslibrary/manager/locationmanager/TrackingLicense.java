package com.varanegar.vaslibrary.manager.locationmanager;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/12/2017.
 */
@Root(name = "Tracking")
public class TrackingLicense {

    @ElementList(name = "TrackingLicense")
    public List<TrackingConfig> configs;

    public Date getExpireDate() {
        TrackingConfig expireAt = Linq.findFirst(configs, new Linq.Criteria<TrackingConfig>() {
            @Override
            public boolean run(TrackingConfig item) {
                return item.name.equals("expireAt");
            }
        });
        return expireAt == null ? null : new Date(Date.parse(expireAt.value));
    }

    public boolean isExpired(Context context) {
        Date expireAt = getExpireDate();
        if (expireAt != null) {
            return expireAt.getTime() < new Date().getTime();
        } else {
            return false;
        }
    }

    public int getTimeInterval() {
        // TODO: 4/22/2018 By homman ahmadi
//        TrackingConfig timeInterval = Linq.findFirst(configs, new Linq.Criteria<TrackingConfig>() {
//            @Override
//            public boolean run(TrackingConfig item) {
//                return item.name.equals("timerInterval");
//            }
//        });
//        if (timeInterval != null)
//            try {
//                return Long.parseLong(timeInterval.value);
//            } catch (Exception ex) {
//                return 15;
//            }
        return
                40;
    }

    public int getMinDistance() {
        // TODO: 4/22/2018 By hooman ahmadi
//        TrackingConfig distance = Linq.findFirst(configs, new Linq.Criteria<TrackingConfig>() {
//            @Override
//            public boolean run(TrackingConfig item) {
//                return item.name.equals("distance");
//            }
//        });
//        if (distance != null)
//            try {
//                return Long.parseLong(distance.value);
//            } catch (Exception ex) {
//                return 10;
//            }
        return 20;
    }

    // This method comment for testing tracking without license
    public static void logLicense(Context context) {
//        SharedPreferences trackingLicenseSharedPref = context.getSharedPreferences("TRACKING_LICENSE", MODE_PRIVATE);
//        String licenseStr = trackingLicenseSharedPref.getString("licenseStr", null);
//        if (licenseStr != null && !licenseStr.isEmpty()) {
//            try {
//                String xml = LicenseEncryptor.decrypt("@zdadV@r@n" + getDeviceId(context).substring(0, 6), licenseStr);
//                TrackingLogManager.addLog(context, LogType.LICENSE_FILE, LogLevel.Info, xml);
//            } catch (Exception e) {
//                TrackingLogManager.addLog(context, LogType.LICENSE_FILE, LogLevel.Error, "فایل لایسنس خراب است!", e.getMessage());
//            }
//        } else {
//            TrackingLogManager.addLog(context, LogType.LICENSE_FILE, LogLevel.Error, "فایل لایسنس خالی است!");
//        }
    }

    // This method change for testing tracking without license
    public static TrackingLicense readLicense(Context context) {
        String xml = "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<Tracking xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
                "\t<TrackingLicense>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>workingday</name>\n" +
                "\t\t\t<id>30</id>\n" +
                "\t\t</config>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>timerInterval</name>\n" +
                "\t\t\t<id>300</id>\n" +
                "\t\t</config>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>duration</name>\n" +
                "\t\t\t<id>8</id>\n" +
                "\t\t</config>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>checkDistance</name>\n" +
                "\t\t\t<id>0</id>\n" +
                "\t\t</config>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>distanceAmount</name>\n" +
                "\t\t\t<id>0</id>\n" +
                "\t\t</config>\n" +
                "\t\t<config>\n" +
                "\t\t\t<name>expireAt</name>\n" +
                "\t\t\t<id>8/13/2018 12:00:00 AM</id>\n" +
                "\t\t</config>\n" +
                "\t</TrackingLicense>\n" +
                "</Tracking>";
        try {
            Serializer serializer = new Persister();
            TrackingLicense trackingLicense = serializer.read(TrackingLicense.class, xml);
            for (TrackingConfig trackingConfig :
                    trackingLicense.configs) {
                if (trackingConfig.name.equals("expireAt")) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.add(Calendar.YEAR, 10); // valid license for 10 years
                    trackingConfig.value = String.valueOf(newDate.getTime());
                }
            }
            return trackingLicense;
        } catch (Exception e) {
            Timber.d("Tracking license file corrupted!");
            return null;
        }
    }

    public static boolean isValid(Context context, Location location) throws TrackingLicenseNotFoundEception, TrackingLicenseExpiredEception {
        final TrackingLicense license = readLicense(context);
        if (license == null)
            throw new TrackingLicenseNotFoundEception();

        if (license.isExpired(context))
            throw new TrackingLicenseExpiredEception();

        long currentDate = location.getTime();

        int startHour = license.getStartWorkingHour(context);
        int endHour = license.getEndWorkingHour(context);

        int hour = new Date(currentDate).getHours();
        return hour <= endHour && hour >= startHour;
    }

    public static boolean isValid(Context context) {
        final TrackingLicense license = readLicense(context);
        return license != null && !license.isExpired(context);
    }

    public int getEndWorkingHour(Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel endTime = sysConfigManager.read(ConfigKey.EndWorkingHour, SysConfigManager.cloud);
        int endTimeInt = SysConfigManager.getIntValue(endTime, 0);
        return endTimeInt;
//        TrackingConfig duration = Linq.findFirst(configs, new Linq.Criteria<TrackingConfig>() {
//            @Override
//            public boolean run(TrackingConfig item) {
//                return item.name.equals("duration");
//            }
//        });
//        int durationPlusStartTime = 0;
//        if (duration != null) {
//            try {
//                int d = Integer.parseInt(duration.value);
//                durationPlusStartTime = d + getStartWorkingHour(context);
//            } catch (Exception ex) {
//                durationPlusStartTime = getStartWorkingHour(context);
//            }
//        }
//        return Math.min(durationPlusStartTime, endTimeInt);
    }

    public int getStartWorkingHour(Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel startTime = sysConfigManager.read(ConfigKey.StartWorkingHour, SysConfigManager.cloud);
        return SysConfigManager.getIntValue(startTime, 0);
//        TrackingConfig duration = Linq.findFirst(configs, new Linq.Criteria<TrackingConfig>() {
//            @Override
//            public boolean run(TrackingConfig item) {
//                return item.name.equals("startWorkingHour");
//            }
//        });
//        if (duration != null)
//            try {
//                return Integer.parseInt(duration.value);
//            } catch (Exception ex) {
//                return 8;
//            }
//        return 8;
    }

    public static int getLicensePolicy(Context context) {
        TrackingLicense license = TrackingLicense.readLicense(context);
        if (license != null) {
            int start = license.getStartWorkingHour(context);
            int end = license.getEndWorkingHour(context);
            Calendar now = Calendar.getInstance();
            int h = now.get(Calendar.HOUR_OF_DAY);
            if (h >= start && h < end)
                return 0;
            else
                return 1;
        } else
            return 1;
    }

    @SuppressLint("HardwareIds")
    @Nullable
    public static String getDeviceId(Context context) {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Timber.e("Manifest.permission.READ_PHONE_STATE Permission not granted");
                    TrackingLogManager.addLog(context, LogType.LICENSE, LogLevel.Error, "Permission READ_PHONE_STATE not granted");
                    SharedPreferences trackingLicenseSharedPref = context.getSharedPreferences("TRACKING_LICENSE", MODE_PRIVATE);
                    deviceId = trackingLicenseSharedPref.getString("DEVICE_ID", null);
                    if (deviceId == null || deviceId.isEmpty())
                        Timber.e("Device id not found in shared preferences");
                } else {
                    if (telephonyManager != null)
                        deviceId = telephonyManager.getDeviceId();
                    else
                        Timber.e("telephonyManager is null!");
                }
            }
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
        if (deviceId == null || deviceId.isEmpty() || deviceId.length() < 7) {
            TrackingLogManager.addLog(context, LogType.LICENSE, LogLevel.Error, "Device Id " + deviceId + " is wrong!!");
            Timber.e("Device Id " + deviceId + " is wrong!!");
            return null;
        }
        SharedPreferences trackingLicenseSharedPref = context.getSharedPreferences("TRACKING_LICENSE", MODE_PRIVATE);
        trackingLicenseSharedPref.edit().putString("DEVICE_ID", deviceId).apply();


        return deviceId;
    }
}
