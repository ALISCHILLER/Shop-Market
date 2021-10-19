package com.varanegar.vaslibrary.pricecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.ProductBatchOnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customerpricemanager.CustomerPriceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPrice;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModel;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModelRepository;
import com.varanegar.vaslibrary.model.customerprice.CustomerPrice;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModelRepository;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQty;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/9/2017.
 */

public abstract class PriceCalculator {
    @Nullable
    public UUID getCallOrderId() {
        return callOrderId;
    }

    @Nullable
    private UUID callOrderId;

    public UUID getCustomerId() {
        return customerId;
    }

    private UUID customerId;

    public Context getContext() {
        return context;
    }

    private Context context;
    int payTypeId;
    int orderTypeId;

    public static PriceCalculator getPriceCalculator(@NonNull Context context, UUID customerId) throws UnknownBackOfficeException {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
        if (backOfficeType == BackOfficeType.Varanegar)
            return new PriceCalculatorSDS(context, customerId);
        else if (backOfficeType == BackOfficeType.Rastak)
            return new PriceCalculatorVnLite(context, customerId);
        else
            return new PriceCalculatorNestle(context, customerId);
    }

    public static PriceCalculator getPriceCalculator(@NonNull Context context, UUID customerId, UUID callOrderId, int payTypeRef, int orderTypeRef, int priceClassRef) throws UnknownBackOfficeException {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
        if (backOfficeType == BackOfficeType.Varanegar)
            return new PriceCalculatorSDS(context, customerId, callOrderId, payTypeRef, orderTypeRef);
        else if (backOfficeType == BackOfficeType.Rastak)
            return new PriceCalculatorVnLite(context, customerId, callOrderId, payTypeRef, orderTypeRef, priceClassRef);
        else
            return new PriceCalculatorNestle(context, customerId, callOrderId, payTypeRef, orderTypeRef);
    }

    public PriceCalculator(@NonNull Context context, @NonNull UUID customerId) {
        this.context = context;
        this.customerId = customerId;
        this.callOrderId = null;
    }

    public PriceCalculator(@NonNull Context context, @NonNull UUID customerId, @NonNull UUID callOrderId, int payTypeId, int orderTypeId) {
        this(context, customerId);
        this.callOrderId = callOrderId;
        this.payTypeId = payTypeId;
        this.orderTypeId = orderTypeId;
    }

    @Nullable
    public abstract List<CustomerBatchPriceModel> calculateBatchPrices();

    public abstract List<CustomerPriceModel> calculatePrices();

    public void calculateAndSavePriceList( boolean persistCustomizedPrice , @NonNull final PriceCalcCallback callback) {
        try {
            final CustomerPriceModelRepository repository = new CustomerPriceModelRepository();
            final HashMap<UUID, CustomerPriceModel> currentCustomerPriceList = new HashMap<>();
            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            final HashMap<UUID, ProductUnitsViewModel> productUnitsViewModelHashMap = new ProductUnitsViewManager(context).getProductsUnits();
            long deleted = 0;

            CustomerPriceManager customerPriceManager = new CustomerPriceManager(getContext());
            List<CustomerPriceModel> customizedPrices = customerPriceManager.getCustomizedPrices(customerId, callOrderId);

            if (callOrderId != null)
                deleted = repository.delete(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerPrice.CallOrderId, callOrderId.toString())));
            else
                deleted = repository.delete(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString())
                        .and(Criteria.isNull(CustomerPrice.CallOrderId)));
            Timber.i(deleted + " customer prices deleted.");
            List<CustomerPriceModel> priceList;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                if (callOrderId != null) {
                    CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
                    priceList = callOrderManager.initCallPrices(callOrderId);
                } else
                    priceList = calculatePrices();
            } else
                priceList = calculatePrices();

            for (int i = 0; i < priceList.size(); i++) {
                final CustomerPriceModel item = priceList.get(i);
                CustomerPriceModel customizedItem = null;
                if (persistCustomizedPrice)
                    customizedItem = Linq.findFirst(customizedPrices, new Linq.Criteria<CustomerPriceModel>() {
                        @Override
                        public boolean run(CustomerPriceModel customizedItem) {
                            return customizedItem.ProductUniqueId.equals(item.ProductUniqueId);
                        }
                    });
                if (customizedItem != null) {
                    item.Price = customizedItem.Price;
                    item.UserPrice = customizedItem.UserPrice;
                    item.UniqueId = customizedItem.UniqueId;
                    item.PriceId = CustomerPriceManager.CustomizedPrice;
                } else
                    item.UniqueId = UUID.randomUUID();
                if (!productUnitsViewModelHashMap.containsKey(item.ProductUniqueId))
                    Timber.d("product unit id not found for product " + item.ProductUniqueId);
                if (currentCustomerPriceList.get(item.ProductUniqueId) == null && productUnitsViewModelHashMap.containsKey(item.ProductUniqueId))
                    currentCustomerPriceList.put(item.ProductUniqueId, item);
            }

            final CustomerBatchPriceModelRepository batchRepository = new CustomerBatchPriceModelRepository();
            final HashMap<Integer, CustomerBatchPriceModel> currentCustomerBatchPriceList = new HashMap<>();
            batchRepository.delete(Criteria.equals(CustomerBatchPrice.CustomerUniqueId, customerId.toString()));
            Timber.i("customer batch prices delete.");
            List<CustomerBatchPriceModel> batchPriceList = calculateBatchPrices();
            if (batchPriceList != null && batchPriceList.size() > 0) {
                for (int i = 0; i < batchPriceList.size(); i++) {
                    CustomerBatchPriceModel item = batchPriceList.get(i);
                    item.UniqueId = UUID.randomUUID();
                    if (currentCustomerBatchPriceList.get(item.BatchRef) == null && productUnitsViewModelHashMap.containsKey(item.ProductUniqueId)) {
                        currentCustomerBatchPriceList.put(item.BatchRef, item);
                    }
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences("InStockProducts", Context.MODE_PRIVATE);
                boolean inStock = sharedPreferences.getBoolean("InStock", false);
                inStock = true; // tebghe sohbate aghaye ahmadi felan faghat batch hayi neshun dade mishe ke mojudi daran
                List<CustomerBatchPriceModel> customerBatchPriceModelArrayList = new ArrayList<>(currentCustomerBatchPriceList.values());
                for (int i = 0; i < customerBatchPriceModelArrayList.size(); i++) {
                    CustomerPriceModel customerPriceModel = currentCustomerPriceList.get(customerBatchPriceModelArrayList.get(i).ProductUniqueId);
                    if (customerPriceModel == null) {
                        CustomerPriceModel newCustomerPriceModel = new CustomerPriceModel();
                        newCustomerPriceModel.UniqueId = UUID.randomUUID();
                        newCustomerPriceModel.CallOrderId = callOrderId;
                        newCustomerPriceModel.CustomerUniqueId = customerBatchPriceModelArrayList.get(i).CustomerUniqueId;
                        newCustomerPriceModel.ProductUniqueId = customerBatchPriceModelArrayList.get(i).ProductUniqueId;
                        newCustomerPriceModel.Price = customerBatchPriceModelArrayList.get(i).Price;
                        newCustomerPriceModel.PriceId = customerBatchPriceModelArrayList.get(i).PriceId;
                        newCustomerPriceModel.UserPrice = customerBatchPriceModelArrayList.get(i).UserPrice;
                        List<ProductBatchOnHandQtyModel> batchList = new ProductBatchOnHandQtyManager(context).getProductBatches(newCustomerPriceModel.ProductUniqueId);
                        if (!SysConfigManager.compare(new SysConfigManager(context).read(ConfigKey.ShowNormalItmDetail, SysConfigManager.cloud), "0")) {
                            Collections.sort(batchList, new Comparator<ProductBatchOnHandQtyModel>() {
                                @Override
                                public int compare(ProductBatchOnHandQtyModel o1, ProductBatchOnHandQtyModel o2) {
                                    String exp1 = o1.ExpDate;
                                    String exp2 = o2.ExpDate;
                                    int res = String.CASE_INSENSITIVE_ORDER.compare(exp1, exp2);
                                    if (res == 0) {
                                        res = exp1.compareTo(exp2);
                                    }
                                    return res;
                                }
                            });
                        }
                        for (ProductBatchOnHandQtyModel batch:
                             batchList) {
                            CustomerBatchPriceModel newPrice = currentCustomerBatchPriceList.get(batch.BatchRef);
                            if (newPrice != null) {
                                if (inStock) {
                                    if (batch.OnHandQty.compareTo(BigDecimal.ZERO)>0) {
                                        newCustomerPriceModel.Price = newPrice.Price;
                                        newCustomerPriceModel.PriceId = newPrice.PriceId;
                                        newCustomerPriceModel.UserPrice = newPrice.UserPrice;
                                        break;
                                    }
                                } else {
                                    newCustomerPriceModel.Price = newPrice.Price;
                                    newCustomerPriceModel.PriceId = newPrice.PriceId;
                                    newCustomerPriceModel.UserPrice = newPrice.UserPrice;
                                    break;
                                }
                            }
                        }
                        currentCustomerPriceList.put(newCustomerPriceModel.ProductUniqueId, newCustomerPriceModel);
                    }
                }

                long l1 = batchRepository.insert(customerBatchPriceModelArrayList);
                Timber.i(customerBatchPriceModelArrayList.size() + " calculated and " + l1 + " saved. CustomerBatchPrice calculated successfully");
            }
            List<CustomerPriceModel> customerPriceModelArrayList = new ArrayList<>(currentCustomerPriceList.values());
            long l = 0;
            if (customerPriceModelArrayList.size()>0)
                l = repository.insert(customerPriceModelArrayList);
            Timber.i(customerPriceModelArrayList.size() + "calculated and " + l + " saved. CustomerPrice calculated successfully");

            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
            callback.onSucceeded();
        } catch (Exception e) {
            Timber.e("Error on delete customer price");
            callback.onFailed(e.getMessage());
        } finally {
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
        }
    }
}