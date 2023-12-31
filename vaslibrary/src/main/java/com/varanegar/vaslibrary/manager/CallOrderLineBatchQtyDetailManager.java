package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModelRepository;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/21/2018.
 */

public class CallOrderLineBatchQtyDetailManager extends BaseManager<CallOrderLineBatchQtyDetailModel> {
    public CallOrderLineBatchQtyDetailManager(@NonNull Context context) {
        super(context, new CallOrderLineBatchQtyDetailModelRepository());
    }


    public List<CallOrderLineBatchQtyDetailModel> getAll() {
        Query query = new Query();
        query.from(From.table(CallOrderLineBatchQtyDetail.CallOrderLineBatchQtyDetailTbl));
        return getItems(query);
    }
    public List<CallOrderLineBatchQtyDetailModel> getLineBatchQtyDetails(UUID CustomerCallOrderLineUniqueId) {
        Query query = new Query();
        query.from(From.table(CallOrderLineBatchQtyDetail.CallOrderLineBatchQtyDetailTbl)).whereAnd(Criteria.equals(CallOrderLineBatchQtyDetail.CustomerCallOrderLineUniqueId, CustomerCallOrderLineUniqueId));
        return getItems(query);
    }

    public List<CallOrderLineBatchQtyDetailModel> getBatchLines(@NonNull UUID orderLineId) {
        return getItems(new Query().from(CallOrderLineBatchQtyDetail.CallOrderLineBatchQtyDetailTbl).whereAnd(
                Criteria.equals(CallOrderLineBatchQtyDetail.CustomerCallOrderLineUniqueId, orderLineId.toString())
        ));
    }

    public void addOrUpdateQty(@NonNull UUID callOrderLineId, List<BatchQty> batchQtyList) throws ValidationException, DbException {
        delete(Criteria.equals(CallOrderLineBatchQtyDetail.CustomerCallOrderLineUniqueId, callOrderLineId.toString()));
        List<CallOrderLineBatchQtyDetailModel> callOrderLineBatchQtyDetailModels = new ArrayList<>();
        for (BatchQty batchQty : batchQtyList) {
            if (batchQty.Qty.compareTo(BigDecimal.ZERO) == 1) {
                CallOrderLineBatchQtyDetailModel callOrderLineBatchQtyDetailModel = new CallOrderLineBatchQtyDetailModel();
                callOrderLineBatchQtyDetailModel.UniqueId = UUID.randomUUID();
                callOrderLineBatchQtyDetailModel.BatchRef = batchQty.BatchRef;
                callOrderLineBatchQtyDetailModel.BatchNo = batchQty.BatchNo;
                callOrderLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId = callOrderLineId;
                callOrderLineBatchQtyDetailModel.Qty = batchQty.Qty;
                callOrderLineBatchQtyDetailModel.ItemRef = batchQty.ItemRef;
                callOrderLineBatchQtyDetailModels.add(callOrderLineBatchQtyDetailModel);
            }
        }
        if (callOrderLineBatchQtyDetailModels.size() > 0)
            insert(callOrderLineBatchQtyDetailModels);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void updatePromoLineBatchQty(@NonNull UUID callOrderLineId, BigDecimal newQty) throws ValidationException, DbException {
        CallInvoiceLineBatchQtyDetailManager callInvoiceLineBatchQtyDetailManager = new CallInvoiceLineBatchQtyDetailManager(getContext());
        List<CallInvoiceLineBatchQtyDetailModel> callInvoiceLineBatchQtyDetailModels =
                callInvoiceLineBatchQtyDetailManager
                        .getItems(new Query().from(CallInvoiceLineBatchQtyDetail.CallInvoiceLineBatchQtyDetailTbl)
                                .whereAnd(Criteria.equals(CallInvoiceLineBatchQtyDetail.CustomerCallOrderLineUniqueId, callOrderLineId.toString())));
        if (callInvoiceLineBatchQtyDetailModels.size() > 0) {
            delete(Criteria.equals(CallOrderLineBatchQtyDetail.CustomerCallOrderLineUniqueId, callOrderLineId.toString()));
            BigDecimal oldQty = BigDecimal.ZERO;
            for (CallInvoiceLineBatchQtyDetailModel item :
                    callInvoiceLineBatchQtyDetailModels) {
                oldQty = oldQty.add(item.Qty);
            }
            BigDecimal subValue = oldQty.subtract(newQty);
            if (callInvoiceLineBatchQtyDetailModels.size() == 1 || oldQty == newQty ||
                    newQty.compareTo(BigDecimal.ZERO) <= 0) {
                for (CallInvoiceLineBatchQtyDetailModel item :
                        callInvoiceLineBatchQtyDetailModels) {
                    if (subValue.compareTo(BigDecimal.ZERO) <= 0)
                        break;
                    if (item.Qty.compareTo(subValue) >= 0) {
                        item.Qty = item.Qty.subtract(subValue);
                        subValue = BigDecimal.ZERO;
                    } else {
                        item.Qty = BigDecimal.ZERO;
                        subValue = subValue.subtract(item.Qty);
                    }
                }
            } else {
               Collections.sort(callInvoiceLineBatchQtyDetailModels, (o1, o2) -> {
                   return o2.Qty.compareTo(o1.Qty);
               });


                int postion = 0;
                BigDecimal simolate = newQty;
                for (int i = 0; i < callInvoiceLineBatchQtyDetailModels.size(); i++) {
                    BigDecimal qty = callInvoiceLineBatchQtyDetailModels.get(i).Qty;
                    if (qty.compareTo(simolate) >= 0) {
                        postion = i;
                        callInvoiceLineBatchQtyDetailModels.get(i).Qty = simolate;
                        break;
                    } else {
                        simolate = simolate.subtract(qty);
                    }
                }

                if (postion + 1 < callInvoiceLineBatchQtyDetailModels.size())
                    for (int i = postion + 1; i < callInvoiceLineBatchQtyDetailModels.size(); i++)
                        callInvoiceLineBatchQtyDetailModels.get(i).Qty = BigDecimal.ZERO;
            }
            List<CallOrderLineBatchQtyDetailModel> callOrderLineBatchQtyDetailModels = new ArrayList<>();
            for (CallInvoiceLineBatchQtyDetailModel item :
                    callInvoiceLineBatchQtyDetailModels) {
                if (item.Qty.compareTo(BigDecimal.ZERO) > 0) {
                    CallOrderLineBatchQtyDetailModel callOrderLineBatchQtyDetailModel = new CallOrderLineBatchQtyDetailModel();
                    callOrderLineBatchQtyDetailModel.UniqueId = UUID.randomUUID();
                    callOrderLineBatchQtyDetailModel.BatchRef = item.BatchRef;
                    callOrderLineBatchQtyDetailModel.BatchNo = item.BatchNo;
                    callOrderLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId = callOrderLineId;
                    callOrderLineBatchQtyDetailModel.Qty = item.Qty;
                    callOrderLineBatchQtyDetailModel.ItemRef = item.ItemRef;
                    callOrderLineBatchQtyDetailModels.add(callOrderLineBatchQtyDetailModel);
                }
            }
            insert(callOrderLineBatchQtyDetailModels);
        }
    }
}
