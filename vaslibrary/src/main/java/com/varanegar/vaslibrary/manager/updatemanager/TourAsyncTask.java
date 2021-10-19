package com.varanegar.vaslibrary.manager.updatemanager;

import androidx.annotation.StringRes;

/**
 * Created by A.Torabi on 1/24/2018.
 */
public interface TourAsyncTask {

    void run(UpdateCall call);

    String name();

    @StringRes
    int group();

    int queueId();

    void cancel();
}
