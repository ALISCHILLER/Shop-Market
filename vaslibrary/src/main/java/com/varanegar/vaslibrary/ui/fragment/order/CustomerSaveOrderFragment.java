package com.varanegar.vaslibrary.ui.fragment.order;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.SimpleTokenizer;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.UserDialogPreferences;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.OrderOption;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.base.SubsystemTypes;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.catalogue.CatalogueHelper;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.CustomerPaymentTypesViewManager;
import com.varanegar.vaslibrary.manager.CustomerProductOrderQtyHistoryManager;
import com.varanegar.vaslibrary.manager.CustomerProductPrizeManager;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.CustomerTotalProductSaleManager;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.PriceClassVnLiteManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesTempManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoicePreviewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.DistOrderStatus;
import com.varanegar.vaslibrary.manager.customercall.SaveOrderUtility;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountConditionManager;
import com.varanegar.vaslibrary.manager.emphaticitems.CustomerEmphaticPackageViewManager;
import com.varanegar.vaslibrary.manager.emphaticitems.CustomerEmphaticProductManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticPackageCheckResult;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EditOrderActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EditOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLineActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.ProductUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreviewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTemp;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProductModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrder;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLiteModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.pricecalculator.PriceCalcCallback;
import com.varanegar.vaslibrary.pricecalculator.PriceCalculator;
import com.varanegar.vaslibrary.print.InvoicePrint.InvoicePrintHelper;
import com.varanegar.vaslibrary.print.datahelper.OrderPrintType;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.ui.dialog.MultiInvoiceVectorDialog;
import com.varanegar.vaslibrary.ui.dialog.ValueEditorDialog;
import com.varanegar.vaslibrary.ui.dialog.choiceprize.ChoicePrizesDialog;
import com.varanegar.vaslibrary.ui.drawer.CustomerReportsDrawerAdapter;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;
import com.varanegar.vaslibrary.ui.fragment.productgroup.ProductGroupFragment;
import com.varanegar.vaslibrary.ui.fragment.settlement.CardReaderDialog;
import com.varanegar.vaslibrary.ui.fragment.settlement.CashPaymentDialog;
import com.varanegar.vaslibrary.ui.fragment.settlement.CustomerPayment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Request;
import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;

import static com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView.ProductCode;
import static com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView.ProductName;
import static com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView.SortId;
import static com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView.TotalQty;
import static varanegar.com.discountcalculatorlib.Global.orderPrize;

/**
 * Created by A.Torabi on 1/14/2018.
 */


/**
 *صفحه تحویل حواله
 * صفحه تحویل سفارش   **
*/
public class CustomerSaveOrderFragment extends VisitFragment implements ChoicePrizesDialog.choicePrizeDialogListener {
    private OnItemQtyChangedHandler onItemQtyChangedHandler;
    private HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap;
    private ProgressDialog progressDialog;
    private CustomerModel customer;
    BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> orderAdapter;
    private CuteToolbar toolbar;
    private List<CustomerCallModel> calls;
    private ImageView calendarImageView;
    private TextView discountTextView;
    private TextView addAmountTextView;
    private TextView usanceDayTextView;
    private TextView usanceTextView;
    private View usanceDayLayout;
    private List<CustomerCallInvoiceModel> customerCallOrderModels;

    private UUID customerId;
    private TextView netAmountTextView;
    private ArrayList<OrderOption<CustomerCallOrderOrderViewModel>> orderOptions;
    private TextView statusTextView;

    private UUID callOrderId;
    private ProgressDialog productStockLevelProgressDialog;
    private ProgressDialog tempTablesProgressDialog;
    private TextView orderCostTextView;
    private CheckBox systemCheckBox;
    private Button calcOnlineUsanceDay;
    private PairedItemsSpinner<CustomerPaymentTypesViewModel> paymentTypesSpinner;
    private PairedItemsSpinner<CustomerOrderTypeModel> orderTypesSpinner;
    private PairedItemsSpinner<PriceClassVnLiteModel> priceClassSpinner;
    private boolean persistCustomizedPrices = true;
    private Currency maxValu;
    private boolean percentType;
    private List<ProductOrderViewModel> productList;
    private CustomerPaymentTypesViewModel selectedPaymentType;
    private CustomerPayment customerPayment;
    private Set<UUID> validPaymentTypes;
    private HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnits;
    private SaveOrderUtility.ISaveOrderCallback saveCallBack;
    private SaveOrderUtility saveOrderUtility;
    private CustomerCallOrderModel customerCallOrderModel;
    private CustomerCallOrderManager customerCallOrderManager;
    private EditText commentEditText;
    private ImageView commentImageView;
    private Context context;
    private int priceClassRef = -1;
    private PairedItems deliveryDateItem;

    private Button contractorDiscountEditText;
    private ImageView contractorDiscountImageView;
    private TextView contractorNetAmountTextView;
    private TextView contractorDiscountTextView;
    private LinearLayout linearLayout;

    public final static String percent = "91ad91f2-795b-43e4-95ab-82353f8716e9";
    public final static String cash = "fc08c7c0-391c-461f-a153-c742e7dc0033";

    private OrderAmount orderAmount;
    private Currency otherDiscount = Currency.ZERO;
    private Currency otherPercent = Currency.ZERO;
    List<CustomerPaymentTypesViewModel> customerPaymentTypes = new ArrayList<>();

    private boolean disableCheckBox = true;

    private int loopCount = 0;
    private boolean last = false;

    int defaultPayment = -1;

    List<DiscountOrderPrizeViewModel> orderPrizeList = new ArrayList<>();

    public void setArguments(UUID customerId, UUID callOrderId) {
        addArgument("9c497998-18e6-4be9-ad80-984fcfb2169c", customerId.toString());
        addArgument("cfa84e29-90a1-461c-aa59-d201f410ac7b", callOrderId.toString());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("eb6d7315-0bd0-420c-bf80-e0257c7b153a", customerId.toString());
        outState.putString("cfa84e29-90a1-461c-aa59-d201f410ac7b", callOrderId.toString());
        if (productStockLevelProgressDialog != null)
            outState.putBoolean("69d2ac5d-cc07-4b66-aa0f-cc09d55e3296", productStockLevelProgressDialog.isShowing());
        if (tempTablesProgressDialog != null)
            outState.putBoolean("afc863f4-0694-45df-b513-4fb131338c57", tempTablesProgressDialog.isShowing());
    }

    private void prepareCalculations() {
        if (!hasCallOrder()) {
            if (UserDialogPreferences.isVisible(context, "a3834f22-fd30-4203-87aa-2e66b065b023") && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(context);
                List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
                if (customerOrderTypeModels.size() > 1) {
                    final SearchBox<CustomerOrderTypeModel> orderTypeDialog = new SearchBox<>();
                    orderTypeDialog.setUserPreferenceId("a3834f22-fd30-4203-87aa-2e66b065b023");
                    SysConfigManager sysConfigManager = new SysConfigManager(context);
                    try {
                        if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Rastak))
                            orderTypeDialog.setTitle(getString(R.string.please_select_sell_type));
                        else
                            orderTypeDialog.setTitle(getString(R.string.please_select_order_type));
                    } catch (UnknownBackOfficeException e) {
                        Timber.e(e);
                    }
                    orderTypeDialog.setCancelable(false);
                    orderTypeDialog.setClosable(false);
                    orderTypeDialog.setItems(customerOrderTypeModels, (SearchBox.SearchMethod<CustomerOrderTypeModel>) (item, text) -> item.OrderTypeName.contains(text));
                    orderTypeDialog.show(getVaranegarActvity().getSupportFragmentManager(), "orderTypeDialog");
                    orderTypeDialog.setOnItemSelectedListener((position, selectedOrderType) -> {
                        orderTypeDialog.dismiss();
                        if (selectedOrderType == null)
                            return;
                        if (selectedOrderType.UniqueId.equals(CustomerOrderTypesManager.OrderType24))
                            customerCallOrderModel.DeliveryDate = new Date(new Date().getTime() + 24 * 3600 * 1000);
                        if (selectedOrderType.UniqueId.equals(CustomerOrderTypesManager.OrderType48))
                            customerCallOrderModel.DeliveryDate = new Date(new Date().getTime() + 48 * 3600 * 1000);
                        deliveryDateItem.setValue(DateHelper.toString(customerCallOrderModel.DeliveryDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context)));

                        customerCallOrderModel.OrderTypeUniqueId = selectedOrderType.UniqueId;
                        customerCallOrderModel.Comment = commentEditText.getText().toString();
                        customerCallOrderModel.RoundOrderOtherDiscount = otherDiscount;
                        orderTypesSpinner.selectItem(position);
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                            if (selectedOrderType.UniqueId.toString().equals("4cc866f9-eb19-4c76-8a41-f8207104cdbf")) {
                                linearLayout.setVisibility(View.VISIBLE);
                                orderTypesSpinner.setEnabled(false);
                            } else {
                                persistCustomizedPrices = false;
                                contractorDiscountTextView.setText("0");
                                linearLayout.setVisibility(View.GONE);
                                otherDiscount = Currency.ZERO;
                                otherPercent = Currency.ZERO;
                                contractorDiscountEditText.setText("");
                            }
                        }
                        startCalculation();
                        try {
                            updateCustomerCallOrder();
                            Timber.i("order type selected");
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    });
                } else {
                    startCalculation();
                }
            } else
                startCalculation();
        } else {
            initRefresh();
        }
    }

    private int getOrderTypeBackOfficeId() {
        return getOrderType() == null ? 0 : getOrderType().BackOfficeId;
    }

    @Nullable
    protected CustomerOrderTypeModel getOrderType() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(context);
            List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
            return customerOrderTypeModels.get(0);
        } else
            return orderTypesSpinner == null ? null : orderTypesSpinner.getSelectedItem();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerCallOrderManager = new CustomerCallOrderManager(context);
        context = getContext();
        onItemQtyChangedHandler = new OnItemQtyChangedHandler() {
            @Override
            public void run(CustomerCallOrderOrderViewModel orderLine, QtyChange change) {
                try {
                    boolean delete = !Linq.exists(change.discreteUnits, item -> item.value > 0);
                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                    if (delete && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        callOrderLineManager.deleteProduct(callOrderId, change.productId, false);
                        refresh(true, false, true);
                        VaranegarApplication.getInstance().printElapsedTime("refresh order view");
                    } else {
                        SysConfigManager sysConfigManager = new SysConfigManager(context);
                        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                        BigDecimal total = new BigDecimal(0);
                        for (DiscreteUnit discreteUnit :
                                change.discreteUnits) {
                            total = total.add(BigDecimal.valueOf(discreteUnit.getTotalQty()));
                        }
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && backOfficeType == BackOfficeType.ThirdParty) {
                            if (orderLine.EditReasonId == null && total.compareTo(orderLine.OriginalTotalQty) < 0) {
                                OrderReturnReasonDialog editReasonDialog = new OrderReturnReasonDialog();
                                editReasonDialog.onItemSelected = reasonUniqueId -> {
                                    addItem(orderLine, change, reasonUniqueId);
                                };
                                editReasonDialog.show(getActivity().getSupportFragmentManager(), "PartialOrderActionDialog");
                            } else if (total.compareTo(orderLine.OriginalTotalQty) == 0) {
                                addItem(orderLine, change, null);
                            } else {
                                addItem(orderLine, change, orderLine.EditReasonId);
                            }
                        } else {
                            addItem(orderLine, change, null);
                        }
                    }
                } catch (UnknownBackOfficeException e) {
                    Timber.e(e);
                    CuteMessageDialog dialog = new CuteMessageDialog(context);
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.back_office_type_is_uknown);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                } catch (Exception e) {
                    Timber.e(e);
                    orderAdapter.notifyItemChanged(change.position);
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.error_saving_request);
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            }
        };

    }

    private void addItem(CustomerCallOrderOrderViewModel orderLine, OnItemQtyChangedHandler.QtyChange change, @Nullable UUID editReasonId) {
        try {
            OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
            ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
            ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(orderLine.ProductId);
            onHandQtyStock.ConvertFactors = productUnitsViewModel.ConvertFactor;
            onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
            if (orderLine.OnHandQty == null)
                orderLine.OnHandQty = BigDecimal.ZERO;
            onHandQtyStock.OnHandQty = orderLine.OnHandQty;
            if (orderLine.RemainedAfterReservedQty == null)
                orderLine.RemainedAfterReservedQty = BigDecimal.ZERO;
            onHandQtyStock.RemainedAfterReservedQty = orderLine.RemainedAfterReservedQty;
            if (orderLine.OrderPoint == null)
                orderLine.OrderPoint = BigDecimal.ZERO;
            onHandQtyStock.OrderPoint = orderLine.OrderPoint;
            if (orderLine.ProductTotalOrderedQty == null)
                orderLine.ProductTotalOrderedQty = BigDecimal.ZERO;
            onHandQtyStock.ProductTotalOrderedQty = orderLine.ProductTotalOrderedQty;
            if (orderLine.RequestBulkQty == null)
                onHandQtyStock.TotalQty = orderLine.TotalQty == null ? BigDecimal.ZERO : orderLine.TotalQty;
            else
                onHandQtyStock.TotalQty = orderLine.TotalQtyBulk == null ? BigDecimal.ZERO : orderLine.TotalQtyBulk;
            onHandQtyStock.HasAllocation = orderLine.HasAllocation;
            ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock, change.discreteUnits, null);
            add(orderLine, change, editReasonId);
            refresh(true, false, true);
            VaranegarApplication.getInstance().printElapsedTime("refresh order view");
        } catch (OnHandQtyWarning e) {
            Timber.e(e);
            orderAdapter.notifyItemChanged(change.position);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Warning);
            dialog.setMessage(e.getMessage());
            dialog.setTitle(R.string.warning);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            try {
                add(orderLine, change, editReasonId);
            } catch (Exception ex) {
                Timber.e(ex);
                orderAdapter.notifyItemChanged(change.position);
                CuteMessageDialog dialog2 = new CuteMessageDialog(getContext());
                dialog2.setIcon(Icon.Error);
                dialog2.setMessage(R.string.error_saving_request);
                dialog2.setTitle(R.string.error);
                dialog2.setPositiveButton(R.string.ok, null);
                dialog2.show();
            }
        } catch (OnHandQtyError e) {
            Timber.e(e);
            orderAdapter.notifyItemChanged(change.position);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(e.getMessage());
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            orderAdapter.notifyItemChanged(change.position);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }

    }


    private void add(CustomerCallOrderOrderViewModel orderLine, OnItemQtyChangedHandler.QtyChange change, @Nullable UUID editReasonId) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            callOrderLineManager.updateQty(orderLine.UniqueId, change.discreteUnits, null, null, true, editReasonId);
        else
            callOrderLineManager.addOrUpdateQty(orderLine.ProductId, change.discreteUnits, null, callOrderId, null, null, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNewFragment())
            new Handler().postDelayed(this::init, 200);
        else {
            refresh(false, false, false);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("afc863f4-0694-45df-b513-4fb131338c57"))
                startTempTablesProgressDialog();
            if (savedInstanceState.getBoolean("69d2ac5d-cc07-4b66-aa0f-cc09d55e3296"))
                startProductStockLevelProgressDialog();
            customerId = UUID.fromString(savedInstanceState.getString("eb6d7315-0bd0-420c-bf80-e0257c7b153a"));
            callOrderId = UUID.fromString(savedInstanceState.getString("cfa84e29-90a1-461c-aa59-d201f410ac7b"));
        } else {
            customerId = UUID.fromString(getArguments().getString("9c497998-18e6-4be9-ad80-984fcfb2169c"));
            callOrderId = UUID.fromString(getArguments().getString("cfa84e29-90a1-461c-aa59-d201f410ac7b"));
        }
        setRetainInstance(true);

        customerCallOrderModel = customerCallOrderManager.getItem(callOrderId);
        saveOrderUtility = new SaveOrderUtility(getContext());
        saveCallBack = new SaveOrderUtility.ISaveOrderCallback() {
            @Override
            public void onSuccess() {
                try {
                    loadCalls();
                    for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                        if (customerPaymentTypesViewModel.UniqueId != null && customerPaymentTypesViewModel.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId))
                            selectedPaymentType = customerPaymentTypesViewModel;
                    }
                    refresh(false, true, false);

                    if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                        saveContractorEventLocation();
                        InvoicePrintHelper printContractor = new InvoicePrintHelper(getVaranegarActvity(), customerId, OrderPrintType.contractor);
                        printContractor.start(null);
                    }

                    if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                        VaranegarApplication.getInstance().remove("CUSTOMER_ID_ORDER_PREVIEW");
                        getVaranegarActvity().popFragment();
                    }
                } catch (Exception ex) {
                    showErrorMessage(R.string.error_saving_request);
                }
            }

            @Override
            public void onError(String msg) {
                loadCalls();
                refresh(false, true, false);
                showErrorMessage(msg);
            }

            @Override
            public void onProcess(String msg) {

            }

            @Override
            public void onAlert(SaveOrderUtility.SaveOrderCallbackType type, String title, String msg, @Nullable SaveOrderUtility.IWarningCallBack callBack) {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                cuteMessageDialog.setTitle(title);
                cuteMessageDialog.setMessage(msg);
                if (type == SaveOrderUtility.SaveOrderCallbackType.NoSystemPayment) {
                    cuteMessageDialog.setIcon(Icon.Alert);
                    cuteMessageDialog.setNeutralButton(R.string.ok, view1 -> {
                        callBack.cancel();
                        systemCheckBox.setChecked(false);
                        paymentTypesSpinner.setVisibility(View.VISIBLE);
                        calcOnlineUsanceDay.setVisibility(View.GONE);
                    });
                    cuteMessageDialog.show();
                } else if (type == SaveOrderUtility.SaveOrderCallbackType.SelectPrize) {
                    showPrizeDialog();
                } else if (type == SaveOrderUtility.SaveOrderCallbackType.SelectReturnReason) {
                    showPinCodeDialogInSaveMode(callBack);
                } else {
                    cuteMessageDialog.setPositiveButton(R.string.ok, view -> callBack.onContinue());
                    cuteMessageDialog.setNegativeButton(R.string.cancel, view -> callBack.cancel());
                    cuteMessageDialog.show();
                }

            }

            @Override
            public void onCancel() {

            }
        };
        saveOrderUtility.setCallback(saveCallBack);


        return inflater.inflate(R.layout.fragment_customer_save_order, container, false);
    }

    private void startProductStockLevelProgressDialog() {
        productStockLevelProgressDialog = new ProgressDialog(getActivity());
        productStockLevelProgressDialog.setMessage(getActivity().getString(com.varanegar.vaslibrary.R.string.Inventory_update));
        productStockLevelProgressDialog.setCancelable(false);
        productStockLevelProgressDialog.show();
    }

    private void stopProductStockLevelProgressDialog() {
        if (productStockLevelProgressDialog != null && productStockLevelProgressDialog.isShowing()) {
            try {
                productStockLevelProgressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

    private void startTempTablesProgressDialog() {
        tempTablesProgressDialog = new ProgressDialog(getActivity());
        tempTablesProgressDialog.setTitle(R.string.please_wait);
        tempTablesProgressDialog.setMessage(getActivity().getString(R.string.calculating_customer_history_and_evc_statute));
        tempTablesProgressDialog.setCancelable(false);
        tempTablesProgressDialog.setProgress(0);
        tempTablesProgressDialog.setIndeterminate(false);
        tempTablesProgressDialog.setMax(5);
        tempTablesProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        tempTablesProgressDialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadCalls();
        customer = new CustomerManager(context).getItem(customerId);

        otherDiscount = new CustomerCallOrderManager(getContext()).getCustomerCallOrder(customerId, callOrderId).RoundOrderOtherDiscount;
        if (otherDiscount == null)
            otherDiscount = Currency.ZERO;

        View totalHeader = view.findViewById(R.id.total_header);
        if (totalHeader != null)
            totalHeader.setVisibility(View.INVISIBLE);

        SimpleToolbar toolbar = view.findViewById(R.id.simple_toolbar);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            TextView orderAmountColumn = view.findViewById(R.id.item_value_column_text_view);
            if (orderAmountColumn != null)
                orderAmountColumn.setText(R.string.returns);
            if (customerCallOrderModel.IsInvoice)
                toolbar.setTitle(getString(R.string.delivery_of_invoice) + " " + getString(R.string.no_label) + " " + customerCallOrderModel.SaleNoSDS + " - " + customer.CustomerName);
            else
                toolbar.setTitle(getString(R.string.delivery_of_draft) + " " + getString(R.string.no_label) + " " + customerCallOrderModel.SaleNoSDS + " - " + customer.CustomerName);
        } else {
            toolbar.setTitle(getString(R.string.save_order_request) + " " + customer.CustomerName + " (" + customer.CustomerCode + ")");
        }

        toolbar.setOnBackClickListener(view12 -> onBackPressed());

        toolbar.setOnMenuClickListener(R.drawable.ic_customer_status_24dp, v -> {
            CustomerAdditionalInfoDialog dialog = new CustomerAdditionalInfoDialog();
            Bundle bundle = new Bundle();
            bundle.putString("CUSTOMER_ID", customerId.toString());
            dialog.setArguments(bundle);
            dialog.show(getChildFragmentManager(), "CustomerOtherInfoDialog");
        });
        ((TextView) view.findViewById(R.id.debit_balance_text_view)).setText(HelperMethods.currencyToString(customer.RemainDebit));
        ((TextView) view.findViewById(R.id.credit_balance_text_view)).setText(HelperMethods.currencyToString(customer.RemainCredit));

        orderOptions = new ArrayList<>();
        orderOptions.add(new OrderOption<CustomerCallOrderOrderViewModel>(context).setProjection(ProductName).setName(R.string.product_name));
        orderOptions.add(new OrderOption<CustomerCallOrderOrderViewModel>(context).setProjection(SortId).setName(R.string.sort_request));
        orderOptions.add(new OrderOption<CustomerCallOrderOrderViewModel>(context).setProjection(ProductCode).setName(R.string.product_code));
        orderOptions.add(new OrderOption<CustomerCallOrderOrderViewModel>(context).setProjection(TotalQty).setName(R.string.total_qty));

        view.findViewById(R.id.sort_fab).setOnClickListener(view13 -> {
            final SearchBox<OrderOption<CustomerCallOrderOrderViewModel>> searchBox = new SearchBox<>();
            searchBox.setItems(orderOptions, (SearchBox.SearchMethod<OrderOption<CustomerCallOrderOrderViewModel>>) (item, text) -> item.getProjection().getSimpleName().contains(text));
            searchBox.show(getChildFragmentManager(), "SearchBox");
            searchBox.setOnItemSelectedListener((position, item) -> {
                orderAdapter.refreshAsync(new CustomerCallOrderOrderViewManager(context).getLinesQuery(callOrderId,
                        new OrderBy(item.getProjection(), OrderType.ASC)));
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("SortOrders", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("OrderOptionsPosition", position);
                editor.apply();
                searchBox.dismiss();
            });
        });
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            view.findViewById(R.id.discount_layout).setVisibility(View.GONE);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            view.findViewById(R.id.dist_status).setVisibility(View.VISIBLE);

        statusTextView = (TextView) view.findViewById(R.id.status_text_view);
        discountTextView = (TextView) view.findViewById(R.id.discount_text_view);
        addAmountTextView = (TextView) view.findViewById(R.id.add_amount_text_view);
        TextView dealerNameTextView = view.findViewById(R.id.dealer_name_text_view);
        TextView dealerMobileTextView = view.findViewById(R.id.dealer_mobile_text_view);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            view.findViewById(R.id.dealer_name_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.dealer_mobile_layout).setVisibility(View.VISIBLE);
        }
        commentImageView = view.findViewById(R.id.comment_image_view);
        commentImageView.setOnClickListener(view14 -> {
            if (view14 != null) {
                InputMethodManager imm = (InputMethodManager) view14.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(view14.getWindowToken(), 0);
            }
            try {
                updateCustomerCallOrder();
            } catch (Exception e) {
                showErrorMessage();
            }
        });
        commentEditText = view.findViewById(R.id.comment_edit_text);
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!text.isEmpty() && text.length() > 2)
                    commentImageView.setVisibility(View.VISIBLE);
                else
                    commentImageView.setVisibility(View.GONE);
            }
        });
        orderCostTextView = view.findViewById(R.id.order_cost_text_view);
        netAmountTextView = view.findViewById(R.id.net_amount_text_view);

        usanceDayLayout = view.findViewById(R.id.usance_day_layout);
        usanceDayTextView = view.findViewById(R.id.usance_day_text_view);
        usanceTextView = view.findViewById(R.id.usance_text_view);

        setupOrderAdapter();
        BaseRecyclerView orderRecyclerView = view.findViewById(R.id.order_recycler_view);
        orderRecyclerView.addItemDecoration(new DividerItemDecoration(context, R.color.grey_light, 1));
        orderRecyclerView.setAdapter(orderAdapter);
        setupToolbarButtons(view);

        dealerNameTextView.setText(customerCallOrderModel.DealerName);
        dealerMobileTextView.setText(customerCallOrderModel.DealerMobile);
        TextView localPaperNo = view.findViewById(R.id.local_paper_no);
        View localPaperNoLayout = view.findViewById(R.id.local_paper_no_layout);

        if (customerCallOrderModel == null)
            throw new NullPointerException("Customer call order is null");
        if (customerCallOrderModel.Comment != null) {
            commentEditText.setText(customerCallOrderModel.Comment);
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            localPaperNoLayout.setVisibility(View.GONE);
        else if (customerCallOrderModel.LocalPaperNo != null)
            localPaperNo.setText(customerCallOrderModel.LocalPaperNo);

        // Get Payment Types and Show to User
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(context);
        final SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            customerPaymentTypes.clear();
            customerPaymentTypes = customerPaymentTypesViewManager.getCustomerPaymentType(customerId);
            List<CustomerPaymentTypesViewModel> customerPaymentTypesForShow = new ArrayList<>();
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);

            for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                if (!(SysConfigManager.compare(sysConfigModel, customerPaymentTypesViewModel.PaymentTypeOrderGroupUniqueId)))
                    customerPaymentTypesForShow.add(customerPaymentTypesViewModel);
            }
            SysConfigModel checkSystemSysConfig = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
            SysConfigModel calcOnline = sysConfigManager.read(ConfigKey.OnliveEvc, SysConfigManager.cloud);
            paymentTypesSpinner = view.findViewById(R.id.payment_types_spinner);
            if (customerPaymentTypesForShow.size() > 0) {
                paymentTypesSpinner.setup(getChildFragmentManager(), customerPaymentTypesForShow, (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                });
            }
            systemCheckBox = view.findViewById(R.id.check_system);
            calcOnlineUsanceDay = view.findViewById(R.id.calc_online_usance_day);
            initSystemCheck();
            if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
                systemCheckBox.setChecked(true);
                checkSystemCheck(editor);
                paymentTypesSpinner.setVisibility(View.GONE);
                calcOnlineUsanceDay.setVisibility(View.VISIBLE);
            } else {
                systemCheckBox.setChecked(false);
                paymentTypesSpinner.setVisibility(View.VISIBLE);
                calcOnlineUsanceDay.setVisibility(View.GONE);
            }

            if (checkSystemSysConfig != null && SysConfigManager.compare(calcOnline, true)) {
                List<CustomerPaymentTypesViewModel> customerPaymentTypesViewModels = customerPaymentTypesViewManager.getSystemCheckPaymentTypes(customerId, checkSystemSysConfig.Value);
                if (customerPaymentTypesViewModels != null && customerPaymentTypesViewModels.size() > 0) {
                    for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel :
                            customerPaymentTypesViewModels) {
                        if (!customerPaymentTypes.contains(customerPaymentTypesViewModel))
                            customerPaymentTypes.add(customerPaymentTypesViewModel);
                    }
                    systemCheckBox.setVisibility(View.VISIBLE);
                    if (customerPaymentTypesForShow.size() == 0) {
                        disableCheckBox = false;
                        systemCheckBox.setChecked(true);
                        editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", true);
                        editor.apply();
                        checkSystemCheck(editor);
                        systemCheckBox.setEnabled(false);
                    } else {
                        disableCheckBox = true;
                        systemCheckBox.setOnClickListener(view1 -> {
                            if (systemCheckBox.isChecked()) {
                                editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", true);
                                editor.apply();
                                checkSystemCheck(editor);
                            } else {
                                editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false);
                                editor.apply();
                                paymentTypesSpinner.setVisibility(View.VISIBLE);
                                calcOnlineUsanceDay.setVisibility(View.GONE);
                                if (defaultPayment == -1)
                                    showPaymentTypeDialog();
                            }
                        });
                    }
                } else
                    systemCheckBox.setVisibility(View.GONE);
            } else {
                systemCheckBox.setVisibility(View.GONE);
            }

            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.ThirdParty) && (customer.RealName != null)) {
                defaultPayment = Linq.findFirstIndex(paymentTypesSpinner.getItems(), item -> customer.RealName.equalsIgnoreCase(item.UniqueId.toString()));
            }
            if (defaultPayment != -1) {
                paymentTypesSpinner.selectItem(defaultPayment);
            } else {
                if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
                    if (customerCallOrderModel != null && customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
                        for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                            if (customerPaymentTypesViewModel.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId))
                                selectedPaymentType = customerPaymentTypesViewModel;
                        }
                    } else {
                        selectedPaymentType = customerPaymentTypes.get(0);
                    }
                } else {
                    if (customerCallOrderModel != null && customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
                        int p = Linq.findFirstIndex(paymentTypesSpinner.getItems(), item -> item.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId));
                        paymentTypesSpinner.selectItem(p);
                    } else {
                        paymentTypesSpinner.selectItem(0);
                    }
                }
            }
            paymentTypesSpinner.setOnItemSelectedListener((position, item) -> {
                UUID oldPaymentType = customerCallOrderModel.OrderPaymentTypeUniqueId;
                UUID newPaymentType = item.UniqueId;
                customerCallOrderModel.OrderPaymentTypeUniqueId = item.UniqueId;
                try {
                    updateCustomerCallOrder();
                    extractAndCalcCustomerPrice();
                    DiscountConditionManager discountConditionManager = new DiscountConditionManager(context);
                    boolean existDiscountItemCountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(true);
                    boolean existDiscountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(false);
                    boolean paymentTypeChanged = !oldPaymentType.equals(newPaymentType);
                    if (paymentTypeChanged && (existDiscountItemCountBaseOnPaymentType || existDiscountBaseOnPaymentType))
                        refresh(existDiscountItemCountBaseOnPaymentType, false, existDiscountBaseOnPaymentType);
                } catch (Exception e) {
                    showErrorMessage();
                }
            });

        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {

            if (!(VaranegarApplication.is(VaranegarApplication.AppId.Contractor))) {
                LinearLayout linearLayout = view.findViewById(R.id.delivery_date_picker);
                linearLayout.setVisibility(View.VISIBLE);
                deliveryDateItem = view.findViewById(R.id.delivery_date_item);
                view.findViewById(R.id.delivery_date_picker).setVisibility(View.VISIBLE);
                calendarImageView = view.findViewById(R.id.calendar_image_view);
                if (customerCallOrderModel.DeliveryDate != null) {
                    deliveryDateItem.setValue(DateHelper.toString(customerCallOrderModel.DeliveryDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context)));
                }
                calendarImageView.setOnClickListener(view15 -> DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(context), calendar -> {
                    // SAP: We Can Edit DeliveryDate Just For 7 Days Later
                    Calendar minDeadLineTimeForThirdParty = Calendar.getInstance();
                    minDeadLineTimeForThirdParty.set(Calendar.HOUR_OF_DAY, 0);
                    minDeadLineTimeForThirdParty.set(Calendar.MINUTE, 0);
                    minDeadLineTimeForThirdParty.set(Calendar.SECOND, 0);
                    minDeadLineTimeForThirdParty.set(Calendar.MILLISECOND, 0);
                    Calendar maxDeadLineTimeForThirdParty = Calendar.getInstance();
                    maxDeadLineTimeForThirdParty.set(Calendar.HOUR_OF_DAY, 23);
                    maxDeadLineTimeForThirdParty.set(Calendar.MINUTE, 59);
                    maxDeadLineTimeForThirdParty.set(Calendar.SECOND, 59);
                    maxDeadLineTimeForThirdParty.set(Calendar.MILLISECOND, 999);
                    maxDeadLineTimeForThirdParty.add(Calendar.DAY_OF_YEAR, 7);
                    SysConfigManager sysConfigManager1 = new SysConfigManager(getContext());
                    OwnerKeysWrapper ownerKeysWrapper = sysConfigManager1.readOwnerKeys();
                    if (ownerKeysWrapper.isZarMakaron() && (calendar.getTime().after(maxDeadLineTimeForThirdParty.getTime()) || calendar.getTime().before(minDeadLineTimeForThirdParty.getTime()))) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setMessage("تاریخ مجاز نیست");
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    } else {
                        customerCallOrderModel.DeliveryDate = calendar.getTime();
                        deliveryDateItem.setValue(DateHelper.toString(calendar, DateFormat.Date));
                        try {
                            updateCustomerCallOrder();
                        } catch (Exception e) {
                            showErrorMessage();
                        }
                    }
                }));
            }

            // Get Order Types and Show to User
            CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(context);
            List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
            orderTypesSpinner = view.findViewById(R.id.order_types_spinner);
            try {
                if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Rastak))
                    orderTypesSpinner.setTitle(getString(R.string.sell_type));
                else
                    orderTypesSpinner.setTitle(getString(R.string.order_type));
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
            }

            orderTypesSpinner.setVisibility(View.VISIBLE);
            orderTypesSpinner.setup(getChildFragmentManager(), customerOrderTypeModels, (item, text) -> {
                String str = HelperMethods.persian2Arabic(text);
                if (str == null)
                    return true;
                str = str.toLowerCase();
                return item.toString().toLowerCase().contains(str);
            });
            if (customerCallOrderModel.OrderTypeUniqueId != null) {
                int p = Linq.findFirstIndex(customerOrderTypeModels, item -> item.UniqueId.equals(customerCallOrderModel.OrderTypeUniqueId));
                orderTypesSpinner.selectItem(p);
            } else
                orderTypesSpinner.selectItem(0);
            orderTypesSpinner.setOnItemSelectedListener((position, selectedOrderType) -> {
                customerCallOrderModel.OrderTypeUniqueId = selectedOrderType.UniqueId;
                try {
                    if (selectedOrderType.UniqueId.equals(CustomerOrderTypesManager.OrderType24))
                        customerCallOrderModel.DeliveryDate = new Date(new Date().getTime() + 24 * 3600 * 1000);
                    if (selectedOrderType.UniqueId.equals(CustomerOrderTypesManager.OrderType48))
                        customerCallOrderModel.DeliveryDate = new Date(new Date().getTime() + 48 * 3600 * 1000);
                    deliveryDateItem.setValue(DateHelper.toString(customerCallOrderModel.DeliveryDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context)));

                    if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                        if (selectedOrderType.UniqueId.toString().equals("4cc866f9-eb19-4c76-8a41-f8207104cdbf")) {
                            linearLayout.setVisibility(View.VISIBLE);
                            orderTypesSpinner.setEnabled(false);
                        } else {
                            contractorDiscountTextView.setText("0");
                            linearLayout.setVisibility(View.GONE);
                            otherDiscount = Currency.ZERO;
                            otherPercent = Currency.ZERO;
                            contractorDiscountEditText.setText("");
                        }
                    }
                    updateCustomerCallOrder();
                    extractAndCalcCustomerPrice();
                } catch (Exception e) {
                    showErrorMessage();
                }
            });
        }

        SysConfigModel priceClass = sysConfigManager.read(ConfigKey.PriceClassEnabled, SysConfigManager.cloud);
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Rastak && SysConfigManager.compare(priceClass, true)) {
                priceClassSpinner = view.findViewById(R.id.price_class_spinner);
                priceClassSpinner.setVisibility(View.VISIBLE);
                PriceClassVnLiteManager priceClassVnLiteManager = new PriceClassVnLiteManager(context);
                List<PriceClassVnLiteModel> classes = priceClassVnLiteManager.getPriceClasses();
                PriceClassVnLiteModel noClass = new PriceClassVnLiteModel();
                noClass.PriceClassName = "";
                noClass.PriceClassRef = -1;
                noClass.UniqueId = null;
                classes.add(0, noClass);
                priceClassSpinner.setup(getChildFragmentManager(), classes, null);
                if (customerCallOrderModel.PriceClassId != null) {
                    int p = Linq.findFirstIndex(classes, item -> item.UniqueId != null && item.UniqueId.equals(customerCallOrderModel.PriceClassId));
                    priceClassRef = classes.get(p).PriceClassRef;
                    priceClassSpinner.selectItem(p);
                } else {
                    priceClassSpinner.selectItem(0);
                }
                priceClassSpinner.setOnItemSelectedListener((position, item) -> {
                    customerCallOrderModel.PriceClassId = item.UniqueId;
                    priceClassRef = item.PriceClassRef;
                    try {
                        updateCustomerCallOrder();
                        extractAndCalcCustomerPrice();
                    } catch (Exception ex) {
                        showErrorMessage();

                    }
                });

            }
        } catch (UnknownBackOfficeException e) {
            Timber.wtf(e);
        }

        customerCallOrderModel.OrderTypeUniqueId = getOrderType() == null ? null : getOrderType().UniqueId;
        if (!sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
            CustomerPaymentTypesViewModel customerPaymentTypesViewModel = paymentTypesSpinner.getSelectedItem();
            customerCallOrderModel.OrderPaymentTypeUniqueId = customerPaymentTypesViewModel != null ? customerPaymentTypesViewModel.UniqueId : null;
        } else {
            refreshUsanceDayAverage();
        }
        try {
            updateCustomerCallOrder();
        } catch (Exception ex) {
            Timber.e(ex);
            showErrorMessage();
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {

            contractorDiscountEditText = view.findViewById(R.id.contractor_discount_show_edit_text);

            contractorDiscountImageView = view.findViewById(R.id.contractor_discount_image_view);
            contractorNetAmountTextView = view.findViewById(R.id.contractor_net_amount_text_view);
            contractorDiscountTextView = view.findViewById(R.id.contractor_discount_text_view);
            linearLayout = view.findViewById(R.id.contractor_discount_layout);
            final List<DiscountTypeModel> discountTypeModels = new ArrayList<>();
            final DiscountTypeModel percentModel = new DiscountTypeModel();
            percentModel.UniqueId = UUID.fromString(percent);
            percentModel.lable = getString(R.string.percent);
            discountTypeModels.add(percentModel);
            DiscountTypeModel amountModel = new DiscountTypeModel();
            amountModel.UniqueId = UUID.fromString(cash);
            amountModel.lable = getString(R.string.amount);
            discountTypeModels.add(amountModel);
            contractorDiscountImageView = view.findViewById(R.id.contractor_discount_image_view);
            if (customerCallOrderModel != null && customerCallOrderModel.OrderTypeUniqueId != null && customerCallOrderModel.OrderTypeUniqueId.toString().equals("4cc866f9-eb19-4c76-8a41-f8207104cdbf")) {
                linearLayout.setVisibility(View.VISIBLE);
            }
            contractorDiscountImageView.setOnClickListener(view16 -> {
                final SearchBox<DiscountTypeModel> searchBox = new SearchBox<>();
                searchBox.setItems(discountTypeModels, (SearchBox.SearchMethod<DiscountTypeModel>) (item, text) -> {
                    text = text.toLowerCase();
                    return item.toString().toLowerCase().contains(text);
                });
                searchBox.disableSearch();
                searchBox.setOnItemSelectedListener((position, item) -> {
                    SharedPreferences discountTypeShP = getContext().getSharedPreferences("DiscountType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = discountTypeShP.edit();
                    final String discountType = discountTypeShP.getString("discountTypeId", percent);
                    if (item.UniqueId.toString().equals(percent)) {
                        editor1.putString("discountTypeId", percent);
                        editor1.apply();
                        if (discountType.equals(cash))
                            contractorDiscountEditText.setText("");
                    } else if (item.UniqueId.toString().equals(cash)) {
                        editor1.putString("discountTypeId", cash);
                        editor1.apply();
                        if (discountType.equals(percent))
                            contractorDiscountEditText.setText("");
                    }
                    discountCalculatorForContractor();
                    searchBox.dismiss();
                });
                searchBox.show(getChildFragmentManager(), "discount type");
            });
            discountCalculatorForContractor();

        }
        refresh(false, false, false);
    }

    private void checkSystemCheck(final SharedPreferences.Editor editor) {
        paymentTypesSpinner.setVisibility(View.GONE);
        calcOnlineUsanceDay.setVisibility(View.VISIBLE);
        editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", true);
        editor.apply();
        calcOnlineUsanceDay.setOnClickListener(view -> CalcPromotion.calcPromotionV3WithDialog(null, null, getActivity(), callOrderId, customerId, EVCType.TOSELL, false, false, true, new PromotionCallback() {
            @Override
            public void onSuccess(CustomerCallOrderPromotion data) {
                try {
                    UUID oldPaymentType = customerCallOrderModel.OrderPaymentTypeUniqueId;
                    UUID newPaymentType = data.OrderPaymentTypeId;
                    PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
                    PaymentTypeOrderModel paymentTypeOrderModel = paymentOrderTypeManager.getItem(new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl).whereAnd(Criteria.equals(PaymentTypeOrder.UniqueId, data.OrderPaymentTypeId)));
                    if (paymentTypeOrderModel != null) {
                        for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                            if (customerPaymentTypesViewModel.UniqueId.equals(paymentTypeOrderModel.UniqueId))
                                selectedPaymentType = customerPaymentTypesViewModel;
                        }
                        if (customerCallOrderModel == null || data == null || data.OrderPaymentTypeId == null) {
                            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                            cuteMessageDialog.setMessage(R.string.error_on_usance_day);
                            cuteMessageDialog.setNeutralButton(R.string.ok, null);
                            cuteMessageDialog.show();
                            if (customerCallOrderModel == null)
                                Timber.e("customerCallOrderModel == null");
                            if (data == null)
                                Timber.e("data == null");
                            else if (data.OrderPaymentTypeId == null)
                                Timber.e("data.OrderPaymentTypeId == null");
                        } else {
                            try {
                                customerCallOrderModel.OrderPaymentTypeUniqueId = data.OrderPaymentTypeId;
                                CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                                List<CallOrderLineModel> customerCallOrderModels = callOrderLineManager.getOrderLines(callOrderId);
                                List<CallOrderLineModel> editedCustomerCallOrderModels = new ArrayList<>();
                                for (CustomerCallOrderLinePromotion customerCallOrderLinePromotion : data.LinesWithPromo) {
                                    for (CallOrderLineModel callOrderLineModel : customerCallOrderModels) {
                                        if (callOrderLineModel.UniqueId.equals(customerCallOrderLinePromotion.UniqueId)) {
                                            callOrderLineModel.PayDuration = customerCallOrderLinePromotion.PayDuration;
                                            editedCustomerCallOrderModels.add(callOrderLineModel);
                                        }
                                    }
                                }
                                callOrderLineManager.update(editedCustomerCallOrderModels);
                                updateCustomerCallOrder();
                                extractAndCalcCustomerPrice();
                                DiscountConditionManager discountConditionManager = new DiscountConditionManager(context);
                                boolean existDiscountItemCountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(true);
                                boolean existDiscountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(false);
                                boolean paymentTypeChanged = !oldPaymentType.equals(newPaymentType);
                                refresh((paymentTypeChanged && existDiscountItemCountBaseOnPaymentType), true, (paymentTypeChanged && existDiscountBaseOnPaymentType));
                            } catch (Exception e) {
                                showErrorMessage();
                            }
                        }

                    } else {
                        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(context);
                        cuteMessageDialog.setIcon(Icon.Alert);
                        cuteMessageDialog.setMessage(R.string.system_payment_not_exist);
                        cuteMessageDialog.setNeutralButton(R.string.ok, view1 -> {
                            systemCheckBox.setChecked(false);
                            editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false);
                            editor.apply();
                            paymentTypesSpinner.setVisibility(View.VISIBLE);
                            calcOnlineUsanceDay.setVisibility(View.GONE);
                        });
                        cuteMessageDialog.show();
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

            }

            @Override
            public void onFailure(String error) {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(context);
                cuteMessageDialog.setIcon(Icon.Error);
                cuteMessageDialog.setMessage(error);
                cuteMessageDialog.setNeutralButton(R.string.ok, null);
                cuteMessageDialog.show();
            }

            @Override
            public void onProcess(String msg) {

            }
        }));
    }

    private void discountCalculatorForContractor() {
        SharedPreferences discountTypeShP = getContext().getSharedPreferences("DiscountType", Context.MODE_PRIVATE);
        final String discountType = discountTypeShP.getString("discountTypeId", percent);
        if (discountType.equals(percent)) {
            contractorDiscountEditText.setHint(R.string.percent_discount);
            maxValu = new Currency(100);
            percentType = true;
        } else if (discountType.equals(cash)) {
            contractorDiscountEditText.setHint(R.string.amount_discount);
            if (orderAmount != null && orderAmount.TotalAmount != null)
                maxValu = orderAmount.TotalAmount;
            else
                maxValu = Currency.ZERO;
            percentType = false;
        }
    }

    protected void showErrorMessage() {
        Activity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(context);
            cuteMessageDialog.setMessage(R.string.error_saving_request);
            cuteMessageDialog.setTitle(R.string.error);
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setPositiveButton(R.string.close, null);
            cuteMessageDialog.show();
        }
    }
 private void showErrorDialog(String err) {
     Context context = getContext();
     if (context != null) {
         CuteMessageDialog dialog = new CuteMessageDialog(context);
         dialog.setTitle(R.string.error);
         dialog.setIcon(Icon.Error);
         dialog.setMessage(err);
         dialog.setPositiveButton(R.string.ok, null);
         dialog.show();
     }
 }
    protected void showErrorMessage(@StringRes int str) {
        Activity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(context);
            cuteMessageDialog.setMessage(str);
            cuteMessageDialog.setTitle(R.string.error);
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setPositiveButton(R.string.close, null);
            cuteMessageDialog.show();
        }
    }

    protected void showErrorMessage(String str) {
        Activity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
            cuteMessageDialog.setMessage(str);
            cuteMessageDialog.setTitle(R.string.error);
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setPositiveButton(R.string.close, null);
            cuteMessageDialog.show();
        }
    }

    protected void updateCustomerCallOrder() throws ValidationException, DbException {
        customerCallOrderModel.Comment = commentEditText.getText().toString();
        customerCallOrderModel.RoundOrderOtherDiscount = otherDiscount;
        customerCallOrderModel.EndTime = new Date();
        try {
            long affectedRows = customerCallOrderManager.update(customerCallOrderModel);
            Timber.e("update customer call order ", affectedRows);
        } catch (Exception ex) {
            Timber.e(ex, "Error on update customer call order");
            throw ex;
        }
    }

    private void init() {
        toolbar.setVisibility(View.GONE);
        initSystemCheck();
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel updateOnHandQty = sysConfigManager.read(ConfigKey.OnlineRefreshStockLevel, SysConfigManager.cloud);
        if (SysConfigManager.compare(updateOnHandQty, true) && Connectivity.isConnected(context)) {
            Timber.d("OnlineRefreshStockLevel is true");
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && !hasCallOrder()) {
                startProductStockLevelProgressDialog();
                PingApi pingApi = new PingApi();
                pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                    @Override
                    public void done(String ipAddress) {
                        ProductUpdateFlow productUpdateFlow = new ProductUpdateFlow(context);
                        productUpdateFlow.syncProductsAndInitPromotionDb(new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                prepareCalculations();
                                stopProductStockLevelProgressDialog();
                            }

                            @Override
                            protected void onFailure(String error) {
                                prepareCalculations();
                                stopProductStockLevelProgressDialog();
                                if (getVaranegarActvity() != null && !getVaranegarActvity().isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(context);
                                    dialog.setMessage(error);
                                    dialog.setTitle(R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void failed() {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed()) {
                            activity.showSnackBar(R.string.error_connecting_to_server, MainVaranegarActivity.Duration.Short);
                            stopProductStockLevelProgressDialog();
                            prepareCalculations();
                        }
                    }
                });

            } else
                prepareCalculations();
        } else {
            prepareCalculations();
        }

    }

    protected void loadCalls() {
        CustomerCallManager callManager = new CustomerCallManager(context);
        calls = callManager.loadCalls(customerId);
        customerCallOrderModel = customerCallOrderManager.getCustomerCallOrder(customerId, callOrderId);
    }

    private boolean hasCallOrder() {
        CustomerCallManager callManager = new CustomerCallManager(context);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            return callManager.hasDistCall(calls, callOrderId);
        else
            return callManager.hasOrderCall(calls, callOrderId);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private boolean hasDistCall(CustomerCallType callType) {
        CustomerCallManager callManager = new CustomerCallManager(context);
        return callManager.hasDistCall(calls, callOrderId, callType);

    }

    private boolean hasPayment() {
        final CustomerCallManager callManager = new CustomerCallManager(context);
        return callManager.hasPaymentCall(calls);
    }

    private boolean isDataSent() {
        return customerCallOrderManager.isDataSent(callOrderId);
    }

    private boolean isConfirmed() {
        CustomerCallManager callManager = new CustomerCallManager(context);
        return callManager.isConfirmed(calls, callOrderId);
    }


    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }
    private void setupToolbarButtons(View view) {
        toolbar = (CuteToolbar) view.findViewById(R.id.options_toolbar);
        toolbar.setVisibility(View.GONE);

        CuteButton reportsButton = new CuteButton();
        reportsButton.setIcon(R.drawable.ic_report_white_36dp);
        reportsButton.setTitle(R.string.customer_reports);
        reportsButton.setEnabled(() -> !hasCallOrder());
        reportsButton.setOnClickListener(() -> getVaranegarActvity().toggleDrawer());

        /**
         * دکمه ثبت سفارش
         */
        CuteButton okButton = new CuteButton();
        okButton.setEnabled(() -> !hasCallOrder());
        okButton.setTitle(R.string.save_order_request);
        okButton.setIcon(R.drawable.ic_done_white_36dp);
        okButton.setOnClickListener(() -> {
            startProgressDialog();
            List<String> customerCode=new ArrayList<>();
            customerCode.add(customer.CustomerCode);
            //                CustomerApi api = new CustomerApi(getContext());
//                api.runWebRequest(api.CheckCustomerCredits(customerCode), new WebCallBack<Boolean>() {
//                    @Override
//                    protected void onFinish() {
//                        stopProgressDialog();
//                    }
//
//                    @Override
//                    protected void onSuccess(Boolean result, Request request) {
//                        if (isResumed()) {
//                            if (result) {
//                                saveOrder();
//                                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
//                                SysConfigModel sendPromotionPreview = sysConfigManager.read(ConfigKey.SendPromotionPreview, SysConfigManager.cloud);
//                                if (SysConfigManager.compare(sendPromotionPreview, true)) {
//                                    UUID customerIdOrderPreview = VaranegarApplication.getInstance().tryRetrieve("CUSTOMER_ID_ORDER_PREVIEW", false);
//                                    if (!customerId.equals(customerIdOrderPreview)) {
//                                        CustomerOrderPreviewFragment previewFragment = new CustomerOrderPreviewFragment();
//                                        previewFragment.setArguments(customerId, callOrderId);
//                                        getVaranegarActvity().pushFragment(previewFragment);
//                                        return;
//                                    }
//                                }
//                            } else {
//                                CuteMessageDialog confirmDialog1 = new CuteMessageDialog(getContext());
//                                confirmDialog1.setTitle(R.string.warning);
//                                confirmDialog1.setMessage("مشتری فاقد اعتبار می باشد \n آیا تمایل به ثبت سفارش دارید دارید؟");
//                                confirmDialog1.setIcon(Icon.Error);
//                                confirmDialog1.setNeutralButton(R.string.cancel, null);
//                                confirmDialog1.setPositiveButton(R.string.yes_i_take_responsibility, v1 -> {
//                                    saveOrder();
//                                    SysConfigManager sysConfigManager = new SysConfigManager(getContext());
//                                    SysConfigModel sendPromotionPreview = sysConfigManager.read(ConfigKey.SendPromotionPreview, SysConfigManager.cloud);
//                                    if (SysConfigManager.compare(sendPromotionPreview, true)) {
//                                        UUID customerIdOrderPreview = VaranegarApplication.getInstance().tryRetrieve("CUSTOMER_ID_ORDER_PREVIEW", false);
//                                        if (!customerId.equals(customerIdOrderPreview)) {
//                                            CustomerOrderPreviewFragment previewFragment = new CustomerOrderPreviewFragment();
//                                            previewFragment.setArguments(customerId, callOrderId);
//                                            getVaranegarActvity().pushFragment(previewFragment);
//                                            return;
//                                        }
//                                    }
//                                });
//                                confirmDialog1.show();
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    protected void onApiFailure(ApiError error, Request request) {
//                        String err = WebApiErrorBody.log(error, getContext());
//
//
//                        if (isResumed()) {
//                            showErrorDialog(err);
//                        }
//                    }
//
//                    @Override
//                    protected void onNetworkFailure(Throwable t, Request request) {
//                        if (isResumed()) {
//                            showErrorDialog(getString(R.string.network_error));
//
//                        }
//                    }
//                });
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel sendPromotionPreview = sysConfigManager.read(ConfigKey.SendPromotionPreview, SysConfigManager.cloud);
                if (SysConfigManager.compare(sendPromotionPreview, true)) {
                    UUID customerIdOrderPreview = VaranegarApplication.getInstance().tryRetrieve("CUSTOMER_ID_ORDER_PREVIEW", false);
                    if (!customerId.equals(customerIdOrderPreview)) {
                        CustomerOrderPreviewFragment previewFragment = new CustomerOrderPreviewFragment();
                        previewFragment.setArguments(customerId, callOrderId);
                        getVaranegarActvity().pushFragment(previewFragment);
                        return;
                    }
                }

            }if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor) && otherDiscount.compareTo(orderAmount.TotalAmount) > 0) {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                cuteMessageDialog.setIcon(Icon.Error);
                cuteMessageDialog.setTitle(R.string.error);
                cuteMessageDialog.setMessage(R.string.discount_can_not_more_amount);
                cuteMessageDialog.setNegativeButton(R.string.ok, null);
                cuteMessageDialog.show();
            } else{
                saveOrder();
            }
        });

        CuteButton searchButton = new CuteButton();
        searchButton.setEnabled(() -> !hasCallOrder());
        searchButton.setOnClickListener(() -> {
            final SearchBox<ProductOrderViewModel> productSearchBox = new SearchBox<>();
            productSearchBox.setPreprocess(text -> {
                if (text != null && !text.isEmpty())
                    text = text.toLowerCase();
                if (text != null && !text.isEmpty())
                    text = HelperMethods.persian2Arabic(text);
                if (text != null && !text.isEmpty())
                    text = HelperMethods.convertToEnglishNumbers(text);
                return text;
            });
            productSearchBox.setTokenizer(new SimpleTokenizer());
            productSearchBox.setItems(productList, (SearchBox.SearchMethodTokenized<ProductOrderViewModel>) (item, keyWords) -> {
                if (keyWords == null || keyWords.length == 0)
                    return true;
                boolean ok = true;
                for (String reg : keyWords) {
                    if (!item.ProductCode.toLowerCase().contains(reg) && !item.ProductName.toLowerCase().contains(reg))
                        ok = false;
                }
                return ok;
            });
            productSearchBox.show(getVaranegarActvity().getSupportFragmentManager(), "ProductSearchBox");
            productSearchBox.setOnItemSelectedListener((position, productOrderViewModel) -> {
                productSearchBox.dismiss();
                final CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = Linq.findFirst(orderAdapter.getItems(),
                        item -> item.ProductId.equals(productOrderViewModel.UniqueId));

                List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
                OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(context);
                if (customerCallOrderOrderViewModel != null) {
                    orderLineQtyModels = orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
                }
                OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
                try {
                    final CalculatorHelper calculatorHelper = new CalculatorHelper(context);
                    final OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                    ProductUnitsViewModel productUnitsViewModel = productUnitsHashMap.get(productOrderViewModel.UniqueId);
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
                    orderCalculatorForm.onCalcFinish = (discreteUnits, bulkUnit1, batchQtyList) -> onAddItem(productOrderViewModel, discreteUnits, bulkUnit1, batchQtyList);
                    orderCalculatorForm.show(getChildFragmentManager(), "2af40365-a4db-4afb-bb13-2a9896803d92");
                } catch (ProductUnitViewManager.UnitNotFoundException e) {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        activity.showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                    }
                    Timber.e(e);
                }
            });
        });
        searchButton.setIcon(R.drawable.ic_search_white_36dp);
        searchButton.setTitle(R.string.fast_search);

        CuteButton listButton = new CuteButton();
        listButton.setTitle(R.string.product_list);
        listButton.setIcon(R.drawable.ic_view_list_white_36dp);
        listButton.setEnabled(() -> !hasCallOrder());
        listButton.setOnClickListener(() -> {
            ProductGroupFragment productGroupFragment = new ProductGroupFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString("1c886632-a88a-4e73-9164-f6656c219917", callOrderId.toString());
            bundle2.putString("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId.toString());
            bundle2.putString("b505233a-aaec-4c3c-a7ec-8fa08b940e74", customerCallOrderModel.OrderTypeUniqueId.toString());
            productGroupFragment.setArguments(bundle2);
            getVaranegarActvity().pushFragment(productGroupFragment);
        });

        CuteButton albumButton = new CuteButton();
        albumButton.setIcon(R.drawable.ic_photo_album_white_36dp);
        albumButton.setTitle(R.string.product_catalog);
        albumButton.setEnabled(() -> !hasCallOrder());
        albumButton.setOnClickListener(() -> new CatalogueHelper(getVaranegarActvity()).openCatalogue(callOrderId, customerId));

        CuteButton previewButton = new CuteButton();
        previewButton.setOnClickListener(() -> {
            CustomerOrderPreviewFragment previewFragment = new CustomerOrderPreviewFragment();
            previewFragment.setArguments(customerId, callOrderId);
            getVaranegarActvity().pushFragment(previewFragment);
        });
        previewButton.setEnabled(() -> !hasCallOrder());
        previewButton.setTitle(R.string.preview);
        previewButton.setIcon(R.drawable.ic_remove_red_eye_white_36dp);


        CuteButton cancelBtn = new CuteButton();
        cancelBtn.setOnClickListener(() -> {
            if (VasHelperMethods.canNotEditOperationAfterPrint(context, customerId)) {
                CuteMessageDialog alert = new CuteMessageDialog(context);
                alert.setPositiveButton(R.string.yes, null);
                alert.setIcon(Icon.Error);
                alert.setTitle(R.string.error);
                alert.setMessage(R.string.can_not_edit_customer_operation_after_print);
                alert.show();
            } else if (isConfirmed()) {
                CuteMessageDialog alert = new CuteMessageDialog(context);
                alert.setPositiveButton(R.string.yes, null);
                alert.setIcon(Icon.Error);
                alert.setTitle(R.string.error);
                alert.setMessage(R.string.customer_operation_is_confirmed);
                alert.show();
            } else {
                if (hasPayment()) {
                    CuteMessageDialog alert = new CuteMessageDialog(context);
                    alert.setPositiveButton(R.string.yes, view1 -> removeCallOrder());
                    alert.setNegativeButton(R.string.no, null);
                    alert.setIcon(Icon.Warning);
                    alert.setTitle(R.string.warning);
                    alert.setMessage(R.string.the_customer_payment_will_be_unconfirmed_do_you_continue);
                    alert.show();
                } else {
                    removeCallOrder();
                }
            }
        });
        cancelBtn.setEnabled(() -> hasCallOrder() && !isDataSent());
        cancelBtn.setTitle(R.string.cancel);
        cancelBtn.setIcon(R.drawable.ic_cancel_black_36dp);

        List<CuteButton> buttons = new ArrayList<>();
        buttons.add(reportsButton);


        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
                buttons.add(searchButton);

            buttons.add(albumButton);
            buttons.add(listButton);

        } else {
            CuteButton returnBtn = new CuteButton();
            returnBtn.setOnClickListener(this::showPinCodeDialogInCompeleteReturnMode);
            returnBtn.setEnabled(() -> {
                if (customerCallOrderModel == null)
                    return false;
                if (customerCallOrderModel.IsInvoice)
                    return false;

                NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getActivity());
                List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getDistOrderReturnReason();
                if (noSalesReasons.size() < 1)
                    return false;

                return !hasCallOrder();
            });
            returnBtn.setTitle(R.string.complete_return);
            returnBtn.setIcon(R.drawable.ic_return_black_36dp);
            buttons.add(returnBtn);

            CuteButton lackOfDeliveryBtn = new CuteButton();
            lackOfDeliveryBtn.setOnClickListener(this::saveDistLackOfDelivery);
            lackOfDeliveryBtn.setEnabled(() -> {
                NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getActivity());
                List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getNonOrderDeliveryReason();
                if (noSalesReasons.size() < 1)
                    return false;

                return !hasCallOrder();
            });
            lackOfDeliveryBtn.setTitle(R.string.lack_of_delivery);
            lackOfDeliveryBtn.setIcon(R.drawable.ic_not_delivery_black_36dp);
            buttons.add(lackOfDeliveryBtn);

            CuteButton resetBtn = new CuteButton();
            resetBtn.setOnClickListener(() -> {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Warning);
                dialog.setTitle(R.string.warning);
                dialog.setMessage(R.string.are_you_sure_you_want_to_reset_order);
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.setPositiveButton(R.string.yes, view12 -> {
                    CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
                    try {
                        new CustomerCallManager(getContext()).removeCalls(customerId, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.CompleteReturnDelivery);
                        callOrderManager.initCall(callOrderId, false);
                        loadCalls();
                        init();
                    } catch (Exception e) {
                        Timber.e(e);
                        showErrorMessage();
                    }
                });
                dialog.show();

            });
            resetBtn.setEnabled(() -> !hasCallOrder());
            resetBtn.setTitle(R.string.reset_order);
            resetBtn.setIcon(R.drawable.ic_repeat_black_36dp);
            buttons.add(resetBtn);
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel checkSystemSysConfig = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
            SysConfigModel calcOnline = sysConfigManager.read(ConfigKey.OnliveEvc, SysConfigManager.cloud);
            if (checkSystemSysConfig != null && SysConfigManager.compare(calcOnline, true)) {
                CuteButton multiInvoiceVectorButton = new CuteButton();
                multiInvoiceVectorButton.setIcon(R.drawable.ic_baseline_date_range_36);
                multiInvoiceVectorButton.setTitle(R.string.calculate_multi_invoice_vector);
                multiInvoiceVectorButton.setOnClickListener(() -> {
                    MultiInvoiceVectorDialog multiInvoiceVectorDialog = new MultiInvoiceVectorDialog();
                    multiInvoiceVectorDialog.setCustomerAndOrderId(customerId, callOrderId);
                    multiInvoiceVectorDialog.show(getChildFragmentManager(), "795753e0-a5a7-40bc-883d-ba5129dccfa9");
                });
                multiInvoiceVectorButton.setEnabled(() -> !isDataSent());
                buttons.add(multiInvoiceVectorButton);
            }
        } else {
            final Currency customerRemain;
            CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());
            CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(customerId);
            if (customerRemainPerLineModel != null)
                customerRemain = customerRemainPerLineModel.CustRemAmount;
            else
                customerRemain = customer.CustomerRemain;

            CuteButton poseButton = new CuteButton();
            poseButton.setIcon(R.drawable.ic_debit_card_white_36dp);
            poseButton.setTitle(R.string.card_reader);
            poseButton.setOnClickListener(() -> {
                CardReaderDialog dialog = new CardReaderDialog();
                PaymentManager paymentManager = new PaymentManager(getContext());
                List<PaymentModel> paymentModels = paymentManager.getCardPayments(customerId);
                Currency totalPayment = paymentManager.getTotalPaid(customerId);
                if (paymentModels == null || paymentModels.size() == 0)
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(true), customerPayment.getTotalAmount(true).subtract(totalPayment), null, customerRemain);
                else {
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(true), customerPayment.getTotalAmount(true).subtract(totalPayment), paymentModels.get(0).UniqueId, customerRemain);
                }
                dialog.preSetRemainedAmount();
                dialog.show(getChildFragmentManager(), "b20d52e2-5b3b-42d0-b3c7-e9060cf24b74");

            });
            poseButton.setEnabled(() -> {
                if (isConfirmed())
                    return false;
                return orderAdapter.size() > 0 && validPaymentTypes.contains(PaymentType.Card) && hasCallOrder();
            });

            CuteButton cashButton = new CuteButton();
            cashButton.setIcon(R.drawable.ic_cash_white_36dp);
            cashButton.setTitle(R.string.cash);
            cashButton.setOnClickListener(() -> {
                CashPaymentDialog dialog = new CashPaymentDialog();
                PaymentManager paymentManager = new PaymentManager(getContext());
                PaymentModel paymentModel = paymentManager.getCashPayment(customerId);
                Currency totalPayment = paymentManager.getTotalPaid(customerId);
                if (paymentModel == null)
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(true), customerPayment.getTotalAmount(true).subtract(totalPayment), null, customerRemain);
                else
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(true), customerPayment.getTotalAmount(true).subtract(totalPayment), paymentModel.UniqueId, customerRemain);
                dialog.preSetRemainedAmount();
                dialog.show(getChildFragmentManager(), "789bec9a-8f17-43af-a7bc-1b3e37caf070");
            });
            cashButton.setEnabled(() -> {
                if (isConfirmed())
                    return false;
                return orderAdapter.size() > 0 && validPaymentTypes.contains(PaymentType.Cash) && hasCallOrder();
            });

            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel scientificVisit = sysConfigManager.read(ConfigKey.ScientificVisit, SysConfigManager.cloud);
            SysConfigModel doubleRequestIsEnabled = sysConfigManager.read(ConfigKey.DoubleRequestIsEnabled, SysConfigManager.cloud);
            if (!SysConfigManager.compare(doubleRequestIsEnabled, true) && !SysConfigManager.compare(scientificVisit, true)) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    CustomerCallInvoicePreviewManager invoiceManager = new CustomerCallInvoicePreviewManager(getActivity());
                    List<CustomerCallInvoicePreviewModel> invoiceModels = invoiceManager.getCustomerCallOrders(customerId);
                    if (invoiceModels.size() == 1) {
                        buttons.add(cashButton);
                        buttons.add(poseButton);
                    }
                } else {
                    buttons.add(cashButton);
                    buttons.add(poseButton);
                }
            }
        }

        buttons.add(cancelBtn);
        buttons.add(okButton);

        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            CuteButton printButton = new CuteButton();
            printButton.setTitle(R.string.save_and_print);
            printButton.setIcon(R.drawable.ic_print_black_36dp);
            printButton.setEnabled(() -> !hasCallOrder());
            printButton.setOnClickListener(() -> {
                if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor) && otherDiscount.compareTo(orderAmount.TotalAmount) > 0) {
                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                    cuteMessageDialog.setIcon(Icon.Error);
                    cuteMessageDialog.setTitle(R.string.error);
                    cuteMessageDialog.setMessage(R.string.discount_can_not_more_amount);
                    cuteMessageDialog.setNegativeButton(R.string.ok, null);
                    cuteMessageDialog.show();
                } else {
                    saveOrder();
                }
            });
            buttons.add(printButton);
        } else {
            buttons.add(previewButton);
        }

        toolbar.setButtons(buttons);
    }

    private void refreshSettlement() {
        PaymentManager paymentManager = new PaymentManager(getContext());
        customerPayment = paymentManager.calculateCustomerPayment(customerId);
        ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
        SysConfigModel configModel = new SysConfigManager(getContext()).read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
        validPaymentTypes = validPayTypeManager.getValidPayTypes(customerCallOrderModel);
        if (validPaymentTypes.isEmpty() && SysConfigManager.compare(configModel, true)) {
            Set<UUID> ids = validPayTypeManager.getDealerValidPayTypes();
            if (ids.contains(PaymentType.Cash))
                validPaymentTypes.add(PaymentType.Cash);
            if (ids.contains(PaymentType.Card))
                validPaymentTypes.add(PaymentType.Card);
        }

    }

    private void saveOrder() {
        if (orderAdapter.size() == 0) {
            CuteMessageDialog alert = new CuteMessageDialog(context);
            alert.setIcon(Icon.Alert);
            alert.setTitle(R.string.error);
            alert.setMessage(R.string.order_is_empty);
            alert.setPositiveButton(R.string.ok, null);
            alert.show();
        } else {
            PaymentManager paymentManager = new PaymentManager(context);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            ConfigMap configMap = sysConfigManager.read(SysConfigManager.cloud);
            CustomerPayment customerPayment = paymentManager.calculateCustomerPayment(customerId);
            String maxMinCheckError = paymentManager.checkMinMaxOrderAndInvoiceAmount(customerPayment, configMap, callOrderId);
            if (!maxMinCheckError.equals("")) {
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(maxMinCheckError);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
                return;
            }
            checkEmphaticItemsAndTrySave();
        }
    }

    private void removeCallOrder() {
        try {
            new CallOrderLineManager(context).removePromotions(callOrderId);
            CustomerCallManager callManager = new CustomerCallManager(context);
            callManager.removeCallOrder(customerId, callOrderId);
            Timber.e("Customer call order canceled");
            loadCalls();
            init();
            final SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
            refresh(true, !(sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)), true);
        } catch (Exception ex) {
            showErrorMessage();
            Timber.e(ex);
        }
    }

    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales, SubsystemTypeId.Supervisor})
    void onAddItem(final ProductOrderViewModel
                           productOrderViewModel, final List<DiscreteUnit> discreteUnits, final BaseUnit
                           bulkUnit, @Nullable final List<BatchQty> batchQtyList) {
        OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
        ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(context);
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
        try {
            ProductOrderViewManager.checkOnHandQty(context, onHandQtyStock, discreteUnits, bulkUnit);
            add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
        } catch (OnHandQtyWarning e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.warning);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, v -> {
                try {
                    add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
                } catch (Exception e1) {
                    Timber.e(e1);
                    CuteMessageDialog dialog1 = new CuteMessageDialog(context);
                    dialog1.setTitle(R.string.error);
                    dialog1.setMessage(R.string.error_saving_request);
                    dialog1.setIcon(Icon.Error);
                    dialog1.setPositiveButton(R.string.ok, null);
                    dialog1.show();
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();
        } catch (OnHandQtyError e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void add(ProductOrderViewModel productOrderViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, List<BatchQty> batchQtyList) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(context);
        callOrderLineManager.addOrUpdateQty(productOrderViewModel.UniqueId, discreteUnits, bulkUnit, callOrderId, null, batchQtyList, false);
        final SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
        refresh(true, !(sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)), true);
    }

    private void setupOrderAdapter() {
        productUnits = new ProductUnitViewManager(getContext()).getUnitSet(ProductType.isForSale);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SortOrders", Context.MODE_PRIVATE);
        OrderOption<CustomerCallOrderOrderViewModel> item = orderOptions.get(sharedPreferences.getInt("OrderOptionsPosition", 0));
        orderAdapter = new OrderAdapter(this, customerCallOrderModel, productUnitsHashMap, productUnits, item, onItemQtyChangedHandler);
    }

    void onEditItem(final CustomerCallOrderOrderViewModel
                            customerCallOrderOrderViewModel, final List<DiscreteUnit> discreteUnits, final BaseUnit
                            bulkUnit, @Nullable final List<BatchQty> batchQtyList) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            BigDecimal total = new BigDecimal(0);
            if (bulkUnit != null)
                total = bulkUnit.getQty();
            for (DiscreteUnit discreteUnit :
                    discreteUnits) {
                total = total.add(BigDecimal.valueOf(discreteUnit.getTotalQty()));
            }
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && backOfficeType == BackOfficeType.ThirdParty) {
                if (customerCallOrderOrderViewModel.EditReasonId == null && total.compareTo(customerCallOrderOrderViewModel.OriginalTotalQty) < 0) {
                    OrderReturnReasonDialog editReasonDialog = new OrderReturnReasonDialog();
                    editReasonDialog.onItemSelected = reasonUniqueId -> {
                        onEdit(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, reasonUniqueId);
                    };
                    editReasonDialog.show(getActivity().getSupportFragmentManager(), "PartialOrderActionDialog");
                } else if (total.compareTo(customerCallOrderOrderViewModel.OriginalTotalQty) == 0) {
                    onEdit(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, null);
                } else {
                    onEdit(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, customerCallOrderOrderViewModel.EditReasonId);
                }
            } else {
                onEdit(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, null);
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    void onEdit(final CustomerCallOrderOrderViewModel
                        customerCallOrderOrderViewModel, final List<DiscreteUnit> discreteUnits, final BaseUnit
                        bulkUnit, @Nullable final List<BatchQty> batchQtyList, @Nullable UUID returnReasonId) {
        try {
            OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
            onHandQtyStock.OnHandQty = customerCallOrderOrderViewModel.OnHandQty;
            onHandQtyStock.RemainedAfterReservedQty = customerCallOrderOrderViewModel.RemainedAfterReservedQty;
            onHandQtyStock.OrderPoint = customerCallOrderOrderViewModel.OrderPoint;
            onHandQtyStock.ProductTotalOrderedQty = customerCallOrderOrderViewModel.ProductTotalOrderedQty;
            if (customerCallOrderOrderViewModel.RequestBulkQty == null)
                onHandQtyStock.TotalQty = customerCallOrderOrderViewModel.TotalQty == null ? BigDecimal.ZERO : customerCallOrderOrderViewModel.TotalQty;
            else
                onHandQtyStock.TotalQty = customerCallOrderOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : customerCallOrderOrderViewModel.TotalQtyBulk;
            onHandQtyStock.HasAllocation = customerCallOrderOrderViewModel.HasAllocation;
            ProductOrderViewManager.checkOnHandQty(context, onHandQtyStock, discreteUnits, bulkUnit);
            add(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, returnReasonId);
        } catch (OnHandQtyWarning e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.warning);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, v -> {
                try {
                    add(customerCallOrderOrderViewModel, discreteUnits, bulkUnit, batchQtyList, returnReasonId);
                } catch (Exception e1) {
                    Timber.e(e1);
                    CuteMessageDialog dialog1 = new CuteMessageDialog(context);
                    dialog1.setTitle(R.string.error);
                    dialog1.setMessage(R.string.error_saving_request);
                    dialog1.setIcon(Icon.Error);
                    dialog1.setPositiveButton(R.string.ok, null);
                    dialog1.show();
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();
        } catch (OnHandQtyError e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(e.getMessage());
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void add(CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, List<BatchQty> batchQtyList, @Nullable UUID editReasonId) throws ValidationException, DbException {
        FreeReasonModel freeReasonModel = null;
        if (customerCallOrderOrderViewModel.FreeReasonId != null)
            freeReasonModel = new FreeReasonManager(context).getItem(customerCallOrderOrderViewModel.FreeReasonId);
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(context);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            callOrderLineManager.updateQty(customerCallOrderOrderViewModel.UniqueId, discreteUnits, bulkUnit, batchQtyList, false, editReasonId);
        else
            callOrderLineManager.addOrUpdateQty(customerCallOrderOrderViewModel.ProductId, discreteUnits, bulkUnit, callOrderId, freeReasonModel, batchQtyList, false);
        final SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
        refresh(true, !(sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)), true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new CustomerReportsDrawerAdapter(getVaranegarActvity(), customerId));
    }

    private void saveContractorEventLocation() throws ValidationException, DbException {
        CustomerActionTimeManager customerActionTimeManager = new CustomerActionTimeManager(getActivity());
        Date endTime = customerActionTimeManager.save(customer.UniqueId, CustomerActions.CustomerCallEnd);
        Date startTime = customerActionTimeManager.get(customer.UniqueId, CustomerActions.CustomerCallStart);
        TrackingLogManager.addLog(getActivity(), LogType.ORDER_EVENT, LogLevel.Info, " ثبت سفارش برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
        final OrderLocationViewModel orderLocationViewModel = new OrderLocationViewModel();
        orderLocationViewModel.CustomerId = customer.UniqueId;
        orderLocationViewModel.eventData = new OrderActivityEventViewModel();
        orderLocationViewModel.eventData.Address = customer.Address;
        orderLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
        orderLocationViewModel.eventData.CustomerName = customer.CustomerName;
        orderLocationViewModel.eventData.StartTime = DateHelper.toString(startTime, DateFormat.MicrosoftDateTime, Locale.US);
        orderLocationViewModel.eventData.EndTime = DateHelper.toString(endTime, DateFormat.MicrosoftDateTime, Locale.US);
        orderLocationViewModel.eventData.StoreName = customer.StoreName;
        orderLocationViewModel.eventData.Phone = customer.Phone;
        orderLocationViewModel.eventData.CustomerId = customer.UniqueId;
        orderLocationViewModel.eventData.OrderLine = new ArrayList<>();
        CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(getContext());
        TourModel tour = new TourManager(getContext()).loadTour();
        CustomerPathViewModel customerPathViewModel = customerPathViewManager.getItem(CustomerPathViewManager.checkIsInDayVisitPath(customer, tour.DayVisitPathId));
        orderLocationViewModel.eventData.IsInVisitDayPath = customerPathViewModel != null;
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
        List<CustomerCallOrderModel> callOrderModels = callOrderManager.getCustomerCallOrders(customer.UniqueId);
        if (callOrderModels.size() > 0) {
            // FIXME: 2/19/2018 add customer call id
            orderLocationViewModel.eventData.UniqueId = callOrderModels.get(0).UniqueId;
            CustomerCallOrderOrderViewManager callOrderLineManager = new CustomerCallOrderOrderViewManager(getActivity());
            BigDecimal totalQty = BigDecimal.ZERO;
            double totalAmount = 0;
            for (CustomerCallOrderModel callOrderModel :
                    callOrderModels) {
                List<CustomerCallOrderOrderViewModel> lines =
                        callOrderLineManager.getLines(callOrderModel.UniqueId, null);
                for (CustomerCallOrderOrderViewModel item :
                        lines) {
                    OrderLineActivityEventViewModel orderLine = new OrderLineActivityEventViewModel();
                    orderLine.Price = HelperMethods.currencyToDouble(item.UnitPrice);
                    orderLine.ProductGuid = item.ProductId;
                    orderLine.ProductName = item.ProductName;
                    orderLine.Qty = HelperMethods.bigDecimalToDouble(item.TotalQty);
                    orderLocationViewModel.eventData.OrderLine.add(orderLine);
                    if (item.TotalQty != null) {
                        totalQty = totalQty.add(item.TotalQty);
                        if (item.UnitPrice != null)
                            totalAmount += (item.UnitPrice.doubleValue() * item.TotalQty.doubleValue());
                    }
                }
            }
            orderLocationViewModel.eventData.OrderQty = totalQty;
            orderLocationViewModel.eventData.OrderAmunt = BigDecimal.valueOf(totalAmount);
        }
        final LocationManager locationManager = new LocationManager(getActivity());
        List<LocationModel> locationModels = locationManager.getLocations(customer.UniqueId, tour.UniqueId);
        locationManager.addTrackingPoint(orderLocationViewModel, new OnSaveLocation() {
            @Override
            public void onSaved(LocationModel location) {
                locationManager.tryToSendItem(location);
            }

            @Override
            public void onFailed(String error) {

            }
        });
        boolean hasOrderAlready = Linq.exists(locationModels, item -> {
            if (item.EventType != null)
                return item.EventType.equals(OrderLocationViewModel.class.getName());
            return false;
        });

        if (hasOrderAlready) {
            TrackingLogManager.addLog(getActivity(), LogType.ORDER_EVENT, LogLevel.Info, " ویرایش سفارش برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
            EditOrderLocationViewModel editOrderLocationViewModel = new EditOrderLocationViewModel();
            editOrderLocationViewModel.eventData = new EditOrderActivityEventViewModel();
            editOrderLocationViewModel.eventData.Address = customer.Address;
            editOrderLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
            editOrderLocationViewModel.eventData.CustomerName = customer.CustomerName;
            editOrderLocationViewModel.eventData.Phone = customer.Phone;
            editOrderLocationViewModel.eventData.StoreName = customer.StoreName;
            editOrderLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
            editOrderLocationViewModel.eventData.PTime = DateHelper.toString(new JalaliCalendar(), DateFormat.MicrosoftDateTime);
            editOrderLocationViewModel.eventData.CustomerId = customer.UniqueId;
            editOrderLocationViewModel.eventData.StartTime = DateHelper.toString(startTime, DateFormat.MicrosoftDateTime, Locale.US);
            editOrderLocationViewModel.eventData.EndTime = DateHelper.toString(endTime, DateFormat.MicrosoftDateTime, Locale.US);
            locationManager.addTrackingPoint(editOrderLocationViewModel, new OnSaveLocation() {
                @Override
                public void onSaved(LocationModel location) {
                    locationManager.tryToSendItem(location);
                }

                @Override
                public void onFailed(String error) {

                }
            });
        }
    }

    private void showPrizeDialog() {
        if (loopCount == (orderPrize.size() - 1))
            last = true;
        ChoicePrizesDialog dialog = new ChoicePrizesDialog();
        dialog.setDiscountId(orderPrize.get(loopCount).discountRef, customerId, new BigDecimal(orderPrize.get(loopCount).qty), callOrderId);
        dialog.setTargetFragment(CustomerSaveOrderFragment.this, 0);
        dialog.setProducts(orderPrize.get(loopCount).orderPrizeList);
        dialog.show(getActivity().getSupportFragmentManager(), "ChoicePrizesDialog");
        loopCount++;
    }

    private void startCalculation() {
        startTempTablesProgressDialog();
        final int[] tasksDone = {0};
        ExecutorService pool = Executors.newFixedThreadPool(2);
        final Handler handler = new Handler(Looper.getMainLooper());
        pool.execute(() -> {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                int paymentTypeOrderGroupRef;
                if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false))
                    paymentTypeOrderGroupRef = selectedPaymentType.PaymentTypeOrderGroupRef;
                else
                    paymentTypeOrderGroupRef = paymentTypesSpinner.getSelectedItem().PaymentTypeOrderGroupRef;
                PriceCalculator priceCalculator = PriceCalculator.getPriceCalculator(context, customerId, callOrderId, paymentTypeOrderGroupRef, getOrderTypeBackOfficeId(), priceClassRef);
                priceCalculator.calculateAndSavePriceList(persistCustomizedPrices, new PriceCalcCallback() {
                    @Override
                    public void onSucceeded() {
                        Timber.i("Prices calculated successfully");
                        handler.post(() -> {
                            tasksDone[0]++;
                            tempTablesProgressDialog.setProgress(tasksDone[0]);
                            if (tasksDone[0] == 4) {
                                initRefresh();
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        Timber.e(error);
                        handler.post(() -> {
                            tasksDone[0]++;
                            tempTablesProgressDialog.setProgress(tasksDone[0]);
                            if (tasksDone[0] == 4) {
                                initRefresh();
                            }
                        });
                    }
                });
            } catch (UnknownBackOfficeException ex) {
                Timber.e(ex);
                handler.post(() -> {
                    tasksDone[0]++;
                    tempTablesProgressDialog.setProgress(tasksDone[0]);
                    if (tasksDone[0] == 4) {
                        if (getVaranegarActvity() != null && !getVaranegarActvity().isFinishing()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(context);
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(R.string.back_office_type_is_uknown);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                            initRefresh();
                        }
                    }
                });
            }
        });

        pool.execute(() -> {
            CustomerTotalProductSaleManager averageProductSaleManager = new CustomerTotalProductSaleManager(context);
            try {
                averageProductSaleManager.calculate(customerId);
            } catch (Exception ex) {
                Timber.e(ex);
            } finally {
                handler.post(() -> {
                    tasksDone[0]++;
                    tempTablesProgressDialog.setProgress(tasksDone[0]);
                    if (tasksDone[0] == 4) {
                        initRefresh();
                    }
                });
            }
        });

        pool.execute(() -> {
            CustomerProductOrderQtyHistoryManager customerProductOrderQtyHistoryManager = new CustomerProductOrderQtyHistoryManager(context);
            try {
                customerProductOrderQtyHistoryManager.calculate(customerId);
            } catch (Exception ex) {
                Timber.e(ex);
            } finally {
                handler.post(() -> {
                    tasksDone[0]++;
                    tempTablesProgressDialog.setProgress(tasksDone[0]);
                    if (tasksDone[0] == 4) {
                        initRefresh();
                    }
                });
            }
        });

        pool.execute(() -> {
            CustomerProductPrizeManager prizeManager = new CustomerProductPrizeManager(context);
            try {
                prizeManager.calculate(customerId);
            } catch (Exception ex) {
                Timber.e(ex);
            } finally {
                handler.post(() -> {
                    tasksDone[0]++;
                    tempTablesProgressDialog.setProgress(tasksDone[0]);
                    if (tasksDone[0] == 4) {
                        initRefresh();
                    }
                });
            }
        });
    }

    private void initRefresh() {
        if (isResumed() && !isRemoving() && tempTablesProgressDialog != null && tempTablesProgressDialog.isShowing())
            tempTablesProgressDialog.setMessage(getString(R.string.calculating_product_visit_template));
        new Thread(() -> {
            if (isResumed() && customerId != null && callOrderId != null) {
                productUnitsHashMap = new ProductUnitsViewManager(context).getProductsUnits();
                productList = new ProductOrderViewManager(getContext()).getItems(ProductOrderViewManager.getAll("", customerId, callOrderId, null, null, false, null));
                if (getVaranegarActvity() != null) {
                    getVaranegarActvity().runOnUiThread(() -> {
                        if (isResumed() && !isRemoving()) {
                            if (tempTablesProgressDialog != null && tempTablesProgressDialog.isShowing()) {
                                tempTablesProgressDialog.setProgress(5);
                                tempTablesProgressDialog.dismiss();
                            }
                            refresh(false, true, false);
                            showEmphaticItems();
                            toolbar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } else {
                if (getVaranegarActvity() != null) {
                    getVaranegarActvity().runOnUiThread(() -> {
                        if (tempTablesProgressDialog != null && tempTablesProgressDialog.isShowing()) {
                            tempTablesProgressDialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    private void showPaymentTypeDialogAndSave() {
        if ((UserDialogPreferences.isVisible(context, "3f73e0a8-4e2e-4c9b-ac8a-b9467cc5b8cd") && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) && defaultPayment == -1) {
            CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(context);
            try {
                customerPaymentTypes.clear();
                customerPaymentTypes = customerPaymentTypesViewManager.getCustomerPaymentType(customerId);
                List<CustomerPaymentTypesViewModel> customerPaymentTypesForShow = new ArrayList<>();
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
                for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                    if (!(SysConfigManager.compare(sysConfigModel, customerPaymentTypesViewModel.PaymentTypeOrderGroupUniqueId))) {
                        customerPaymentTypesForShow.add(customerPaymentTypesViewModel);
                    }
                }
                final SearchBox<CustomerPaymentTypesViewModel> paymentTypeDialog = new SearchBox<>();
                paymentTypeDialog.setUserPreferenceId("3f73e0a8-4e2e-4c9b-ac8a-b9467cc5b8cd");
                paymentTypeDialog.setTitle(getString(R.string.please_select_payment_type));
                paymentTypeDialog.setCancelable(false);
                paymentTypeDialog.setClosable(false);
                paymentTypeDialog.setItems(customerPaymentTypesForShow, (SearchBox.SearchMethod<CustomerPaymentTypesViewModel>) (item, text) -> item.PaymentTypeOrderName.contains(text));
                paymentTypeDialog.show(getVaranegarActvity().getSupportFragmentManager(), "paymentTypeDialog");
                paymentTypeDialog.setOnItemSelectedListener((position, item) -> {
                    paymentTypeDialog.dismiss();
                    if (customerCallOrderModel != null) {
                        final UUID oldPaymentType = customerCallOrderModel.OrderPaymentTypeUniqueId;
                        if (!oldPaymentType.equals(item.UniqueId) && new DiscountConditionManager(context).existDiscountBaseOnPaymentType(true) && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                            PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
                            String oldPaymentTypeName = paymentOrderTypeManager.getPaymentType(oldPaymentType).PaymentTypeOrderName;
                            String errorMessage = getString(R.string.error_on_changing_payment_type_1) + oldPaymentTypeName + getString(R.string.error_on_changing_payment_type_2);
                            dialog.setMessage(errorMessage);
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, view -> updateAndSave());
                            dialog.setNegativeButton(R.string.cancel, view -> {

                            });
                            dialog.show();
                        } else {
                            saveOrderUtility.setOldPaymentTypeId(customerCallOrderModel.OrderPaymentTypeUniqueId);
                            customerCallOrderModel.OrderPaymentTypeUniqueId = item.UniqueId;
                            updateAndSave();
                        }
                    }
                });
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
                showErrorMessage();
            }
        } else {
            updateAndSave();
        }
    }


    private void updateAndSave() {
        try {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                saveOrderUtility.setOrderPrizeList(orderPrizeList);
            saveOrderUtility.setPriceClassRef(priceClassRef);
            updateCustomerCallOrder();
            saveOrderUtility.saveOrderWithProgressDialog(customerCallOrderModel);
        } catch (Exception e) {
            Timber.e(e);
            showErrorMessage();
        }
    }


    private void showPaymentTypeDialog() {
        if (((UserDialogPreferences.isVisible(context, "3f73e0a8-4e2e-4c9b-ac8a-b9467cc5b8cd") && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)))) {
            CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(context);
            try {
                customerPaymentTypes.clear();
                customerPaymentTypes = customerPaymentTypesViewManager.getCustomerPaymentType(customerId);
                List<CustomerPaymentTypesViewModel> customerPaymentTypesForShow = new ArrayList<>();
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
                for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                    if (!(SysConfigManager.compare(sysConfigModel, customerPaymentTypesViewModel.PaymentTypeOrderGroupUniqueId))) {
                        customerPaymentTypesForShow.add(customerPaymentTypesViewModel);
                    }
                }
                final SearchBox<CustomerPaymentTypesViewModel> paymentTypeDialog = new SearchBox<>();
                paymentTypeDialog.setUserPreferenceId("3f73e0a8-4e2e-4c9b-ac8a-b9467cc5b8cd");
                paymentTypeDialog.setTitle(getString(R.string.please_select_payment_type));
                paymentTypeDialog.setCancelable(false);
                paymentTypeDialog.setClosable(false);
                paymentTypeDialog.setItems(customerPaymentTypesForShow, (SearchBox.SearchMethod<CustomerPaymentTypesViewModel>) (item, text) -> item.PaymentTypeOrderName.contains(text));
                paymentTypeDialog.show(getVaranegarActvity().getSupportFragmentManager(), "paymentTypeDialog");
                paymentTypeDialog.setOnItemSelectedListener((position, item) -> {
                    paymentTypeDialog.dismiss();
                    if (customerCallOrderModel != null) {
                        final UUID oldPaymentType = customerCallOrderModel.OrderPaymentTypeUniqueId;
                        if (!oldPaymentType.equals(item.UniqueId) && new DiscountConditionManager(context).existDiscountBaseOnPaymentType(true) && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                            PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
                            String oldPaymentTypeName = paymentOrderTypeManager.getPaymentType(oldPaymentType).PaymentTypeOrderName;
                            String errorMessage = getString(R.string.error_on_changing_payment_type_1) + oldPaymentTypeName + getString(R.string.error_on_changing_payment_type_2);
                            dialog.setMessage(errorMessage);
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, view -> {
                                try {
                                    updateCustomerCallOrder();
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            });
                            dialog.setNegativeButton(R.string.cancel, view -> {

                            });
                            dialog.show();
                        } else {
                            customerCallOrderModel.OrderPaymentTypeUniqueId = item.UniqueId;
                            try {
                                updateCustomerCallOrder();
                                boolean paymentTypeChanged = !customerCallOrderModel.OrderPaymentTypeUniqueId.equals(item.UniqueId);
                                if (paymentTypeChanged)
                                    extractAndCalcCustomerPrice();
                                DiscountConditionManager discountConditionManager = new DiscountConditionManager(context);
                                boolean existDiscountItemCountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(true);
                                boolean existDiscountBaseOnPaymentType = discountConditionManager.existDiscountBaseOnPaymentType(false);
                                refresh((paymentTypeChanged && existDiscountItemCountBaseOnPaymentType), true, (paymentTypeChanged && existDiscountBaseOnPaymentType));
                            } catch (Exception e) {
                                Timber.e(e);
                                showErrorMessage();
                            }
                        }
                    }
                });
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
                showErrorMessage();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (orderAdapter.size() == 0) {
            cancel();
        } else if (hasCallOrder()) {
            getVaranegarActvity().popFragment();
        } else {
            CuteMessageDialog alert = new CuteMessageDialog(context);
            alert.setIcon(Icon.Alert);
            alert.setMessage(R.string.you_should_save_order);
            alert.setTitle(R.string.save_order_request);
            alert.setNegativeButton(R.string.save_order, v -> saveOrder());
            alert.setPositiveButton(R.string.cancel, v -> cancel());
            alert.show();
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private void saveDistReturnPartially() {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType != BackOfficeType.ThirdParty) {
                OrderReturnReasonDialog returnReasonDialog = new OrderReturnReasonDialog();
                returnReasonDialog.onItemSelected = reasonUniqueId -> {
                    saveOrderUtility.setReturnReasonUniqueId(reasonUniqueId);
                    try {
                        updateCustomerCallOrder();
                        showPaymentTypeDialogAndSave();
                    } catch (Exception e) {
                        showErrorMessage();
                    }
                };
                returnReasonDialog.show(getActivity().getSupportFragmentManager(), "PartialOrderActionDialog");
            } else {
                CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                List<CallOrderLineModel> customerCallOrderModels = callOrderLineManager.getOrderLines(callOrderId);
                CallOrderLineModel partialCallOrderLine = Linq.findFirst(customerCallOrderModels, item -> item.EditReasonId != null);
                if (partialCallOrderLine == null) {
                    OrderReturnReasonDialog returnReasonDialog = new OrderReturnReasonDialog();
                    returnReasonDialog.onItemSelected = reasonUniqueId -> {
                        saveOrderUtility.setReturnReasonUniqueId(reasonUniqueId);
                        try {
                            updateCustomerCallOrder();
                            showPaymentTypeDialogAndSave();
                        } catch (Exception e) {
                            showErrorMessage();
                        }
                    };
                    returnReasonDialog.show(getActivity().getSupportFragmentManager(), "PartialOrderActionDialog");
                } else {
                    saveOrderUtility.setReturnReasonUniqueId(partialCallOrderLine.EditReasonId);
                    updateCustomerCallOrder();
                    showPaymentTypeDialogAndSave();
                }
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e) {
            showErrorMessage();
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private void saveDistReturn() {
        OrderReturnReasonDialog returnReasonDialog = new OrderReturnReasonDialog();
        returnReasonDialog.onItemSelected = reasonUniqueId -> {
            try {
                new CustomerCallManager(getContext()).saveDistReturnCall(customerId, callOrderId, reasonUniqueId);
                loadCalls();
                refresh(false, false, false);
            } catch (Exception e) {
                Timber.e(e);
                showErrorMessage();
            }
        };
        returnReasonDialog.show(getActivity().getSupportFragmentManager(), "NonOrderActionDialog");
    }

    private void showPinCodeDialogInCompeleteReturnMode(){
        CustomerCallInvoiceManager customerCallOrderManager = new CustomerCallInvoiceManager(getActivity());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallInvoices(customerId);

        InsertPinDialog dialog = new InsertPinDialog();
        dialog.setCancelable(false);
        dialog.setClosable(false);
        dialog.setValues(customerCallOrderModels.get(0).PinCode3);
        dialog.setOnResult(new InsertPinDialog.OnResult() {
            @Override
            public void done() {
                saveDistReturn();
            }

            @Override
            public void failed(String error) {
                Timber.e(error);
                if (error.equals(getActivity().getString(R.string.pin_code_in_not_correct))) {
                    printFailed(getActivity(), error);
                } else {

                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "InsertPinDialog");
    }

    private void showPinCodeDialogInSaveMode(@Nullable SaveOrderUtility.IWarningCallBack callBack) {
        CustomerCallInvoiceManager customerCallOrderManager = new CustomerCallInvoiceManager(getActivity());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallInvoices(customerId);

        InsertPinDialog dialog = new InsertPinDialog();
        dialog.setCancelable(false);
        dialog.setClosable(false);
        dialog.setValues(customerCallOrderModels.get(0).PinCode3);
        dialog.setOnResult(new InsertPinDialog.OnResult() {
            @Override
            public void done() {
                OrderReturnReasonDialog orderReturnReasonDialog = new OrderReturnReasonDialog();
                orderReturnReasonDialog.onItemSelected = reasonUniqueId -> {
                    saveOrderUtility.setReturnReasonUniqueId(reasonUniqueId);
                    if (callBack != null)
                        callBack.onContinue();
                };
                orderReturnReasonDialog.show(getChildFragmentManager(), "OrderReturnReasonDialog");
            }

            @Override
            public void failed(String error) {
                callBack.cancel();
                Timber.e(error);
                if (error.equals(getActivity().getString(R.string.pin_code_in_not_correct))) {
                    printFailed(getActivity(), error);
                } else {

                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "InsertPinDialog");
    }

    private void printFailed(Context context, String error) {
        try {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.DeliveryReasons);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e1) {
            Timber.e(e1);
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private void saveDistLackOfDelivery() {
        NonDeliveryReasonDialog nonDeliveryReasonDialog = new NonDeliveryReasonDialog();
        nonDeliveryReasonDialog.onItemSelected = reasonUniqueId -> {
            try {
                new CustomerCallManager(getContext()).saveDistLackOfDeliveryCall(customerId, callOrderId, reasonUniqueId);
                loadCalls();
                refresh(false, false, false);
            } catch (Exception e) {
                Timber.e(e);
                showErrorMessage();
            }
        };
        nonDeliveryReasonDialog.show(getActivity().getSupportFragmentManager(), "NonOrderActionDialog");
    }

    private void cancel() {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(context);
        try {
            callOrderManager.cancelCustomerOrder(customerId, callOrderId);
            getVaranegarActvity().popFragment();
        } catch (Exception ex) {
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null && !activity.isFinishing() && isResumed())
                activity.showSnackBar(R.string.error, MainVaranegarActivity.Duration.Short);
        }
    }

    private void checkEmphaticItemsAndTrySave() {
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            CustomerEmphaticProductManager customerEmphaticProductManager = new CustomerEmphaticProductManager(context);
            List<CustomerEmphaticProductModel> emphaticItems = customerEmphaticProductManager.getAll(customerId);

            boolean isInOrder = false;
            if (emphaticItems.size() == 0) {
                isInOrder = true;
            }
            for (final CustomerEmphaticProductModel emphaticItem :
                    emphaticItems) {
                isInOrder = Linq.exists(orderAdapter.getItems(), item -> item.ProductId.equals(emphaticItem.ProductId)
                        && item.TotalQty != null
                        && item.TotalQty.compareTo(item.EmphaticProductCount == null ? BigDecimal.ZERO : item.EmphaticProductCount) >= 0);
                if (!isInOrder)
                    break;
            }

            EmphaticPackageCheckResult result = new CustomerEmphaticPackageViewManager(getContext()).checkEmphaticPackages(customerId, orderAdapter.getItems());

            if ((!isInOrder || result.getError() != null || result.getWarning() != null) && !SysConfigManager.compare(new SysConfigManager(getContext()).read(ConfigKey.SimplePresale, SysConfigManager.cloud), true)) {
                EmphaticProductDialog emphaticProductDialog = new EmphaticProductDialog();
                emphaticProductDialog.onOrderUpdate = () -> refresh(true, false, true);
                emphaticProductDialog.onUserAccept = () -> {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                    if (!sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false))
                        showPaymentTypeDialogAndSave();
                    else {
                        updateAndSave();
                    }
                };
                Bundle bundle = new Bundle();
                bundle.putString("d55e60a5-f997-406a-b420-b015488c22a1", customerId.toString());
                bundle.putString("dee377db-d44a-4021-93fc-9792d620b88b", callOrderId.toString());
                emphaticProductDialog.setArguments(bundle);
                emphaticProductDialog.show(getChildFragmentManager(), "EmphaticProductDialog");
            } else {

                SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                if (!sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false))
                    showPaymentTypeDialogAndSave();
                else {
                    updateAndSave();
                }
            }
        } else {
            /**
             * چک کردن تحویل قسمتی و تغییر کردن اجناس
             */
            DistOrderStatus status = customerCallOrderManager.getDistStatus(customerId, callOrderId);
            if (!status.PartiallyDelivered) {
                saveOrderUtility.setReturnReasonUniqueId(null);
                try {
                    updateCustomerCallOrder();
                    showPaymentTypeDialogAndSave();
                } catch (Exception e) {
                    Timber.e(e);
                    showErrorMessage();
                }
            } else
                saveDistReturnPartially();
        }
    }

    private void showEmphaticItems() {
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Supervisor) && !hasCallOrder()) {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel dealerInformationNotification = sysConfigManager.read(ConfigKey.DealerInformationNotification, SysConfigManager.cloud);
            // check owner key for Poober in 2.1.1 in later versions we have a config for this!
            if (SysConfigManager.compare(dealerInformationNotification, true)) {
                CustomerEmphaticProductManager customerEmphaticProductManager = new CustomerEmphaticProductManager(context);
                List<CustomerEmphaticProductModel> emphaticItems = customerEmphaticProductManager.getAll(customerId);

                boolean isInOrder = false;
                if (emphaticItems.size() == 0) {
                    isInOrder = true;
                }
                for (final CustomerEmphaticProductModel emphaticItem :
                        emphaticItems) {
                    isInOrder = Linq.exists(orderAdapter.getItems(), item -> item.ProductId.equals(emphaticItem.ProductId)
                            && item.TotalQty != null
                            && item.TotalQty.compareTo(item.EmphaticProductCount == null ? BigDecimal.ZERO : item.EmphaticProductCount) >= 0);
                    if (!isInOrder)
                        break;
                }

                EmphaticPackageCheckResult result = new CustomerEmphaticPackageViewManager(getContext()).checkEmphaticPackages(customerId, orderAdapter.getItems());

                if (isInOrder) {
                    isInOrder = !Linq.exists(productList, item -> item.PrizeComment != null && !item.PrizeComment.isEmpty() && !Linq.exists(orderAdapter.getItems(), p -> item.UniqueId.equals(p.ProductId)));
                }
                if ((!isInOrder || result.getError() != null || result.getWarning() != null) && !SysConfigManager.compare(new SysConfigManager(getContext()).read(ConfigKey.SimplePresale, SysConfigManager.cloud), true)) {
                    FastOrderProductsDialog dialog = new FastOrderProductsDialog();
                    dialog.setProductOrderViewModels(productList);
                    Bundle bundle = new Bundle();
                    bundle.putString("CUSTOMER_ID", customerId.toString());
                    bundle.putString("ORDER_ID", callOrderId.toString());
                    dialog.setArguments(bundle);
                    dialog.show(getChildFragmentManager(), "FastOrderProductsDialog");
                    dialog.onOrderUpdate = productOrderViewModel -> orderAdapter.refresh();
                }
            }
        }
    }

    protected void extractAndCalcCustomerPrice() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.calculating_prices_for_customer));
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Handler handler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                int paymentTypeOrderGroupRef;
                if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false))
                    paymentTypeOrderGroupRef = selectedPaymentType.PaymentTypeOrderGroupRef;
                else
                    paymentTypeOrderGroupRef = paymentTypesSpinner.getSelectedItem().PaymentTypeOrderGroupRef;
                PriceCalculator priceCalculator = PriceCalculator.getPriceCalculator(context, customerId, callOrderId, paymentTypeOrderGroupRef, getOrderTypeBackOfficeId(), priceClassRef);
                priceCalculator.calculateAndSavePriceList(persistCustomizedPrices, new PriceCalcCallback() {
                    @Override
                    public void onSucceeded() {
                        handler.post(() -> {
                            if (getVaranegarActvity() != null) {
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                                initRefresh();
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        handler.post(() -> {
                            if (getVaranegarActvity() != null) {
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        });
                    }
                });
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
                handler.post(() -> {
                    CuteMessageDialog dialog = new CuteMessageDialog(context);
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.back_office_type_is_uknown);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                });
            }

        });
        thread.start();
    }

    private void enableItems(boolean enabled) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (customerCallOrderModel.IsInvoice) {
                if (paymentTypesSpinner != null)
                    paymentTypesSpinner.setEnabled(false);
                if (priceClassSpinner != null)
                    priceClassSpinner.setEnabled(false);
                if (systemCheckBox != null)
                    systemCheckBox.setEnabled(false);
                if (calcOnlineUsanceDay != null)
                    calcOnlineUsanceDay.setEnabled(false);
            } else {
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel allowEditSettlement = sysConfigManager.read(ConfigKey.AllowEditSettlement, SysConfigManager.cloud);
                if (SysConfigManager.compare(allowEditSettlement, true)) {
                    if (paymentTypesSpinner != null) {
                        if (defaultPayment != -1)
                            paymentTypesSpinner.setEnabled(false);
                        else
                            paymentTypesSpinner.setEnabled(enabled);
                    }
                    if (priceClassSpinner != null)
                        priceClassSpinner.setEnabled(enabled);
                    if (systemCheckBox != null && disableCheckBox)
                        systemCheckBox.setEnabled(enabled);
                    if (calcOnlineUsanceDay != null)
                        calcOnlineUsanceDay.setEnabled(enabled);
                } else {
                    if (paymentTypesSpinner != null)
                        paymentTypesSpinner.setEnabled(false);
                    if (priceClassSpinner != null)
                        priceClassSpinner.setEnabled(false);
                    if (systemCheckBox != null)
                        systemCheckBox.setEnabled(false);
                    if (calcOnlineUsanceDay != null)
                        calcOnlineUsanceDay.setEnabled(false);
                }
            }
            if (orderTypesSpinner != null)
                orderTypesSpinner.setEnabled(false);
        } else {
            if (paymentTypesSpinner != null) {
                if (defaultPayment != -1)
                    paymentTypesSpinner.setEnabled(false);
                else
                    paymentTypesSpinner.setEnabled(enabled);
            }
            if (orderTypesSpinner != null)
                orderTypesSpinner.setEnabled(enabled);
            if (priceClassSpinner != null)
                priceClassSpinner.setEnabled(enabled);
            if (systemCheckBox != null && disableCheckBox)
                systemCheckBox.setEnabled(enabled);
            if (calcOnlineUsanceDay != null)
                calcOnlineUsanceDay.setEnabled(enabled);

        }
        if (commentEditText != null)
            commentEditText.setEnabled(enabled);
        if (calendarImageView != null)
            calendarImageView.setEnabled(enabled);
//        if (systemCheckBox != null)
//            systemCheckBox.setEnabled(enabled);
//        if (calcOnlineUsanceDay != null)
//            calcOnlineUsanceDay.setEnabled(enabled);
        if (contractorDiscountEditText != null) {
            if (enabled) {
                contractorDiscountEditText.setOnClickListener(view -> {
                    ValueEditorDialog valueEditorDialog = new ValueEditorDialog();
                    valueEditorDialog.setMaxValue(maxValu);
                    valueEditorDialog.setPercentType(percentType);
                    valueEditorDialog.setPriceChangedListener(value -> {
                        String s = "0";
                        if (value != null)
                            s = value.toString();
                        contractorDiscountEditText.setText(s);
                        SharedPreferences discountTypeShP = getContext().getSharedPreferences("DiscountType", Context.MODE_PRIVATE);
                        final String discountType = discountTypeShP.getString("discountTypeId", percent);
                        if (discountType.equals(percent)) {
                            Currency percent = new Currency(Long.parseLong(s));
                            Currency result;
                            if (percent.compareTo(new Currency(100)) < 0)
                                result = (percent.multiply(orderAmount.TotalAmount)).divide(new Currency(100));
                            else {
                                result = orderAmount.TotalAmount;
                            }
                            otherDiscount = result;
                            otherPercent = percent;
                            contractorDiscountTextView.setText(HelperMethods.currencyToString(result));
                            contractorNetAmountTextView.setText(HelperMethods.currencyToString(orderAmount.TotalAmount.subtract(result)));
                        } else if (discountType.equals(cash)) {
                            Currency discountAmount = new Currency(Long.parseLong(s.replaceAll(",", "")));
                            Currency result;
                            if (discountAmount.compareTo(orderAmount.TotalAmount) < 0)
                                result = discountAmount;
                            else {
                                result = orderAmount.TotalAmount;
                            }
                            otherDiscount = result;
                            otherPercent = Currency.ZERO;
                            contractorDiscountTextView.setText(HelperMethods.currencyToString(result));
                            contractorNetAmountTextView.setText(HelperMethods.currencyToString(orderAmount.TotalAmount.subtract(result)));
                        }
                    });
                    valueEditorDialog.show(getActivity().getSupportFragmentManager(), "ValueEditorDialog");
                });
            }
        }
        if (contractorDiscountImageView != null)
            contractorDiscountImageView.setEnabled(enabled);
        if (orderTypesSpinner != null && customerCallOrderModel != null && customerCallOrderModel.OrderTypeUniqueId != null && customerCallOrderModel.OrderTypeUniqueId.toString().equals("4cc866f9-eb19-4c76-8a41-f8207104cdbf"))
            orderTypesSpinner.setEnabled(false);
    }

    protected void refresh(boolean deletePrize, boolean checkUsanceDay, boolean deleteTempPromotions) {
        toolbar.refresh();
        boolean enabled = !hasCallOrder();
        orderAdapter.setEnabled(enabled);
        enableItems(enabled);

        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        orderAmount = customerCallOrderOrderViewManager.calculateTotalAmount(callOrderId);
        orderAdapter.refresh();
        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            SharedPreferences discountTypeShP = getContext().getSharedPreferences("DiscountType", Context.MODE_PRIVATE);
            final String discountType = discountTypeShP.getString("discountTypeId", percent);
            if (discountType.equals(percent) && otherPercent.compareTo(Currency.ZERO) > 0)
                otherDiscount = (otherPercent.multiply(orderAmount.TotalAmount)).divide(new Currency(100));
            contractorDiscountTextView.setText(String.valueOf(otherDiscount));
            contractorNetAmountTextView.setText(String.valueOf(orderAmount.TotalAmount.subtract(otherDiscount)));
            discountCalculatorForContractor();
        }
        orderCostTextView.setText(HelperMethods.currencyToString(orderAmount.TotalAmount));
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            netAmountTextView.setText(HelperMethods.currencyToString(orderAmount.NetAmount));
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            if (enabled) {
                discountTextView.setText("--");
                addAmountTextView.setText("--");
            } else {
                discountTextView.setText(HelperMethods.currencyToString(orderAmount.DiscountAmount));
                addAmountTextView.setText(HelperMethods.currencyToString(orderAmount.AddAmount));
            }
        } else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (hasDistCall(CustomerCallType.OrderDelivered)) {
                netAmountTextView.setText(HelperMethods.currencyToString(orderAmount.NetAmount));
                discountTextView.setText(HelperMethods.currencyToString(orderAmount.DiscountAmount));
                addAmountTextView.setText(HelperMethods.currencyToString(orderAmount.AddAmount));
                statusTextView.setText(getContext().getString(R.string.delivered_complete));
                statusTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.green));
            } else if (hasDistCall(CustomerCallType.OrderPartiallyDelivered)) {
                netAmountTextView.setText(HelperMethods.currencyToString(orderAmount.NetAmount));
                discountTextView.setText(HelperMethods.currencyToString(orderAmount.DiscountAmount));
                addAmountTextView.setText(HelperMethods.currencyToString(orderAmount.AddAmount));
                statusTextView.setText(getContext().getString(R.string.delivered_partially));
                statusTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.orange_light));
            } else if (hasDistCall(CustomerCallType.OrderReturn)) {
                netAmountTextView.setText("--");
                discountTextView.setText("--");
                addAmountTextView.setText("--");
                statusTextView.setText(getContext().getString(R.string.complete_return));
                statusTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.red));
            } else if (hasDistCall(CustomerCallType.OrderLackOfDelivery)) {
                netAmountTextView.setText("--");
                discountTextView.setText("--");
                addAmountTextView.setText("--");
                statusTextView.setText(getContext().getString(R.string.lack_of_delivery));
                statusTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.orange));
            } else {
                netAmountTextView.setText("--");
                discountTextView.setText("--");
                addAmountTextView.setText("--");
                statusTextView.setText(getContext().getString(R.string.unKnown));
                statusTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.grey));
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
        if ((!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) || (systemCheckBox.getVisibility() == View.VISIBLE)) {
            usanceDayLayout.setVisibility(View.VISIBLE);
            if (checkUsanceDay)
                refreshUsanceDayAverage();
        } else {
            usanceDayLayout.setVisibility(View.GONE);
        }

        if (defaultPayment != -1) {
            paymentTypesSpinner.selectItem(defaultPayment);
        } else {
            if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
                if (customerCallOrderModel != null && customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
                    for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                        if (customerPaymentTypesViewModel.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId))
                            selectedPaymentType = customerPaymentTypesViewModel;
                    }
                } else {
                    selectedPaymentType = customerPaymentTypes.get(0);
                }
            } else {
                if (customerCallOrderModel != null && customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
                    int p = Linq.findFirstIndex(paymentTypesSpinner.getItems(), item -> item.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId));
                    paymentTypesSpinner.selectItem(p);
                } else {
                    paymentTypesSpinner.selectItem(0);
                }
            }
        }

        refreshSettlement();

        if (deletePrize) {
            OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
            List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, callOrderId));
            if (orderPrizeModels.size() > 0) {
                try {
                    orderPrizeManager.delete(Criteria.equals(OrderPrize.CustomerId, customerId).and(Criteria.equals(OrderPrize.CallOrderId, callOrderId)));
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing() && isResumed())
                        activity.showSnackBar(R.string.order_prizes_deleted, MainVaranegarActivity.Duration.Long);
                } catch (DbException e) {
                    Timber.e(e);
                }
            }
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && deleteTempPromotions) {
            try {
                CallOrderLinesTempManager callOrderLinesTempManager = new CallOrderLinesTempManager(context);
                callOrderLinesTempManager.delete(Criteria.equals(CallOrderLinesTemp.OrderUniqueId, callOrderId.toString()));
            } catch (DbException e) {
                Timber.e(e);
            }
        }
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    @Override
    public void onFinishChoicePrize(int disRef, UUID customerId, UUID callOrderId, HashMap<UUID, OrderPrizeModel> oldPrize) {
        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
        List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, disRef, callOrderId));
        for (OrderPrizeModel orderPrizeModel : orderPrizeModels) {
            DiscountOrderPrizeViewModel choicePrizeDiscountOrderPrizeViewModel = new DiscountOrderPrizeViewModel();
            choicePrizeDiscountOrderPrizeViewModel.discountRef = disRef;
            ProductModel productModel = new ProductManager(getContext()).getItem(orderPrizeModel.ProductId);
            choicePrizeDiscountOrderPrizeViewModel.goodsRef = productModel.BackOfficeId;
            choicePrizeDiscountOrderPrizeViewModel.orderDiscountRef = orderPrize.get(0).orderDiscountRef;
            choicePrizeDiscountOrderPrizeViewModel.qty = Integer.parseInt(String.valueOf(orderPrizeModel.TotalQty.setScale(0, RoundingMode.HALF_DOWN)));
            DiscountPrizeViewModel discountPrizeViewModel = orderPrize.get(loopCount - 1);
            for (DiscountOrderPrizeViewModel discountOrderPrizeViewModel : discountPrizeViewModel.orderPrizeList) {
                if (discountOrderPrizeViewModel.goodsRef == choicePrizeDiscountOrderPrizeViewModel.goodsRef) {
                    choicePrizeDiscountOrderPrizeViewModel.unitRef = discountOrderPrizeViewModel.unitRef;
                    choicePrizeDiscountOrderPrizeViewModel.id = discountOrderPrizeViewModel.id;
                }
            }
            orderPrizeList.add(choicePrizeDiscountOrderPrizeViewModel);
        }
        if (last)
            updateAndSave();
        else
            showPrizeDialog();
    }

    public abstract class OnItemQtyChangedHandler {
        HashMap<UUID, List<ItemInfo>> qtys = new HashMap<>();

        public abstract void run(CustomerCallOrderOrderViewModel orderLine, QtyChange change);

        public synchronized void plusQty(int position, UUID productId, final DiscreteUnit unit, @Nullable DiscreteUnit otherUnit) {
            if (qtys.containsKey(productId)) {
                List<ItemInfo> qtyItems = qtys.get(productId);
                ItemInfo qtyItem = Linq.findFirst(qtyItems, item -> item.unit.ProductUnitId.equals(unit.ProductUnitId));
                if (qtyItem == null) {
                    qtyItem = new ItemInfo(unit, position, unit.value);
                    qtyItems.add(qtyItem);
                } else {
                    qtyItem.qty = unit.value;
                }
            } else {
                List<ItemInfo> l = new ArrayList<>();
                l.add(new ItemInfo(unit, position, unit.value));
                if (otherUnit != null)
                    l.add(new ItemInfo(otherUnit, position, otherUnit.value));
                qtys.put(productId, l);
            }
        }

        public synchronized void minusQty(int position, UUID productId, final DiscreteUnit unit, @Nullable DiscreteUnit otherUnit) {
            if (qtys.containsKey(productId)) {
                List<ItemInfo> qtyItems = qtys.get(productId);
                ItemInfo qtyItem = Linq.findFirst(qtyItems, item -> item.unit.ProductUnitId.equals(unit.ProductUnitId));
                if (qtyItem == null) {
                    qtyItem = new ItemInfo(unit, position, unit.value);
                    qtyItems.add(qtyItem);
                } else {
                    qtyItem.qty = unit.value;
                }
            } else {
                List<ItemInfo> l = new ArrayList<>();
                l.add(new ItemInfo(unit, position, unit.value));
                if (otherUnit != null)
                    l.add(new ItemInfo(otherUnit, position, otherUnit.value));
                qtys.put(productId, l);
            }
        }

        public synchronized void start(CustomerCallOrderOrderViewModel orderLine) {
            for (UUID key : qtys.keySet()) {
                List<ItemInfo> itemInfo = qtys.get(key);
                run(orderLine, new QtyChange(key, itemInfo.get(0).position, itemInfo));
            }
            qtys.clear();
        }

        public class QtyChange {
            public QtyChange(UUID productId, int position, List<ItemInfo> qtys) {
                this.productId = productId;
                this.position = position;
                discreteUnits = new ArrayList<>();
                for (ItemInfo qty :
                        qtys) {
                    DiscreteUnit discreteUnit = new DiscreteUnit();
                    discreteUnit.ConvertFactor = qty.unit.ConvertFactor;
                    discreteUnit.IsDefault = qty.unit.IsDefault;
                    discreteUnit.Name = qty.unit.Name;
                    discreteUnit.ProductUnitId = qty.unit.ProductUnitId;
                    discreteUnit.value = qty.qty;
                    discreteUnits.add(discreteUnit);
                }
            }

            public UUID productId;
            public int position;
            public List<DiscreteUnit> discreteUnits;
        }

        private class ItemInfo {
            public ItemInfo(DiscreteUnit unit, int position, double qty) {
                this.unit = unit;
                this.position = position;
                this.qty = qty;
            }

            public DiscreteUnit unit;
            public int position;
            public double qty;
        }
    }


    class DiscountTypeModel extends BaseModel {
        public String lable;

        public String toString() {
            return lable;
        }

    }

    protected void refreshUsanceDayAverage() {
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
        int usanceDayAverage = customerCallOrderOrderViewManager.calculateUsanceDayAverage(callOrderId, String.valueOf(customer.BackOfficeId));
        if (usanceDayAverage == 1)
            usanceDayAverage = 0;
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.DAY_OF_YEAR, usanceDayAverage);
        String date = DateHelper.toString(newDate.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
            usanceDayTextView.setText(selectedPaymentType.PaymentTypeOrderName);
            usanceTextView.setText("");
        } else if (customerCallOrderOrderViewManager.getLines(callOrderId, null).size() > 0) {
            usanceDayTextView.setText(date);
            usanceTextView.setText(usanceDayAverage + " " + getString(R.string.day));
        } else
            usanceDayTextView.setText("-");
        if (!(usanceDayTextView.getText().toString().equals("-")) && sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false)) {
            usanceDayTextView.startAnimation(getBlinkAnimation());
            usanceTextView.startAnimation(getBlinkAnimation());
            @SuppressLint("RestrictedApi") ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.holo_red_light), getResources().getColor(R.color.white4));
            colorAnimation.setDuration(1000); // milliseconds
            colorAnimation.addUpdateListener(animator -> {
                usanceDayTextView.setBackgroundColor((int) animator.getAnimatedValue());
                usanceTextView.setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnimation.start();
        }

    }

    public Animation getBlinkAnimation() {
        Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
        animation.setDuration(1000);                             // duration - half a second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in
        return animation;
    }

    private void initSystemCheck() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
            PaymentTypeOrderModel paymentTypeOrderModel = paymentOrderTypeManager.getPaymentType(customerCallOrderModel.OrderPaymentTypeUniqueId);
            if (SysConfigManager.compare(sysConfigModel, paymentTypeOrderModel.PaymentTypeOrderGroupUniqueId)) {
                systemCheckBox.setChecked(true);
                for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                    if (customerPaymentTypesViewModel.UniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId))
                        selectedPaymentType = customerPaymentTypesViewModel;
                }
                checkSystemCheck(editor);
            } else {
                editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false);
                editor.apply();
                systemCheckBox.setChecked(false);
                paymentTypesSpinner.setVisibility(View.VISIBLE);
                calcOnlineUsanceDay.setVisibility(View.GONE);
            }
        }
    }
}
