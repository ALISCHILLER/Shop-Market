package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCount;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModelRepository;
import com.varanegar.vaslibrary.model.msl.MslProductPattern;
import com.varanegar.vaslibrary.model.msl.MslProductPatternModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/16/2017.
 */

public class EmphaticProductCountManager extends BaseManager<EmphaticProductCountModel> {
    public EmphaticProductCountManager(@NonNull Context context) {
        super(context, new EmphaticProductCountModelRepository());
    }

    public List<EmphaticProductCountModel> getAll() {
        return getItems(new Query().from(EmphaticProductCount.EmphaticProductCountTbl));
    }
    public EmphaticProductCountModel getItem(UUID productId) {
        return getItem(new Query().from(EmphaticProductCount.EmphaticProductCountTbl)
                .whereAnd(Criteria.equals(EmphaticProductCount.ProductId,productId)));
    }

}
