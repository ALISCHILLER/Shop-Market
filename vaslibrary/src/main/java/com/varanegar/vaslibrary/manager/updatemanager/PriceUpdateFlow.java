package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.DealerPaymentTypeManager;
import com.varanegar.vaslibrary.manager.PriceHistoryManager;
import com.varanegar.vaslibrary.manager.contractpricemanager.ContractPriceManager;
import com.varanegar.vaslibrary.manager.contractpricemanager.ContractPriceSDSManager;
import com.varanegar.vaslibrary.manager.contractpricemanager.ContractPriceVnLiteManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountConditionManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountSDSManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountVnLiteManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;

import java.util.Date;
import java.util.List;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;

/**
 * Created by g.aliakbar on 25/04/2018.
 */

public class PriceUpdateFlow extends UpdateFlow {
    public PriceUpdateFlow(Context context) {
        super(context);
    }

    public static void clearAdditionalData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DATA_RECOVERY", Context.MODE_PRIVATE);
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Varanegar) {
                ContractPriceSDSManager contractPriceSDSManager = new ContractPriceSDSManager(context);
                contractPriceSDSManager.clearAdditionalData();
                DiscountSDSManager discountSDSManager = new DiscountSDSManager(context);
                discountSDSManager.clearAdditionalData();
                DiscountConditionManager discountConditionManager = new DiscountConditionManager(context);
                discountConditionManager.clearAdditionalData();
            } else if (backOfficeType == BackOfficeType.Rastak) {
                ContractPriceVnLiteManager contractPriceVnLiteManager = new ContractPriceVnLiteManager(context);
                contractPriceVnLiteManager.clearAdditionalData();
                DiscountVnLiteManager discountVnLiteManager = new DiscountVnLiteManager(context);
                discountVnLiteManager.clearAdditionalData();
            }
            PriceHistoryManager priceHistoryManager = new PriceHistoryManager(context);
            priceHistoryManager.clearAdditionalData();
            sharedPreferences.edit().putLong("Last_Clear_Date", new Date().getTime()).apply();
        } catch (Exception e) {
            Timber.i("Load BackOfficeType :" + e.getMessage());
        }
    }

    @Override
    protected void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ContractPriceManager contractPriceManager = new ContractPriceManager(getContext());
                contractPriceManager.sync(call);
            }

            @Override
            public String name() {
                return "ContractPrice";
            }

            @Override
            public int group() {
                return R.string.price_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {

                PriceHistoryManager priceHistoryManager = new PriceHistoryManager(getContext());
                priceHistoryManager.sync(call);
            }

            @Override
            public String name() {
                return "PriceHistory";
            }

            @Override
            public int group() {
                return R.string.price_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                DealerPaymentTypeManager dealerPaymentTypeManager = new DealerPaymentTypeManager(getContext());
                dealerPaymentTypeManager.sync(call);
            }

            @Override
            public String name() {
                return "DealerPaymentType";
            }

            @Override
            public int group() {
                return R.string.price_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

    }

    public void syncPriceAndInitPromotionDb(@NonNull final UpdateCall call) {
        start(new UpdateCall() {
            @Override
            protected void onSuccess() {
                try {
                    clearAdditionalData(getContext());
                    DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(getContext());
                    DiscountCalculatorHandler.init(disc, 0, null);
                    DiscountCalculatorHandler.fillProductInitData(disc);
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
