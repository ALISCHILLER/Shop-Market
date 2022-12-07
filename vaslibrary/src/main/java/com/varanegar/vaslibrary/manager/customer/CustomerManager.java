package com.varanegar.vaslibrary.manager.customer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.mapper.ContentValueMap;
import com.varanegar.framework.database.mapper.ContentValueMapList;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerDataForRegisterManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.VisitTemplatePathCustomerManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyModel;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModel;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customer.CustomerModelRepository;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customercallview.CustomerCallView;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.dataforregister.CustomerDataForRegisterModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.update.UpdateLog;
import com.varanegar.vaslibrary.model.update.UpdateLogModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncSendCustomerFinanceDataViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerUpdateDataViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerUpdateLocationViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by atp on 12/19/2016.
 */
public class CustomerManager extends BaseManager<CustomerModel> {
    private Call<List<CustomerModel>> call;
    private Call<List<SyncSendCustomerFinanceDataViewModel>> call2;

    public CustomerManager(Context context) {
        super(context, new CustomerModelRepository());
    }

    public CustomerModel getCustomer(UUID customerId){
        Query query = new Query();
        query.from(Customer.CustomerTbl)
                .whereAnd(Criteria.contains(Customer.UniqueId, String.valueOf(customerId)));
        return getItem(query);
    }
    public List<CustomerModel> getAll() {
        Query query = new Query();
        query.from(Customer.CustomerTbl).orderByAscending(Customer.rowIndex);
        return getItems(query);
    }

    public List<CustomerModel> getAllTb() {
        Query query = new Query();
        query.from(Customer.CustomerTbl);
        return getItems(query);
    }

    public List<CustomerModel> getCustomersWithCustomerCalls() {
        Query query = new Query();
        query.from(From.table(Customer.CustomerTbl).innerJoin(CustomerCall.CustomerCallTbl)
                .on(Customer.UniqueId, CustomerCall.CustomerId));
        Query q = query.select(Projection.column(Customer.CustomerAll))
                .groupBy(Customer.UniqueId);
        return getItems(q);
    }

    public List<CustomerModel> getCustomersWithDataSent() {
        Query query = new Query();
        query.from(From.table(Customer.CustomerTbl).innerJoin(CustomerCall.CustomerCallTbl)
                .on(Customer.UniqueId, CustomerCall.CustomerId)
                .onAnd(Criteria.equals(CustomerCall.CallType, CustomerCallType.SendData.ordinal())));
        Query q = query.select(Projection.column(Customer.CustomerAll));
        return getItems(q);
    }

    private Date getCustomerLatestUpdate(UUID customerId) {
        UpdateManager updateManager = new UpdateManager(getContext());
        UpdateLogModel model = updateManager.getItem(new Query().from(UpdateLog.UpdateLogTbl)
                .whereAnd(Criteria.equals(UpdateLog.Name, "customer_update_" + customerId.toString())));
        if (model == null) {
            return new Date(87, 3, 1);
        } else {
            return model.Date;
        }
    }

    private void setCustomerLatestUpdate(UUID customerId) {
        UpdateManager updateManager = new UpdateManager(getContext());
        UpdateLogModel model;
        model = updateManager.getItem(new Query().from(UpdateLog.UpdateLogTbl)
                .whereAnd(Criteria.equals(UpdateLog.Name, "customer_update_" + customerId.toString())));
        if (model == null) {
            model = new UpdateLogModel();
            model.UniqueId = UUID.randomUUID();
            model.Date = new Date();
            model.Name = "customer_update_" + customerId.toString();
            try {
                updateManager.insert(model);
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            model.Date = new Date();
            try {
                updateManager.update(model);
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }
    }

    public void sync(final UUID customerId, final UpdateCall updateCall) {
        clearCache();
        Date date = getCustomerLatestUpdate(customerId);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        final CustomerApi customerApi = new CustomerApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        final SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        final String dealerId = UserManager.readFromFile(getContext()).UniqueId.toString();
        TourModel tourModel = new TourManager(getContext()).loadTour();
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            call = customerApi.get(tourModel.UniqueId.toString());
        else
            call = customerApi.get(dateString, dealerId, customerId.toString(), settingsId.Value);
        customerApi.runWebRequest(call, new WebCallBack<List<CustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerModel> result, Request request) {

                if (result.size() > 0) {
                    try {
                        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            for (CustomerModel item : result
                            ) {
                                item.Barcode = item.CustomerCode;

                                CustomerShipToPartyManager shipToPartyManager = new
                                        CustomerShipToPartyManager(getContext());
                                CustomerShipToPartyModel customerShipToPartyModel = new CustomerShipToPartyModel();

                                customerShipToPartyModel.CustomerName = item.CustomerName;
                                customerShipToPartyModel.BackOfficeId = item.CustomerCode;
                                customerShipToPartyModel.Latitude = item.Latitude;
                                customerShipToPartyModel.Longitude = item.Latitude;
                                customerShipToPartyModel.Address = item.Address;
                                customerShipToPartyModel.SoldToPartyUniqueId = item.UniqueId;
                                customerShipToPartyModel.UniqueId = item.UniqueId;
                                customerShipToPartyModel.Mobile = item.Mobile;
                                customerShipToPartyModel.Phone = item.Phone;
                                customerShipToPartyModel.StoreName = item.StoreName;
                                customerShipToPartyModel.PostCode = item.CustomerPostalCode;
                                customerShipToPartyModel.IgnoreLocation = item.IgnoreLocation;
                                customerShipToPartyModel.NationalCode = item.NationalCode;
                                customerShipToPartyModel.EconomicCode = item.EconomicCode;
                                customerShipToPartyModel.IsActive = item.IsActive;
                                shipToPartyManager.insertOrUpdate(customerShipToPartyModel);
                            }
                        }
                        insertOrUpdate(result);
                        updateFinanceData(new UpdateCall() {
                            @Override
                            protected void onFinish() {
//                                OldInvoiceManager oldInvoiceManager = new OldInvoiceManager(getContext());
//                                oldInvoiceManager.sync(null, null, customerId, new UpdateCall() {
//                                    @Override
//                                    protected void onSuccess() {
//
//                                    }
//
//                                    @Override
//                                    protected void onFailure(String error) {
//                                        updateCall.failure(error);
//                                    }
//                                });
                                // TODO: 8/14/2019 why these codes are in onFinish - elnaz
                                setCustomerLatestUpdate(customerId);
                                updateCall.success();
                            }

                            @Override
                            protected void onSuccess() {
                            }

                            @Override
                            protected void onFailure(String error) {
                                Timber.e(error);
                            }
                        }, customerApi, dealerId, settingsId.Value, customerId.toString());

                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("List of customers was empty");
                    updateFinanceData(new UpdateCall() {
                        @Override
                        protected void onFinish() {
//                            OldInvoiceManager oldInvoiceManager = new OldInvoiceManager(getContext());
//                            oldInvoiceManager.sync(null, null, customerId, new UpdateCall() {
//                                @Override
//                                protected void onSuccess() {
//                                    setCustomerLatestUpdate(customerId);
//                                    updateCall.success();
//                                }
//
//                                @Override
//                                protected void onFailure(String error) {
//                                    updateCall.failure(error);
//                                }
//                            });
                            // TODO: 8/14/2019 why these codes are in onFinish - elnaz
                            setCustomerLatestUpdate(customerId);
                            updateCall.success();
                        }

                        @Override
                        protected void onSuccess() {
                        }

                        @Override
                        protected void onFailure(String error) {
                            Timber.e(error);
                        }
                    }, customerApi, dealerId, settingsId.Value, customerId.toString());

                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    public void sync(@NonNull final UpdateCall updateCall, final boolean isTourUpdateFlow) {
        try {
            updateOPathIds(new ArrayList<>(), new ArrayList<>());
        } catch (Exception e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_saving_request));
            return;
        }
        clearCache();
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.Customer);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        final CustomerApi customerApi = new CustomerApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        final SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (settingsId == null) {
            updateCall.failure(getContext().getString(R.string.settings_id_not_found));
            return;
        }
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            updateCall.failure(getContext().getString(R.string.user_not_found));
            return;
        }
        final String dealerId = userModel.UniqueId.toString();
        TourModel tourModel = new TourManager(getContext()).loadTour();
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            call = customerApi.get(tourModel.UniqueId.toString());
        else
            call = customerApi.get(dateString, dealerId, null, settingsId.Value);
        customerApi.runWebRequest(call, new WebCallBack<List<CustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            public void onSuccess(List<CustomerModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        List<CustomerModel> oldCustomerModels = getItems(new Query()
                                .select(Customer.CustomerAll)
                                .from(From.table(Customer.CustomerTbl)
                                        .innerJoin(CustomerCall.CustomerCallTbl)
                                        .on(Customer.UniqueId, CustomerCall.CustomerId))
                                .whereAnd(Criteria.equals(CustomerCall.CallType,
                                        CustomerCallType.ChangeLocation.ordinal())));
                        HashMap<UUID, CustomerModel> hasMap = new HashMap<>();
                        if (oldCustomerModels.size() > 0) {
                            hasMap = Linq.toHashMap(oldCustomerModels, new Linq.Map<CustomerModel, UUID>() {
                                @Override
                                public UUID run(CustomerModel item) {
                                    return item.UniqueId;
                                }
                            });


                            for (CustomerModel sever :
                                    result) {
                                if (sever.Longitude == 0 && sever.Latitude == 0) {
                                    CustomerModel oldCustomerModel = hasMap.get(sever.UniqueId);
                                    if (oldCustomerModel != null) {
                                        sever.Latitude = oldCustomerModel.Latitude;
                                        sever.Longitude = oldCustomerModel.Longitude;
                                    }
                                }
                            }
                        }


                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && isTourUpdateFlow) {
                            deleteAll();
                            insert(result);
                        } else {
                            sync(result);
                            CustomerShipToPartyManager shipToPartyManager = new
                                    CustomerShipToPartyManager(getContext());
                            List<CustomerModel> customerModels = getAllTb();
                            for (CustomerModel sever :
                                    customerModels) {
                                CustomerShipToPartyModel customerShipToPartyModel = new CustomerShipToPartyModel();
                                customerShipToPartyModel.CustomerName = sever.CustomerName;
                                customerShipToPartyModel.BackOfficeId = sever.CustomerCode;
                                customerShipToPartyModel.Latitude = sever.Latitude;
                                customerShipToPartyModel.Longitude = sever.Latitude;
                                customerShipToPartyModel.Address = sever.Address;
                                customerShipToPartyModel.SoldToPartyUniqueId = sever.UniqueId;
                                customerShipToPartyModel.UniqueId = sever.UniqueId;
                                customerShipToPartyModel.Mobile = sever.Mobile;
                                customerShipToPartyModel.Phone = sever.Phone;
                                customerShipToPartyModel.StoreName = sever.StoreName;
                                customerShipToPartyModel.PostCode = sever.CustomerPostalCode;
                                customerShipToPartyModel.IgnoreLocation = sever.IgnoreLocation;
                                customerShipToPartyModel.NationalCode = sever.NationalCode;
                                customerShipToPartyModel.EconomicCode = sever.EconomicCode;
                                customerShipToPartyModel.IsActive = sever.IsActive;
                                if (isTourUpdateFlow)
                                    shipToPartyManager.insertOrUpdate(customerShipToPartyModel);
                                else
                                    shipToPartyManager.insertOrUpdate(customerShipToPartyModel);
                            }

                        }
                        updateFinanceData(updateCall, customerApi, dealerId, settingsId.Value, null);
                        new UpdateManager(getContext()).addLog(UpdateKey.Customer);
                        Timber.i("Updating customers completed");
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    setcustomertoship();
                    updateFinanceData(updateCall, customerApi, dealerId, settingsId.Value, null);
                }
            }

            @Override
            public void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            public void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    public void setcustomertoship() {
        CustomerShipToPartyManager shipToPartyManager = new
                CustomerShipToPartyManager(getContext());
        List<CustomerModel> customerModels = getAllTb();
        for (CustomerModel sever :
                customerModels) {
            CustomerShipToPartyModel customerShipToPartyModel = new CustomerShipToPartyModel();
            customerShipToPartyModel.CustomerName = sever.CustomerName;
            customerShipToPartyModel.BackOfficeId = sever.CustomerCode;
            customerShipToPartyModel.Latitude = sever.Latitude;
            customerShipToPartyModel.Longitude = sever.Latitude;
            customerShipToPartyModel.Address = sever.Address;
            customerShipToPartyModel.SoldToPartyUniqueId = sever.UniqueId;
            customerShipToPartyModel.UniqueId = sever.UniqueId;
            customerShipToPartyModel.Mobile = sever.Mobile;
            customerShipToPartyModel.Phone = sever.Phone;
            customerShipToPartyModel.StoreName = sever.StoreName;
            customerShipToPartyModel.PostCode = sever.CustomerPostalCode;
            customerShipToPartyModel.IgnoreLocation = sever.IgnoreLocation;
            customerShipToPartyModel.NationalCode = sever.NationalCode;
            customerShipToPartyModel.EconomicCode = sever.EconomicCode;
            customerShipToPartyModel.IsActive = sever.IsActive;
            try {
                shipToPartyManager.insertOrUpdate(customerShipToPartyModel);
            } catch (ValidationException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
        else if (call2 != null && !call2.isCanceled() && call2.isExecuted())
            call2.cancel();
    }

    private void updateFinanceData(final UpdateCall updateCall, final CustomerApi customerApi, final String dealerId, final String settingsNo, @Nullable String customerId) {
        TourModel tourModel = new TourManager(getContext()).loadTour();
        call2 = customerApi.getFinanceData(VaranegarApplication.getInstance().getAppId().toString(), tourModel.UniqueId.toString(), dealerId, settingsNo, customerId);
        customerApi.runWebRequest(call2, new WebCallBack<List<SyncSendCustomerFinanceDataViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SyncSendCustomerFinanceDataViewModel> result, Request request) {
                try {
                    update(new ContentValueMapList<SyncSendCustomerFinanceDataViewModel, CustomerModel>(result) {
                        @Override
                        protected ContentValueMap<SyncSendCustomerFinanceDataViewModel, CustomerModel> getContentValueMap(SyncSendCustomerFinanceDataViewModel item) {
                            return new ContentValueMap<SyncSendCustomerFinanceDataViewModel, CustomerModel>(CustomerModel.class)
                                    .map(item.ErrorMessage, Customer.ErrorMessage)
                                    .map(item.CustomerRemain, Customer.CustomerRemain)
                                    .map(item.CustRemAmountAll, Customer.CustRemAmountAll)
                                    .map(item.CustRemAmountForSaleOffice, Customer.CustRemAmountForSaleOffice)
                                    .map(item.InitCredit, Customer.InitCredit)
                                    .map(item.InitDebit, Customer.InitDebit)
                                    .map(item.OpenChequeAmount, Customer.OpenChequeAmount)
                                    .map(item.OpenChequeCount, Customer.OpenChequeCount)
                                    .map(item.OpenInvoicesAmount, Customer.OpenInvoiceAmount)
                                    .map(item.OpenInvoicesCount, Customer.OpenInvoiceCount)
                                    .map(item.RemainCredit, Customer.RemainCredit)
                                    .map(item.RemainDebit, Customer.RemainDebit)
                                    .map(item.ReturnChequeAmount, Customer.ReturnChequeAmount)
                                    .map(item.ReturnChequeCount, Customer.ReturnChequeCount)
                                    .map(item.CustomerMessage, Customer.CustomerMessage);
                        }

                        @Override
                        public UUID getUniqueId(SyncSendCustomerFinanceDataViewModel item) {
                            return item.CustomerUniqueId;
                        }

                    });
                    updateCall.success();
                } catch (DbException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.data_error));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    /**
     * Returns null if customer information was not edited
     *
     * @param customerId
     * @return
     */
    @Nullable
    public SyncGetCustomerUpdateDataViewModel getCustomerUpdateDataViewModel(@NonNull UUID customerId) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        CustomerCallModel callModel = callManager.loadCall(CustomerCallType.EditCustomer, customerId);
        if (callModel != null) {
            CustomerModel customerModel = getItem(customerId);
            if (customerModel != null) {
                SyncGetCustomerUpdateDataViewModel viewModel = new SyncGetCustomerUpdateDataViewModel();
                viewModel.Address = customerModel.Address;
                viewModel.CustomerId = customerModel.UniqueId;
                viewModel.CustomerName = customerModel.CustomerName;
                viewModel.EconomicCode = customerModel.EconomicCode;
                viewModel.Mobile = customerModel.Mobile;
                viewModel.NationalCode = customerModel.NationalCode;
                viewModel.Phone = customerModel.Phone;
                viewModel.StoreName = customerModel.StoreName;
                viewModel.PostalCode = customerModel.CustomerPostalCode;
                viewModel.CityId = customerModel.CityId;
                viewModel.CountyId = customerModel.CountyId;
                viewModel.StateId = customerModel.StateId;
                viewModel.CityZone = customerModel.CityZone;
                if (customerModel.OwnerTypeRef != 0)
                    viewModel.OwnerTypeRef = String.valueOf(customerModel.OwnerTypeRef);
                viewModel.CustomerActivityId = customerModel.CustomerActivityId;
                if (customerModel.CustomerActivityRef != 0)
                    viewModel.CustomerActivityRef = String.valueOf(customerModel.CustomerActivityRef);
                viewModel.CustomerCategoryId = customerModel.CustomerCategoryId;
                if (customerModel.CustomerCategoryRef != 0)
                    viewModel.CustomerCategoryRef = String.valueOf(customerModel.CustomerCategoryRef);
                viewModel.CustomerLevelId = customerModel.CustomerLevelId;
                if (customerModel.CustomerLevelRef != 0)
                    viewModel.CustomerLevelRef = String.valueOf(customerModel.CustomerLevelRef);
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
                if (ownerKeysWrapper.isZarMakaron()) {
                    CustomerDataForRegisterManager customerDataForRegisterManager = new CustomerDataForRegisterManager(getContext());
                    List<CustomerDataForRegisterModel> customerDataForRegisterModels = customerDataForRegisterManager.getAll(customerId);
                    if (customerDataForRegisterModels.size() > 0) {
                        viewModel.ExtraFields = new ArrayList<>();
                        viewModel.ExtraFields.addAll(customerDataForRegisterModels);
                    }
                }
                return viewModel;
            }
        }
        return null;
    }

    /**
     * returns null if customer location was not changed
     *
     * @param customerModel
     * @return
     */
    @Nullable
    public SyncGetCustomerUpdateLocationViewModel getCustomerLocationDataViewModel(@NonNull CustomerModel customerModel) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        CustomerCallModel callModel = callManager.loadCall(CustomerCallType.ChangeLocation, customerModel.UniqueId);
        if (callModel != null || (customerModel.Longitude != 0 && customerModel.Latitude != 0 && customerModel.ParentCustomerId != null)) {
            SyncGetCustomerUpdateLocationViewModel viewModel = new SyncGetCustomerUpdateLocationViewModel();
            viewModel.CustomerId = customerModel.UniqueId;
            viewModel.Latitude = customerModel.Latitude;
            viewModel.Longitude = customerModel.Longitude;
            return viewModel;

        }
        return null;
    }

    public List<CustomerModel> getCustomers(List<UUID> ids) {
        List<String> sids = Linq.map(ids, new Linq.Map<UUID, String>() {
            @Override
            public String run(UUID item) {
                return item.toString();
            }
        });
        Query query = new Query().from(Customer.CustomerTbl).whereAnd(Criteria.in(Customer.UniqueId, sids));
        return getItems(query);
    }

    public List<CustomerModel> getVisitedCustomers() {
        Query query = new Query();
        query.select(Projection.column(Customer.CustomerTbl, Customer.All));
        query.from(From.table(Customer.CustomerTbl).innerJoin(CustomerCall.CustomerCallTbl)
                .on(CustomerCall.CustomerId, Customer.UniqueId)
                .onAnd(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithoutRef.ordinal())
                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithRef.ordinal())
                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveOrderRequest.ordinal())
                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.LackOfOrder.ordinal())
                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.LackOfVisit.ordinal())
                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.CompleteLackOfDelivery.ordinal())
                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.CompleteReturnDelivery.ordinal())
                                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderDelivered.ordinal())
                                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderLackOfDelivery.ordinal())
                                                                                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderPartiallyDelivered.ordinal())
                                                                                                .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.OrderReturn.ordinal()))))))))))))
                .innerJoin(From.table(CustomerCallView.CustomerCallViewTbl))
                .on(CustomerCallView.CustomerId, Customer.UniqueId));
        query.whereAnd(Criteria.equals(CustomerCallView.Confirmed, true));
        query.groupBy(Customer.UniqueId);
        return getItems(query);
    }

    public void updateIsNewCustomer() {
        // TODO: 8/26/2018 Add update for single column
        List<CustomerModel> customerModels = getAll();
        for (CustomerModel customerModel :
                customerModels) {
            customerModel.IsNewCustomer = false;
        }
        try {
            update(customerModels);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public boolean isCustomerTurn(final CustomerModel customerModel) {
        CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(getContext());

        TourManager tourManager = new TourManager(getContext());
        final TourModel tourModel = tourManager.loadTour();

        VisitTemplatePathCustomerManager visitTemplatePathCustomerManager = new VisitTemplatePathCustomerManager(getContext());
        List<VisitTemplatePathCustomerModel> visitTemplatePathCustomerModels = visitTemplatePathCustomerManager.getCustomerPathUniqueId(customerModel.UniqueId);

        boolean isDayCustomer = Linq.exists(visitTemplatePathCustomerModels, new Linq.Criteria<VisitTemplatePathCustomerModel>() {
            @Override
            public boolean run(VisitTemplatePathCustomerModel item) {
                return tourModel != null && tourModel.DayVisitPathId.equals(item.VisitTemplatePathId);
            }
        });

        if (isDayCustomer) {
            List<CustomerPathViewModel> visitedCustomerPathViewModels = customerPathViewManager.getVisitedPathCustomers(tourModel.DayVisitPathId, true);
            List<CustomerPathViewModel> allCustomerPathViewModels = customerPathViewManager.getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, true));
            final CustomerPathViewModel targetCustomerPathViewModel = Linq.findFirst(allCustomerPathViewModels, new Linq.Criteria<CustomerPathViewModel>() {
                @Override
                public boolean run(CustomerPathViewModel item) {
                    return item.UniqueId.equals(customerModel.UniqueId);
                }
            });
            if (targetCustomerPathViewModel != null) {
                List<CustomerPathViewModel> visitedCustomerPathViewModelsBeforeTargetCustomer = Linq.findAll(visitedCustomerPathViewModels, new Linq.Criteria<CustomerPathViewModel>() {
                    @Override
                    public boolean run(CustomerPathViewModel item) {
                        return item.PathRowId < targetCustomerPathViewModel.PathRowId;
                    }
                });
                List<CustomerPathViewModel> allCustomerPathBeforeTargetViewModels = Linq.findAll(allCustomerPathViewModels, new Linq.Criteria<CustomerPathViewModel>() {
                    @Override
                    public boolean run(CustomerPathViewModel item) {
                        return item.PathRowId < targetCustomerPathViewModel.PathRowId;
                    }
                });
                if (visitedCustomerPathViewModelsBeforeTargetCustomer.size() < allCustomerPathBeforeTargetViewModels.size())
                    return false;
            }
        }
        return true;
    }

    public void cacheOriginalCustomer(@NonNull UUID customerId) {
        CustomerModel customer = getItem(customerId);
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("CUSTOMER_CACHE", Context.MODE_PRIVATE);
            String saved = sharedPreferences.getString(customerId.toString(), null);
            if (saved == null) {
                String json = VaranegarGsonBuilder.build().create().toJson(customer);
                sharedPreferences.edit().putString(customerId.toString(), json).apply();
            }
        } catch (Error ex) {
            Timber.e(ex);
        }
    }

    @Nullable
    public CustomerModel getCachedOriginalCustomer(@NonNull UUID customerId) {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("CUSTOMER_CACHE", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString(customerId.toString(), null);
            if (json != null) {
                return VaranegarGsonBuilder.build().create().fromJson(json, CustomerModel.class);
            } else
                return null;
        } catch (Error ex) {
            Timber.e(ex);
            return null;
        }
    }

    public void clearCache() {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("CUSTOMER_CACHE", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        } catch (Error error) {
            Timber.e(error);
        }
    }

    public void resetEditedCustomer(@NonNull UUID customerId) throws ValidationException, DbException {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        CustomerCallModel callModel = callManager.loadCall(CustomerCallType.EditCustomer, customerId);
        if (callModel != null) {
            CustomerModel customerModel = getCachedOriginalCustomer(customerId);
            if (customerModel != null)
                update(customerModel);
        }
    }

    public List<CustomerModel> getCustomersWithLocation() {
        return Linq.findAll(getAll(), item -> item.Latitude != 0 && item.Longitude != 0);
    }

    public void updateOPathIds(List<CustomerModel> customerModels, @NonNull List<LatLng> points) throws ValidationException, DbException {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("OPTIMIZED_PATH", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        List<String> l = Linq.map(points, (Linq.Map<LatLng, String>) item -> item.latitude + "," + item.longitude);
        int i = 0;
        for (String s :
                l) {
            sharedPreferences.edit().putString("point-" + i, s).commit();
            i++;
        }
        sharedPreferences.edit().putInt("count", i - 1).commit();
        List<CustomerModel> all = getAll();
        for (CustomerModel customer :
                all) {
            customer.OPathId = 0;
        }
        if (all.size() > 0)
            update(all);
        if (customerModels.size() > 0)
            update(customerModels);
    }
}
