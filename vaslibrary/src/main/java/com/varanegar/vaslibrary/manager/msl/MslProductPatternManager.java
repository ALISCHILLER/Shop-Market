package com.varanegar.vaslibrary.manager.msl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.msl.Msl;
import com.varanegar.vaslibrary.model.msl.MslModel;
import com.varanegar.vaslibrary.model.msl.MslModelRepository;
import com.varanegar.vaslibrary.model.msl.MslProductPattern;
import com.varanegar.vaslibrary.model.msl.MslProductPatternModel;
import com.varanegar.vaslibrary.model.msl.MslProductPatternModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/28/2018.
 */

public class MslProductPatternManager extends BaseManager<MslProductPatternModel> {
    public MslProductPatternManager(@NonNull Context context) {
        super(context, new MslProductPatternModelRepository());
    }


    public List<MslProductPatternModel> getAll() {
        return getItems(new Query().from(MslProductPattern.MslProductPatternTbl));
    }
}
