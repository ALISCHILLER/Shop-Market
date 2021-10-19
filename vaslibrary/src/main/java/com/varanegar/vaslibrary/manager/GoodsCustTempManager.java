package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.goodscusttemp.GoodsCustTempModel;
import com.varanegar.vaslibrary.model.goodscusttemp.GoodsCustTempModelRepository;

/**
 * Created by A.Jafarzadeh on 11/18/2018.
 */

public class GoodsCustTempManager extends BaseManager<GoodsCustTempModel> {
    public GoodsCustTempManager(@NonNull Context context) {
        super(context, new GoodsCustTempModelRepository());
    }
}
