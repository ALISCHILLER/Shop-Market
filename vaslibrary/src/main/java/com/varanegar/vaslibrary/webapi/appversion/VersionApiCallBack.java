package com.varanegar.vaslibrary.webapi.appversion;

import com.varanegar.framework.base.AppVersionInfo;

/**
 * Created by A.Torabi on 2/10/2018.
 */

public interface VersionApiCallBack {
    void onSuccess(AppVersionInfo versionInfo);
    void onFailure(String error);
    void onCancel();
}
