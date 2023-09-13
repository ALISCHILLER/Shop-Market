package com.varanegar.vaslibrary.ui.fragment.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.ContextMenuItemRaw;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.OrderOption;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;
import com.varanegar.vaslibrary.ui.fragment.productgroup.ProductGroupFragment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class OrderAdapter extends BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> {
    private final UUID customerId;
    private final UUID callOrderId;

    public CustomerSaveOrderFragment.OnItemQtyChangedHandler getOnItemQtyChangedHandler() {
        return onItemQtyChangedHandler;
    }

    private final CustomerSaveOrderFragment.OnItemQtyChangedHandler onItemQtyChangedHandler;

    public HashMap<UUID, ProductUnitsViewModel> getProductUnitsHashMap() {
        return productUnitsHashMap;
    }

    public HashMap<UUID, ProductUnitViewManager.ProductUnits> getProductUnits() {
        return productUnits;
    }

    public CustomerCallOrderModel getCallOrderModel() {
        return callOrderModel;
    }

    private final HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap;
    private final HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnits;
    private final CustomerCallOrderModel callOrderModel;

    public OrderAdapter(@NonNull CustomerSaveOrderFragment fragment,
                        CustomerCallOrderModel callOrderModel,
                        HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap,
                        HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnits,
                        OrderOption<CustomerCallOrderOrderViewModel> item,
                        CustomerSaveOrderFragment.OnItemQtyChangedHandler onItemQtyChangedHandler) {

        super((VaranegarActivity) fragment.getActivity(),
                new CustomerCallOrderOrderViewModelRepository(),
                new CustomerCallOrderOrderViewManager(fragment.getContext()).getLinesQuery(callOrderModel.UniqueId, new OrderBy(item.getProjection(), OrderType.ASC)));
        this.callOrderModel = callOrderModel;
        customerId = callOrderModel.CustomerUniqueId;
        callOrderId = callOrderModel.UniqueId;
        this.productUnitsHashMap = productUnitsHashMap;
        this.productUnits = productUnits;
        this.onItemQtyChangedHandler = onItemQtyChangedHandler;

        if ((!VaranegarApplication.is(VaranegarApplication.AppId.Dist) || !callOrderModel.IsInvoice))
            addContextMenuItem(new ContextMenuItem() {
                @Override
                public boolean isAvailable(int position) {
                    CustomerCallOrderOrderViewModel orderViewModel = get(position);
                    return !orderViewModel.IsPromoLine && orderViewModel.cart.isEmpty();
                }

                @Override
                public String getName(int position) {
                    return getActivity().getString(R.string.edit);
                }

                @Override
                public int getIcon(int position) {
                    return R.drawable.ic_mode_edit_black_24dp;
                }

                @Override
                public void run(int position) {
                    final CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = get(position);
                    OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getActivity());
                    List<OrderLineQtyModel> orderLineQtyModels = orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
                    OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
                    try {
                        CalculatorHelper calculatorHelper = new CalculatorHelper(getActivity());

                        OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                        ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getActivity());
                        ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(customerCallOrderOrderViewModel.ProductId);
                        onHandQtyStock.ConvertFactors = productUnitsViewModel.ConvertFactor;
                        onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
                        if (customerCallOrderOrderViewModel.OnHandQty == null)
                            customerCallOrderOrderViewModel.OnHandQty = BigDecimal.ZERO;
                        onHandQtyStock.OnHandQty = customerCallOrderOrderViewModel.OnHandQty;
                        if (customerCallOrderOrderViewModel.RemainedAfterReservedQty == null)
                            customerCallOrderOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
                        onHandQtyStock.RemainedAfterReservedQty = customerCallOrderOrderViewModel.RemainedAfterReservedQty;
                        if (customerCallOrderOrderViewModel.OrderPoint == null)
                            customerCallOrderOrderViewModel.OrderPoint = BigDecimal.ZERO;
                        onHandQtyStock.OrderPoint = customerCallOrderOrderViewModel.OrderPoint;
                        if (customerCallOrderOrderViewModel.ProductTotalOrderedQty == null)
                            customerCallOrderOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
                        onHandQtyStock.ProductTotalOrderedQty = customerCallOrderOrderViewModel.ProductTotalOrderedQty;
                        if (customerCallOrderOrderViewModel.RequestBulkQty == null)
                            onHandQtyStock.TotalQty = customerCallOrderOrderViewModel.TotalQty == null ? BigDecimal.ZERO : customerCallOrderOrderViewModel.TotalQty;
                        else
                            onHandQtyStock.TotalQty = customerCallOrderOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : customerCallOrderOrderViewModel.TotalQtyBulk;
                        onHandQtyStock.HasAllocation = customerCallOrderOrderViewModel.HasAllocation;
                        BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(customerCallOrderOrderViewModel);
                        if (customerCallOrderOrderViewModel.ExpDate == null)
                            orderCalculatorForm.setArguments(customerCallOrderOrderViewModel.ProductId,
                                    customerCallOrderOrderViewModel.ProductName,
                                    calculatorHelper.generateCalculatorUnits(customerCallOrderOrderViewModel.ProductId,
                                            orderLineQtyModels, bulkUnit, ProductType.isForSale),
                                    customerCallOrderOrderViewModel.UnitPrice,
                                    customerCallOrderOrderViewModel.UserPrice, onHandQtyStock,
                                    customerId,
                                    callOrderId,"");
                        else {
                            ProductOrderViewModel productOrderViewModel = new
                                    ProductOrderViewManager(getActivity()).getLine(customerId,
                                    callOrderId,
                                    customerCallOrderOrderViewModel.ProductId,
                                    false);


                            orderCalculatorForm.setArguments(customerCallOrderOrderViewModel.ProductId,
                                    customerCallOrderOrderViewModel.ProductName,
                                    CalculatorBatchUnits.generate(getActivity(),
                                            productOrderViewModel,
                                            customerCallOrderOrderViewModel == null ? null
                                                    : customerCallOrderOrderViewModel.UniqueId,
                                            productOrderViewModel.Price,
                                            productOrderViewModel.PriceId,
                                            productOrderViewModel.UserPrice),
                                    customerCallOrderOrderViewModel.UserPrice,
                                    onHandQtyStock,
                                    customerId,
                                    callOrderId,productOrderViewModel.PrizeComment);
                        }

                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                            orderCalculatorForm.setMaxValue(customerCallOrderOrderViewModel.OriginalTotalQty);

                        orderCalculatorForm.onCalcFinish = (discreteUnits, bulkUnit1, batchQtyList) ->
                                fragment.onEditItem(customerCallOrderOrderViewModel, discreteUnits, bulkUnit1, batchQtyList);
                        orderCalculatorForm.show(fragment.getChildFragmentManager(), "a383a846-118d-4161-a4ea-3d1518468d1e");
                    } catch (ProductUnitViewManager.UnitNotFoundException ex) {
                        MainVaranegarActivity activity = (MainVaranegarActivity) getActivity();
                        if (activity != null && !activity.isFinishing() && fragment.isResumed()) {
                            activity.showSnackBar(com.varanegar.vaslibrary.R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                        }
                        Timber.e(ex, "product unit not found in save order fragment");
                    }

                }
            });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            addContextMenuItem(new ContextMenuItem() {
                @Override
                public boolean isAvailable(int position) {
                    return true;
                }

                @Override
                public String getName(int position) {
                    return getActivity().getString(R.string.remove);
                }

                @Override
                public int getIcon(int position) {
                    return R.drawable.ic_delete_forever_black_24dp;
                }

                @Override
                public void run(final int position) {
                    final CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = get(position);
                    if (customerCallOrderOrderViewModel != null) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setIcon(Icon.Warning);
                        dialog.setPositiveButton(R.string.yes, v -> {
                            CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getActivity());
                            try {
                                callOrderLineManager.delete(customerCallOrderOrderViewModel.UniqueId);
                                fragment.refresh(true, false, true);
                            } catch (DbException e) {
                                Timber.e(e);
                            }
                        });
                        dialog.setNegativeButton(R.string.no, null);
                        dialog.setMessage(R.string.are_you_sure);
                        dialog.show();
                    }
                }
            });
        addContextMenuItem(new ContextMenuItemRaw() {
            @Override
            public boolean isAvailable(int position) {
                return true;
            }

            @Nullable
            @Override
            protected View onCreateView(int position, View convertView, ViewGroup parent) {
                CustomerCallOrderOrderViewModel orderOrderViewModel = get(position);
                View view = LayoutInflater.from(convertView.getContext()).inflate(R.layout.order_line_description, parent, false);
                EditText editText = view.findViewById(R.id.edit_text);
                editText.setText(orderOrderViewModel.Description);
                editText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        save(orderOrderViewModel, v.getText().toString());
                    }
                    return false;
                });
                view.findViewById(R.id.save_btn).setOnClickListener(v -> save(orderOrderViewModel, editText.getText().toString()));
                return view;
            }

            void save(CustomerCallOrderOrderViewModel orderOrderViewModel, String desc) {
                try {
                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getActivity());
                    CallOrderLineModel line = callOrderLineManager.getItem(orderOrderViewModel.UniqueId);
                    line.Description = desc;
                    callOrderLineManager.update(line);
                    refresh();
                    getContextItemDialog().dismissAllowingStateLoss();
                } catch (Exception e) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.error_saving_request);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.close, null);
                    dialog.show();
                }
            }


        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == 0) {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(com.varanegar.vaslibrary.R.layout.simple_row_customer_order_order_view, parent, false);
//            return new SimpleCustomerOrderLineViewHolder(itemView, this, getActivity());
//        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(com.varanegar.vaslibrary.R.layout.row_customer_order_order_view, parent, false);
            return new CustomerOrderLineViewHolder(itemView, this, getActivity());
//        }
    }

    @Override
    protected ItemContextView<CustomerCallOrderOrderViewModel> onCreateContextView() {
        return new CustomerOrderContextView(this, getActivity());
    }

    @Override
    public int getItemViewType(int position) {
        CustomerCallOrderOrderViewModel line = get(position);
        if (line == null)
            return 1;
        if (line.IsFreeItem)
            return 1;

        ProductUnitViewManager.ProductUnits pu = productUnits.get(line.ProductId);
        if (pu == null)
            return 1;
        return line.ExpDate == null && !pu.hasThirdUnit() && !pu.isBulk() ? 0 : 1;
    }
}
