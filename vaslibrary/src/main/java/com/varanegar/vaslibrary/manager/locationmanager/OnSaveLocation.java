package com.varanegar.vaslibrary.manager.locationmanager;

import com.varanegar.vaslibrary.model.location.LocationModel;

/**
 * Created by A.Torabi on 8/9/2017.
 */
public interface OnSaveLocation {
    void onSaved(LocationModel location);

    void onFailed(String error);
}
