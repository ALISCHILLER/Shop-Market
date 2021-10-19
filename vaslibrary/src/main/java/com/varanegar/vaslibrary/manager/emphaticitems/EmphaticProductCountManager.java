package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModelRepository;

/**
 * Created by A.Torabi on 7/16/2017.
 */

public class EmphaticProductCountManager extends BaseManager<EmphaticProductCountModel> {
    public EmphaticProductCountManager(@NonNull Context context) {
        super(context, new EmphaticProductCountModelRepository());
    }
}
