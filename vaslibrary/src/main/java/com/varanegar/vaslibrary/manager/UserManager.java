package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.account.Account;
import com.varanegar.framework.base.account.AccountManager;
import com.varanegar.framework.base.account.OnError;
import com.varanegar.framework.base.account.OnTokenAcquired;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.SecurityUtils;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

import com.varanegar.vaslibrary.model.user.User;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.model.user.UserModelRepository;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by atp on 3/7/2017.
 */

public class UserManager extends BaseManager<UserModel> {
    private static final String USER_FILE_NAME = "user.dat";
    private static UserModel staticUserModel;

    public UserManager(Context context) {
        super(context, new UserModelRepository());
    }

    public static Query getAll(@Nullable String text) {
        Query query = new Query().from(User.UserTbl);
        if (text != null)
            query.whereAnd(Criteria.contains(User.UserName, text));

        query.orderByAscending(User.UserName);
        return query;
    }

    public UserModel getUser(String username) {
        Query query = new Query();
        query.from(User.UserTbl).whereAnd(Criteria.equals(User.UserName, username));
        UserModel userModel = getItem(query);

        if (userModel == null) {
            Timber.e("User not found in database");
            return null;
        } else {
            Timber.d("User " + username + " fetched from database");
            return userModel;
        }
    }
    public UserModel getUsers(String username) {
        Query query = new Query();
        UserModel selectedUserModel=null;
        query.from(User.UserTbl).whereAnd(Criteria.contains(User.UserName, username));
        List<UserModel> userModels = getItems(query);
        for(UserModel userModel: userModels ){
            if(userModel.UserName.trim().equals(username)){
             selectedUserModel=userModel;
             break;
            }
        }

        if (selectedUserModel == null) {
            Timber.e("User not found in database");
            return null;
        } else {
            Timber.d("User " + username + " fetched from database");
            return selectedUserModel;
        }
    }


    public synchronized void login(String username, String password,String deviceId,String token,String vpnUser,
                                   final OnTokenAcquired onTokenAcquired, final OnError onError) {
        final Account account = new Account(username, password,deviceId,token,vpnUser);
        PingApi pingApi = new PingApi();
        Timber.d("Ping before login.");
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                Timber.d("Ping was successful.");
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
                AccountManager manager = new AccountManager();
                manager.setBaseUrl(ipAddress);
                manager.getAuthToken(account, ownerKeys.OwnerKey + "," + ownerKeys.DataOwnerKey + "," +
                        ownerKeys.DataOwnerCenterKey, onTokenAcquired, onError);
            }

            @Override
            public void failed() {
                Timber.e("Ping failed!");
                onError.onNetworkFailure(new RuntimeException("server not found"));
            }
        });
    }

    public synchronized static void writeToFile(UserModel user, Context context) {
        Timber.d("Writing user to file");
        try {
            String json = VaranegarGsonBuilder.build().create().toJson(user);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE));
            String text = SecurityUtils.encrypt(json);
            outputStreamWriter.write(text);
            outputStreamWriter.close();
            Timber.d("User was written to file successfully");
            staticUserModel = user;
        } catch (Exception e) {
            Timber.e(e, "Writing user to file failed.");
        }
    }

    public synchronized static UserModel readFromFile(Context context) {
        if (staticUserModel != null)
            return staticUserModel;
        Timber.d("Reading user from file");
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream inputStream = context.openFileInput(USER_FILE_NAME);
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = inputStream.read(buffer)) != -1) {
                content.append(new String(buffer, 0, n));
            }
            String json = SecurityUtils.decrypt(content.toString());
            staticUserModel = VaranegarGsonBuilder.build().create().fromJson(json, UserModel.class);
            return staticUserModel;
        } catch (FileNotFoundException e) {
            Timber.d("User file not found!");
            return null;
        } catch (Exception e) {
            Timber.e(e, "Reading user from file failed! File content = " + content.toString());
            return null;
        }
    }


    public synchronized static void logout(VaranegarActivity activity) {
        if (activity != null) {
            Timber.d("User signed out!");
            activity.deleteFile(USER_FILE_NAME);
            staticUserModel = null;
            if (!activity.isFinishing())
                activity.finish();
        }
    }

    public static String getFilePath(Context context) {
        return context.getFilesDir() + "/" + UserManager.USER_FILE_NAME;
    }
}
