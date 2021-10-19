package com.varanegar.vaslibrary.ui.fragment.productgroup;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class RecommendedProductsDialog extends CuteDialogWithToolbar {
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommended_products_layout, container, false);
        view.findViewById(R.id.close_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setTitle(getString(R.string.recommended_products));
        recyclerView = view.findViewById(R.id.emphatic_items_recycler_view);
        return view;
    }

    public interface OnOrderUpdate {
        void run(ProductOrderViewModel productOrderViewModel);
    }

    public OnOrderUpdate onOrderUpdate;
    private ArrayList<String> recommendedProductIds;
    private HashSet<UUID> set = new HashSet<>();
    private UUID customerId;
    private UUID callOrderId;
    private BaseRecyclerView recyclerView;
    private BaseRecyclerAdapter<ProductOrderViewModel> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            recommendedProductIds = bundle.getStringArrayList("RECOMMENDED_PRODUCT_IDS");
            for (String id :
                    recommendedProductIds) {
                set.add(UUID.fromString(id));
            }
            String customerIdStr = bundle.getString("CUSTOMER_ID", null);
            String callOrderIdStr = bundle.getString("ORDER_ID", null);
            if (customerIdStr != null)
                customerId = UUID.fromString(customerIdStr);
            if (callOrderIdStr != null)
                callOrderId = UUID.fromString(callOrderIdStr);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new BaseRecyclerAdapter<ProductOrderViewModel>(getVaranegarActvity()) {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_product_row_item, parent, false);
                return new ItemViewHolder(itemView, this, getContext());
            }
        };
        recyclerView.setAdapter(adapter);
        refreshAdapter();
    }

    private void refreshAdapter() {
        if (recommendedProductIds != null && recommendedProductIds.size() > 0) {
            List<ProductOrderViewModel> productOrderViewModels = new ProductOrderViewManager(getContext()).getItems(ProductOrderViewManager.getAll(null, customerId, callOrderId, null, true, false, null));
            List<ProductOrderViewModel> recommendedProducts = Linq.findAll(productOrderViewModels, new Linq.Criteria<ProductOrderViewModel>() {
                @Override
                public boolean run(ProductOrderViewModel item) {
                    return set.contains(item.UniqueId);
                }
            });
            if (recommendedProducts.size() > 0) {
                adapter.clear();
                adapter.addAll(recommendedProducts);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<ProductOrderViewModel>() {
                    @Override
                    public void run(int position) {
                        final ProductOrderViewModel productOrderViewModel = adapter.get(position);
                        CustomerCallOrderOrderViewModelRepository repository = new CustomerCallOrderOrderViewModelRepository();
                        CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = repository.getItem(
                                new Query().
                                        from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl).
                                        whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId.toString())
                                                .and(Criteria.equals(CustomerCallOrderOrderView.ProductId, productOrderViewModel.UniqueId)))
                        );
                        List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
                        OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
                        if (customerCallOrderOrderViewModel != null) {
                            orderLineQtyModels = orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
                        }

                        OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
                        try {
                            CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());

                            final OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                            ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
                            ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(productOrderViewModel.UniqueId);
                            onHandQtyStock.ConvertFactors = productUnitsViewModel.ConvertFactor;
                            onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
                            if (productOrderViewModel.OnHandQty == null)
                                productOrderViewModel.OnHandQty = BigDecimal.ZERO;
                            onHandQtyStock.OnHandQty = productOrderViewModel.OnHandQty;
                            if (productOrderViewModel.RemainedAfterReservedQty == null)
                                productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
                            onHandQtyStock.RemainedAfterReservedQty = productOrderViewModel.RemainedAfterReservedQty;
                            if (productOrderViewModel.OrderPoint == null)
                                productOrderViewModel.OrderPoint = BigDecimal.ZERO;
                            onHandQtyStock.OrderPoint = productOrderViewModel.OrderPoint;
                            if (productOrderViewModel.ProductTotalOrderedQty == null)
                                productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
                            onHandQtyStock.ProductTotalOrderedQty = productOrderViewModel.ProductTotalOrderedQty;
                            if (productOrderViewModel.TotalQty == null)
                                productOrderViewModel.TotalQty = BigDecimal.ZERO;
                            onHandQtyStock.TotalQty = productOrderViewModel.TotalQty;
                            onHandQtyStock.HasAllocation = productOrderViewModel.HasAllocation;
                            BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(customerCallOrderOrderViewModel);
                            if (productOrderViewModel.ExpDate == null)
                                orderCalculatorForm.setArguments(productOrderViewModel.UniqueId, productOrderViewModel.ProductName, calculatorHelper.generateCalculatorUnits(productOrderViewModel.UniqueId, orderLineQtyModels, bulkUnit, ProductType.isForSale), productOrderViewModel.Price, productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId);
                            else
                                orderCalculatorForm.setArguments(productOrderViewModel.UniqueId, productOrderViewModel.ProductName, CalculatorBatchUnits.generate(getContext(), productOrderViewModel, customerCallOrderOrderViewModel == null ? null : customerCallOrderOrderViewModel.UniqueId, productOrderViewModel.Price, productOrderViewModel.PriceId, productOrderViewModel.UserPrice), productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId);
                            orderCalculatorForm.onCalcFinish = new OrderCalculatorForm.OnCalcFinish() {
                                @Override
                                public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList) {
                                    onAddOrUpdateItem(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
                                }
                            };
                            orderCalculatorForm.show(getChildFragmentManager(), "2e55c4ab-f8cd-4b64-9de7-19dded141a4b");
                        } catch (ProductUnitViewManager.UnitNotFoundException e) {
                            getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                            Timber.e(e, getString(R.string.save_order_request));
                        }
                    }
                });

            }
        }

    }

    void onAddOrUpdateItem(final ProductOrderViewModel productOrderViewModel, final List<DiscreteUnit> discreteUnits, final BaseUnit bulkUnit, @Nullable final List<BatchQty> batchQtyList) {
        final OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
        onHandQtyStock.ConvertFactors = productOrderViewModel.ConvertFactor;
        ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
        ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(productOrderViewModel.UniqueId);
        onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
        if (productOrderViewModel.OnHandQty == null)
            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
        onHandQtyStock.OnHandQty = productOrderViewModel.OnHandQty;
        if (productOrderViewModel.RemainedAfterReservedQty == null)
            productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
        onHandQtyStock.RemainedAfterReservedQty = productOrderViewModel.RemainedAfterReservedQty;
        if (productOrderViewModel.OrderPoint == null)
            productOrderViewModel.OrderPoint = BigDecimal.ZERO;
        onHandQtyStock.OrderPoint = productOrderViewModel.OrderPoint;
        if (productOrderViewModel.ProductTotalOrderedQty == null)
            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
        onHandQtyStock.ProductTotalOrderedQty = productOrderViewModel.ProductTotalOrderedQty;
        onHandQtyStock.TotalQty = productOrderViewModel.TotalQty;
        onHandQtyStock.HasAllocation = productOrderViewModel.HasAllocation;

        try {
            ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock, discreteUnits, bulkUnit);
            add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
        } catch (OnHandQtyWarning e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.warning);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
                    } catch (Exception e) {
                        Timber.e(e);
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.error_saving_request);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            });
            dialog.setPositiveButton(R.string.cancel, null);
            dialog.show();
        } catch (OnHandQtyError e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }

    }

    private void add(ProductOrderViewModel productOrderViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, List<BatchQty> batchQtyList) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        callOrderLineManager.addOrUpdateQty(productOrderViewModel.UniqueId, discreteUnits, bulkUnit, callOrderId, null, batchQtyList, false);
        ProductOrderViewModel updatedProductOrderViewModel = new ProductOrderViewManager(getContext()).getLine(customerId, callOrderId, productOrderViewModel.UniqueId, false);
        update(updatedProductOrderViewModel);
    }

    protected void update(ProductOrderViewModel productOrderViewModel) {
        refreshAdapter();
        if (onOrderUpdate != null)
            onOrderUpdate.run(productOrderViewModel);
    }


    private class ItemViewHolder extends BaseViewHolder<ProductOrderViewModel> {


        private final TextView productNameTextView;
        private final TextView priceTextView;
        private final TextView qtyTextView;

        public ItemViewHolder(View itemView, BaseRecyclerAdapter<ProductOrderViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            qtyTextView = itemView.findViewById(R.id.qty_text_view);
        }

        @Override
        public void bindView(final int position) {
            ProductOrderViewModel productOrderViewModel = adapter.get(position);
            if (productOrderViewModel != null) {
                productNameTextView.setText(productOrderViewModel.ProductCode + " " + productOrderViewModel.ProductName);
                priceTextView.setText(VasHelperMethods.currencyToString(productOrderViewModel.Price));
                qtyTextView.setText(productOrderViewModel.Qty);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.runItemClickListener(position);
                    }
                });
            }
        }


    }
}
