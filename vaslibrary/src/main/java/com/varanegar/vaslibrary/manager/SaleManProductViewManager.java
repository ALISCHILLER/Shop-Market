package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.target.SaleManProductViewModel;
import com.varanegar.vaslibrary.model.target.SaleManProductViewModelRepository;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

public class SaleManProductViewManager extends BaseManager<SaleManProductViewModel> {
    public SaleManProductViewManager(@NonNull Context context) {
        super(context, new SaleManProductViewModelRepository());
    }

//    private static Query baseQuery(UUID targetMasterUniqueId) {
//
//    }
}
