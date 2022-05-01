package com.varanegar.vaslibrary.ui.fragment.order;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.emphaticitems.CustomerEmphaticPackageViewManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphasisProductErrorTypeId;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticPackageCheckResult;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticProductManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.customeremphaticpackageview.CustomerEmphaticPackageViewModel;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderView;
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
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/17/2017.
 */

public class EmphaticProductDialog extends CuteAlertDialog {
    private UUID customerId;
    private EmphaticItemsRecyclerAdapter adapter;
    private List<ProductOrderViewModel> lines;
    private EmphaticPackageCheckResult packageResult;

    public UUID getCallOrderId() {
        return callOrderId;
    }

    private UUID callOrderId;
    private ProductOrderViewManager manager;

    public interface OnUserAccept {
        void run();
    }

    public OnUserAccept onUserAccept;

    public interface OnOrderUpdate {
        void run();
    }

    public OnOrderUpdate onOrderUpdate;

    protected void update() {
        adapter.clear();
        List<ProductOrderViewModel> lines = manager.getItems(ProductOrderViewManager.getAllEmphaticItems(null, customerId, callOrderId, null, null, new OrderBy(ProductOrderView.EmphaticPriority, OrderType.DESC)));
        adapter.addAll(convertItems(lines));
        if (onOrderUpdate != null)
            onOrderUpdate.run();
    }

    private List<EmphaticItemViewModel> convertItems(List<ProductOrderViewModel> lines) {
        lines = Linq.findAll(lines, new Linq.Criteria<ProductOrderViewModel>() {
            @Override
            public boolean run(ProductOrderViewModel item) {
                if (item.EmphaticProductCount == null)
                    item.EmphaticProductCount = BigDecimal.ZERO;
                return item.EmphaticProductCount.compareTo(BigDecimal.ZERO) > 0;
            }
        });
        List<EmphaticItemViewModel> items = Linq.map(lines, new Linq.Map<ProductOrderViewModel, EmphaticItemViewModel>() {
            @Override
            public EmphaticItemViewModel run(ProductOrderViewModel item) {
                EmphaticItemViewModel em = new EmphaticItemViewModel();
                em.EmphaticCount = item.EmphaticProductCount;
                em.EmphaticType = item.EmphaticType;
                em.ItemName = item.ProductName;
                em.TotalQty = item.TotalQty;
                em.UniqueId = item.UniqueId;
                return em;
            }
        });
        return items;
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        customerId = UUID.fromString(getArguments().getString("d55e60a5-f997-406a-b420-b015488c22a1"));
        callOrderId = UUID.fromString(getArguments().getString("dee377db-d44a-4021-93fc-9792d620b88b"));
        setTitle(getString(R.string.emphatic_items));
        View view = inflater.inflate(R.layout.emphatic_products_alert_dialog, viewGroup, true);
        BaseRecyclerView recyclerView = view.findViewById(R.id.emphatic_items_recycler_view);
        BaseRecyclerView packagesRcyclerView = view.findViewById(R.id.emphatic_packages_recycler_view);

        // items
        adapter = new EmphaticItemsRecyclerAdapter(getVaranegarActvity());
        manager = new ProductOrderViewManager(getContext());
        lines = manager.getItems(ProductOrderViewManager.getAllEmphaticItems(null, customerId, callOrderId, null, null, new OrderBy(ProductOrderView.EmphaticPriority, OrderType.DESC)));
        adapter.addAll(convertItems(lines));
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<EmphaticItemViewModel>() {
            @Override
            public void run(final int position) {
                final ProductOrderViewModel productOrderViewModel = Linq.findFirst(lines, new Linq.Criteria<ProductOrderViewModel>() {
                    @Override
                    public boolean run(ProductOrderViewModel item) {
                        return item.UniqueId.equals(adapter.get(position).UniqueId);
                    }
                });
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
                    if (productOrderViewModel.RequestBulkQty == null)
                        onHandQtyStock.TotalQty = productOrderViewModel.TotalQty == null ? BigDecimal.ZERO : productOrderViewModel.TotalQty;
                    else
                        onHandQtyStock.TotalQty = productOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : productOrderViewModel.TotalQtyBulk;
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
        recyclerView.setAdapter(adapter);

        // packages
        CustomerEmphaticPackageViewManager packageViewManager = new CustomerEmphaticPackageViewManager(getContext());
        CustomerCallOrderOrderViewManager callOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
        List<CustomerCallOrderOrderViewModel> orderLines = callOrderOrderViewManager.getLines(callOrderId, null);
        packageResult = packageViewManager.checkEmphaticPackages(customerId, orderLines);
        List<CustomerEmphaticPackageViewModel> packageRules = packageViewManager.getPackageRules(customerId);
        List<EmphaticItemViewModel> packages = Linq.map(packageRules, new Linq.Map<CustomerEmphaticPackageViewModel, EmphaticItemViewModel>() {
            @Override
            public EmphaticItemViewModel run(CustomerEmphaticPackageViewModel item) {
                EmphaticItemViewModel em = new EmphaticItemViewModel();
                em.UniqueId = item.UniqueId;
                em.ItemName = item.Title;
                BigDecimal qty = packageResult.getQtys().get(item.RuleId);
                em.TotalQty = qty == null ? BigDecimal.ZERO : qty;
                if (item.TypeId.equals(EmphasisProductErrorTypeId.DETERRENT))
                    em.EmphaticType = EmphasisType.Deterrent;
                else if (item.TypeId.equals(EmphasisProductErrorTypeId.WARNING))
                    em.EmphaticType = EmphasisType.Warning;
                else
                    em.EmphaticType = EmphasisType.Suggestion;
                em.EmphaticCount = item.PackageCount;
                return em;
            }
        });
        EmphaticItemsRecyclerAdapter packagesAdapter = new EmphaticItemsRecyclerAdapter(getVaranegarActvity());
        packagesAdapter.addAll(packages);
        packagesRcyclerView.setAdapter(packagesAdapter);
    }

    void onAddOrUpdateItem(final ProductOrderViewModel productOrderViewModel,
                           final List<DiscreteUnit> discreteUnits,
                           final BaseUnit bulkUnit,
                           @Nullable final List<BatchQty> batchQtyList) {
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
        if (productOrderViewModel.RequestBulkQty == null)
            onHandQtyStock.TotalQty = productOrderViewModel.TotalQty == null ? BigDecimal.ZERO : productOrderViewModel.TotalQty;
        else
            onHandQtyStock.TotalQty = productOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : productOrderViewModel.TotalQtyBulk;
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
            dialog.setNegativeButton(R.string.cancel, null);
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
        callOrderLineManager.addOrUpdateQty(productOrderViewModel.UniqueId, discreteUnits, bulkUnit, getCallOrderId(), null, batchQtyList, false);
        update();
    }

    @Override
    public void ok() {
        List<EmphaticItemViewModel> lines = convertItems(manager.getItems(ProductOrderViewManager.getAllEmphaticItems(null, customerId, callOrderId, null, null, new OrderBy(ProductOrderView.EmphaticPriority, OrderType.DESC))));
        String error = packageResult.getError();
        for (EmphaticItemViewModel line :
                lines) {
            if (line.TotalQty == null || line.TotalQty.compareTo(line.EmphaticCount) < 0) {
                if (line.EmphaticType == EmphasisType.Deterrent) {
                    if (error == null)
                        error = "";
                    error += getString(R.string.emphatic_product_error, line.ItemName, line.EmphaticCount, VasHelperMethods.bigDecimalToString(line.TotalQty)) + "\n";
                }
            }
        }
        if (error == null) {
            dismiss();
            if (onUserAccept != null)
                onUserAccept.run();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(error);
            builder.setTitle(R.string.error);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
        }
    }

    @Override
    public void cancel() {
        dismiss();
    }

    private class EmphaticItemViewModel {

        public String ItemName;
        public BigDecimal TotalQty;
        public BigDecimal EmphaticCount;
        public EmphasisType EmphaticType;
        public UUID UniqueId;
    }

    private class EmphaticItemsRecyclerAdapter extends BaseRecyclerAdapter<EmphaticItemViewModel> {

        public EmphaticItemsRecyclerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emphatic_item, parent, false);
            return new EmphaticItemViewHolder(itemView, this, getContext());
        }
    }

    private class EmphaticItemViewHolder extends BaseViewHolder<EmphaticItemViewModel> {

        private final TextView productTextView;
        private final TextView orderQtyTextView;
        private final TextView targetQtyTextView;
        private final TextView typeTextView;
        private final EmphaticProductManager manager;

        public EmphaticItemViewHolder(View itemView, BaseRecyclerAdapter<EmphaticItemViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            productTextView = itemView.findViewById(R.id.product_text_view);
            orderQtyTextView = itemView.findViewById(R.id.order_qty_text_view);
            targetQtyTextView = itemView.findViewById(R.id.target_qty_text_view);
            typeTextView = itemView.findViewById(R.id.type_text_view);
            manager = new EmphaticProductManager(getContext());
        }

        @Override
        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerAdapter.runItemClickListener(position);
                }
            });
            EmphaticItemViewModel itemViewModel = recyclerAdapter.get(position);
            productTextView.setText(itemViewModel.ItemName);
            orderQtyTextView.setText(itemViewModel.TotalQty == null ? "0" : itemViewModel.TotalQty.toString());
            targetQtyTextView.setText(String.valueOf(itemViewModel.EmphaticCount));
            typeTextView.setText(manager.getTypeName(itemViewModel.EmphaticType));
            if (itemViewModel.EmphaticType == EmphasisType.Deterrent) {
                if (itemViewModel.TotalQty == null)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.red));
                else if (itemViewModel.TotalQty.compareTo(itemViewModel.EmphaticCount) < 0)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.red));
                else
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.green));
            } else if (itemViewModel.EmphaticType == EmphasisType.Suggestion) {
                if (itemViewModel.TotalQty == null)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
                else if (itemViewModel.TotalQty.compareTo(itemViewModel.EmphaticCount) < 0)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
                else
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.green));
            } else if (itemViewModel.EmphaticType == EmphasisType.Warning) {
                if (itemViewModel.TotalQty == null)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.orange_light));
                else if (itemViewModel.TotalQty.compareTo(itemViewModel.EmphaticCount) < 0)
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.orange_light));
                else
                    orderQtyTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.green));
            }
        }
    }
}
