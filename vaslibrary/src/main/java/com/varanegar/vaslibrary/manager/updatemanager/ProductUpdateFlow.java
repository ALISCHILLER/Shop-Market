package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductBoGroupManager;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.discountmanager.ProductTaxInfoManager;
import com.varanegar.vaslibrary.manager.productUnit.ProductUnitManager;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;

import java.util.List;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;

/**
 * Created by A.Torabi on 2/14/2018.
 */

public class ProductUpdateFlow extends UpdateFlow {
    public ProductUpdateFlow(Context context) {
        super(context);
    }

    @Override
    public void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ProductGroupManager productGroupManager = new ProductGroupManager(getContext());
                productGroupManager.sync(call);
            }

            @Override
            public String name() {
                return "ProductGroup";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ProductBoGroupManager productBoGroupManager = new ProductBoGroupManager(getContext());
                productBoGroupManager.sync(call);
            }

            @Override
            public String name() {
                return "ProductBoGroup";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ProductManager productManager = new ProductManager(getContext());
                productManager.sync(true, call);
            }

            @Override
            public String name() {
                return "Product";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                OnHandQtyManager onHandQtyManager = new OnHandQtyManager(getContext());
                onHandQtyManager.sync(call,true);
            }

            @Override
            public String name() {
                return "OnHandQty";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 1;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ProductUnitManager productUnitManager = new ProductUnitManager(getContext());
                productUnitManager.sync(true, call);
            }

            @Override
            public String name() {
                return "ProductUnit";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 1;
            }
        });

        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                ProductTaxInfoManager productTaxInfoManager = new ProductTaxInfoManager(getContext());
                productTaxInfoManager.sync(call);
            }

            @Override
            public String name() {
                return "ProductTaxInfo";
            }

            @Override
            public int group() {
                return R.string.product_info;
            }

            @Override
            public int queueId() {
                return 1;
            }
        });

    }

    public void syncProductsAndInitPromotionDb(@NonNull final UpdateCall call) {
        start(new UpdateCall() {
            @Override
            protected void onSuccess() {
                try {
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
