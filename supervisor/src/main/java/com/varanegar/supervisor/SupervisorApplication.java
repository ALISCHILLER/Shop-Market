package com.varanegar.supervisor;

import android.app.Application;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.SQLiteConnectionString;
import com.varanegar.vaslibrary.base.VasApplication;

import timber.log.Timber;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class SupervisorApplication extends VasApplication {
    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.Supervisor;
    }

    @Override
    protected VaranegarApplication.GRSAppId grsAppId() {
        return VaranegarApplication.GRSAppId.Supervisor;
    }

    @Override
    protected String getDatabaseName() {
        return "supervisor";
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        try {
//            SQLiteConnectionString connectionString = new SQLiteConnectionString(getApplicationContext(), "supervisor");
//            DbHandler dbHandler = new DbHandler(this, connectionString);
//            VaranegarApplication.Init(dbHandler, VaranegarApplication.AppId.Supervisor, VaranegarApplication.GRSAppId.Supervisor);
//        } catch (Exception e) {
//            Timber.e(e);
//            throw new RuntimeException(e);
//        }
//    }
}
