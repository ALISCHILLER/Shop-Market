package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.evcHeaderSDS.EVCHeaderSDSModel;
import com.varanegar.vaslibrary.model.evcHeaderSDS.EVCHeaderSDSModelRepository;

/**
 * Created by A.Jafarzadeh on 6/15/2017.
 */

public class EVCHeaderSDSManager extends BaseManager<EVCHeaderSDSModel>
{
    public EVCHeaderSDSManager(@NonNull Context context)
    {
        super(context, new EVCHeaderSDSModelRepository());
    }



}
