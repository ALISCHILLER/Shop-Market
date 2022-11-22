package com.varanegar.vaslibrary.ui.fragment.productgroup;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.filter.Filter;
import com.varanegar.framework.util.filter.FiltersAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.ContextMenuItemRaw;
import com.varanegar.framework.util.recycler.expandablerecycler.ChildRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerView;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.OrderOption;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesTempManager;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTemp;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailView;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModelRepository;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderView;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.FreeOrderCalculatorFrom;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.FreeOrderItem;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;
import com.varanegar.vaslibrary.ui.viewholders.ChildProductGroupViewHolder;
import com.varanegar.vaslibrary.ui.viewholders.ProductGroupViewHolder;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product.CustomerNotAllowProductManager;
import com.varanegar.vaslibrary.webapi.device.CompanyDeviceAppResult;
import com.varanegar.vaslibrary.webapi.device.DeviceApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.rs.RecSysConfig;
import com.varanegar.vaslibrary.webapi.rs.RecSysException;
import com.varanegar.vaslibrary.webapi.rs.RecommendedProductModel;
import com.varanegar.vaslibrary.webapi.rs.RecommenderSystemApi;
import com.varanegar.vaslibrary.webapi.tracking.CompanyDeviceAppData;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

import static com.varanegar.vaslibrary.manager.updatemanager.UpdateManager.MIN_DATE;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.Average;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.EmphaticPriority;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.Price;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.ProductCode;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.ProductName;
import static com.varanegar.vaslibrary.model.productorderview.ProductOrderView.TotalQty;

/**
 * Created by s.foroughi on 14/02/2017.
 */

public class ProductGroupFragment extends VisitFragment {

    private View recommendBtn;
    private View recProgress;
    int possitonitem;
    String productNumber;
    enum OrderLineViewType {
        Calculator,
        SimpleMode
    }

    private CheckBox inStockCheckBox;
    private ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel> groupsAdapter;
    private PairedItemsSpinner<OrderOption<ProductOrderViewModel>> orderBySpinner;
    private TextView orderCostTextView, usanceDayTextView, usanceDayLable;
    protected BaseRecyclerAdapter<ProductOrderViewModel> productsAdapter;
    protected UUID callOrderId;
    protected UUID orderTypeId;
    protected UUID customerId;
    FiltersAdapter filtersAdapter;
    Filter allFilter;
    Filter freeFilter;
    Filter emphaticFilter;
    Filter unSoldFilter;
    public boolean ascending = false;
    private ArrayList<OrderOption<ProductOrderViewModel>> orderOptions;
    private ImageView searchImageView;
    private View toolbarLayout;
    private View searchLayout;
    private EditText searchEditText;
    private UUID[] selectedGroupIds = new UUID[0];
    private List<ProductOrderViewModel> productsList = new ArrayList<>();
    private List<ProductOrderViewModel> filteredProductsList = new ArrayList<>();
    private FilterType selectedFilter;
    private boolean inStock;
    private String keyWord;
    private BaseRecyclerView productsRecyclerView;
    public static String[] splits;
    private View fragmentView;
    private SysConfigModel showStockLevel;
    private SysConfigModel orderPointCheckType;
    private SysConfigModel customerStockCheckType;
    private SysConfigModel checkCustomerStock;
    private boolean showAverageFactor;
    private HashMap<UUID, ProductUnitsViewModel> productUnitsViewModelHashMap;
    private ConfigMap sysConfigMap;
    SharedPreferences sharedPreferences;
    private OnItemQtyChangedHandler onItemQtyChangedHandler;
    //    private ProductSimpleOrderViewHolder.OnItemClickedListener onItemClickedListener;
    private ProductSimpleOrderViewHolder.OnPriceChanged onPriceChangedListener;
    private ProductOrderViewManager productOrderViewManager;
    private ArrayList<String> recommendedProductIds;
    private FloatingActionButton fabVoice;

    private boolean isContractor() {
        return VaranegarApplication.is(VaranegarApplication.AppId.Contractor);
//        SysConfigModel sysConfigModel = new SysConfigManager(getContext())
//        .read(ConfigKey.SimplePresale, SysConfigManager.cloud);
//        return SysConfigManager.compare(sysConfigModel, true);
    }

    void onAdd(final int position, final UUID productId, final List<DiscreteUnit> discreteUnits,
               final BaseUnit bulkUnit, @Nullable final FreeReasonModel freeReason,
               @Nullable final List<BatchQty> batchQtyList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProductOrderViewModel productOrderViewModel = productOrderViewManager
                        .getItem(ProductOrderViewManager.get(productId, customerId, callOrderId,
                                freeReason != null));
                if (productOrderViewModel == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Timber.e(productId + " not found");
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Error);
                            dialog.setMessage(R.string.error_saving_request);
                            dialog.setTitle(R.string.error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        }
                    });
                    return;
                }

                VaranegarApplication.getInstance().resetElapsedTime("start to save order");
                OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                onHandQtyStock.OnHandQty = productOrderViewModel.OnHandQty;
                onHandQtyStock.RemainedAfterReservedQty = productOrderViewModel.RemainedAfterReservedQty;
                onHandQtyStock.OrderPoint = productOrderViewModel.OrderPoint;
                onHandQtyStock.ProductTotalOrderedQty = productOrderViewModel.ProductTotalOrderedQty;
                if (productOrderViewModel.RequestBulkQty == null)
                    onHandQtyStock.TotalQty = productOrderViewModel.TotalQty == null ? BigDecimal.ZERO : productOrderViewModel.TotalQty;
                else
                    onHandQtyStock.TotalQty = productOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : productOrderViewModel.TotalQtyBulk;
                onHandQtyStock.HasAllocation = productOrderViewModel.HasAllocation;

                try {
                    ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock,
                            discreteUnits, bulkUnit);

                    //manager customerid productid
                    CustomerNotAllowProductManager.checkNotAllowed(getContext()
                            , customerId, productId);

                    add();
                } catch (final OnHandQtyWarning e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Timber.e(e);
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Warning);
                            dialog.setMessage(e.getMessage());
                            dialog.setTitle(R.string.warning);
                            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        add();
                                    } catch (Exception e) {
                                        Timber.e(e);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                                dialog.setIcon(Icon.Error);
                                                dialog.setMessage(R.string.error_saving_request);
                                                dialog.setTitle(R.string.error);
                                                dialog.setPositiveButton(R.string.ok, null);
                                                dialog.show();
                                            }
                                        });
                                    }
                                }
                            });
                            dialog.setPositiveButton(R.string.cancel, null);
                            dialog.show();
                        }
                    });
                } catch (final OnHandQtyError e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Timber.e(e);
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Error);
                            dialog.setMessage(e.getMessage());
                            dialog.setTitle(R.string.error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        }
                    });
                } catch (Exception e) {
                    Timber.e(e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Error);
                            dialog.setMessage(R.string.error_saving_request);
                            dialog.setTitle(R.string.error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        }
                    });
                }
            }

            private void add() throws ValidationException, DbException {
                CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                callOrderLineManager.addOrUpdateQty(productId, discreteUnits, bulkUnit, callOrderId,
                        freeReason, batchQtyList, false);
                final ProductOrderViewModel updatedProductOrderViewModel = productOrderViewManager
                        .getItem(ProductOrderViewManager.get(productId, customerId, callOrderId,
                                freeReason != null));
                VaranegarApplication.getInstance().printElapsedTime("save order line");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshAdapter(position, updatedProductOrderViewModel, true);
                        refreshTotalPrice();
                        VaranegarApplication.getInstance().printElapsedTime("refresh order view");
                    }
                });
            }
        }).start();
    }

    public void setArguments(String callOrderId, String customerId, String orderTypeId) {
        Bundle bundle = new Bundle();
        bundle.putString("1c886632-a88a-4e73-9164-f6656c219917", callOrderId);
        bundle.putString("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId);
        bundle.putString("b505233a-aaec-4c3c-a7ec-8fa08b940e74", orderTypeId);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callOrderId = UUID.fromString(getArguments().getString("1c886632-a88a-4e73-9164-f6656c219917"));
        customerId = UUID.fromString(getArguments().getString("3af8c4e9-c5c7-4540-8678-4669879caa79"));
        orderTypeId = UUID.fromString(getArguments().getString("b505233a-aaec-4c3c-a7ec-8fa08b940e74"));
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        sysConfigMap = sysConfigManager.read(SysConfigManager.cloud);
        showStockLevel = sysConfigMap.get(ConfigKey.ShowStockLevel);
        orderPointCheckType = sysConfigMap.get(ConfigKey.OrderPointCheckType);
        customerStockCheckType = sysConfigMap.get(ConfigKey.CustomerStockCheckType);
        checkCustomerStock = sysConfigMap.get(ConfigKey.CheckCustomerStock);
        productOrderViewManager = new ProductOrderViewManager(getContext());
        sharedPreferences = getContext().getSharedPreferences("SortProducts", Context.MODE_PRIVATE);
        SysConfigModel simplePresale = sysConfigManager.read(ConfigKey.SimplePresale, SysConfigManager.cloud);
        final boolean isSimpleMode = SysConfigManager.compare(simplePresale, true) || VaranegarApplication.is(VaranegarApplication.AppId.Contractor);
        onItemQtyChangedHandler = new OnItemQtyChangedHandler() {
            @Override
            public boolean run(ProductOrderViewModel productOrderViewModel, QtyChange change) {
                try {
                    OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
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
                    ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock, change.discreteUnits, null);
                    //manager customerid productid
                    CustomerNotAllowProductManager.checkNotAllowed(getContext()
                            , customerId, productOrderViewModel.UniqueId);
                    add(productOrderViewModel, change);
                    return true;
                } catch (OnHandQtyWarning e) {
                    Timber.e(e);
                    productsAdapter.notifyItemChanged(change.position);
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Warning);
                    dialog.setMessage(e.getMessage());
                    dialog.setTitle(R.string.warning);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    try {
                        add(productOrderViewModel, change);
                        return true;
                    } catch (Exception validationException) {
                        Timber.e(e);
                        productsAdapter.notifyItemChanged(change.position);
                        CuteMessageDialog dialog2 = new CuteMessageDialog(getContext());
                        dialog2.setIcon(Icon.Error);
                        dialog2.setMessage(R.string.error_saving_request);
                        dialog2.setTitle(R.string.error);
                        dialog2.setPositiveButton(R.string.ok, null);
                        dialog2.show();
                        return false;
                    }

                } catch (OnHandQtyError e) {
                    Timber.e(e);
                    productsAdapter.notifyItemChanged(change.position);
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(e.getMessage());
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    return false;
                } catch (Exception e) {
                    Timber.e(e);
                    productsAdapter.notifyItemChanged(change.position);
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.error_saving_request);
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    return false;
                }

            }

            private void add(ProductOrderViewModel productOrderViewModel, QtyChange change) throws ValidationException, DbException {
                CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                callOrderLineManager.addOrUpdateQty(productOrderViewModel.UniqueId, change.discreteUnits, null, callOrderId, null, null, isSimpleMode);
                ProductOrderViewModel updatedProductOrderViewModel = productOrderViewManager.getItem(ProductOrderViewManager.get(change.productId, customerId, callOrderId, false));
                if (updatedProductOrderViewModel.TotalQty == null || updatedProductOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) == 0)
                    callOrderLineManager.deleteProduct(callOrderId, change.productId, false);
                updatedProductOrderViewModel = productOrderViewManager.getItem(ProductOrderViewManager.get(change.productId, customerId, callOrderId, false));
                refreshAdapter(change.position, updatedProductOrderViewModel, true);
                refreshTotalPrice();
                VaranegarApplication.getInstance().printElapsedTime("refresh order view");
            }
        };
//        onItemClickedListener = new ProductSimpleOrderViewHolder.OnItemClickedListener() {
//            @Override
//            public void run(final int position, DiscreteUnit selected) {
//                final ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
//                if (productOrderViewModel.UniqueId == null) {
//                    Timber.wtf("ProductUnitId of product order view is null");
//                } else {
//                    CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = new CustomerCallOrderOrderViewModelRepository().getItem(
//                            new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
//                                    .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId))
//                                    .whereAnd(Criteria.equals(CustomerCallOrderOrderView.ProductId, productOrderViewModel.UniqueId))
//                                    .whereAnd(Criteria.equals(CustomerCallOrderOrderView.IsRequestFreeItem, freeFilter.selected))
//                    );
//                    List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
//                    OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
//                    if (customerCallOrderOrderViewModel != null) {
//                        orderLineQtyModels =
//                                orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
//                    }
//                    OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
//                    try {
//                        CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
//                        CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(customerCallOrderOrderViewModel.ProductId, orderLineQtyModels, null, ProductType.isForSale);
//                        for (DiscreteUnit discreteUnit :
//                                calculatorUnits.getDiscreteUnits()) {
//                            if (discreteUnit.ProductUnitId.equals(selected.ProductUnitId))
//                                discreteUnit.IsDefault = true;
//                            else
//                                discreteUnit.IsDefault = false;
//                        }
//                        orderCalculatorForm.setArguments(customerCallOrderOrderViewModel.ProductId, customerCallOrderOrderViewModel.ProductName, calculatorUnits, productOrderViewModel.Price, productOrderViewModel.UserPrice, null, customerId, callOrderId);
//                        orderCalculatorForm.onCalcFinish = new OrderCalculatorForm.OnCalcFinish() {
//                            @Override
//                            public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList) {
//                                try {
//                                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
//                                    callOrderLineManager.addOrUpdateQty(discreteUnits, null, callOrderId, null, null);
//                                    final ProductOrderViewModel updatedProductOrderViewModel = productOrderViewManager.getItem(ProductOrderViewManager.get(productOrderViewModel.UniqueId, customerId, callOrderId, false));
//                                    VaranegarApplication.getInstance().printElapsedTime("save order line");
//                                    refreshAdapter(position, updatedProductOrderViewModel);
//                                    refreshTotalPrice();
//                                    VaranegarApplication.getInstance().printElapsedTime("refresh order view");
//                                } catch (Exception e) {
//                                    productsAdapter.notifyItemChanged(position);
//                                    Timber.e(e);
//                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
//                                    dialog.setIcon(Icon.Error);
//                                    dialog.setMessage(R.string.error_saving_request);
//                                    dialog.setTitle(R.string.error);
//                                    dialog.setPositiveButton(R.string.ok, null);
//                                    dialog.show();
//
//
//                                }
//
//
//                            }
//                        };
//
//                        orderCalculatorForm.show(getChildFragmentManager(), "645c790c-e979-460e-94e5-5c8ae1c36c5a");
//                    } catch (ProductUnitViewManager.UnitNotFoundException e) {
//                        getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
//                        Timber.e(e, "product unit not found in product group fragment");
//                    }
//                }
//            }
//        };
        onPriceChangedListener = new ProductSimpleOrderViewHolder.OnPriceChanged() {
            @Override
            public void run() {
                refreshTotalPrice();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_group_list, container, false);
        recommendBtn = view.findViewById(R.id.recommend_btn);
        recProgress = view.findViewById(R.id.rec_progress);
        fabVoice=view.findViewById(R.id.fabVoice);
        searchImageView = view.findViewById(R.id.search_image_view);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        searchLayout = view.findViewById(R.id.search_layout);
        orderCostTextView = view.findViewById(R.id.order_cost_text_view);
        usanceDayLable = view.findViewById(R.id.usance_day_lable);
        usanceDayTextView = view.findViewById(R.id.usance_day_text_view);
        if (searchImageView != null)
            searchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolbarLayout.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                }
            });


        fabVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language =  "fa-IR";
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);

                try {
                    startActivityForResult(intent,5000);
                } catch (Exception e){

                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000) {
            if (requestCode != RESULT_OK && null != data) {
                ArrayList<String> result =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchEditText.setText(result.get(0));
            }
        }else if (requestCode == 4000){
            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            productNumber=result.get(0);
            showCalculator(possitonitem);

        }
    }

    boolean isPortrait() {
        return searchImageView != null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getVaranegarActvity().removeDrawer();
        if (isPortrait())
            openCategoriesDrawer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });
        view.findViewById(R.id.all_products_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsAdapter.deselect();
                selectedGroupIds = new UUID[0];
                refreshAdapter();
            }
        });
        productsRecyclerView = view.findViewById(R.id.product_list_recycler_view);
        this.fragmentView = view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupGroupsList(fragmentView);
        setupOrderBy(fragmentView);
        setupInStockOption(fragmentView);
        setupSearch(fragmentView);
        setupFilters(fragmentView);
        setupProductList();
        refreshTotalPrice();
        PingApi pingApi = new PingApi();
        if (Connectivity.isConnected(getContext())) {
            pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    setupRecommendedProducts();
                }

                @Override
                public void failed() {

                }
            });
        }
    }


    private void setupRecommendedProducts() {
        if (recommendBtn != null) {
            final SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.RecSysServer, SysConfigManager.local);
            if (sysConfigModel != null) {
                recommendBtn.setClickable(false);
                recProgress.setVisibility(View.VISIBLE);
                getRecommendationsLicense(new IOnRecommendationLicense() {
                    @Override
                    public void onSuccess() {
                        getRecommendations(new IOnGetRecommendations() {
                            @Override
                            public void onSuccess() {
                                if (recommendedProductIds != null && recommendedProductIds.size() > 0) {
                                    showRecommendationDialog();
                                } else {
                                    Activity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing() && isResumed()) {
                                        recommendBtn.setClickable(true);
                                        recProgress.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Activity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                    recommendBtn.setClickable(true);
                                    recProgress.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Activity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed()) {
                            recommendBtn.setClickable(true);
                            recProgress.setVisibility(View.GONE);
                        }
                    }
                });
            }

            recommendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.RecSysServer, SysConfigManager.local);
                    if (sysConfigModel == null) {
                        recommendBtn.setClickable(false);
                        recProgress.setVisibility(View.VISIBLE);
                        RecommenderSystemApi api = new RecommenderSystemApi(getContext());
                        api.runWebRequest(api.getConfigs(), new WebCallBack<RecSysConfig>() {
                            @Override
                            protected void onFinish() {

                            }

                            @Override
                            protected void onSuccess(RecSysConfig result, Request request) {
                                try {
                                    if (result != null && result.BiUrl != null) {
                                        sysConfigManager.save(ConfigKey.RecSysServer, result.BiUrl, SysConfigManager.local);
                                        getRecommendationsLicense(new IOnRecommendationLicense() {
                                            @Override
                                            public void onSuccess() {
                                                getRecommendations(new IOnGetRecommendations() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (recommendedProductIds != null && recommendedProductIds.size() > 0) {
                                                            showRecommendationDialog();
                                                        } else {
                                                            Activity activity = getVaranegarActvity();
                                                            if (activity != null && !activity.isFinishing() && isResumed()) {
                                                                recommendBtn.setClickable(true);
                                                                recProgress.setVisibility(View.GONE);
                                                                showErrorMessage(R.string.no_data_to_load);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(String error) {
                                                        Activity activity = getVaranegarActvity();
                                                        if (activity != null && !activity.isFinishing() && isResumed()) {
                                                            recommendBtn.setClickable(true);
                                                            recProgress.setVisibility(View.GONE);
                                                            showErrorMessage(error);
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Activity activity = getVaranegarActvity();
                                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                                    recommendBtn.setClickable(true);
                                                    recProgress.setVisibility(View.GONE);
                                                    showErrorMessage(error);
                                                }
                                            }
                                        });
                                    } else {
                                        Timber.d("Recommendation server ip is not found");
                                        Activity activity = getVaranegarActvity();
                                        if (activity != null && !activity.isFinishing() && isResumed()) {
                                            recommendBtn.setClickable(true);
                                            recProgress.setVisibility(View.GONE);
                                            showErrorMessage(R.string.config_not_found);
                                        }
                                    }
                                } catch (Exception e) {
                                    Timber.e(e);
                                    Activity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing() && isResumed()) {
                                        recommendBtn.setClickable(true);
                                        recProgress.setVisibility(View.GONE);
                                        showErrorMessage(R.string.error_saving_request);
                                    }
                                }
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                String e = WebApiErrorBody.log(error, getContext());
                                Activity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                    recommendBtn.setClickable(true);
                                    recProgress.setVisibility(View.GONE);
                                    showErrorMessage(e);
                                }
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                Timber.e(t);
                                Activity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                    recommendBtn.setClickable(true);
                                    recProgress.setVisibility(View.GONE);
                                    showErrorMessage(getString(R.string.network_error));
                                }
                            }
                        });
                    } else {
                        recommendBtn.setClickable(false);
                        recProgress.setVisibility(View.VISIBLE);
                        getRecommendationsLicense(new IOnRecommendationLicense() {
                            @Override
                            public void onSuccess() {
                                if (recommendedProductIds != null && recommendedProductIds.size() > 0) {
                                    Activity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing() && isResumed()) {
                                        recProgress.setVisibility(View.GONE);
                                        recommendBtn.setClickable(true);
                                        showRecommendationDialog();
                                    }
                                } else {
                                    getRecommendations(new IOnGetRecommendations() {
                                        @Override
                                        public void onSuccess() {
                                            if (recommendedProductIds != null && recommendedProductIds.size() > 0) {
                                                showRecommendationDialog();
                                            } else {
                                                Activity activity = getVaranegarActvity();
                                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                                    recommendBtn.setClickable(true);
                                                    recProgress.setVisibility(View.GONE);
                                                    showErrorMessage(R.string.no_data_to_load);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Activity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing() && isResumed()) {
                                                recommendBtn.setClickable(true);
                                                recProgress.setVisibility(View.GONE);
                                                showErrorMessage(error);
                                            }
                                        }
                                    });
                                    Activity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing() && isResumed()) {
                                        recProgress.setVisibility(View.GONE);
                                        recommendBtn.setClickable(true);
                                        showErrorMessage(R.string.no_data_to_load);
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Activity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                    recommendBtn.setClickable(true);
                                    recProgress.setVisibility(View.GONE);
                                    showErrorMessage(error);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    interface IOnRecommendationLicense {
        void onSuccess();

        void onError(String error);
    }

    private void getRecommendationsLicense(final IOnRecommendationLicense callback) {
        final SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel license = sysConfigManager.read(ConfigKey.RecSysLicense, SysConfigManager.local);
        if (SysConfigManager.compare(license, "1")) {
            callback.onSuccess();
        } else {
            String deviceId = getDeviceId();
            if (deviceId == null) {
                callback.onError(getString(R.string.device_id_is_not_available));
                return;
            }
            DeviceApi deviceApi = new DeviceApi(getContext());
            CompanyDeviceAppData data = new CompanyDeviceAppData();
            data.DeviceModelName = Build.MODEL;
            data.IMEI = deviceId;
            data.UserName = UserManager.readFromFile(getContext()).UserName;
            data.IsRecommendedSystem = true;
            LicenseRequestBody body = new LicenseRequestBody();
            body.companyDeviceAppData = data;
            deviceApi.runWebRequest(deviceApi.checkLicense(body), new WebCallBack<CompanyDeviceAppResult>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(CompanyDeviceAppResult result, Request request) {
                    Timber.d("Result for : " + request.body().toString());
                    Timber.d("Type: " + result.Type + " Message: " + result.Message);
                    if (result.Type == 200) {
                        try {
                            sysConfigManager.save(ConfigKey.RecSysLicense, "1", SysConfigManager.local);
                            callback.onSuccess();
                        } catch (Exception e) {
                            callback.onError(getString(R.string.error_saving_request));
                        }
                    } else
                        callback.onError(result.Message);

                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String e = WebApiErrorBody.log(error, getContext());
                    callback.onError(e);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    callback.onError(getString(R.string.network_error));

                }
            });
        }
    }

    @Nullable
    private String getDeviceId() {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Timber.e("Manifest.permission.READ_PHONE_STATE Permission not granted");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
                } else {
                    if (telephonyManager != null)
                        deviceId = telephonyManager.getDeviceId();
                    else
                        Timber.e("telephonyManager is null!");
                }
                if (deviceId == null || deviceId.isEmpty()) {
                    Timber.e("Device Id " + deviceId + " is wrong!!");
                    return null;
                }
            }
            return deviceId;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }

    }

    private void showRecommendationDialog() {
        RecommendedProductsDialog dialog = new RecommendedProductsDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("RECOMMENDED_PRODUCT_IDS", recommendedProductIds);
        bundle.putString("CUSTOMER_ID", customerId.toString());
        bundle.putString("ORDER_ID", callOrderId.toString());
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), "RecommendedProductsDialog");
        dialog.onOrderUpdate = new RecommendedProductsDialog.OnOrderUpdate() {
            @Override
            public void run(final ProductOrderViewModel productOrderViewModel) {
                int index = Linq.findFirstIndex(filteredProductsList, new Linq.Criteria<ProductOrderViewModel>() {
                    @Override
                    public boolean run(ProductOrderViewModel item) {
                        return item.UniqueId.equals(productOrderViewModel.UniqueId);
                    }
                });
                refreshAdapter(index, productOrderViewModel, true);
            }
        };
    }

    interface IOnGetRecommendations {
        void onSuccess();

        void onError(String error);
    }

    private void getRecommendations(final IOnGetRecommendations callback) {
        RecommenderSystemApi api = new RecommenderSystemApi(getContext());
        try {
            api.runWebRequest(api.getRecommendedProducts(customerId), new WebCallBack<List<RecommendedProductModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(final List<RecommendedProductModel> result, Request request) {
                    recommendedProductIds = new ArrayList<>();
                    for (RecommendedProductModel item :
                            result) {
                        if (item.ProductId != null)
                            recommendedProductIds.add(item.ProductId.toString());
                    }
                    callback.onSuccess();
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String e = WebApiErrorBody.log(error, getContext());
                    callback.onError(e);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t.getMessage());
                    callback.onError(getString(R.string.network_error));
                }
            });
        } catch (RecSysException e) {
            Timber.e(e);
            callback.onError(e.getMessage());
        }
    }

    private void showErrorMessage(@StringRes int message) {
        Activity activity1 = getVaranegarActvity();
        if (activity1 != null && !activity1.isFinishing() && isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity1);
            dialog.setMessage(message);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.show();
        }
    }

    private void showErrorMessage(String message) {
        Activity activity1 = getVaranegarActvity();
        if (activity1 != null && !activity1.isFinishing() && isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity1);
            dialog.setMessage(message);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.show();
        }
    }


    //comment for find show list of product pattern pre visit by mehrdad latifi
    private void setupProductList() {
        final ProgressDialog loadProgressDialog = new ProgressDialog(getContext());
        loadProgressDialog.setTitle(R.string.please_wait);
        loadProgressDialog.setMessage(getString(R.string.loading_data_items));
        loadProgressDialog.setCanceledOnTouchOutside(false);
        loadProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
                orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
                customerStockCheckType = sysConfigManager.read(ConfigKey.CustomerStockCheckType, SysConfigManager.cloud);
                checkCustomerStock = sysConfigManager.read(ConfigKey.CheckCustomerStock, SysConfigManager.cloud);
                Integer count = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(new Query().select(Projection.countRows()).from(OldInvoiceDetailView.OldInvoiceDetailViewTbl));
                showAverageFactor = count != null && count != 0;

                ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
                productUnitsViewModelHashMap = productUnitsViewManager.getProductsUnits();
                final ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());

                if (selectedFilter == FilterType.Emphatic) {
                    if (inStock)
                        productsList = productOrderViewManager
                                .getItems(ProductOrderViewManager
                                        .getAllEmphaticItems(null, customerId, callOrderId, null, inStock, null));
                    else
                        productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                .getAllEmphaticItems(null, customerId, callOrderId,
                                        null, null, null));
                } else if (selectedFilter == FilterType.Free) {
                    if (inStock)
                        productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                .getAllFreeItems(null, customerId, callOrderId, null, inStock, null));
                    else
                        productsList = productOrderViewManager.getItems(ProductOrderViewManager.getAllFreeItems(null, customerId, callOrderId, null, null, null));
                } else if (selectedFilter == FilterType.UnSold) {
                    if (new UpdateManager(getContext()).getLog(UpdateKey.CustomerOldInvoice).equals(MIN_DATE)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setMessage(R.string.please_update_customer_history);
                                dialog.setTitle(R.string.warning);
                                dialog.setIcon(Icon.Warning);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        });
                        productsList.clear();
                    } else {
                        if (inStock)
                            productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                    .getAllUnSoldItems(null, customerId, callOrderId,
                                            null, inStock, null));
                        else
                            productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                    .getAllUnSoldItems(null, customerId, callOrderId,
                                            null, null, null));
                    }
                } else {
                    if (inStock)
                        productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                .getAll(null, customerId, callOrderId,
                                        null, inStock, false, null));
                    else
                        productsList = productOrderViewManager.getItems(ProductOrderViewManager
                                .getAll(null, customerId, callOrderId, null,
                                        null, false, null));
                }

/*                try {
                    new MslManager(getContext()).deleteAll();
                } catch (DbException e) {
                    e.printStackTrace();
                }*/

//                new MslManager(getContext()).insertTest();

/*                List<MslModel> mslModels = new MslManager(getContext()).getAll(customerLevelId.toString());
                List<ProductOrderViewModel> checkMsl = new ArrayList<>();
                for (ProductOrderViewModel item : productsList) {
                    boolean find = false;
                    for (MslModel model : mslModels) {
                        if (item.UniqueId.equals(model.ProductId)) {
                            find = true;
                            if (model.IsForce) {
                                MslProductPatternModel mslProductPatternModel = new MslProductPatternModel(model.CustomerLevelId, model.ProductId, model.IsForce);
                                mslProductPatternModel.UniqueId = model.UniqueId;
                                try {
                                    new MslProductPatternManager(getContext()).insert(mslProductPatternModel);
                                } catch (ValidationException e) {
                                    e.printStackTrace();
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                item.EmphaticType = EmphasisType.Deterrent;
                                checkMsl.add(item);
                                break;
                            }
                        }
                    }
                    if (!find)
                        checkMsl.add(item);
                }

                productsList.clear();
                productsList.addAll(checkMsl);*/


                final HashMap<UUID, ProductUnitViewManager.ProductUnits> unitSet = new
                        ProductUnitViewManager(getContext()).getUnitSet(ProductType.isForSale);
                if (productsList != null && productsList.size() > 0)
                    Timber.d(productsList.size() + " products fetched from db");
                else
                    Timber.d("0 products fetched from db");
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && productsRecyclerView != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            productsAdapter = new BaseRecyclerAdapter<ProductOrderViewModel>(getVaranegarActvity(), filteredProductsList) {
                                @Override
                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                                    if (viewType == OrderLineViewType.Calculator.ordinal()) {
                                        View view = LayoutInflater.from(parent.getContext()).inflate(
                                                R.layout.product_order_view_line,
                                                parent,
                                                false);
                                        return new ProductOrderViewHolder(view,
                                                this,
                                                showStockLevel,
                                                orderPointCheckType,
                                                customerStockCheckType,
                                                checkCustomerStock,
                                                showAverageFactor,
                                                callOrderId,
                                                productUnitsViewModelHashMap,
                                                unitSet,
                                                getActivity());
 /*                                   } else {
                                        View view = LayoutInflater.from(parent.getContext()).inflate(
                                                R.layout.product_order_view_line_simple,
                                                parent,
                                                false);
                                        return new ProductSimpleOrderViewHolder(view,
                                                showStockLevel,
                                                orderPointCheckType,
                                                customerStockCheckType,
                                                checkCustomerStock,
                                                productUnitsViewModelHashMap,
                                                this,
                                                callOrderId,
                                                unitSet,
                                                getActivity(),
                                                onItemQtyChangedHandler,
                                                onPriceChangedListener,
                                                orderTypeId);
                                    }*/
                                }

                                @Override
                                public int getItemViewType(int position) {
                                    if (freeFilter.selected)
                                        return OrderLineViewType.Calculator.ordinal();
                                    if (isContractor())
                                        return OrderLineViewType.SimpleMode.ordinal();
                                    ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
                                    if (productOrderViewModel == null)
                                        return 1;

                                    ProductUnitViewManager.ProductUnits pu = unitSet.get(productOrderViewModel.UniqueId);
                                    return productOrderViewModel.ExpDate == null && !pu.hasThirdUnit() && !pu.isBulk() ?
                                            OrderLineViewType.SimpleMode.ordinal() :
                                            OrderLineViewType.Calculator.ordinal();
                                }
                            };


                            productsAdapter.addContextMenuItem(new ContextMenuItem() {
                                @Override
                                public boolean isAvailable(int position) {
                                    if (position < 0)
                                        return false;
                                    return !isContractor();
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
                                public void run(final int position) {
                                    if (position == -1)
                                        return;
                                    if (isContractor())
                                        showCalculator(position);
                                    else {
                                        if (freeFilter.selected) {
                                            showFreeCalculator(position);
                                        } else
                                            showCalculator(position);
                                    }
                                }
                            });
                            productsAdapter.addContextMenuItem(new ContextMenuItem() {
                                @Override
                                public boolean isAvailable(int position) {
                                    if (position < 0)
                                        return false;
                                    ProductOrderViewModel orderViewModel = productsAdapter.get(position);
                                    return orderViewModel != null && orderViewModel.TotalQty != null &&
                                            orderViewModel.TotalQty.compareTo(BigDecimal.ZERO) != 0;
                                }

                                @Override
                                public String getName(int position) {
                                    return getActivity().getString(R.string.delete);
                                }

                                @Override
                                public int getIcon(int position) {
                                    return R.drawable.ic_delete_forever_black_24dp;
                                }

                                @Override
                                public void run(int position) {
                                    if (position == -1)
                                        return;
                                    ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
                                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                                    try {
                                        callOrderLineManager.deleteProduct(callOrderId, productOrderViewModel.UniqueId,
                                                productOrderViewModel.IsRequestFreeItem);
                                        ProductOrderViewModel updatedProductOrderViewModel =
                                                productOrderViewManager.getLine(customerId, callOrderId,
                                                        productOrderViewModel.UniqueId, productOrderViewModel.IsRequestFreeItem);
                                        refreshAdapter(position, updatedProductOrderViewModel, true);
                                        refreshTotalPrice();
                                    } catch (Exception ex) {
                                        Timber.e(ex);
                                    }
                                }
                            });
                            productsAdapter.addContextMenuItem(new ContextMenuItemRaw() {
                                @Override
                                public boolean isAvailable(int position) {
                                    ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
                                    return productOrderViewModel.OrderLineId != null;
                                }

                                @Nullable
                                @Override
                                protected View onCreateView(int position, View convertView, ViewGroup parent) {
                                    ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
                                    View view = LayoutInflater.from(convertView.getContext())
                                            .inflate(R.layout.order_line_description, parent, false);
                                    EditText editText = view.findViewById(R.id.edit_text);
                                    editText.setText(productOrderViewModel.Description);
                                    editText.setOnEditorActionListener((v, actionId, event) -> {
                                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                                            save(productOrderViewModel, v.getText().toString(), position);
                                        }
                                        return false;
                                    });
                                    view.findViewById(R.id.save_btn).setOnClickListener(v ->
                                            save(productOrderViewModel, editText.getText().toString(), position));
                                    return view;
                                }

                                void save(ProductOrderViewModel productOrderViewModel, String desc, int position) {
                                    try {
                                        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getActivity());
                                        CallOrderLineModel line = callOrderLineManager.getItem(productOrderViewModel.OrderLineId);
                                        line.Description = desc;
                                        callOrderLineManager.update(line);
                                        productOrderViewModel.Description = line.Description;
                                        productsAdapter.notifyItemChanged(position);
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


                            productsRecyclerView.setAdapter(productsAdapter);
                            refreshAdapter();
                            if (loadProgressDialog.isShowing())
                                loadProgressDialog.dismiss();
                        }

                    });
                }
            }
        }).start();

    }

    HashMap<Integer, Integer> map = new HashMap<>();

    private void refreshAdapter() {
        final OrderBy orderOption = getOrderOption();

        int order = 1;
        if (orderOption.getType() == OrderType.DESC)
            order = -1;
        if (orderOption.getColumn() == ProductOrderView.EmphaticPriority) {
            final int finalOrder = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.EmphaticPriority < second.EmphaticPriority)
                        return -1 * finalOrder;
                    else if (first.EmphaticPriority > second.EmphaticPriority)
                        return 1 * finalOrder;
                    else return 0;
                }

            });
        } else if (orderOption.getColumn() == ProductOrderView.ProductCode) {
            final int finalOrder1 = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.ProductCode == null)
                        return -1;
                    if (second.ProductCode == null)
                        return 1;
                    return first.ProductCode.compareTo(second.ProductCode) * finalOrder1;
                }

            });
        } else if (orderOption.getColumn() == ProductOrderView.ProductName) {
            final int finalOrder2 = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.ProductName == null)
                        return -1;
                    if (second.ProductName == null)
                        return 1;
                    return first.ProductName.compareTo(second.ProductName) * finalOrder2;
                }

            });
        } else if (orderOption.getColumn() == ProductOrderView.TotalQty) {
            final int finalOrder3 = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.TotalQty == null)
                        first.TotalQty = BigDecimal.ZERO;
                    if (second.TotalQty == null)
                        second.TotalQty = BigDecimal.ZERO;
                    return first.TotalQty.compareTo(second.TotalQty) * finalOrder3;
                }

            });
        } else if (orderOption.getColumn() == ProductOrderView.Average) {
            final int finalOrder4 = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.Average > second.Average)
                        return -1 * finalOrder4;
                    else if (first.Average < second.Average)
                        return 1 * finalOrder4;
                    else return 0;
                }

            });
        } else if (orderOption.getColumn() == ProductOrderView.Price) {
            final int finalOrder5 = order;
            Linq.sort(productsList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    ProductOrderViewModel first = (ProductOrderViewModel) o;
                    ProductOrderViewModel second = (ProductOrderViewModel) t1;
                    if (first.Price == null)
                        first.Price = Currency.ZERO;
                    if (second.Price == null)
                        second.Price = Currency.ZERO;
                    return first.Price.compareTo(second.Price) * finalOrder5;
                }

            });
        }


        preprocessKeyWord();
        map = new HashMap<>();
        int i = 0;
        filteredProductsList.clear();
        for (int j = 0; j < productsList.size(); j++) {
            ProductOrderViewModel item = productsList.get(j);
            if (checkFilterType(item) && filterProductOrName(item)
                    && checkInStock(item) && checkGroup(item)) {
                filteredProductsList.add(item);
                map.put(i, j);
                i++;
            }
        }
        productsAdapter.notifyDataSetChanged();
    }

    private void preprocessKeyWord() {
        splits = null;

        if (keyWord != null && !keyWord.isEmpty())
            keyWord = keyWord.toLowerCase();
        if (keyWord != null && !keyWord.isEmpty())
            keyWord = HelperMethods.persian2Arabic(keyWord);
        if (keyWord != null && !keyWord.isEmpty())
            keyWord = HelperMethods.convertToEnglishNumbers(keyWord);
        if (keyWord != null && !keyWord.isEmpty())
            splits = keyWord.split("\\s+");
    }

    private void refreshAdapter(int position, ProductOrderViewModel
            updatedProductOrderViewModel, boolean deletePrize) {
        if (filteredProductsList.size() > position)
            filteredProductsList.set(position, updatedProductOrderViewModel);
        productsList.set(map.get(position), updatedProductOrderViewModel);
        productsAdapter.notifyDataSetChanged();
        if (deletePrize) {
            OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
            List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, callOrderId));
            if (orderPrizeModels.size() > 0) {
                try {
                    orderPrizeManager.delete(Criteria.equals(OrderPrize.CustomerId, customerId).and(Criteria.equals(OrderPrize.CallOrderId, callOrderId)));
                    getVaranegarActvity().showSnackBar(R.string.order_prizes_deleted, MainVaranegarActivity.Duration.Long);
                } catch (DbException e) {
                    Timber.e(e);
                }
            }
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                try {
                    CallOrderLinesTempManager callOrderLinesTempManager = new CallOrderLinesTempManager(getContext());
                    callOrderLinesTempManager.delete(Criteria.equals(CallOrderLinesTemp.OrderUniqueId, callOrderId.toString()));
                } catch (DbException e) {
                    Timber.e(e);
                }
            }
        }
    }

    private boolean checkGroup(ProductOrderViewModel item) {
        if (selectedGroupIds == null)
            return true;
        if (selectedGroupIds.length == 0)
            return true;


        for (UUID groupId : selectedGroupIds) {
            if (item.ProductGroupId != null && item.ProductGroupId.equals(groupId.toString()))
                return true;
        }
        return false;
    }

    private boolean checkInStock(ProductOrderViewModel item) {
        if (item.OnHandQty == null)
            item.OnHandQty = BigDecimal.ZERO;
        return !inStock || item.OnHandQty.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean filterProductOrName(ProductOrderViewModel item) {

        if (splits == null || splits.length == 0)
            return true;

        boolean ok = true;
        for (String reg : splits) {
            if (!item.ProductCode.toLowerCase().contains(reg) && !item.ProductName.toLowerCase().contains(reg))
                ok = false;
        }
        return ok;

    }

    private boolean checkFilterType(ProductOrderViewModel item) {
        if (selectedFilter == null)
            return true;
        if (selectedFilter == FilterType.Emphatic)
            return item.EmphaticType != EmphasisType.NotEmphatic;
        else
            return selectedFilter != FilterType.Free || item.IsFreeItem;
    }

    private void setupFilters(View view) {
        RecyclerView reportOptionsRecyclerView = view.findViewById(R.id.options_recycler_view);
        if (sysConfigMap.compare(ConfigKey.SimplePresale, true)) {
            reportOptionsRecyclerView.setVisibility(View.GONE);
        } else {
            allFilter = new Filter(getContext().getString(R.string.all_items), "all", true);
            freeFilter = new Filter(getContext().getString(R.string.free_items), "free", true);
            emphaticFilter = new Filter(getContext().getString(R.string.emphatic_items), "emphatic", true);
            unSoldFilter = new Filter(getContext().getString(R.string.un_sold_items), "unsold", true);
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = null;
            try {
                backOfficeType = sysConfigManager.getBackOfficeType();
                if (sysConfigMap.compare(ConfigKey.FreeStockRegistration, true) && backOfficeType == BackOfficeType.Varanegar)
                    filtersAdapter = new FiltersAdapter(getContext(), UUID.fromString("db956bd8-5fbe-462f-b028-fe593755de04"), 0, allFilter, freeFilter, emphaticFilter, unSoldFilter);
                else
                    filtersAdapter = new FiltersAdapter(getContext(), UUID.fromString("db956bd8-5fbe-462f-b028-fe593755de04"), 0, allFilter, emphaticFilter, unSoldFilter);
                if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
                    reportOptionsRecyclerView.setVisibility(View.INVISIBLE);
                reportOptionsRecyclerView.setAdapter(filtersAdapter);
                filtersAdapter.setOnItemClickListener(new FiltersAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        if (allFilter.selected) {
                            selectedFilter = FilterType.All;
                        } else if (freeFilter.selected) {
                            selectedFilter = FilterType.Free;
                        } else if (emphaticFilter.selected) {
                            selectedFilter = FilterType.Emphatic;
                        } else if (unSoldFilter.selected) {
                            selectedFilter = FilterType.UnSold;
                        }
                        setupProductList();
                    }
                });
                reportOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                if (allFilter.selected) {
                    selectedFilter = FilterType.All;
                } else if (freeFilter.selected) {
                    selectedFilter = FilterType.Free;
                } else if (emphaticFilter.selected) {
                    selectedFilter = FilterType.Emphatic;
                } else if (unSoldFilter.selected) {
                    selectedFilter = FilterType.UnSold;
                }
            } catch (UnknownBackOfficeException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSearch(View view) {
        searchEditText = (EditText) view.findViewById(R.id.product_search_edit_text);
        final ImageView clearSearchImageView = (ImageView) view.findViewById(R.id.clear_search_image_view);
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                if (isPortrait()) {
                    searchLayout.setVisibility(View.GONE);
                    toolbarLayout.setVisibility(View.VISIBLE);
                    Activity activity = getActivity();
                    if (activity != null) {
                        View v = getActivity().getCurrentFocus();
                        if (v != null) {
                            Context context = getContext();
                            if (context != null) {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null)
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    }
                }
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            String before = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String after = s.toString();
                if (after.equals(before))
                    return;
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (System.currentTimeMillis() > (last_text_edit + delay)) {
                            String str = null;
                            if (after.length() > 1)
                                str = after;
                            keyWord = str;
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshAdapter();
                                    }
                                });
                            }
                        }
                    }
                }, delay + 10);

                if (after.isEmpty())
                    clearSearchImageView.setVisibility(View.GONE);
                else
                    clearSearchImageView.setVisibility(View.VISIBLE);
                before = after;
            }
        });
    }

    Handler handler = new Handler();

    long delay = 200; // milliseconds after user stops typing
    long last_text_edit = 0;

    private void setupInStockOption(View view) {
        inStockCheckBox = (CheckBox) view.findViewById(R.id.in_stock_check_box);
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("InStockProducts", Context.MODE_PRIVATE);
        inStock = sharedPreferences.getBoolean("InStock", false);
        inStockCheckBox.setChecked(inStock);
        inStockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                inStock = isChecked;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("InStock", inStock);
                editor.apply();
                refreshAdapter();
                setupProductList();
            }
        });
    }

    protected void refreshTotalPrice() {
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
        Currency totalPrice = customerCallOrderOrderViewManager.calculateTotalAmount(callOrderId).TotalAmount;
        DecimalFormat formatter = new DecimalFormat("#,###");
        orderCostTextView.setText(formatter.format(totalPrice));
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            usanceDayLable.setVisibility(View.VISIBLE);
            usanceDayTextView.setVisibility(View.VISIBLE);
            CustomerManager customerManager = new CustomerManager(getContext());
            CustomerModel customer = customerManager.getItem(customerId);
            int usanceDayAverage = customerCallOrderOrderViewManager.calculateUsanceDayAverage(callOrderId, String.valueOf(customer.BackOfficeId));
            if (usanceDayAverage == 1)
                usanceDayAverage = 0;
            Calendar newDate = Calendar.getInstance();
            newDate.add(Calendar.DAY_OF_YEAR, usanceDayAverage);
            String date = DateHelper.toString(newDate.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            if (customerCallOrderOrderViewManager.getLines(callOrderId, null).size() > 0)
                usanceDayTextView.setText(date);
            else
                usanceDayTextView.setText("-");
        } else {
            usanceDayLable.setVisibility(View.GONE);
            usanceDayTextView.setVisibility(View.GONE);
        }
    }

    private void setupOrderBy(View view) {
        orderBySpinner = view.findViewById(R.id.order_by_spinner);
        orderOptions = new ArrayList<>();
        if (!isContractor())
            orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(EmphaticPriority).setName(R.string.emphasis));
        orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(ProductCode).setName(R.string.product_code));
        orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(ProductName).setName(R.string.product_name));
        orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(TotalQty).setName(R.string.total_qty));
        if (!isContractor())
            orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(Average).setName(R.string.average_old_factor));
        orderOptions.add(new OrderOption<ProductOrderViewModel>(getContext()).setProjection(Price).setName(R.string.unit_price));

        orderBySpinner.setup(getFragmentManager(), orderOptions, null);
        //orderBySpinner.selectItem(0);
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("SortProducts", Context.MODE_PRIVATE);
        orderBySpinner.selectItem(sharedPreferences.getInt("SortPosition", 0));
        ascending = sharedPreferences.getBoolean("SortAsc", false);
        if (ascending)
            orderBySpinner.setIconResource(R.drawable.ic_asc_sort_white_24dp);
        else
            orderBySpinner.setIconResource(R.drawable.ic_desc_sort_white_24dp);

        orderBySpinner.setOnSpinnerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ascending = !ascending;
                if (ascending)
                    orderBySpinner.setIconResource(R.drawable.ic_asc_sort_white_24dp);
                else
                    orderBySpinner.setIconResource(R.drawable.ic_desc_sort_white_24dp);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("SortAsc", ascending);
                editor.apply();
                refreshAdapter();
            }
        });
        orderBySpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<OrderOption<ProductOrderViewModel>>() {
            @Override
            public void onItemSelected(int position, OrderOption<ProductOrderViewModel> item) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("SortPosition", position);
                editor.apply();
                refreshAdapter();
            }
        });

    }


    private void showFreeCalculator(final int position) {
        ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
        if (productOrderViewModel.UniqueId == null) {
            Timber.wtf("ProductUnitId of product order view is null");
        } else {
            final ProductModel productModel = new ProductManager(getContext()).getItem(productOrderViewModel.UniqueId);
            if (productModel == null)
                throw new NullPointerException("product id " + productOrderViewModel.UniqueId + " not found");
            try {
                FreeOrderCalculatorFrom orderCalculatorForm = new FreeOrderCalculatorFrom();
                orderCalculatorForm.setArguments(getContext(), callOrderId, productModel);
                orderCalculatorForm.onCalcFinish = new FreeOrderCalculatorFrom.OnCalcFinish() {
                    @Override
                    public void run(List<FreeOrderItem> items) {
                        for (FreeOrderItem freeOrderItem :
                                items) {
                            if (freeOrderItem.getTotalQty().compareTo(BigDecimal.ZERO) == 1)
                                onAdd(position, productModel.UniqueId, freeOrderItem.discreteUnits, freeOrderItem.bulkUnit, freeOrderItem.freeReason, null);
                        }
                    }
                };
                orderCalculatorForm.show(getChildFragmentManager(), "2cde638c-9702-41d2-9b3c-710c3e5ea8ea");
            } catch (ProductUnitViewManager.UnitNotFoundException e) {
                getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                Timber.e(e, "product unit not found in product group fragment");
            }
        }
    }

    void showCalculator(final int position) {
        possitonitem=position;
        final ProductOrderViewModel productOrderViewModel = productsAdapter.get(position);
        if (productOrderViewModel.UniqueId == null) {
            Timber.wtf("ProductUnitId of product order view is null");
        } else {
            CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = new CustomerCallOrderOrderViewModelRepository().getItem(
                    new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                            .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId))
                            .whereAnd(Criteria.equals(CustomerCallOrderOrderView.ProductId, productOrderViewModel.UniqueId))
                            .whereAnd(Criteria.equals(CustomerCallOrderOrderView.IsRequestFreeItem, freeFilter != null && freeFilter.selected))
            );
            List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
            OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
            if (customerCallOrderOrderViewModel != null) {
                orderLineQtyModels =
                        orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
            }
            OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
            try {
                CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
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
                CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productOrderViewModel.UniqueId, orderLineQtyModels, bulkUnit, ProductType.isForSale);
                if (productOrderViewModel.ExpDate == null) {
                    orderCalculatorForm.setArguments(productOrderViewModel.UniqueId,
                            productOrderViewModel.ProductName, calculatorUnits, productOrderViewModel.Price,
                            productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId);
                    Bundle bundle = new Bundle();
                    bundle.putString("productNumber", productNumber);
                    orderCalculatorForm.setArguments(bundle);
                }else
                    orderCalculatorForm.setArguments(productOrderViewModel.UniqueId,
                            productOrderViewModel.ProductName, CalculatorBatchUnits.generate(getContext(),
                                    productOrderViewModel, customerCallOrderOrderViewModel == null ? null :
                                            customerCallOrderOrderViewModel.UniqueId, productOrderViewModel.Price,
                                    productOrderViewModel.PriceId, productOrderViewModel.UserPrice), productOrderViewModel.UserPrice,
                            onHandQtyStock, customerId, callOrderId);
                orderCalculatorForm.onCalcFinish = new OrderCalculatorForm.OnCalcFinish() {
                    @Override
                    public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList) {
                        onAdd(position, productOrderViewModel.UniqueId, discreteUnits, bulkUnit, null, batchQtyList);
                    }
                };

                orderCalculatorForm.show(getChildFragmentManager(), "645c790c-e979-460e-94e5-5c8ae1c36c5a");
            } catch (ProductUnitViewManager.UnitNotFoundException e) {
                getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                Timber.e(e, "product unit not found in product group fragment");
            }
        }
    }

    private void setupGroupsList(View view) {
        ImageView categoriesImageView = view.findViewById(R.id.categories_image_view);
        if (categoriesImageView != null) {
            categoriesImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleCategoriesDrawer();
                }
            });
        }
        ExpandableRecyclerView mainRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.groups_recycler_view);
        groupsAdapter =
                new ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel>(
                        getVaranegarActvity(), new ProductGroupManager(getContext()).getParentItems(ProductType.isForSale),
                        new ExpandableRecyclerAdapter.Children<ProductGroupModel, ProductGroupModel>() {
                            @Override
                            public List<ProductGroupModel> onCreate(ProductGroupModel parentItem) {
                                return new ProductGroupModelRepository().getItems(ProductGroupManager.getSubGroups(parentItem.UniqueId, ProductType.isForSale));
                            }
                        }) {
                    @Override
                    public BaseViewHolder<ProductGroupModel> onCreateParent(ViewGroup parent) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
                        return new ProductGroupViewHolder(view, this, getContext());
                    }

                    @Override
                    public BaseViewHolder<ProductGroupModel> onCreateChild(ViewGroup parent, ChildRecyclerAdapter<ProductGroupModel> adapter) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
                        return new ChildProductGroupViewHolder(itemView, adapter, getContext());
                    }

                };

        groupsAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<ProductGroupModel>() {
            @Override
            public void run(int position) {
                ProductGroupModel item = groupsAdapter.get(position);
                selectedGroupIds = new ProductGroupManager(getContext()).getSubGroupIds(item.UniqueId, ProductType.isForSale);
                refreshAdapter();
            }
        });
        groupsAdapter.setOnChildItemClickListener(new ExpandableRecyclerAdapter.OnChildItemClick<ProductGroupModel>() {
            @Override
            public void onClick(int position, ProductGroupModel clickedItem) {
                getVaranegarActvity().closeDrawer();
                if (clickedItem.UniqueId == null) {
                    Timber.wtf("unique id of product group is null");
                } else {
                    selectedGroupIds = new UUID[]{clickedItem.UniqueId};
                    refreshAdapter();
                }
            }
        });
        mainRecyclerView.setAdapter(groupsAdapter);
    }

    private OrderBy getOrderOption() {
        int position = orderBySpinner.getSelectedPosition();
        if (position >= 0 && position < orderOptions.size()) {
            return new OrderBy(orderOptions.get(position).getProjection(), ascending ? OrderType.ASC : OrderType.DESC);
        } else {
            return new OrderBy(ProductOrderView.EmphaticPriority, ascending ? OrderType.ASC : OrderType.DESC);
        }
    }

    public void closeCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        drawerLayout.closeDrawer(drawerView);
    }

    public void openCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        drawerLayout.openDrawer(drawerView);
    }

    public void toggleCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        if (drawerLayout.isDrawerOpen(drawerView))
            closeCategoriesDrawer();
        else
            openCategoriesDrawer();
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    private enum FilterType {
        All,
        Free,
        Emphatic,
        UnSold
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inStockCheckBox = null;
        groupsAdapter = null;
        orderBySpinner = null;
        orderCostTextView = null;
        productsAdapter = null;
        callOrderId = null;
        customerId = null;
        filtersAdapter = null;
        allFilter = null;
        freeFilter = null;
        orderOptions = null;
        searchImageView = null;
        toolbarLayout = null;
        searchLayout = null;
        searchEditText = null;
        selectedGroupIds = null;
        filteredProductsList = null;
        selectedFilter = null;
        keyWord = null;
        splits = null;
        fragmentView = null;
        showStockLevel = null;
        orderPointCheckType = null;
        customerStockCheckType = null;
        checkCustomerStock = null;
        productUnitsViewModelHashMap = null;
        Runtime.getRuntime().gc();
    }
}
