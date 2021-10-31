package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.filter.Filter;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customercallview.CustomerCallView;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathView;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModelRepository;
import com.varanegar.vaslibrary.model.customerpathview.UnConfirmedCustomerPathView;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

public class CustomerPathViewManager extends BaseManager<CustomerPathViewModel> {
    public static String All_PATHS = "95d1c1ae-3e33-4309-be04-3c377d55b798";
    private static Context context;

    public CustomerPathViewManager(@NonNull Context context) {
        super(context, new CustomerPathViewModelRepository());
        this.context = context;
    }

    private static Query baseQuery(@Nullable Boolean checkConfirmStatus) {
        if (checkConfirmStatus == null) {
            Query subQuery = new Query();
            subQuery.from(UnConfirmedCustomerPathView.UnConfirmedCustomerPathViewTbl);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel inactiveCustomers = sysConfigManager.read(ConfigKey.SendInactiveCustomers, SysConfigManager.cloud);
            if (SysConfigManager.compare(inactiveCustomers, false))
                subQuery.whereAnd(Criteria.equals(UnConfirmedCustomerPathView.IsActive, true).or(Criteria.equals(UnConfirmedCustomerPathView.IsNewCustomer, 1)));
            return subQuery;
        } else if (checkConfirmStatus) {
            Query subQuery = new Query();
            subQuery.from(CustomerPathView.CustomerPathViewTbl);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel inactiveCustomers = sysConfigManager.read(ConfigKey.SendInactiveCustomers, SysConfigManager.cloud);
            if (SysConfigManager.compare(inactiveCustomers, false))
                subQuery.whereAnd(Criteria.equals(CustomerPathView.IsActive, true).or(Criteria.equals(CustomerPathView.IsNewCustomer, 1)));
            return subQuery;
        } else {
            Query subQuery = new Query();
            subQuery.from(UnConfirmedCustomerPathView.UnConfirmedCustomerPathViewTbl);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel inactiveCustomers = sysConfigManager.read(ConfigKey.SendInactiveCustomers, SysConfigManager.cloud);
            if (SysConfigManager.compare(inactiveCustomers, false))
                subQuery.whereAnd((Criteria.equals(UnConfirmedCustomerPathView.IsActive, true)
                        .or(Criteria.equals(UnConfirmedCustomerPathView.IsNewCustomer, 1))));
            subQuery.whereAnd(Criteria.equals(UnConfirmedCustomerPathView.ConfirmStatus, false));
            return subQuery;
        }
    }

    public static Query checkIsInDayVisitPath(CustomerModel customer, UUID TourVisitPathId) {
        Query query = new Query();
        query.from(CustomerPathView.CustomerPathViewTbl).whereAnd(Criteria.equals(CustomerPathView.UniqueId, customer.UniqueId).and(Criteria.equals(CustomerPathView.VisitTemplatePathId, TourVisitPathId)));
        return query;
    }


//    public int getVisitDayCustomerCount(UUID dayPathId) {
//        Query query = new Query();
//        query.from(CustomerPathView.CustomerPathViewTbl);
//        query.whereAnd(Criteria.equals(CustomerPathView.VisitTemplatePathId, dayPathId.toString()));
//        query.select(Projection.countRows());
//        Integer count = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
//        if (count == null)
//            return 0;
//        else return count;
//    }

    public static Query getDay(UUID dayPathId, boolean isActive) {
        Query query = new Query();
        query.from(CustomerPathView.CustomerPathViewTbl);
        query.whereAnd(Criteria.equals(CustomerPathView.VisitTemplatePathId, dayPathId.toString()));
        if (isActive)
            query.whereAnd(Criteria.equals(CustomerPathView.IsActive, true));
        return query;
    }

    @Nullable
    private static Criteria createCallTypeCriteria(ArrayList<CustomerCallType> customerCallType, @Nullable Boolean checkConfirmStatus) {
        Criteria cc = null;
        if (customerCallType != null && customerCallType.size() > 0) {
            if (checkConfirmStatus == null) {
                cc = Criteria.equals(UnConfirmedCustomerPathView.CallType, String.valueOf(customerCallType.get(0).ordinal()))
                        .or(Criteria.startsWith(UnConfirmedCustomerPathView.CallType, customerCallType.get(0).ordinal() + ","))
                        .or(Criteria.endsWith(UnConfirmedCustomerPathView.CallType, "," + customerCallType.get(0).ordinal()))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.CallType, "," + customerCallType.get(0).ordinal() + ","));
                if (customerCallType.size() > 1) {
                    for (int i = 1; i < customerCallType.size(); i++) {
                        cc = cc.or(Criteria.equals(UnConfirmedCustomerPathView.CallType, String.valueOf(customerCallType.get(i).ordinal())))
                                .or(Criteria.startsWith(UnConfirmedCustomerPathView.CallType, customerCallType.get(i).ordinal() + ","))
                                .or(Criteria.endsWith(UnConfirmedCustomerPathView.CallType, "," + customerCallType.get(i).ordinal()))
                                .or(Criteria.contains(UnConfirmedCustomerPathView.CallType, "," + customerCallType.get(i).ordinal() + ","));
                    }
                }
            } else if (checkConfirmStatus) {
                cc = Criteria.equals(CustomerPathView.CallType, String.valueOf(customerCallType.get(0).ordinal()))
                        .or(Criteria.startsWith(CustomerPathView.CallType, customerCallType.get(0).ordinal() + ","))
                        .or(Criteria.endsWith(CustomerPathView.CallType, "," + customerCallType.get(0).ordinal()))
                        .or(Criteria.contains(CustomerPathView.CallType, "," + customerCallType.get(0).ordinal() + ","));
                if (customerCallType.size() > 1) {
                    for (int i = 1; i < customerCallType.size(); i++) {
                        cc = cc.or(Criteria.equals(CustomerPathView.CallType, String.valueOf(customerCallType.get(i).ordinal())))
                                .or(Criteria.startsWith(CustomerPathView.CallType, customerCallType.get(i).ordinal() + ","))
                                .or(Criteria.endsWith(CustomerPathView.CallType, "," + customerCallType.get(i).ordinal()))
                                .or(Criteria.contains(CustomerPathView.CallType, "," + customerCallType.get(i).ordinal() + ","));
                    }
                }
            }
        }
        return cc;
    }

    public static Query getAll(List<Filter> paths, String key, @Nullable ArrayList<CustomerCallType> customerCallType, @Nullable Boolean checkConfirmStatus) {
        List<Object> pathsString = Linq.map(paths, new Linq.Map<Filter, Object>() {
            @Override
            public String run(Filter item) {
                return item.value;
            }
        });
        Query query = baseQuery(checkConfirmStatus);
        Criteria criteria = createCallTypeCriteria(customerCallType, checkConfirmStatus);
        if (criteria != null)
            query.whereAnd(criteria);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.whereAnd(Criteria.in(CustomerPathView.VisitTemplatePathId, pathsString)).whereAnd(Criteria.contains(CustomerPathView.CustomerName, key)
                    .or(Criteria.contains(CustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(CustomerPathView.Address, key))
                    .or(Criteria.contains(CustomerPathView.Phone, key))
                    .or(Criteria.contains(CustomerPathView.Mobile, key))
                    .or(Criteria.contains(CustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)));
        } else {
            query.whereAnd(Criteria.in(UnConfirmedCustomerPathView.VisitTemplatePathId, pathsString)).whereAnd(Criteria.contains(UnConfirmedCustomerPathView.CustomerName, key)
                    .or(Criteria.contains(UnConfirmedCustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Address, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Phone, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Mobile, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)));
        }
        return query;
    }

    public static Query getAllWithUnknownVisitStatus(List<Filter> paths, String key, @Nullable Boolean checkConfirmStatus) {
        List<Object> pathsString = Linq.map(paths, new Linq.Map<Filter, Object>() {
            @Override
            public String run(Filter item) {
                return item.value;
            }
        });
        Query query = baseQuery(checkConfirmStatus);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.whereAnd(Criteria.isNull(CustomerPathView.CallType));
            query.whereAnd(Criteria.in(CustomerPathView.VisitTemplatePathId, pathsString)).whereAnd(Criteria.contains(CustomerPathView.CustomerName, key)
                    .or(Criteria.contains(CustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(CustomerPathView.Address, key))
                    .or(Criteria.contains(CustomerPathView.Phone, key))
                    .or(Criteria.contains(CustomerPathView.Mobile, key))
                    .or(Criteria.contains(CustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)));
            CustomerCallManager customerCallManager = new CustomerCallManager(context);
            List<CustomerCallModel> unConfirmedCalls = customerCallManager.getUnconfirmedCalls();
            String[] unConfirmedCallsCustomerId = new String[unConfirmedCalls.size()];
            for (int i = 0; i < unConfirmedCalls.size(); i++) {
                unConfirmedCallsCustomerId[i] = unConfirmedCalls.get(i).CustomerId.toString();
            }
            query.whereAnd(Criteria.notIn(CustomerPathView.UniqueId, unConfirmedCallsCustomerId));
        } else {
            query.whereAnd(Criteria.isNull(UnConfirmedCustomerPathView.CallType));
            query.whereAnd(Criteria.in(UnConfirmedCustomerPathView.VisitTemplatePathId, pathsString)).whereAnd(Criteria.contains(UnConfirmedCustomerPathView.CustomerName, key)
                    .or(Criteria.contains(UnConfirmedCustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Address, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Phone, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Mobile, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)));
        }
        return query;
    }

    public static Query getAll(List<Filter> paths, @Nullable ArrayList<CustomerCallType> callType, @Nullable Boolean checkConfirmStatus) {
        Criteria criteria = createCallTypeCriteria(callType, checkConfirmStatus);
        List<Object> pathsString = Linq.map(paths, new Linq.Map<Filter, Object>() {
            @Override
            public String run(Filter item) {
                return item.value;
            }
        });
        Query query = baseQuery(checkConfirmStatus);
        if (criteria != null)
            query.whereAnd(criteria);
        if (checkConfirmStatus != null && checkConfirmStatus)
            query.whereAnd(Criteria.in(CustomerPathView.VisitTemplatePathId, pathsString))
                    .orderByDescending(CustomerPathView.OPathId)
                    .orderByAscending(CustomerPathView.PathRowId);
        else
            query.whereAnd(Criteria.in(UnConfirmedCustomerPathView.VisitTemplatePathId, pathsString))
                    .orderByDescending(UnConfirmedCustomerPathView.OPathId)
                    .orderByAscending(UnConfirmedCustomerPathView.PathRowId);
        return query;
    }

    public static Query getAllWithUnknownVisitStatus(List<Filter> paths, @Nullable Boolean checkConfirmStatus) {
        List<Object> pathsString = Linq.map(paths, new Linq.Map<Filter, Object>() {
            @Override
            public String run(Filter item) {
                return item.value;
            }
        });
        Query query = baseQuery(checkConfirmStatus);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.whereAnd(Criteria.isNull(CustomerPathView.CallType));
            query.whereAnd(Criteria.in(CustomerPathView.VisitTemplatePathId, pathsString));
            CustomerCallManager customerCallManager = new CustomerCallManager(context);
            List<CustomerCallModel> unConfirmedCalls = customerCallManager.getUnconfirmedCalls();
            String[] unConfirmedCallsCustomerId = new String[unConfirmedCalls.size()];
            for (int i = 0; i < unConfirmedCalls.size(); i++) {
                unConfirmedCallsCustomerId[i] = unConfirmedCalls.get(i).CustomerId.toString();
            }
            query.whereAnd(Criteria.notIn(CustomerPathView.UniqueId, unConfirmedCallsCustomerId));
            query.orderByDescending(CustomerPathView.OPathId);
            query.orderByAscending(CustomerPathView.PathRowId);
        } else {
            query.whereAnd(Criteria.isNull(UnConfirmedCustomerPathView.CallType));
            query.whereAnd(Criteria.in(UnConfirmedCustomerPathView.VisitTemplatePathId, pathsString))
                    .orderByDescending(UnConfirmedCustomerPathView.OPathId)
                    .orderByAscending(UnConfirmedCustomerPathView.PathRowId);
        }
        return query;
    }

    public static Query getAll(@Nullable ArrayList<CustomerCallType> callType, @Nullable Boolean checkConfirmStatus) {
        Criteria criteria = createCallTypeCriteria(callType, checkConfirmStatus);
        Query query = baseQuery(checkConfirmStatus);
        if (criteria != null)
            query.whereAnd(criteria);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.orderByDescending(CustomerPathView.OPathId);
            query.orderByAscending(CustomerPathView.PathRowId);
        } else {
            query.orderByDescending(UnConfirmedCustomerPathView.OPathId);
            query.orderByAscending(UnConfirmedCustomerPathView.PathRowId);
        }
        return query;
    }

    public static Query getAllWithUnknownStatus(@Nullable Boolean checkConfirmStatus) {
        Query query = baseQuery(checkConfirmStatus);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.whereAnd(Criteria.isNull(CustomerPathView.CallType));
            query.orderByDescending(CustomerPathView.OPathId);
            query.orderByAscending(CustomerPathView.PathRowId);
            CustomerCallManager customerCallManager = new CustomerCallManager(context);
            List<CustomerCallModel> unConfirmedCalls = customerCallManager.getUnconfirmedCalls();
            String[] unConfirmedCallsCustomerId = new String[unConfirmedCalls.size()];
            for (int i = 0; i < unConfirmedCalls.size(); i++) {
                unConfirmedCallsCustomerId[i] = unConfirmedCalls.get(i).CustomerId.toString();
            }
            query.whereAnd(Criteria.notIn(CustomerPathView.UniqueId, unConfirmedCallsCustomerId));
        } else {
            query.whereAnd(Criteria.isNull(UnConfirmedCustomerPathView.CallType));
            query.orderByDescending(UnConfirmedCustomerPathView.OPathId);
            query.orderByAscending(UnConfirmedCustomerPathView.PathRowId);
        }
        return query;
    }

    public static Query getAll(String key, @Nullable ArrayList<CustomerCallType> customerCallType, @Nullable Boolean checkConfirmStatus) {
        Query query = baseQuery(checkConfirmStatus);
        Criteria criteria = createCallTypeCriteria(customerCallType, checkConfirmStatus);
        if (criteria != null) {
            if (checkConfirmStatus != null && checkConfirmStatus) {
                query.whereAnd(Criteria.contains(CustomerPathView.CustomerName, key)
                        .or(Criteria.contains(CustomerPathView.CustomerCode, key))
                        .or(Criteria.contains(CustomerPathView.Address, key))
                        .or(Criteria.contains(CustomerPathView.Phone, key))
                        .or(Criteria.contains(CustomerPathView.Mobile, key))
                        .or(Criteria.contains(CustomerPathView.StoreName, key))
                        .or(Criteria.contains(CustomerPathView.NationalCode, key))
                        .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                        .whereAnd(criteria)
                        .orderByDescending(CustomerPathView.OPathId)
                        .orderByAscending(CustomerPathView.PathRowId);
            } else {
                query.whereAnd(Criteria.contains(UnConfirmedCustomerPathView.CustomerName, key)
                        .or(Criteria.contains(UnConfirmedCustomerPathView.CustomerCode, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Address, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Phone, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Mobile, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.StoreName, key))
                        .or(Criteria.contains(CustomerPathView.NationalCode, key))
                        .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                        .whereAnd(criteria)
                        .orderByDescending(UnConfirmedCustomerPathView.OPathId)
                        .orderByAscending(UnConfirmedCustomerPathView.PathRowId);
            }
        } else {
            if (checkConfirmStatus != null && checkConfirmStatus) {
                query.whereAnd(Criteria.contains(CustomerPathView.CustomerName, key)
                        .or(Criteria.contains(CustomerPathView.CustomerCode, key))
                        .or(Criteria.contains(CustomerPathView.Address, key))
                        .or(Criteria.contains(CustomerPathView.Phone, key))
                        .or(Criteria.contains(CustomerPathView.Mobile, key))
                        .or(Criteria.contains(CustomerPathView.StoreName, key))
                        .or(Criteria.contains(CustomerPathView.NationalCode, key))
                        .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                        .orderByDescending(CustomerPathView.OPathId)
                        .orderByAscending(CustomerPathView.PathRowId);
            } else {
                query.whereAnd(Criteria.contains(UnConfirmedCustomerPathView.CustomerName, key)
                        .or(Criteria.contains(UnConfirmedCustomerPathView.CustomerCode, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Address, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Phone, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.Mobile, key))
                        .or(Criteria.contains(UnConfirmedCustomerPathView.StoreName, key))
                        .or(Criteria.contains(CustomerPathView.NationalCode, key))
                        .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                        .orderByDescending(UnConfirmedCustomerPathView.OPathId)
                        .orderByAscending(UnConfirmedCustomerPathView.PathRowId);
            }
        }

        return query;
    }

    public static Query getAllWithUnknownStatus(String key, @Nullable Boolean checkConfirmStatus) {
        Query query = baseQuery(checkConfirmStatus);
        if (checkConfirmStatus != null && checkConfirmStatus) {
            query.whereAnd(Criteria.contains(CustomerPathView.CustomerName, key)
                    .or(Criteria.contains(CustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(CustomerPathView.Address, key))
                    .or(Criteria.contains(CustomerPathView.Phone, key))
                    .or(Criteria.contains(CustomerPathView.Mobile, key))
                    .or(Criteria.contains(CustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                    .whereAnd(Criteria.isNull(CustomerPathView.CallType))
                    .orderByDescending(CustomerPathView.OPathId)
                    .orderByAscending(CustomerPathView.PathRowId);
            CustomerCallManager customerCallManager = new CustomerCallManager(context);
            List<CustomerCallModel> unConfirmedCalls = customerCallManager.getUnconfirmedCalls();
            String[] unConfirmedCallsCustomerId = new String[unConfirmedCalls.size()];
            for (int i = 0; i < unConfirmedCalls.size(); i++) {
                unConfirmedCallsCustomerId[i] = unConfirmedCalls.get(i).CustomerId.toString();

            }
            query.whereAnd(Criteria.notIn(CustomerPathView.UniqueId, unConfirmedCallsCustomerId));
        } else
            query.whereAnd(Criteria.contains(UnConfirmedCustomerPathView.CustomerName, key)
                    .or(Criteria.contains(UnConfirmedCustomerPathView.CustomerCode, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Address, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Phone, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.Mobile, key))
                    .or(Criteria.contains(UnConfirmedCustomerPathView.StoreName, key))
                    .or(Criteria.contains(CustomerPathView.NationalCode, key))
                    .or(Criteria.contains(CustomerPathView.CustomerCategoryName, key)))
                    .whereAnd(Criteria.isNull(UnConfirmedCustomerPathView.CallType))
                    .orderByDescending(UnConfirmedCustomerPathView.OPathId)
                    .orderByAscending(UnConfirmedCustomerPathView.PathRowId);

        return query;
    }

    public static Query getByBarcode(String barcode) {
        Query query = new Query();
        return query.from(CustomerPathView.CustomerPathViewTbl).whereAnd(Criteria.equals(CustomerPathView.Barcode, barcode));
    }

    public List<CustomerPathViewModel> getVisitedPathCustomers(UUID visitPathId, boolean checkActive) {
        return getItems(hasOperationCustomers(visitPathId, checkActive, true));
    }

    public boolean isInVisitDayPath(UUID customerId) {
        TourModel tourModel = new TourManager(getContext()).loadTour();
        Query query = new Query().from(CustomerPathView.CustomerPathViewTbl).whereAnd(Criteria.equals(CustomerPathView.UniqueId, customerId)
                .and(Criteria.equals(CustomerPathView.VisitTemplatePathId, tourModel.DayVisitPathId)));
        CustomerPathViewModel customerPathViewModel = getItem(query);
        return customerPathViewModel != null;
    }

    public boolean isAllCustomersOfPathVisited() {
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.MandatoryCustomerVisit, SysConfigManager.cloud);

        if (sysConfigModel != null && sysConfigModel.Value.equals("True") && !(VaranegarApplication.is(VaranegarApplication.AppId.Contractor))) {
            TourManager tourManager = new TourManager(getContext());
            TourModel tourModel = tourManager.loadTour();
            List<CustomerPathViewModel> visitedPathCustomers = getVisitedPathCustomers(tourModel.DayVisitPathId, false);
            List<CustomerPathViewModel> allPathCustomers = getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, false));
            return visitedPathCustomers.size() == allPathCustomers.size();
        } else
            return true;
    }

    public List<CustomerPathViewModel> getNotVisitedPathCustomers(UUID visitPathId) {
        List<CustomerPathViewModel> visitedPathCustomers = getVisitedPathCustomers(visitPathId, false);
        List<CustomerPathViewModel> allPathCustomers = getItems(CustomerPathViewManager.getDay(visitPathId, false));
        List<CustomerPathViewModel> notVisitedCustomers = Linq.relativeComplement(allPathCustomers, visitedPathCustomers, new Linq.Comparator<CustomerPathViewModel>() {
            @Override
            public boolean compare(CustomerPathViewModel item1, CustomerPathViewModel item2) {
                return item1.UniqueId.equals(item2.UniqueId);
            }
        });
        return notVisitedCustomers;
    }

    public boolean hasIncompleteOperation() {
        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();
        return (getItems(hasOperationCustomers(tourModel.DayVisitPathId, false, false)).size() > 0);
    }

    public Query hasOperationCustomers(UUID visitPathId, boolean checkActive, boolean confirmed) {
        Query query = new Query();
        query.select(Projection.column(CustomerPathView.CustomerPathViewTbl, CustomerPathView.All));
        query.from(From.table(CustomerPathView.CustomerPathViewTbl).innerJoin(CustomerCall.CustomerCallTbl)
                .on(CustomerCall.CustomerId, CustomerPathView.UniqueId)
                .onAnd(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithoutRef.ordinal())
                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithRef.ordinal())
                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveOrderRequest.ordinal())
                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.LackOfOrder.ordinal())
                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.LackOfVisit.ordinal())
                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.CompleteLackOfDelivery.ordinal())
                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.CompleteReturnDelivery.ordinal())
                                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderDelivered.ordinal())
                                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderPartiallyDelivered.ordinal())
                                                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderLackOfDelivery.ordinal())
                                                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderReturn.ordinal()))))))))))))
                .innerJoin(From.table(CustomerCallView.CustomerCallViewTbl))
                .on(CustomerCallView.CustomerId, CustomerPathView.UniqueId));
        query.whereAnd(Criteria.equals(CustomerPathView.VisitTemplatePathId, visitPathId.toString())
                .and(Criteria.equals(CustomerCallView.Confirmed, confirmed)));
        if (checkActive)
            query.whereAnd(Criteria.equals(CustomerPathView.IsActive, true));
        query.groupBy(CustomerPathView.UniqueId);
        return query;
    }
}
