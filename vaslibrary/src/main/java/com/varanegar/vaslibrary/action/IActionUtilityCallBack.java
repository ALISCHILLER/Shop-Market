package com.varanegar.vaslibrary.action;

import android.view.View;

public interface IActionUtilityCallBack {

    void onDone();

    void onFailed(String error);

    void onCancel();
}
