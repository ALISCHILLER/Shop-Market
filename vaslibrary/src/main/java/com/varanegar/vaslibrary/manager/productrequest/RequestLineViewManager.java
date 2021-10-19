package com.varanegar.vaslibrary.manager.productrequest;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineView;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModelRepository;
import com.varanegar.vaslibrary.webapi.tour.SyncGetRequestLineModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetRequestLineQtyModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestLineViewManager extends BaseManager<RequestLineViewModel> {
    public RequestLineViewManager(@NonNull Context context) {
        super(context, new RequestLineViewModelRepository());
    }

    public static Query getLines(@Nullable BigDecimal totalQty) {
        if (totalQty == null)
            return new Query().from(RequestLineView.RequestLineViewTbl);
        else
            return new Query().from(RequestLineView.RequestLineViewTbl).whereAnd(Criteria.greaterThan(RequestLineView.TotalQty, totalQty.doubleValue()));
    }

    public static Query searchQuery(@Nullable String key, @Nullable UUID[] groupIds) {
        Query query = null;
        if (key == null || key.isEmpty())
            query = new Query().from(RequestLineView.RequestLineViewTbl);
        else {
            key = HelperMethods.persian2Arabic(key);
            query = new Query().from(RequestLineView.RequestLineViewTbl).whereAnd(Criteria.contains(RequestLineView.ProductCode, key)
                    .or(Criteria.contains(RequestLineView.ProductName, key)));
        }

        if (groupIds != null){
            String[] ids = new String[groupIds.length];
            for (int i = 0; i < groupIds.length; i++) {
                ids[i] = groupIds[i].toString();
            }
            query.whereAnd(Criteria.in(RequestLineView.ProductGroupId, ids));
        }

        return query;


    }

    public List<SyncGetRequestLineModel> getSyncRequestLine() {
        List<RequestLineViewModel> requestLineModels = getItems(getLines(BigDecimal.ZERO));
        final List<SyncGetRequestLineModel> syncGetRequestLineModels = new ArrayList<>();
        if (requestLineModels != null && requestLineModels.size() > 0) {
            Linq.forEach(requestLineModels, new Linq.Consumer<RequestLineViewModel>() {
                @Override
                public void run(RequestLineViewModel item) {
                    final SyncGetRequestLineModel syncGetRequestLineModel = new SyncGetRequestLineModel();
                    syncGetRequestLineModel.UniqueId = item.RequestLineUniqueId;
                    syncGetRequestLineModel.ProductId = item.UniqueId;
                    syncGetRequestLineModel.UnitPrice = HelperMethods.currencyToDouble(item.UnitPrice);
                    syncGetRequestLineModel.RowIndex = item.RowIndex;
                    syncGetRequestLineModel.BulkQty = item.BulkQty;
                    syncGetRequestLineModel.BulkQtyUnitUniqueId = item.BulkQtyUnitUniqueId;

                    RequestLineQtyManager requestLineQtyManager = new RequestLineQtyManager(getContext());
                    List<RequestLineQtyModel> requestLineQtyModels = requestLineQtyManager.getQtyLines(item.RequestLineUniqueId);
                    final List<SyncGetRequestLineQtyModel> syncGetRequestLineQtyModels = new ArrayList<>();
                    if (requestLineQtyModels.size() > 0) {
                        Linq.forEach(requestLineQtyModels, new Linq.Consumer<RequestLineQtyModel>() {
                            @Override
                            public void run(RequestLineQtyModel item) {
                                if (item.Qty.compareTo(BigDecimal.ZERO) == 1) {
                                    SyncGetRequestLineQtyModel syncGetRequestLineQtyModel = new SyncGetRequestLineQtyModel();
                                    syncGetRequestLineQtyModel.UniqueId = item.UniqueId;
                                    syncGetRequestLineQtyModel.Qty = item.Qty;
                                    syncGetRequestLineQtyModel.ProductUnitId = item.ProductUnitId;
                                    syncGetRequestLineQtyModels.add(syncGetRequestLineQtyModel);
                                }
                            }
                        });
                    }

                    syncGetRequestLineModel.RequestItemLineQtyDetails = syncGetRequestLineQtyModels;
                    syncGetRequestLineModels.add(syncGetRequestLineModel);
                }
            });
            return syncGetRequestLineModels;
        }
        return null;
    }
}
