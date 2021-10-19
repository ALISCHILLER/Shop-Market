package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013View;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013ViewModel;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013ViewModelRepository;

/**
 * Created by s.foroughi on 17/01/2017.
 */

public class Rep3013ViewManager extends BaseManager<Rep3013ViewModel> {
    public Rep3013ViewManager(Context context) {
        super(context, new Rep3013ViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(Rep3013View.Rep3013ViewTbl);
        return query;
    }

    public static Query getAll(String key) {
        Query query = new Query();
        query.from(Rep3013View.Rep3013ViewTbl).whereAnd(Criteria.contains(Rep3013View.CustomerCode, key));
        return query;
    }


}

