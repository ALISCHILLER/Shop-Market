package com.varanegar.vaslibrary.manager.cataloguelog;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.model.CatalogueLog;
import com.varanegar.vaslibrary.model.CatalogueLogModel;
import com.varanegar.vaslibrary.model.CatalogueLogModelRepository;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallCatalogViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CatalogueLogManager extends BaseManager<CatalogueLogModel> {
    public CatalogueLogManager(@NonNull Context context) {
        super(context, new CatalogueLogModelRepository());
    }

    synchronized public void catalogueLogStart(@NonNull UUID catalogueType, @NonNull UUID entityId, @NonNull UUID customerId) {
        CatalogueLogModelRepository repository = new CatalogueLogModelRepository();
        Query query = new Query().from(CatalogueLog.CatalogueLogTbl)
                .whereAnd(Criteria.equals(CatalogueLog.EntityId, entityId)
                        .and(Criteria.equals(CatalogueLog.CustomerId, customerId)));
        CatalogueLogModel log = repository.getItem(query);
        if (log == null || log.EndTime != null) {
            log = new CatalogueLogModel();
            log.UniqueId = UUID.randomUUID();
            log.CustomerId = customerId;
            log.CatalogTypeUniqueId = catalogueType;
            log.EntityId = entityId;
            log.StartTime = new Date();
            repository.insert(log);
        }
    }

    synchronized public void catalogueLogEnd(UUID entityId, UUID customerId) {
        CatalogueLogModelRepository repository = new CatalogueLogModelRepository();
        Query query = new Query().from(CatalogueLog.CatalogueLogTbl)
                .whereAnd(Criteria.equals(CatalogueLog.EntityId, entityId)
                        .and(Criteria.equals(CatalogueLog.CustomerId, customerId))
                        .and(Criteria.isNull(CatalogueLog.EndTime)));
        CatalogueLogModel log = repository.getItem(query);
        if (log != null) {
            log.EndTime = new Date();
            repository.update(log);
        }
    }

    public List<SyncGetCustomerCallCatalogViewModel> getLogs(UUID customerId) {
        Query query = new Query().from(CatalogueLog.CatalogueLogTbl)
                .whereAnd(Criteria.notIsNull(CatalogueLog.EndTime)
                        .and(Criteria.notIsNull(CatalogueLog.StartTime))
                        .and(Criteria.equals(CatalogueLog.CustomerId, customerId)));
        List<CatalogueLogModel> logs = getItems(query);
        HashMap<UUID, SyncGetCustomerCallCatalogViewModel> logMap = new HashMap<>();
        for (CatalogueLogModel log :
                logs) {
            if (logMap.containsKey(log.EntityId)) {
                SyncGetCustomerCallCatalogViewModel vm = logMap.get(log.EntityId);
                vm.ViewDuration += (log.EndTime.getTime() - log.StartTime.getTime()) / 1000;
                vm.ViewCount++;
            } else {
                SyncGetCustomerCallCatalogViewModel vm = new SyncGetCustomerCallCatalogViewModel();
                vm.CatalogTypeUniqueId = log.CatalogTypeUniqueId;
                if (log.CatalogTypeUniqueId.equals(CatalogManager.BASED_ON_PRODUCT_GROUP))
                    vm.CatalogUniqueId = log.EntityId;
                else
                    vm.ProductUniqueId = log.EntityId;
                vm.UniqueId = log.UniqueId;
                vm.ViewCount = 1;
                vm.ViewDuration = (log.EndTime.getTime() - log.StartTime.getTime()) / 1000;
                logMap.put(log.EntityId, vm);
            }
        }
        Iterator<SyncGetCustomerCallCatalogViewModel> iterator = logMap.values().iterator();
        List<SyncGetCustomerCallCatalogViewModel> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

}
