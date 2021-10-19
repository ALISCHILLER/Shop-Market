package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeader;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModelRepository;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 02/12/2017.
 */

public class CustomerOldInvoiceHeaderManager extends BaseManager<CustomerOldInvoiceHeaderModel> {
    public CustomerOldInvoiceHeaderManager(Context context) {
        super(context, new CustomerOldInvoiceHeaderModelRepository());
    }

    public static Query getCustomerInvoiceDesc(UUID customerUniqueId) {
        Query q = new Query();
        q.from(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl).whereAnd(Criteria.equals(CustomerOldInvoiceHeader.CustomerId, customerUniqueId));
        q.orderByDescending(CustomerOldInvoiceHeader.SalePDate);
        return q;
    }


    public static Query getItemsInPeriod(Date startTime, Date endTime) {
        Query query = new Query();
        query.from(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl).whereAnd(Criteria.between(CustomerOldInvoiceHeader.SaleDate, startTime, endTime));
        return query;
    }

    public CustomerOldInvoiceHeaderModel getInvoiceByNo(String backOfficeInvoiceNo) {
        Query q = new Query();
        q.from(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl).whereAnd(Criteria.equals(CustomerOldInvoiceHeader.SaleNo, backOfficeInvoiceNo));
        return getItem(q);
    }

    public CustomerOldInvoiceHeaderModel getInvoiceBySaleRef(String saleRef) {
        Query q = new Query();
        q.from(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl).whereAnd(Criteria.equals(CustomerOldInvoiceHeader.SaleRef, saleRef));
        return getItem(q);
    }

//    public void syncAll(@NonNull final UpdateCall updateCall)
//    {
//        UpdateManager updateManager = new UpdateManager(getContext());
//        Date date = updateManager.getLog(UpdateKey.CustomerOldInvoiceHeader);
//        String dateString = DateHelper.toString(date, DateFormat.Dashed);
//        TourManager tourManager = new TourManager(getContext());
//        TourModel tourModel = tourManager.getItem(TourManager.getTour());
//        CustomerOldInvoiceHeaderApi customerOldInvoiceHeaderApi = new CustomerOldInvoiceHeaderApi(getContext());
//        customerOldInvoiceHeaderApi.runWebRequest(customerOldInvoiceHeaderApi.getAll(tourModel.UniqueId.toString(), dateString), new WebCallBack<CustomerOldInvoicesViewModel>() {
//            @Override
//            protected void onFinish()
//            {
//
//            }
//
//            @Override
//            protected void onSuccess(CustomerOldInvoicesViewModel result)
//            {
//
//            }
//
//            @Override
//            protected void onApiFailure(ApiError onError)
//            {
//
//            }
//
//            @Override
//            protected void onNetworkFailure(Throwable t)
//            {
//
//            }
//        });
//
//        customerOldInvoiceHeaderApi.runWebRequest(customerOldInvoiceHeaderApi.getAll(tourModel.UniqueId.toString(), dateString), new WebCallBack<List<CustomerOldInvoicesViewModel>>() {
//            @Override
//            protected void onFinish() {
//
//            }
//
//            @Override
//            protected void onSuccess(List<CustomerOldInvoiceHeaderModel> result) {
//                if (result == null || result.size() == 0)
//                {
//                    Timber.i("Updating customeroldinvoiceheader group with null object");
//                    updateCall.success();
//                }
//                else {
//                    insertOrUpdate(result, new CallBack() {
//                        @Override
//                        public void onValidationFailed(List<ConstraintViolation> errors) {
//                            Timber.e(Validator.toString(errors));
//                            updateCall.dbFailure(Validator.toString(errors));
//                        }
//
//                        @Override
//                        public void onSucceeded(int affectedRows) {
//                            new UpdateManager(getContext()).addLog(UpdateKey.CustomerOldInvoiceHeader);
//                            Timber.i("Updating customeroldinvoiceheader completed");
//                            updateCall.success();
//                        }
//
//                        @Override
//                        public void onFailed(String onError) {
//                            Timber.e(onError);
//                            updateCall.dbFailure(onError);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            protected void onApiFailure(ApiError onError) {
//                Timber.e(onError.getMessage());
//                updateCall.webApiFailure(onError.getMessage());
//            }
//
//            @Override
//            protected void onNetworkFailure(Throwable t) {
//                Timber.e(t.getMessage());
//                updateCall.networkFailure(t.getMessage());
//            }
//        });
//    }

}
