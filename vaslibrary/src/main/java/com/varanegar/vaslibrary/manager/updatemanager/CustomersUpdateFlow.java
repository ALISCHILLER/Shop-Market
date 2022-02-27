package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.VisitTemplatePathCustomerManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyManager;
import com.varanegar.vaslibrary.manager.customer.CustomerBarcodeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.visitday.VisitDayManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;

/**
 * Created by A.Torabi on 2/14/2018.
 */

public class CustomersUpdateFlow extends UpdateFlow {

    private UUID customerId;

    public CustomersUpdateFlow(Context context, @Nullable UUID customerId) {
        super(context);
        this.customerId = customerId;
    }

    @Override
    protected void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                CustomerManager customerManager = new CustomerManager(getContext());
                if (customerId == null)
                    customerManager.sync(call, false);
                else
                    customerManager.sync(customerId, call);
            }

            @Override
            public String name() {
                return "Customer";
            }

            @Override
            public int group() {
                return R.string.customer_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                CustomerShipToPartyManager shipToPartyManager = new
                        CustomerShipToPartyManager(getContext());
                CustomerManager customerManager = new CustomerManager(getContext());
                if (customerId == null)
                    shipToPartyManager.sync(call, false);
            }

            @Override
            public String name() {
                return "shipToPartyManager";
            }

            @Override
            public int group() {
                return R.string.customer_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(getContext());
                paymentOrderTypeManager.sync(call);
            }

            @Override
            public String name() {
                return "PaymentOrderType";
            }

            @Override
            public int group() {
                return R.string.customer_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    VisitTemplatePathCustomerManager visitTemplatePathCustomerManager = new VisitTemplatePathCustomerManager(getContext());
                    visitTemplatePathCustomerManager.sync(customerId, call);
                }

                @Override
                public String name() {
                    return "VisitTemplatePathCustomer";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    VisitDayManager visitDayManager = new VisitDayManager(getContext());
                    visitDayManager.sync(call);
                }

                @Override
                public String name() {
                    return "VisitDay";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
        }
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Varanegar) {
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());
                        customerRemainPerLineManager.sync(customerId, call);
                    }

                    @Override
                    public String name() {
                        return "CustomerRemainPerLine";
                    }

                    @Override
                    public int group() {
                        return R.string.customer_info;
                    }

                    @Override
                    public int queueId() {
                        return 0;
                    }
                });
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
        SysConfigModel checkBarcode = sysConfigManager.read(ConfigKey.CheckBarcode, SysConfigManager.cloud);
        if (SysConfigManager.compare(checkBarcode, true))
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerBarcodeManager customerBarcodeManager = new CustomerBarcodeManager(getContext());
                    customerBarcodeManager.sync(call);
                }

                @Override
                public String name() {
                    return "CustomerBarcode";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
    }

    public void syncCustomersAndInitPromotionDb(final UpdateCall call) {
        start(new UpdateCall() {
            @Override
            protected void onSuccess() {
                try {
                    DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(getContext());
                    DiscountCalculatorHandler.init(disc, 0, null);
                    DiscountCalculatorHandler.fillCustomerInitData(disc);
                    call.onSuccess();
                } catch (Exception ex) {
                    Timber.e(ex);
                    call.onFailure(getContext().getString(R.string.init_discount_failed));
                }
            }

            @Override
            protected void onFailure(String error) {
                call.failure(error);
            }
        });
    }
}
