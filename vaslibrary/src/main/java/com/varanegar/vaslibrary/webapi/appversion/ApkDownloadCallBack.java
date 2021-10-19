package com.varanegar.vaslibrary.webapi.appversion;

/**
 * Created by A.Torabi on 2/10/2018.
 */

public interface ApkDownloadCallBack {
     void onSuccess(String apkPath);

    void onFailure(String err);
}
