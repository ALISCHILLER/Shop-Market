package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.enums.EvcType;
import com.varanegar.vaslibrary.exception.DistException;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesRequestViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.PriceHistoryManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnInitException;
import com.varanegar.vaslibrary.manager.customercall.ReturnLineQtyManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercallreturnview.CustomerCallReturnAfterDiscountViewManager;
import com.varanegar.vaslibrary.manager.customercallreturnview.CustomerCallReturnViewManager;
import com.varanegar.vaslibrary.manager.dealer.DealerManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceDetailManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceHeaderManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.call.ReturnLinesModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesRequestViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountViewModelRepository;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnBaseViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModelRepository;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.dealer.Dealer;
import com.varanegar.vaslibrary.model.dealer.DealerModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.pricecalculator.PriceCalculator;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorForm;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorItem;
import com.varanegar.vaslibrary.ui.fragment.order.OrderReturnReasonDialog;
import com.varanegar.vaslibrary.ui.report.ReturnReportAdapter;
import com.varanegar.vaslibrary.ui.report.ReturnReportAfterDiscountAdapter;
import com.varanegar.vaslibrary.webapi.promotion.PromotionApi;
import com.varanegar.vaslibrary.webapi.promotion.ReturnDisItemViewModel;
import com.varanegar.vaslibrary.webapi.promotion.ReturnDistViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by atp on 4/4/2017.
 */

public class CustomerSaveReturnFragment extends VisitFragment {

    ReportAdapter adapter;
    boolean withRef;
    boolean withRefReplacementRegistration;
    boolean withoutRefReplacementRegistration;
    private UUID customerId, dealerId;
    private EditText commentEditText;
    private ImageView commentImageView;
    private List<CustomerCallModel> calls;
    private CuteToolbar toolbar;
    private PairedItemsSpinner<DealerModel> dealerNameSpinner;
    private PairedItems amountPairedItems;
    private PairedItems netAmountPairtedItems;
    private PairedItems discountAmountPairtedItems;
    private PairedItems addAmountPairtedItems;
    private boolean isFromRequest;
    private ReportView reportView;
    private ProgressDialog discountProgressDialog;
    private Activity activity;


    private boolean isEmpty(@Nullable UUID returnId) {
        CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines;
        if (returnId == null)
            lines = linesViewManager.getLines(customerId, withRef, true);
        else
            lines = linesViewManager.getReturnLines(returnId, false);
        for (CustomerCallReturnLinesViewModel line :
                lines) {
            if (line.TotalReturnQty.compareTo(BigDecimal.ZERO) > 0 && !line.IsPromoLine)
                return false;
        }
        return true;
    }

    private boolean isChangedFromRequest(@Nullable UUID returnId) {
        CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        CustomerCallReturnLinesRequestViewManager requestViewManager = new CustomerCallReturnLinesRequestViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines;
        if (returnId == null)
            lines = linesViewManager.getLines(customerId, withRef, true);
        else
            lines = linesViewManager.getReturnLines(returnId, false);
        List<CustomerCallReturnLinesRequestViewModel> requestLines = requestViewManager.getLines(customerId, withRef);
        HashMap<UUID, CustomerCallReturnLinesRequestViewModel> requestLinesMap = Linq.toHashMap(requestLines, new Linq.Map<CustomerCallReturnLinesRequestViewModel, UUID>() {
            @Override
            public UUID run(CustomerCallReturnLinesRequestViewModel item) {
                return item.UniqueId;
            }
        });
        for (CustomerCallReturnLinesViewModel line :
                lines) {
            if (!requestLinesMap.containsKey(line.UniqueId))
                return true;
            else {
                CustomerCallReturnLinesRequestViewModel requestLine = requestLinesMap.get(line.UniqueId);
                if (requestLine.TotalReturnQty.compareTo(line.TotalReturnQty) != 0)
                    return true;
            }
        }
        return false;
    }

    private boolean isFromRequest() {
        return isFromRequest;
    }

    private void extractAndCalcCustomerPrice() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getString(R.string.calculating_prices_for_customer));
        progressDialog.show();
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sellReturnAmountTypeId = sysConfigManager.read(ConfigKey.SellReturnAmountTypeId, SysConfigManager.cloud);
            List<CustomerPriceModel> priceList;
            if (SysConfigManager.compare(sellReturnAmountTypeId, "9a30c8eb-580e-44f4-a040-9a0e333381f4")) {
                PriceCalculator priceCalculator = PriceCalculator.getPriceCalculator(getContext(), customerId);
                priceList = priceCalculator.calculatePrices();
            } else {
                PriceHistoryManager priceHistoryManager = new PriceHistoryManager(getContext());
                priceList = priceHistoryManager.getReturnPriceFromPriceHistory(customerId);
            }
            VaranegarApplication.getInstance().save("955119ea-eb2d-428a-9dd7-03bb95844d1f", priceList);
            progressDialog.dismiss();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            progressDialog.dismiss();
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("69d2ac5d-cc07-4b66-aa0f-cc09d55e3296"))
                showProgressDialog();
        }
        customerId = UUID.fromString(getArguments().getString("2c7d1543-3063-48b5-b738-e163c102354b"));
        withRef = getArguments().getBoolean("c4afed7f-901a-4eae-898c-76dedb2bd738", false);
        isFromRequest = getArguments().getBoolean("4333ae1b-e90f-423b-aa1c-6425c46ef4fc", false);
        withRefReplacementRegistration = getArguments().getBoolean("84788E31-2337-4FD4-BB82-253C5FF9E6F1", false);
        withoutRefReplacementRegistration = getArguments().getBoolean("42A25C92-A12C-485C-B625-65DDF3F0E7D3", false);
        if (withRefReplacementRegistration)
            withRef = true;
        final View view = inflater.inflate(R.layout.fragment_customer_save_return, container, false);
        SimpleToolbar simpleToolbar = view.findViewById(R.id.simple_toolbar);
        simpleToolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        amountPairedItems = view.findViewById(R.id.amount_paired_items);
        netAmountPairtedItems = view.findViewById(R.id.net_amount_paired_items);
        discountAmountPairtedItems = view.findViewById(R.id.discount_amount_paired_items);
        addAmountPairtedItems = view.findViewById(R.id.add_amount_paired_items);
        commentImageView = (ImageView) view.findViewById(R.id.comment_image_view);
        commentEditText = (EditText) view.findViewById(R.id.comment_edit_text);

        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        BackOfficeType backOfficeType = null;
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
            if (withRef) {
                netAmountPairtedItems.setVisibility(View.VISIBLE);
                discountAmountPairtedItems.setVisibility(View.VISIBLE);
                addAmountPairtedItems.setVisibility(View.VISIBLE);
            }
            if (isFromRequest && !withRef && BackOfficeType.ThirdParty.equals(backOfficeType)) {
                amountPairedItems.setVisibility(View.GONE);
                netAmountPairtedItems.setVisibility(View.VISIBLE);
            }

            if (!isFromRequest()) {
                commentImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            updateCustomerCallReturn();
                        } catch (Exception ex) {
                            showErrorMessage();
                        }
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });


                CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getContext());
                List<CustomerCallReturnModel> returnModels = returnManager.getReturnCalls(customerId, withRef, isFromRequest());
                if (returnModels.size() != 0)
                    commentEditText.setText(returnModels.get(0).Comment);
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
                            commentImageView.setVisibility(View.INVISIBLE);
                    }
                });


                if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && !withRef && !isFromRequest()) {
                    simpleToolbar.setTitle(getString(R.string.print_return));
                    DealerManager dealerManager = new DealerManager(getContext());

                    List<DealerModel> dealers = dealerManager.getItems(new Query().from(Dealer.DealerTbl));
                    dealerNameSpinner = view.findViewById(R.id.dealer_name);
                    dealerNameSpinner.setVisibility(View.VISIBLE);
                    dealerNameSpinner.setup(getChildFragmentManager(), dealers, new SearchBox.SearchMethod<DealerModel>() {
                        @Override
                        public boolean onSearch(DealerModel item, String text) {
                            String str = HelperMethods.persian2Arabic(text);
                            if (str == null)
                                return true;
                            str = str.toLowerCase();
                            return item.Name.toLowerCase().contains(str);
                        }
                    });
                    if (returnModels.size() != 0) {
                        final CustomerCallReturnModel returnModel = returnModels.get(0);
                        if (returnModel.DealerUniqueId != null) {
                            dealerId = returnModel.DealerUniqueId;
                        } else {
                            String d = getArguments().getString("2c939db1-ba5a-480c-8d94-103b4d0d232f");
                            if (d != null)
                                dealerId = UUID.fromString(d);
                        }

                        if (dealerId != null) {
                            int index = Linq.findFirstIndex(dealers, new Linq.Criteria<DealerModel>() {
                                @Override
                                public boolean run(DealerModel item) {
                                    return item.UniqueId.equals(dealerId);
                                }
                            });
                            if (index >= 0)
                                dealerNameSpinner.selectItem(index);
                        }
                    }
                    dealerNameSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<DealerModel>() {
                        @Override
                        public void onItemSelected(int position, DealerModel item) {
                            dealerId = item.UniqueId;
                        }
                    });
                }
            } else {
                commentEditText.setVisibility(View.GONE);
                commentImageView.setVisibility(View.GONE);
            }

            reportView = (ReportView) view.findViewById(R.id.order_report_view);

            CuteButton listCuteBtn = new CuteButton();
            listCuteBtn.setTitle(R.string.product_list);
            listCuteBtn.setIcon(R.drawable.ic_view_list_white_36dp);
            listCuteBtn.setOnClickListener(new CuteButton.OnClickListener() {
                @Override
                public void onClick() {
                    ReturnProductGroupFragment returnProductGroupFragment = new ReturnProductGroupFragment();
                    returnProductGroupFragment.setArguments(withRef, customerId, dealerId, withoutRefReplacementRegistration, withRefReplacementRegistration);
                    getVaranegarActvity().pushFragment(returnProductGroupFragment);
                }
            });
            listCuteBtn.setEnabled(new CuteButton.IIsEnabled() {
                @Override
                public boolean run() {
                    return !hasReturnCall() && !isDataSent();
                }
            });

            CuteButton cancelButton = new CuteButton();
            cancelButton.setIcon(R.drawable.ic_cancel_black_36dp);
            cancelButton.setTitle(R.string.uncertain);
            cancelButton.setEnabled(new CuteButton.IIsEnabled() {
                @Override
                public boolean run() {
                    return hasReturnCall() && !isDataSent();
                }
            });
            cancelButton.setOnClickListener(new CuteButton.OnClickListener() {
                @Override
                public void onClick() {
                    if (isConfirmed()) {
                        CuteMessageDialog alert = new CuteMessageDialog(getContext());
                        alert.setPositiveButton(R.string.yes, null);
                        alert.setIcon(Icon.Error);
                        alert.setTitle(R.string.error);
                        alert.setMessage(R.string.customer_operation_is_confirmed);
                        alert.show();
                    } else {
                        if (hasPayment()) {
                            CuteMessageDialog alert = new CuteMessageDialog(getContext());
                            alert.setPositiveButton(R.string.yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    removeReturnCall();
                                }
                            });
                            alert.setNegativeButton(R.string.no, null);
                            alert.setIcon(Icon.Warning);
                            alert.setTitle(R.string.warning);
                            alert.setMessage(R.string.the_customer_payment_will_be_unconfirmed_do_you_continue);
                            alert.show();
                        } else {
                            removeReturnCall();
                        }
                    }
                }
            });

            CuteButton revokeRequestButton = new CuteButton();
            revokeRequestButton.setTitle(R.string.revoke_return_request);
            revokeRequestButton.setIcon(R.drawable.ic_cancel_return_black_26dp);
            revokeRequestButton.setOnClickListener(new CuteButton.OnClickListener() {
                @Override
                public void onClick() {
                    try {
                        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                        if (backOfficeType.equals(BackOfficeType.ThirdParty)) {
                            OrderReturnReasonDialog editReasonDialog = new OrderReturnReasonDialog();
                            editReasonDialog.onItemSelected = reasonUniqueId -> {
                                try {
                                    revokeRequest(null, reasonUniqueId);
                                    update();
                                } catch (Exception e) {
                                    showErrorMessage();
                                }
                            };
                            editReasonDialog.show(getVaranegarActvity().getSupportFragmentManager(), "EditReasonDialog");
                        } else {
                            revokeRequest(null, null);
                            update();
                        }
                    } catch (Exception e) {
                        showErrorMessage();
                    }
                }
            });
            revokeRequestButton.setEnabled(new CuteButton.IIsEnabled() {
                @Override
                public boolean run() {
                    return !hasReturnCall() && !isDataSent();
                }
            });


            CuteButton okButton = new CuteButton();
            okButton.setTitle(R.string.save_request);
            okButton.setIcon(R.drawable.ic_done_white_36dp);
            okButton.setOnClickListener(new CuteButton.OnClickListener() {
                @Override
                public void onClick() {
                    save();
                }
            });
            okButton.setEnabled(new CuteButton.IIsEnabled() {
                @Override
                public boolean run() {
                    return !hasReturnCall() && !isDataSent();
                }
            });


            CuteButton previewButton = new CuteButton();
            previewButton.setOnClickListener(new CuteButton.OnClickListener() {
                @Override
                public void onClick() {
                    CustomerReturnPreviewFragment previewFragment = new CustomerReturnPreviewFragment();
                    previewFragment.setArguments(customerId, withRef, isFromRequest);
                    getVaranegarActvity().pushFragment(previewFragment);
                }
            });
            previewButton.setEnabled(new CuteButton.IIsEnabled() {
                @Override
                public boolean run() {
                    return true;
                }
            });
            previewButton.setTitle(R.string.preview);
            previewButton.setIcon(R.drawable.ic_remove_red_eye_white_36dp);

            toolbar = view.findViewById(R.id.return_cute_toolbar);
            if (isFromRequest()) {
                if (BackOfficeType.ThirdParty.equals(backOfficeType))
                    toolbar.setButtons(cancelButton, revokeRequestButton, previewButton, okButton);
                else
                    toolbar.setButtons(cancelButton, revokeRequestButton, okButton);
            } else {
                if (BackOfficeType.ThirdParty.equals(backOfficeType))
                    toolbar.setButtons(listCuteBtn, cancelButton, previewButton, okButton);
                else
                    toolbar.setButtons(listCuteBtn, cancelButton, okButton);
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
        return view;
    }

    private void revokeRequest(@Nullable UUID returnId, @Nullable UUID editReasonId) throws ValidationException, DbException {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            CustomerCallReturnViewManager returnViewManager = new CustomerCallReturnViewManager(getContext());
            List<CustomerCallReturnViewModel> callReturnViewModels;
            if (returnId == null)
                callReturnViewModels = returnViewManager.getCustomerCallReturns(customerId, withRef, isFromRequest);
            else {
                CustomerCallReturnViewModel call = returnViewManager.getCustomerCallReturn(returnId);
                callReturnViewModels = new ArrayList<>();
                if (call != null)
                    callReturnViewModels.add(call);
            }
            CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getContext());
            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
            for (CustomerCallReturnViewModel customerCallReturnViewModel :
                    callReturnViewModels) {
                CustomerCallReturnModel callReturnModel = returnManager.getItem(customerCallReturnViewModel.ReturnUniqueId);
                callReturnModel.IsCancelled = true;
                List<ReturnLinesModel> lines = returnLinesManager.getReturnLines(customerCallReturnViewModel.ReturnUniqueId);
                for (ReturnLinesModel line :
                        lines) {
                    line.TotalRequestAdd1Amount = Currency.ZERO;
                    line.TotalRequestAdd2Amount = Currency.ZERO;
                    line.TotalRequestAddOtherAmount = Currency.ZERO;
                    line.TotalRequestCharge = Currency.ZERO;
                    line.TotalRequestDis1Amount = Currency.ZERO;
                    line.TotalRequestDis2Amount = Currency.ZERO;
                    line.TotalRequestDis3Amount = Currency.ZERO;
                    line.TotalRequestDisOtherAmount = Currency.ZERO;
                    line.TotalRequestNetAmount = Currency.ZERO;
                    line.TotalRequestTax = Currency.ZERO;
                    line.EditReasonId = editReasonId;
                    returnLinesManager.update(line);
                    ReturnLineQtyManager qtyManager = new ReturnLineQtyManager(getContext());
                    List<ReturnLineQtyModel> qtys = qtyManager.getQtyLines(line.UniqueId);
                    for (ReturnLineQtyModel qty :
                            qtys) {
                        qty.Qty = BigDecimal.ZERO;
                        qtyManager.update(qty);
                    }
                }
                if (!backOfficeType.equals(BackOfficeType.ThirdParty))
                    callReturnModel.Comment = commentEditText.getText().toString();
                else
                    callReturnModel.Comment = customerCallReturnViewModel.Comment;
                callReturnModel.EndTime = new Date();
                callReturnModel.DealerUniqueId = dealerId;
                returnManager.update(callReturnModel);
                CustomerCallManager callManager = new CustomerCallManager(getContext());
                callManager.saveReturnCall(customerId, withRef, true);
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            showErrorMessage(R.string.back_office_type_is_uknown);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCalls();
        if (isNewFragment()) {
            extractAndCalcCustomerPrice();
            if (isFromRequest() && !hasReturnCall()) {
                CustomerCallReturnManager manager = new CustomerCallReturnManager(getContext());
                try {
                    manager.initCall(customerId, withRef);
                } catch (ReturnInitException e) {
                    showErrorMessage(R.string.error_init_return_request);
                } catch (DbException e) {
                    showErrorMessage(R.string.error_init_return_request);
                } catch (ValidationException e) {
                    showErrorMessage(R.string.error_init_return_request);
                }
            }
        }
        refresh();
    }

    private void removeReturnCall() {
        final CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.removeCallReturn(customerId, withRef, isFromRequest());
            Timber.e("Customer call order canceled");
            extractAndCalcCustomerPrice();
            if (isFromRequest()) {
                CustomerCallReturnManager manager = new CustomerCallReturnManager(getContext());
                manager.initCall(customerId, withRef);
            } else
                removePromotion();
            loadCalls();
            refresh();
        } catch (ReturnInitException e) {
            showErrorMessage(R.string.error_init_return_request);
        } catch (DbException e) {
            showErrorMessage(R.string.error_init_return_request);
        } catch (ValidationException e) {
            showErrorMessage(R.string.error_init_return_request);
        } catch (Exception ex) {
            showErrorMessage();
            Timber.e(ex);
        }
    }

    private void removePromotion() throws DbException, ValidationException {
        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && withRef) {
            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
            returnLinesManager.removePromotions(customerId, isFromRequest());
        }
    }

    private void setupAdapter() {
        try {
            if (hasReturnCall() || isFromRequest()) {
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                adapter = new ReturnReportAfterDiscountAdapter(getVaranegarActvity(), withRef, backOfficeType);
                adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                adapter.create(new CustomerCallReturnAfterDiscountViewModelRepository(), CustomerCallReturnAfterDiscountViewManager.getLines(customerId, withRef, isFromRequest(), hasReturnCall()), null);
            } else {
                adapter = new ReturnReportAdapter(getVaranegarActvity(), withRef);
                adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                adapter.create(new CustomerCallReturnViewModelRepository(), CustomerCallReturnViewManager.getLines(customerId, withRef, isFromRequest()), null);
                if (!isFromRequest())
                    adapter.addOnItemClickListener(new ContextMenuItem() {
                        @Override
                        public boolean isAvailable(int position) {
                            return true;
                        }

                        @Override
                        public String getName(int position) {
                            return getString(R.string.delete);
                        }

                        @Override
                        public int getIcon(int position) {
                            return R.drawable.ic_delete_forever_black_24dp;
                        }

                        @Override
                        public void run(final int position) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(R.string.are_you_sure);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CustomerCallReturnViewModel returnViewModel = (CustomerCallReturnViewModel) adapter.get(position);
                                    ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                                    try {
                                        returnLinesManager.removeReturnLineModel(returnViewModel);
                                        adapter.refresh();
                                    } catch (Exception ex) {
                                        Timber.e(ex, "onError deleting return line");
                                    }
                                }
                            });
                            builder.setNegativeButton(R.string.no, null);
                            builder.show();
                        }
                    });
            }

            if (!hasReturnCall())
                adapter.addOnItemClickListener(new ContextMenuItem() {
                    @Override
                    public boolean isAvailable(int position) {
                        CustomerCallReturnBaseViewModel returnViewModel = (CustomerCallReturnBaseViewModel) adapter.get(position);
                        return !returnViewModel.IsPromoLine;
                    }

                    @Override
                    public String getName(int position) {
                        return getString(R.string.edit);
                    }

                    @Override
                    public int getIcon(int position) {
                        return R.drawable.ic_mode_edit_black_24dp;
                    }

                    @Override
                    public void run(final int position) {
                        final CustomerCallReturnBaseViewModel returnViewModel = (CustomerCallReturnBaseViewModel) adapter.get(position);
                        final ProductModel productModel = new ProductManager(getContext()).getItem(returnViewModel.ProductId);
                        if (productModel == null)
                            throw new NullPointerException("Product id " + returnViewModel.ProductId + " not found");
                        try {
                            ReturnCalculatorForm calculatorForm = new ReturnCalculatorForm();
                            CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                            CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForReturn, true);
                            calculatorForm.setArguments(getContext(), productModel, calculatorUnits, returnViewModel, returnViewModel.RequestUnitPrice, withRef);
                            calculatorForm.onItemDeleted = new ReturnCalculatorForm.OnItemDeleted() {
                                @Override
                                public void run(ReturnCalculatorItem returnCalculatorItem) {
                                    ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                                    try {
                                        returnLinesManager.removeReturnLineModel(returnCalculatorItem);
                                        Timber.i("Deleting an item");
                                        adapter.refresh();
                                    } catch (Exception ex) {
                                        Timber.e(ex, "Error deleting return item");
                                        getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
                                    }
                                }
                            };
                            calculatorForm.onCalcFinish = new ReturnCalculatorForm.OnCalcFinish() {
                                @Override
                                public void run(List<ReturnCalculatorItem> returnCalculatorItems, Currency price) {
                                    try {
                                        BigDecimal total = BigDecimal.ZERO;
                                        for (ReturnCalculatorItem item :
                                                returnCalculatorItems) {
                                            total = total.add(item.getTotalQty());
                                        }
                                        if (withRef && !isFromRequest()) {
                                            if (total.compareTo(returnViewModel.InvoiceQty) > 0) {
                                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                                dialog.setIcon(Icon.Error);
                                                dialog.setTitle(R.string.error);
                                                dialog.setMessage(R.string.return_qty_is_larger_than_total_qty);
                                                dialog.setPositiveButton(R.string.ok, null);
                                                dialog.show();
                                                return;
                                            }
                                        }
                                        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                                        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && backOfficeType == BackOfficeType.ThirdParty && isFromRequest) {
                                            if (returnViewModel.EditReasonId == null && total.compareTo(returnViewModel.OriginalTotalReturnQty) < 0) {
                                                OrderReturnReasonDialog editReasonDialog = new OrderReturnReasonDialog();
                                                editReasonDialog.onItemSelected = reasonUniqueId -> {
                                                    updateReturnLine(returnViewModel, returnCalculatorItems, price, reasonUniqueId);
                                                };
                                                editReasonDialog.show(getVaranegarActvity().getSupportFragmentManager(), "ReturnReasonDialog");
                                            } else if (total.compareTo(returnViewModel.OriginalTotalReturnQty) == 0) {
                                                updateReturnLine(returnViewModel, returnCalculatorItems, price, null);
                                            } else {
                                                updateReturnLine(returnViewModel, returnCalculatorItems, price, returnViewModel.EditReasonId);
                                            }
                                        } else {
                                            updateReturnLine(returnViewModel, returnCalculatorItems, price, null);
                                        }
                                    } catch (UnknownBackOfficeException e) {
                                        Timber.e(e);
                                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                        dialog.setTitle(R.string.error);
                                        dialog.setMessage(R.string.back_office_type_is_uknown);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(R.string.ok, null);
                                        dialog.show();
                                    }
                                }
                            };
                            if (isFromRequest()) {
                                calculatorForm.setMaxValue(returnViewModel.OriginalTotalReturnQty);
                                calculatorForm.setFromRequest(true);
                            }
                            calculatorForm.show(getChildFragmentManager(), "3ba317b9-3c38-4cf3-9d05-392aafdb0fbd");
                        } catch (ProductUnitViewManager.UnitNotFoundException e) {
                            getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                            Timber.e(e, "product unit not found in Customer save return fragment");
                        }
                    }
                });

            reportView.setAdapter(adapter);
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
    }

    private void updateCustomerCallReturn() throws ValidationException, DbException {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getContext());
            CustomerOldInvoiceHeaderManager oldInvoiceHeaderManager = new CustomerOldInvoiceHeaderManager(getContext());
            List<CustomerCallReturnModel> returnModels = returnManager.getReturnCalls(customerId, withRef, isFromRequest());
            for (CustomerCallReturnModel returnModel :
                    returnModels) {
                if (!backOfficeType.equals(BackOfficeType.ThirdParty))
                    returnModel.Comment = commentEditText.getText().toString();
                returnModel.EndTime = new Date();
                if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && withRef && !isFromRequest()) {
                    if (returnModel.BackOfficeInvoiceId != null) {
                        CustomerOldInvoiceHeaderModel invoice = oldInvoiceHeaderManager.getItem(returnModel.BackOfficeInvoiceId);
                        if (invoice != null)
                            returnModel.DealerUniqueId = invoice.DealerId;
                    } else {
                        returnModel.DealerUniqueId = dealerId;
                    }
                } else
                    returnModel.DealerUniqueId = dealerId;
            }
            returnManager.update(returnModels);
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            showErrorMessage(R.string.back_office_type_is_uknown);
        }
    }


    private void showErrorMessage() {
        showErrorMessage(R.string.error_saving_request);
    }

    private void showErrorMessage(@StringRes int error) {
        showErrorMessage(getString(error));
    }

    private void showErrorMessage(String error) {
        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
        cuteMessageDialog.setMessage(error);
        cuteMessageDialog.setTitle(R.string.error);
        cuteMessageDialog.setIcon(Icon.Error);
        cuteMessageDialog.setPositiveButton(R.string.ok, null);
        cuteMessageDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (adapter.size() == 0 && !hasReturnCall()) {
            cancel();
        } else {
            if (!hasReturnCall()) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Alert);
                dialog.setTitle(R.string.save_return_request);
                dialog.setMessage(R.string.you_should_save_order);
                dialog.setPositiveButton(R.string.save_return_request, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                    }
                });
                dialog.setNegativeButton(R.string.cancel_return_request, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancel();
                    }
                });
                dialog.show();
            } else {
                VaranegarApplication.getInstance().remove("955119ea-eb2d-428a-9dd7-03bb95844d1f");
                getVaranegarActvity().popFragment();
            }
        }
    }

    private void save() {
        if (isFromRequest() && isEmpty(null)) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setMessage(R.string.do_you_cancel_return_request);
            dialog.setTitle(R.string.order_is_empty);
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                        if (backOfficeType.equals(BackOfficeType.ThirdParty)) {
                            OrderReturnReasonDialog editReasonDialog = new OrderReturnReasonDialog();
                            editReasonDialog.onItemSelected = reasonUniqueId -> {
                                try {
                                    revokeRequest(null, reasonUniqueId);
                                    update();
                                } catch (Exception e) {
                                    showErrorMessage();
                                }
                            };
                            editReasonDialog.show(getVaranegarActvity().getSupportFragmentManager(), "EditReasonDialog");
                        } else {
                            revokeRequest(null, null);
                            update();
                        }
                    } catch (Exception e) {
                        showErrorMessage();
                    }
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();
            return;

        }
        if (adapter.size() == 0) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setMessage(R.string.cancel_return_request);
            dialog.setTitle(R.string.order_is_empty);
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();
            return;
        }

        if (!(VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) && !withRef && dealerId == null && !isFromRequest()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setMessage(R.string.choose_the_dealer_nam);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && !withRef && !isFromRequest())) {
                dialog.setNegativeButton(R.string.choose_me, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DealerManager dealerManager = new DealerManager(getContext());
                        List<DealerModel> dealers = dealerManager.getItems(new Query().from(Dealer.DealerTbl));
                        dealerId = UserManager.readFromFile(getContext()).UniqueId;
                        int index = Linq.findFirstIndex(dealers, new Linq.Criteria<DealerModel>() {
                            @Override
                            public boolean run(DealerModel item) {
                                return item.UniqueId.equals(dealerId);
                            }
                        });
                        if (index > 0)
                            dealerNameSpinner.selectItem(index);
                        preUpdate();
                    }
                });
            }
            dialog.show();
            return;
        }
        preUpdate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null && context instanceof Activity)
            activity = (Activity) context;
    }

    private void preUpdate() {
        try {
            BackOfficeType backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
            if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && (withRef || backOfficeType == BackOfficeType.ThirdParty)) {
                showProgressDialog();
                new Thread(() -> {
                    if (backOfficeType == BackOfficeType.ThirdParty) {
                        CalcPromotion.calcReturnPromotion(getContext(), customerId, withRef, isFromRequest, new PromotionCallback() {
                            @Override
                            public void onSuccess(CustomerCallOrderPromotion data) {
                                if (activity != null && !activity.isFinishing())
                                    activity.runOnUiThread(() -> {
                                        try {
                                            savePromotions(data);
                                            update();
                                        } catch (Exception e) {
                                            showErrorMessage();
                                        } finally {
                                            dismissProgressDialog();
                                        }
                                    });
                            }

                            @Override
                            public void onFailure(String error) {
                                if (activity != null && !activity.isFinishing())
                                    activity.runOnUiThread(() -> {
                                        dismissProgressDialog();
                                        showErrorMessage(error);
                                    });
                            }

                            @Override
                            public void onProcess(String msg) {

                            }
                        });
                    } else {
                        try {
                            List<CustomerCallReturnModel> customerCallReturnModels = new CustomerCallReturnManager(getContext()).getReturnCalls(customerId, true, null);
                            List<DiscountCallReturnLineData> discountCallReturnLineData = new ArrayList<>();
                            List<ReturnDisEvcHeaderViewModel> returnDisEvcHeaderViewModels = new ArrayList<>();
                            SysConfigModel serverAddress = new SysConfigManager(getContext()).read(ConfigKey.OnliveEvc, SysConfigManager.cloud);
                            for (CustomerCallReturnModel item : customerCallReturnModels) {
                                if (!isEmpty(item.UniqueId)) {
                                    if (SysConfigManager.compare(serverAddress, true))
                                        returnDisEvcHeaderViewModels.add(fillReturnDisEvcData(item));
                                    else
                                        discountCallReturnLineData.addAll(CalcPromotion.calcReturnPromotionV3(getContext(), item, EVCType.SELLRETURN));
                                } else if (isEmpty(item.UniqueId)) {
                                    revokeRequest(item.UniqueId, null);
                                }
                            }
                            if (SysConfigManager.compare(serverAddress, true))
                                calcReturnPromotionOnline(returnDisEvcHeaderViewModels);
                            else {
                                ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                                returnLinesManager.savePromotion(getVaranegarActvity(), customerId, isFromRequest(), discountCallReturnLineData, new ReturnLinesManager.OnPromotionCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        if (activity != null && !activity.isFinishing())
                                            activity.runOnUiThread(() -> {
                                                dismissProgressDialog();
                                                update();
                                            });
                                    }

                                    @Override
                                    public void onFailure() {
                                        if (activity != null && !activity.isFinishing())
                                            activity.runOnUiThread(() -> {
                                                dismissProgressDialog();
                                                showErrorMessage();
                                            });
                                    }

                                    @Override
                                    public void onCancel() {
                                        if (activity != null && !activity.isFinishing())
                                            activity.runOnUiThread(() -> {
                                                dismissProgressDialog();
                                                refresh();
                                            });
                                    }
                                });
                            }
                        } catch (final DistException e) {
                            if (activity != null && !activity.isFinishing())
                                activity.runOnUiThread(() -> {
                                    dismissProgressDialog();
                                    showErrorMessage(e.getMessage());
                                });
                        } catch (Exception ex) {
                            if (activity != null && !activity.isFinishing())
                                activity.runOnUiThread(() -> {
                                    dismissProgressDialog();
                                    showErrorMessage();
                                });

                        }
                    }


                }).start();
            } else if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && backOfficeType.equals(BackOfficeType.Varanegar) && (!isFromRequest() || isChangedFromRequest(null)) && !withRef) {
                ProductManager productManager = new ProductManager(getContext());
                CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
                List<CustomerCallReturnLinesViewModel> lines = linesViewManager.getLines(customerId, withRef, isFromRequest());
                ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                for (final CustomerCallReturnLinesViewModel returnLine :
                        lines) {
                    ReturnLinesModel returnLinesModel = returnLinesManager.getItem(returnLine.UniqueId);
                    if (returnLinesModel != null) {
                        ProductModel productModel = productManager.getItem(returnLine.ProductUniqueId);
                        returnLinesModel.TotalRequestAdd1Amount = (productModel.TaxPercent == null ? Currency.ZERO : new Currency(productModel.TaxPercent)).multiply(returnLine.TotalRequestAmount).divide(Currency.valueOf(100));
                        returnLinesModel.TotalRequestAdd2Amount = (productModel.ChargePercent == null ? Currency.ZERO : new Currency(productModel.ChargePercent)).multiply(returnLine.TotalRequestAmount).divide(Currency.valueOf(100));
                        returnLinesModel.TotalRequestNetAmount = returnLine.TotalRequestAmount.add(returnLinesModel.TotalRequestAdd1Amount.add(returnLinesModel.TotalRequestAdd2Amount));
                        returnLinesManager.update(returnLinesModel);
                    }
                }
                update();
            } else
                update();
        } catch (UnknownBackOfficeException e) {
            showErrorMessage(getString(R.string.back_office_type_is_uknown));
        } catch (Exception ex) {
            showErrorMessage();
        }

    }

    private void calcReturnPromotionOnline(List<ReturnDisEvcHeaderViewModel> returnDisEvcHeaderViewModels) {
        ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
        PromotionApi promotionApi = new PromotionApi(getContext());
        promotionApi.runWebRequest(promotionApi.returnDisControl(returnDisEvcHeaderViewModels), new WebCallBack<ReturnDistViewModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(ReturnDistViewModel result, Request request) {
                try {
                    if (result.message != null && result.message.length() > 0) {
                        activity.runOnUiThread(() -> {
                            dismissProgressDialog();
                            showErrorMessage(result.message);
                        });
                    }
                   else {
                    List<DiscountCallReturnLineData> discountCallReturnLineData = returnDisItemToDiscountCallReturnData(result.returnDisItems);
                    returnLinesManager.savePromotion(getVaranegarActvity(), customerId, isFromRequest(), discountCallReturnLineData, new ReturnLinesManager.OnPromotionCallBack() {
                        @Override
                        public void onSuccess() {
                            if (activity != null && !activity.isFinishing())
                                activity.runOnUiThread(() -> {
                                    dismissProgressDialog();
                                    update();
                                });
                        }

                        @Override
                        public void onFailure() {
                            if (activity != null && !activity.isFinishing())
                                activity.runOnUiThread(() -> {
                                    dismissProgressDialog();
                                    showErrorMessage();
                                });
                        }

                        @Override
                        public void onCancel() {
                            if (activity != null && !activity.isFinishing())
                                activity.runOnUiThread(() -> {
                                    dismissProgressDialog();
                                    refresh();
                                });
                        }
                    });
                } }catch (Exception ex) {
                    if (activity != null && !activity.isFinishing())
                        activity.runOnUiThread(() -> {
                            dismissProgressDialog();
                            showErrorMessage();
                        });
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                if (activity != null && !activity.isFinishing())
                    activity.runOnUiThread(() -> {
                        dismissProgressDialog();
                        showErrorMessage();
                    });
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                if (activity != null && !activity.isFinishing())
                    activity.runOnUiThread(() -> {
                        dismissProgressDialog();
                        showErrorMessage();
                    });
            }
        });
    }

    private List<DiscountCallReturnLineData> returnDisItemToDiscountCallReturnData(List<ReturnDisItemViewModel> result) {
        CustomerCallReturnLinesViewManager customerCallReturnLinesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        CustomerCallReturnManager customerCallReturnManager = new CustomerCallReturnManager(getContext());
        List<CustomerCallReturnLinesViewModel> returnLines = customerCallReturnLinesViewManager.getLines(customerId, true, isFromRequest());
        List<CustomerCallReturnModel> returnModels = customerCallReturnManager.getReturnCalls(customerId, withRef, isFromRequest());
        CustomerOldInvoiceHeaderManager oldInvoiceHeaderManager = new CustomerOldInvoiceHeaderManager(getContext());
        CustomerOldInvoiceDetailManager oldInvoiceDetailManager = new CustomerOldInvoiceDetailManager(getContext());
        List<DiscountCallReturnLineData> list = new ArrayList<>();
        for (ReturnDisItemViewModel item :
                result) {
            CustomerOldInvoiceHeaderModel oldInvoiceHeaderModel = oldInvoiceHeaderManager.getInvoiceBySaleRef(item.RefId);
//            List<CustomerOldInvoiceDetailModel> oldInvoiceItems = oldInvoiceDetailManager.getOldInvoiceItems(oldInvoiceHeaderModel.UniqueId);
            ProductModel productModel = new ProductManager(getContext()).getProductByBackOfficeId(item.GoodsRef);
            CustomerCallReturnModel returnModel = Linq.findFirst(returnModels, ret -> ret.BackOfficeInvoiceNo.equals(oldInvoiceHeaderModel.SaleNo));
            CustomerCallReturnLinesViewModel returnLine = Linq.findFirst(returnLines, line -> line.ReturnUniqueId.equals(returnModel.UniqueId) && productModel.ProductCode.equals(line.ProductCode));
            DiscountCallReturnLineData discountCallReturnLineData = new DiscountCallReturnLineData();
            discountCallReturnLineData.productId = productModel.BackOfficeId;
            discountCallReturnLineData.productName = productModel.ProductName;
            discountCallReturnLineData.productCode = productModel.ProductCode;
            discountCallReturnLineData.returnLineUniqueId = returnLine != null ? returnLine.UniqueId.toString() : UUID.randomUUID().toString();
            discountCallReturnLineData.referenceNo = returnModel.BackOfficeInvoiceNo;
            discountCallReturnLineData.callUniqueId = returnModel.UniqueId.toString();
            discountCallReturnLineData.returnReasonId = returnLine != null ? returnLine.ReturnReasonId : null;
            discountCallReturnLineData.ReturnProductTypeId = returnLine != null ? returnLine.ReturnProductTypeId : null;
            discountCallReturnLineData.totalReturnAdd1Amount = HelperMethods.currencyToBigDecimal(item.TotalRequestAdd1Amount);
            discountCallReturnLineData.totalReturnAdd2Amount = HelperMethods.currencyToBigDecimal(item.TotalRequestAdd2Amount);
            discountCallReturnLineData.totalReturnDis1 = HelperMethods.currencyToBigDecimal(item.TotalRequestDis1Amount);
            discountCallReturnLineData.totalReturnDis2 = HelperMethods.currencyToBigDecimal(item.TotalRequestDis2Amount);
            discountCallReturnLineData.totalReturnDis3 = HelperMethods.currencyToBigDecimal(item.TotalRequestDis3Amount);
            discountCallReturnLineData.totalReturnAddOtherAmount = HelperMethods.currencyToBigDecimal(item.TotalRequestAddOtherAmount);
            discountCallReturnLineData.totalReturnDisOther = HelperMethods.currencyToBigDecimal(item.TotalRequestDisOtherAmount);
            discountCallReturnLineData.totalReturnTax = HelperMethods.currencyToBigDecimal(item.TotalRequestTax);
            discountCallReturnLineData.totalReturnCharge = HelperMethods.currencyToBigDecimal(item.TotalRequestCharge);
            discountCallReturnLineData.totalReturnNetAmount = HelperMethods.currencyToBigDecimal(item.TotalRequestNetAmount);
            discountCallReturnLineData.isPromoLine = item.IsPromoLine > 0;
            if (item.UnitPrice != null)
                discountCallReturnLineData.unitPrice = HelperMethods.currencyToBigDecimal(item.UnitPrice);
            discountCallReturnLineData.returnTotalQty = item.TotalQty;
            list.add(discountCallReturnLineData);
        }
        return list;
    }

    private ReturnDisEvcHeaderViewModel fillReturnDisEvcData(CustomerCallReturnModel item) {
        CustomerOldInvoiceHeaderManager oldInvoiceHeaderManager = new CustomerOldInvoiceHeaderManager(getContext());
        CustomerOldInvoiceHeaderModel oldInvoiceHeaderModel = oldInvoiceHeaderManager.getInvoiceByNo(item.BackOfficeInvoiceNo);
        ReturnDisEvcHeaderViewModel headerViewModel = new ReturnDisEvcHeaderViewModel();
        headerViewModel.CustRef = new CustomerManager(getContext()).getItem(item.CustomerUniqueId).BackOfficeId;
        headerViewModel.OrderTypeRef = oldInvoiceHeaderModel.OrderType;
        headerViewModel.SaleOfficeRef = item.SaleOfficeRefSDS;
        headerViewModel.OrderDate = item.BackOfficeInvoiceDate;
        headerViewModel.DealerRef = oldInvoiceHeaderModel.DealerRef;
        headerViewModel.BuyTypeRef = oldInvoiceHeaderModel.BuyTypeRef;
        headerViewModel.DisType = oldInvoiceHeaderModel.DisType;
        headerViewModel.PaymentUsanceRef = oldInvoiceHeaderModel.PaymentUsanceRef;
        headerViewModel.EvcType = EvcType.SELLRETURN.getCode();
        headerViewModel.RefId = oldInvoiceHeaderModel.SaleRef;
        headerViewModel.StockDCRef = oldInvoiceHeaderModel.StockDCCode;
        headerViewModel.ReturnDisEvcDetails = new ArrayList<>();
        CustomerCallReturnLinesViewManager returnLinesManager = new CustomerCallReturnLinesViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines = returnLinesManager.getReturnLines(item.UniqueId, true);
        for (CustomerCallReturnLinesViewModel line :
                lines) {
            ReturnDisEvcDetailViewModel returnDisEvcDetailViewModel = new ReturnDisEvcDetailViewModel();
            returnDisEvcDetailViewModel.GoodsRef = new ProductManager(getContext()).getBackOfficeId(line.ProductUniqueId);
            returnDisEvcDetailViewModel.TotalQty = line.TotalReturnQty;
            headerViewModel.ReturnDisEvcDetails.add(returnDisEvcDetailViewModel);
        }
        return headerViewModel;
    }

    private void savePromotions(CustomerCallOrderPromotion data) throws ValidationException, DbException {
        CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines = linesViewManager.getLines(customerId, withRef, isFromRequest());
        ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());

        for (final CustomerCallOrderLinePromotion p :
                data.LinesWithPromo) {
            CustomerCallReturnLinesViewModel returnLinesViewModel = Linq.findFirst(lines, item -> item.UniqueId.equals(p.UniqueId));
            if (returnLinesViewModel != null) {
                ReturnLinesModel returnLinesModel = returnLinesManager.getItem(returnLinesViewModel.UniqueId);
                if (returnLinesModel != null) {
                    returnLinesModel.TotalRequestAdd1Amount = HelperMethods.zeroIfNull(p.InvoiceAdd1);
                    returnLinesModel.TotalRequestAdd2Amount = HelperMethods.zeroIfNull(p.InvoiceAdd2);
                    returnLinesModel.TotalRequestAddOtherAmount = HelperMethods.zeroIfNull(p.InvoiceAddOther);
                    returnLinesModel.TotalRequestCharge = HelperMethods.zeroIfNull(p.InvoiceCharge);
                    returnLinesModel.TotalRequestTax = HelperMethods.zeroIfNull(p.InvoiceTax);
                    returnLinesModel.TotalRequestDis1Amount = HelperMethods.zeroIfNull(p.InvoiceDis1);
                    returnLinesModel.TotalRequestDis2Amount = HelperMethods.zeroIfNull(p.InvoiceDis2);
                    returnLinesModel.TotalRequestDis3Amount = HelperMethods.zeroIfNull(p.InvoiceDis3);
                    returnLinesModel.TotalRequestDisOtherAmount = HelperMethods.zeroIfNull(p.InvoiceDisOther);
                    returnLinesManager.update(returnLinesModel);
                }
            }
        }
    }

    private void showProgressDialog() {
        discountProgressDialog = new ProgressDialog(activity);
        discountProgressDialog.setMessage(getString(R.string.calculating_discount));
        discountProgressDialog.setCancelable(false);
        discountProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (discountProgressDialog != null && discountProgressDialog.isShowing()) {
            try {
                discountProgressDialog.dismiss();
            } catch (Exception ignored) {
                Timber.e(ignored);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
    }

    private void update() {
        try {
            updateCustomerCallReturn();
            CustomerCallManager callManager = new CustomerCallManager(getContext());
            callManager.saveReturnCall(customerId, withRef, isFromRequest());
            loadCalls();
            refresh();
        } catch (Exception ex) {
            if (isResumed())
                showErrorMessage();
        }
    }

    private void cancel() {
        CustomerModel customerModel = new CustomerManager(getContext()).getItem(customerId);
        CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getContext());
        try {
            callReturnManager.cancel(customerModel, withRef, isFromRequest());
            getVaranegarActvity().popFragment();
        } catch (Exception ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.could_not_delete_request);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
        }
    }

    protected void loadCalls() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        calls = callManager.loadCalls(customerId);
    }

    private boolean hasPayment() {
        final CustomerCallManager callManager = new CustomerCallManager(getContext());
        return callManager.hasPaymentCall(calls);
    }

    private boolean hasReturnCall() {
        final CustomerCallManager callManager = new CustomerCallManager(getContext());
        return callManager.hasReturnCall(calls, withRef, isFromRequest());
    }


    private boolean isDataSent() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        return callManager.isDataSent(calls, null);
    }

    private boolean isConfirmed() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        return callManager.isConfirmed(calls);
    }

    private void refresh() {
        setupAdapter();
        loadCalls();
        toolbar.refresh();
        adapter.refresh();
        if (hasReturnCall() || isDataSent()) {
            if (dealerNameSpinner != null)
                dealerNameSpinner.setEnabled(false);
            commentEditText.setEnabled(false);
            OrderAmount amount = new CustomerCallReturnLinesViewManager(getContext()).calculateTotalAmount(customerId, withRef, isFromRequest());
            amountPairedItems.setValue(HelperMethods.currencyToString(amount.TotalAmount));
            netAmountPairtedItems.setValue(HelperMethods.currencyToString(amount.NetAmount));
            discountAmountPairtedItems.setValue(HelperMethods.currencyToString(amount.DiscountAmount));
            addAmountPairtedItems.setValue(HelperMethods.currencyToString(amount.AddAmount));
        } else {
            adapter.setEnabled(true);
            if (dealerNameSpinner != null)
                dealerNameSpinner.setEnabled(true);
            commentEditText.setEnabled(true);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
        outState.putString("2c939db1-ba5a-480c-8d94-103b4d0d232f", dealerId != null ? dealerId.toString() : null);
        if (discountProgressDialog != null)
            outState.putBoolean("69d2ac5d-cc07-4b66-aa0f-cc09d55e3296", discountProgressDialog.isShowing());
    }

    public void setArguments(UUID customerId, boolean isRef, boolean isRequest, boolean isRefReplacementRegistration, boolean withoutRefReplacementRegistration) {
        addArgument("2c7d1543-3063-48b5-b738-e163c102354b", customerId.toString());
        addArgument("c4afed7f-901a-4eae-898c-76dedb2bd738", isRef);
        addArgument("4333ae1b-e90f-423b-aa1c-6425c46ef4fc", isRequest);
        addArgument("84788E31-2337-4FD4-BB82-253C5FF9E6F1", isRefReplacementRegistration);
        addArgument("42A25C92-A12C-485C-B625-65DDF3F0E7D3", withoutRefReplacementRegistration);
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    private void updateReturnLine(CustomerCallReturnBaseViewModel returnViewModel, List<ReturnCalculatorItem> returnCalculatorItems, Currency price, @Nullable UUID editReasonId) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            CustomerCallReturnManager returnManger = new CustomerCallReturnManager(getContext());
            String saleNo = "0";
            if (returnViewModel.SaleNo != null && !(returnViewModel.SaleNo.equals("")))
                saleNo = returnViewModel.SaleNo;
            String comment;
            if (!backOfficeType.equals(BackOfficeType.ThirdParty))
                comment = commentEditText.getText().toString();
            else
                comment = returnViewModel.Comment;
            long affectedRows = returnManger.addOrUpdateCallReturn(isFromRequest(),
                    returnCalculatorItems,
                    customerId,
                    returnViewModel.InvoiceId,
                    returnViewModel.StockId,
                    price,
                    comment,
                    saleNo,
                    dealerId,
                    returnViewModel.ReturnUniqueId,
                    withoutRefReplacementRegistration || withRefReplacementRegistration,
                    returnViewModel.ReferenceNo,
                    editReasonId);
            Timber.i("Updating return qty lines succeeded. number of affected rows: %d", affectedRows);
            adapter.refresh();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            showErrorMessage(R.string.back_office_type_is_uknown);
        } catch (Exception ex) {
            Timber.e(ex, "Error updating return qty lines");
            getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
        }
    }
}