package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.target.TargetProductGroupModel;
import com.varanegar.vaslibrary.model.target.TargetProductGroupModelRepository;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetProductGroupManager extends BaseManager<TargetProductGroupModel> {
    public TargetProductGroupManager(@NonNull Context context) {
        super(context, new TargetProductGroupModelRepository());
    }
}
