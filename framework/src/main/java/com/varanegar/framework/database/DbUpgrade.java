package com.varanegar.framework.database;

import android.database.sqlite.SQLiteDatabase;

import com.varanegar.framework.base.AppVersionInfo;

/**
 * Created by atp on 5/21/2017.
 */

public interface DbUpgrade {
    int getVersion();
    void onUpgrade(SQLiteDatabase db);
}
