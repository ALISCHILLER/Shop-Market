package com.varanegar.vaslibrary.base;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.logging.FileLoggingTree;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.SQLiteConnectionString;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysNotFoundException;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.backupapi.BackupApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 10/3/2017.
 */

public class BackupManager {
    public static final java.lang.String SEND_TOUR_BACKUP = "last";

    public synchronized static void exportData(final Context context, boolean full, ImageType... imageTypes) throws Exception {
        exportData(context, full, null, imageTypes);
    }

    public synchronized static void exportData(final Context context, boolean full, String prefix, ImageType... imageTypes) throws Exception {
        List<ImageType> imageTypesList = new ArrayList<>();
        Collections.addAll(imageTypesList, imageTypes);
        exportData(context, full, prefix, imageTypesList);
    }

    public synchronized static void exportData(final Context context, boolean full, List<ImageType> imageTypes) throws Exception {
        exportData(context, full, null, imageTypes);
    }

    public synchronized static void exportData(final Context context, boolean full, String prefix, List<ImageType> imageTypes) throws Exception {
        Timber.d("Exporting data started.");

        String databaseFileName = null;
        if (full)
            databaseFileName = VaranegarApplication.getInstance().getDbHandler().exportDb(context.getFilesDir(), null);
        else {
            String tempDbFileName = DateHelper.toString(new Date(), DateFormat.FileName, Locale.US);
            tempDbFileName = "Tmp_" + tempDbFileName;
            VaranegarApplication.getInstance().getDbHandler().exportDb(tempDbFileName, TourManager.getListOfTourTempTables());
            databaseFileName = new DbHandler(context, new SQLiteConnectionString(tempDbFileName)).exportDb(context.getFilesDir(), null);
        }
        String promotionDatabaseFileName = null;
        if (full) {
            try {
                promotionDatabaseFileName = HelperMethods.copyFile(VaranegarApplication.getInstance().getDbHandler().getDatabaseFolder() + "/DiscountDbV3.db", context.getFilesDir(), "/DiscountDbV3.db");
            } catch (Exception ex) {
                Timber.i("promotion database not found");
            }
        }
        String userModelFileName = UserManager.getFilePath(context);
        String tourModelFilename = TourManager.getFilePath(context);
        String userTokenFilename = context.getFilesDir() + "/" + "user.token";
        String appTokenFileName = context.getFilesDir() + "/" + "app.token";
        BackupInfo backupInfo = new BackupInfo();
        backupInfo.IsFullBackup = full;
        backupInfo.Date = new Date();
        backupInfo.UniqueId = UUID.randomUUID();
        backupInfo.DatabaseVersion = VaranegarApplication.getInstance().getDbHandler().getConnectionString().getVersion();
        backupInfo.PackageName = context.getPackageName();
        backupInfo.AppVersionCode = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        backupInfo.AppVersionName = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        backupInfo.DeviceModel = Build.DEVICE;
        backupInfo.DeviceBrand = Build.BRAND;
        backupInfo.DeviceManufacturer = Build.MANUFACTURER;
        backupInfo.DeviceSdk = Build.VERSION.SDK_INT;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= 29)
                backupInfo.DeviceIMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            else
                backupInfo.DeviceIMEI = telephonyManager.getDeviceId();
        } catch (Throwable ex) {
            backupInfo.DeviceIMEI = "Unknown";
        }


        try {
            backupInfo.OwnerKeys = new SysConfigManager(context).readOwnerKeys();
        } catch (OwnerKeysNotFoundException ex) {
            backupInfo.OwnerKeys = null;
        }
        TourManager tourManager = new TourManager(context);
        if (tourManager.isTourAvailable())
        {
            backupInfo.TourId = tourManager.loadTour().UniqueId;
            backupInfo.TourNo = tourManager.loadTour().TourNo;
        }
        UserModel userModel = UserManager.readFromFile(context);
        if (userModel != null) {
            backupInfo.UserName = userModel.UserName;
            backupInfo.BackOfficeId = userModel.BackOfficeId;
        }
        String backupInfoFileName = saveBackupInfo(context, backupInfo);

        File file = new File(FileLoggingTree.getLogDirectory(context));
        List<String> logFilePaths = new ArrayList<>();
        if (file.exists()) {
            File[] logFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.endsWith(".log");
                }
            });
            for (File f :
                    logFiles) {
                logFilePaths.add(f.getAbsolutePath());
            }
        }
        String date = DateHelper.toString(new Date(), DateFormat.FileName, Locale.US);
        String zipFilename;

        zipFilename = date + "_f.backup";
        if (!full)
            zipFilename = date + "_p.backup";

        if (prefix != null)
            zipFilename = prefix + "_" + zipFilename;

        List<String> files = new ArrayList<>();
        files.add(backupInfoFileName);
        files.add(databaseFileName);
        if (full)
            files.add(promotionDatabaseFileName);
        files.add(userModelFileName);
        files.add(tourModelFilename);
        files.add(userTokenFilename);
        files.add(appTokenFileName);
        files.addAll(logFilePaths);
        for (ImageType imageType :
                imageTypes) {
            List<String> imageFiles = listRecursive(HelperMethods.getExternalFilesDir(context, ImageManager.ImagesFolder + "/" + imageType.getName()));
            files.addAll(imageFiles);
        }
        zip(context, files, zipFilename);
        if (!full)
            new File(databaseFileName).delete();
        Timber.d("Exporting data finished.");
    }

    public synchronized static void importData(Context context, String _zipFile) throws IOException {
        Timber.d("Importing backup started...");
        FileInputStream fin = new FileInputStream(_zipFile);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze;
        String dbName = VaranegarApplication.getInstance().getDbHandler().getConnectionString().getName();
        while ((ze = zin.getNextEntry()) != null) {
            String entryName = ze.getName();
            if (entryName != null) {
                if (entryName.equals("app.token")) {
                    saveToFile(context.getFilesDir() + "/app.token", zin);
                } else if (entryName.equals("tour.dat")) {
                    saveToFile(TourManager.getFilePath(context), zin);
                    TourManager tourManager = new TourManager(context);
                    TourModel tourModel = tourManager.loadTour();
                    if (tourModel != null) {
                        tourModel.IsFromBackup = true;
                        tourManager.saveTour(tourModel, context);
                    }
                } else if (entryName.equals("user.dat")) {
                    saveToFile(UserManager.getFilePath(context), zin);
                } else if (entryName.equals("user.token")) {
                    saveToFile(context.getFilesDir() + "/user.token", zin);
                } else if (entryName.equals(dbName)) {
                    String dbPath = context.getDatabasePath(VaranegarApplication.getInstance().getDbHandler().getConnectionString().getName()).getPath();
                    saveToFile(dbPath, zin);
                } else if (entryName.equals("DiscountDbV3.db")) {
                    String ddbPath = context.getDatabasePath("DiscountDbV3.db").getPath();
                    saveToFile(ddbPath, zin);
                } else {
                    if (entryName.startsWith("Tmp_")) {
                        String tmpDbPath = context.getDatabasePath(entryName).getPath();
                        saveToFile(tmpDbPath, zin);
                        VaranegarApplication.getInstance().getDbHandler().importDb(entryName, TourManager.getListOfTourTempTables());
                    } else if (entryName.startsWith("images")) {
                        saveToFile(HelperMethods.getExternalFilesDir(context, "").getPath() + "/" + entryName, zin);
                    }
                }
            }
        }
        zin.close();
        Timber.d("Importing backup finished.");
    }

    private static void saveToFile(String filePath, ZipInputStream zin) throws IOException {
        if (filePath == null || filePath.isEmpty())
            return;

        if (filePath.contains("images")) {
            String subfolder = filePath.substring(0, filePath.lastIndexOf("/"));
            File f = new File(subfolder);
            if (!f.exists()) {
                f.mkdirs();
            }
        }

        int BUFFER = 1024;
        byte data[] = new byte[BUFFER];
        FileOutputStream fout = new FileOutputStream(filePath);
        int n;
        while ((n = zin.read(data, 0, BUFFER)) > -1) {
            fout.write(data, 0, n);
        }
        zin.closeEntry();
        fout.close();
    }

    public static String saveBackupInfo(Context context, BackupInfo backupInfo) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(context.openFileOutput("backup.json", Context.MODE_PRIVATE));
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
        streamWriter.write(gson.toJson(backupInfo));
        streamWriter.close();
        return context.getFilesDir() + "/" + "backup.json";
    }

    @Nullable
    public static BackupInfo getBackupInfo(String zipFile) {
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals("backup.json")) {
                    int BUFFER = 1024;
                    byte data[] = new byte[BUFFER];
                    StringBuilder content = new StringBuilder();
                    int n;
                    while ((n = zin.read(data, 0, BUFFER)) != -1) {
                        content.append(new String(data, 0, n));
                    }
                    zin.closeEntry();
                    String json = content.toString();
                    return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create().fromJson(json, BackupInfo.class);
                }
            }
            zin.close();
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static File[] getLastBackUpFiles(Context context) {
        String path = HelperMethods.getExternalFilesDir(context, "backups").getPath();
        File directory = new File(path);
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".backup") && pathname.getName().startsWith(SEND_TOUR_BACKUP);
            }
        });
        return files;
    }

    public static File[] getBackUpFiles(Context context) {
        String path = HelperMethods.getExternalFilesDir(context, "backups").getPath();
        File directory = new File(path);
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".backup") && !(pathname.getName().startsWith(SEND_TOUR_BACKUP));
            }
        });
        return files;
    }

    @NonNull
    public static List<File> getList(Context context, @Nullable final BackupType backupType) {
        try {
            String path = HelperMethods.getExternalFilesDir(context, "backups").getPath();
            File directory = new File(path);
            File[] files = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (backupType == null)
                        return pathname.getName().endsWith(".backup");
                    else if (backupType == BackupType.Full)
                        return pathname.getName().endsWith("_f.backup");
                    else
                        return pathname.getName().endsWith("_p.backup");
                }
            });
            if (files == null || files.length == 0)
                return new ArrayList<>();
            return Arrays.asList(files);
        } catch (Exception ex) {
            Timber.e(ex);
            return new ArrayList<>();
        }
    }

    @Nullable
    public static File getLast(Context context) {
        try {
            String path = HelperMethods.getExternalFilesDir(context, "backups").getPath();
            File directory = new File(path);
            File[] files = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".backup") && !(pathname.getName().startsWith(SEND_TOUR_BACKUP));
                }
            });
            if (files == null || files.length == 0)
                return null;
            List<File> fileList = Arrays.asList(files);
            Linq.sort(fileList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    File b1 = (File) o1;
                    File b2 = (File) o2;
                    return b2.getName().compareTo(b1.getName());
                }
            });
            return fileList.get(0);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }


    public interface IUploadCallBack {
        void onSuccess();

        void onFailure(String error);
    }

    public static void uploadBackup(@NonNull final Context context, @NonNull final IUploadCallBack callBack) {
        final UserModel userModel = UserManager.readFromFile(context);
        if (userModel == null) {
            Timber.e("User not found.");
            callBack.onFailure(context.getString(R.string.user_not_found));
            return;
        }
        final File file = getLast(context);
        if (file != null) {
            PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    BackupApi backupApi = new BackupApi(context);
                    backupApi.runWebRequest(backupApi.uploadBackup(userModel, file), new WebCallBack<ResponseBody>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(ResponseBody result, Request request) {
                            Timber.i("Backup file sent successfully");
                            callBack.onSuccess();
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String err = WebApiErrorBody.log(error, context);
                            callBack.onFailure(err);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Timber.e(t);
                            callBack.onFailure(context.getString(R.string.network_error));
                        }
                    });
                }

                @Override
                public void failed() {
                    Timber.e(context.getString(R.string.network_error));
                    callBack.onFailure(context.getString(R.string.network_error));
                }
            });

        } else {
            Timber.e("Backup not found.");
            callBack.onFailure(context.getString(R.string.backup_not_found));
        }

    }

    public static void zip(Context context, List<String> _files, String zipFileName) throws IOException {
        int BUFFER = 1024;
        BufferedInputStream origin;
        FileOutputStream dest = new FileOutputStream(HelperMethods.getExternalFilesDir(context, "backups") + "/" + zipFileName);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                dest));
        byte data[] = new byte[BUFFER];

        for (String f : _files) {
            if (f != null) {
                try {
                    FileInputStream fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry;
                    if (f.contains("images")) {
                        int s = f.indexOf("/images/");
                        String d = f.substring(s + 1, f.length());
                        entry = new ZipEntry(d);
                    } else
                        entry = new ZipEntry(f.substring(f.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                } catch (FileNotFoundException ex) {
                    Timber.i("File  " + f + " not found.");
                }
            }
        }
        out.close();
    }

    public static void wipeOldDataBaseOnQty(Context context) {
        File[] backupInfos = getBackUpFiles(context);
        if (backupInfos != null && backupInfos.length > 0) {
            Arrays.sort(backupInfos);
            if (backupInfos.length > 10) {
                for (int i = 0; i < backupInfos.length - 10; i++)
                    backupInfos[i].delete();
            }
        }
        File[] lastBackupInfos = getLastBackUpFiles(context);
        if (lastBackupInfos != null && lastBackupInfos.length > 0) {
            Arrays.sort(lastBackupInfos);
            if (lastBackupInfos.length > 10) {
                for (int i = 0; i < lastBackupInfos.length - 10; i++)
                    lastBackupInfos[i].delete();
            }
        }
    }

    public static void wipeOldData(Context context) {
        List<File> files = getList(context, null);
        for (File file :
                files) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -4);
            if (new Date(file.lastModified()).before(cal.getTime())) {
                try {
                    file.delete();
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        }
    }

    public static void wipeAllBackups(Context context) {
        String path = HelperMethods.getExternalFilesDir(context, "backups").getPath();
        File file = new File(path);
        String[] entries = file.list();
        for (String s : entries) {
            File currentFile = new File(file.getPath(), s);
            currentFile.delete();
        }
        file.delete();
    }

    public static void exportDataAsync(final Context context, final boolean full) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BackupManager.exportData(context, full);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        thread.start();
    }

    public enum BackupType {
        Full,
        Partial
    }

    private static List<String> listRecursive(File fileOrDirectory) {
        List<String> files = new ArrayList<>();
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                files.addAll(listRecursive(child));
        else
            files.add(fileOrDirectory.getAbsolutePath());
        return files;
    }
}
