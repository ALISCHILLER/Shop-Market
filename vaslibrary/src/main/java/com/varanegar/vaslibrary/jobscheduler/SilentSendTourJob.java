package com.varanegar.vaslibrary.jobscheduler;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.jobscheduler.Job;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 8/27/2017.
 */

public class SilentSendTourJob implements Job {

    @Override
    public Long getInterval() {
        return 60L;
    }

    @Override
    public void run(final Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AutoSynch, SysConfigManager.cloud);
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && SysConfigManager.compare(sysConfigModel, true)) {
            TourManager tourManager = new TourManager(context);
            tourManager.verifyData(new TourManager.VerifyCallBack() {
                @Override
                public void onFailure(String error) {
                    Timber.e(error);
                }

                @Override
                public void onSuccess(final List<UUID> result) {
                    if (result != null && result.size() > 0) {
                        CustomerManager customerManager = new CustomerManager(context);
                        List<CustomerModel> customerModels = customerManager.getCustomers(result);
                        String msg = context.getString(R.string.do_you_want_to_restore_these_customers);
                        for (CustomerModel customerModel :
                                customerModels) {
                            msg += System.getProperty("line.separator") + customerModel.CustomerName;
                        }
                        Timber.d(msg);
                    } else {
                        List<CustomerModel> customerModels;
                        final CustomerManager customerManager = new CustomerManager(context);
                        customerModels = customerManager.getAll();
                        Linq.forEach(customerModels, new Linq.Consumer<CustomerModel>() {
                            @Override
                            public void run(final CustomerModel item) {
                                CustomerCallManager callManager = new CustomerCallManager(context);
                                List<CustomerCallModel> callModels = callManager.loadCalls(item.UniqueId);
                                if (((VaranegarApplication.is(VaranegarApplication.AppId.Dist) && callManager.hasDeliveryOrReturnCall(callModels)) || (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && callManager.hasOrderOrReturnCall(callModels))) && callManager.isConfirmed(callModels) && !callManager.isDataSent(callModels, true)) {
                                    TourManager tourManager = new TourManager(context);
                                    tourManager.populatedAndSendTour(item.UniqueId, new TourManager.TourCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            Timber.i("customer data has been sent, customer id = " + item.UniqueId.toString());
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Timber.e(error, "error sending customer data, customer id = " + item.UniqueId.toString());
                                        }

                                        @Override
                                        public void onProgressChanged(String progress) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });


        }
    }
}
