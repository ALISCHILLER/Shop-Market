package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.target.TargetProductModel;
import com.varanegar.vaslibrary.model.target.TargetProductModelRepository;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetProductManager extends BaseManager<TargetProductModel> {
    public TargetProductManager(@NonNull Context context) {
        super(context, new TargetProductModelRepository());
    }
}
