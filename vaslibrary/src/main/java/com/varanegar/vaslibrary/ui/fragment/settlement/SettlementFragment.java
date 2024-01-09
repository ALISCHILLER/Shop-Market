package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.prefs.Preferences;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.enums.ThirdPartyPaymentTypes;
import com.varanegar.vaslibrary.manager.CustomerOpenInvoicesViewManager;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderViewManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.SaveOrderUtility;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerInvoicePaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ControlPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ThirdPartyControlPaymentChangedException;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.InvoicePrint.InvoicePrintHelper;
import com.varanegar.vaslibrary.print.datahelper.OrderPrintType;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/28/2017.
 */

/**
 * صفحه تسویه حساب
 */
public class SettlementFragment extends VisitFragment {

    private UUID customerId;
    private List<PaymentModel> paymentsList;
    private RecyclerView.Adapter adapter;
    private PaymentManager paymentManager;
    private PairedItems totalPaymentPairedItems;
    private PairedItems netAmountPairedItems;
    private PairedItems remainingPairedItems;
    private PairedItems invoiceAmountPairedItems;
    private PairedItems oldInvoicesAmountPairedItems;
    private PairedItems returnAmountPairedItems;
    private PairedItems discountPairedItems;
    private PairedItems oldRemainItems;
    private PairedItems oldRemainAfterPaymentItems;
    private PairedItems usanceDayItems, usanceRefItems, usancePayItems;
    private List<CustomerCallModel> customerCalls;
    private CustomerPayment customerPayment;
    private Set<UUID> validPaymentTypes;
    private CustomerModel customer;
    private ConfigMap sysConfigMap;
    private List<CustomerCallOrderModel> customerCallOrderModels;
    private CuteToolbar cuteToolbar;

    @NonNull
    @Override
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NonNull UUID customerId) {
        addArgument("f846d9ed-cfe9-4f1e-8f73-2b9e96d1cfb8", customerId.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            customerId = UUID.fromString(getArguments().getString("f846d9ed-cfe9-4f1e-8f73-2b9e96d1cfb8"));
        } catch (Exception ex) {
            throw new NullPointerException("You have to set customer id");
        }
        SysConfigManager sysConfigmanager = new SysConfigManager(getContext());
        sysConfigMap = sysConfigmanager.read(SysConfigManager.cloud);
        CustomerManager customerManager = new CustomerManager(getContext());
        customer = customerManager.getItem(customerId);
        paymentManager = new PaymentManager(getContext());
        customerPayment = paymentManager.calculateCustomerPayment(customerId);
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        updateCustomerPayment();
        ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
        SysConfigModel configModel = new SysConfigManager(getContext()).read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
        validPaymentTypes = validPayTypeManager.getValidPayTypes(customerCallOrderModels, customerPayment.getSelectedOldInvoices(), SysConfigManager.compare(configModel, true));


        if (isNewFragment()) {
            Preferences preferences = new Preferences(getContext());
            boolean allInvoicesSelected = preferences.getBoolean(Preferences.InvoiceSelection, customerId, null, null, false);
            boolean selectAll = preferences.getBoolean(Preferences.UserPreferences, null, null, UUID.fromString("dba935a4-9260-4183-9d5b-6827b91b4255"), false);
            if (!allInvoicesSelected && selectAll) {
                OldInvoiceHeaderViewManager oldInvoiceHeaderViewManager = new OldInvoiceHeaderViewManager(getContext());
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel OpenInvoiceBase = sysConfigManager.read(ConfigKey.OpenInvoicesBasedOn, SysConfigManager.cloud);
                List<OldInvoiceHeaderViewModel> oldInvoiceHeaderViewModels;
                if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(OpenInvoiceBase, CustomerOpenInvoicesViewManager.OpenInvoiceBaseOnType.BaseOnDealer)) {
                    UserModel userModel = UserManager.readFromFile(getContext());
                    oldInvoiceHeaderViewModels = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId, userModel.UniqueId);
                } else {
                    oldInvoiceHeaderViewModels = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId);
                }
                final CustomerInvoicePaymentManager customerInvoicePaymentManager = new CustomerInvoicePaymentManager(getContext());
                try {
                    customerInvoicePaymentManager.removeAll(customerId);
                    for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                            oldInvoiceHeaderViewModels) {
                        customerInvoicePaymentManager.add(oldInvoiceHeaderViewModel);
                    }
                    preferences.putBoolean(Preferences.InvoiceSelection, customerId, null, null, true);
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }

        }
    }

    private void updateCustomerPayment() {
        customerPayment = paymentManager.calculateCustomerPayment(customerId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settlement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            loadCustomerCalls();
            setupPaymentButtons(view);
            setupPaymentsList(view);
            ((TextView) view.findViewById(R.id.title_text_view)).setText(customer.CustomerName + " " + customer.CustomerCode);
            View printImageBtn = view.findViewById(R.id.print_image_button);
            printImageBtn.setVisibility(View.VISIBLE);
            printImageBtn.setOnClickListener(view1 -> {
                if (!isConfirmed()) {
                    SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                    SysConfigModel print = sysConfigManager.read(ConfigKey.PrintInvoice, SysConfigManager.cloud);
                    if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales) && SysConfigManager.compare(print, false)) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setMessage(R.string.pre_factor_printing_is_not_allowed);
                        dialog.show();
                    } else {
                        try {
                            controlPayments();
                            InvoicePrintHelper printInvoice = new InvoicePrintHelper(getVaranegarActvity(), customerId, OrderPrintType.Preview);
                            printInvoice.start(null);
                        } catch (ControlPaymentException e) {
                            Timber.e(e);
                            showErrorDialog(e.getMessage());
                        } catch (UnknownBackOfficeException e) {
                            Timber.e(e);
                            showErrorDialog();
                        } catch (ThirdPartyControlPaymentChangedException e) {
                            thirdPartyControlPayments(e.getMessage(), e.getThirdPartyPaymentType(), e.getPinCode(),e.getPinType(), true);
                        }
                    }
                }
            });

            view.findViewById(R.id.back_image_view).setOnClickListener(view12 -> getVaranegarActvity().popFragment());
            totalPaymentPairedItems = view.findViewById(R.id.total_payment_paired_items);
            netAmountPairedItems = view.findViewById(R.id.net_amount_paired_items);
            remainingPairedItems = view.findViewById(R.id.remaining_paired_items);
            invoiceAmountPairedItems = view.findViewById(R.id.invoice_amount_paired_items);
            oldInvoicesAmountPairedItems = view.findViewById(R.id.old_invoices_amount_paired_items);
            returnAmountPairedItems = view.findViewById(R.id.return_amount_paired_items);
            discountPairedItems = view.findViewById(R.id.discount_paired_items);
            oldRemainItems = view.findViewById(R.id.old_remain_amount_paired_items);
            oldRemainAfterPaymentItems = view.findViewById(R.id.old_remain_after_payement_amount_paired_items);
            usanceDayItems = view.findViewById(R.id.usance_day_paired_items);
            usanceRefItems = view.findViewById(R.id.usance_ref_paired_items);
            usancePayItems = view.findViewById(R.id.usance_pay_paired_items);

            oldRemainItems.setVisibility(View.GONE);
            totalPaymentPairedItems.setVisibility(View.GONE);
            returnAmountPairedItems.setTitle("مبلغ آنی");

            oldInvoicesAmountPairedItems.setTitle("مبلغ نقدی");

            invoiceAmountPairedItems.setTitle("مبلغ عرفی ");

            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.ThirdParty) {
                usanceDayItems.setVisibility(View.VISIBLE);
                usanceRefItems.setVisibility(View.VISIBLE);
                usancePayItems.setVisibility(View.VISIBLE);
            } else {
                usanceDayItems.setVisibility(View.GONE);
                usanceRefItems.setVisibility(View.GONE);
                usancePayItems.setVisibility(View.GONE);
            }
            refreshSettlement();
        } catch (UnknownBackOfficeException e) {
            showErrorDialog(getString(R.string.back_office_type_is_uknown));
        }

    }

    private void loadCustomerCalls() {
        customerCalls = new CustomerCallManager(getContext()).loadCalls(customerId);
    }

    private boolean isConfirmed() {
        return new CustomerCallManager(getContext()).isConfirmed(customerCalls);
    }

    void controlPayments() throws ControlPaymentException, UnknownBackOfficeException, ThirdPartyControlPaymentChangedException {
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
        if (backOfficeType == BackOfficeType.ThirdParty)
            paymentManager.thirdPartyControlPayments(customer, customerCallOrderModels, customerPayment, false);
        else {
            paymentManager.controlPayments(customer, customerCallOrderModels, customerPayment, sysConfigMap);
        }
    }

    private void saveSettlement() {
        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (adapter.getItemCount() > 0 || backOfficeType == BackOfficeType.ThirdParty) {
                try {
                    controlPayments();
                    customerCallManager.savePaymentCall(customerId);
                    getVaranegarActvity().popFragment();
                } catch (ControlPaymentException e) {
                    showErrorDialog(e.getMessage());
                } catch (UnknownBackOfficeException e) {
                    showErrorDialog();
                } catch (ThirdPartyControlPaymentChangedException e) {
                    thirdPartyControlPayments(e.getMessage(), e.getThirdPartyPaymentType(), e.getPinCode(),e.getPinType(), false);
                } catch (DbException e) {
                    showErrorDialog();
                } catch (ValidationException e) {
                    showErrorDialog();
                }
            } else {
                try {
                    customerCallManager.removePaymentCall(customerId);
                    getVaranegarActvity().popFragment();
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        } catch (UnknownBackOfficeException e) {
            showErrorDialog();
        }
    }

    void thirdPartyControlPayments(String errorMessage, @Nullable ThirdPartyPaymentTypes paymentType,
                                   @Nullable String pinCode,@Nullable String pinType, boolean print) {
        CustomerCallInvoiceManager customerCallOrderManager = new CustomerCallInvoiceManager(getActivity());
        List<CustomerCallInvoiceModel>  customerCallOrderModels = customerCallOrderManager
                .getCustomerCallInvoices(getCustomerId());
        if (paymentType == null) {
            showErrorDialog(errorMessage);
        } else {
            if (pinCode == null) {
                changePaymentType(paymentType, print);
            } else {
                InsertPinDialog dialog = new InsertPinDialog();
                dialog.setCancelable(false);
                dialog.setClosable(false);
                dialog.setValues(pinCode);
                dialog.setValuesRequst(pinType,getCustomerId(),customerCallOrderModels
                        .get(0).UniqueId,customerCallOrderModels.get(0).DealerCodeSDS,getActivity()
                        .getString(R.string.please_insert_pin_code));
                dialog.setOnResult(new InsertPinDialog.OnResult() {
                    @Override
                    public void done() {
                        changePaymentType(paymentType, print);
                    }

                    @Override
                    public void failed(String error) {
                        Timber.e(error);
                        if (print) {
                            printFailed(getContext(), error);
                        } else {
                            saveSettlementFailed(getContext(), error);
                        }
                    }
                });
                dialog.show(requireActivity().getSupportFragmentManager(), "InsertPinDialog");
            }
        }
    }

    private SaveOrderUtility.ISaveOrderCallback saveOrderCallBack(boolean print, CustomerCallOrderModel customerCallOrderModel, SaveOrderUtility saveOrderUtility) {
        return new SaveOrderUtility.ISaveOrderCallback() {
            @Override
            public void onSuccess() {
                try {
                    paymentManager.thirdPartyControlPaymentsAfterEvc(customer, customerCallOrderModels, customerPayment);
                    if (print) {
                        InvoicePrintHelper printInvoice = new InvoicePrintHelper(getVaranegarActvity(), customerId, OrderPrintType.Preview);
                        printInvoice.start(null);
                    } else {
                        try {
                            new CustomerCallManager(getContext()).savePaymentCall(customerId);
                            refreshSettlement();
//                        getVaranegarActvity().popFragment();
                        } catch (ValidationException e) {
                            showErrorDialog();
                        } catch (DbException e) {
                            showErrorDialog();
                        }
                    }
                } catch (ControlPaymentException e) {
                    Timber.e(e);
                    showErrorDialog(e.getMessage());
                }
            }

            @Override
            public void onError(String msg) {
                showErrorDialog(msg);
                try {
                    customerCallOrderModel.OrderPaymentTypeUniqueId = saveOrderUtility.getOldPaymentType().UniqueId;
                    new CustomerCallOrderManager(getContext()).update(customerCallOrderModel);
                } catch (Exception e) {
                    showErrorDialog();
                }
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

                    });
                } else {
                    cuteMessageDialog.setPositiveButton(R.string.ok, view -> callBack.onContinue());
                    cuteMessageDialog.setNegativeButton(R.string.cancel, view -> {
                        callBack.cancel();
                    });
                }
                cuteMessageDialog.show();
            }

            @Override
            public void onCancel() {
                try {
                    customerCallOrderModel.OrderPaymentTypeUniqueId = saveOrderUtility.getOldPaymentType().UniqueId;
                    new CustomerCallOrderManager(getContext()).update(customerCallOrderModel);
                } catch (Exception e) {
                    showErrorDialog();
                }
            }
        };
    }

    private void showErrorDialog() {
        showErrorDialog(getString(R.string.error_saving_request));
    }

    private void showErrorDialog(String error) {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setIcon(Icon.Error);
        dialog.setTitle(R.string.error);
        dialog.setMessage(error);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.show();
    }

    private void setupPaymentsList(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.payments_recycler_view);
        adapter = new RecyclerView.Adapter() {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row_layout, parent, false);
                return new PaymentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                PaymentViewHolder paymentViewHolder = (PaymentViewHolder) holder;
                paymentViewHolder.bind(position);
            }

            @Override
            public int getItemCount() {
                return paymentsList.size();
            }
        };

        paymentManager = new PaymentManager(getContext());
        paymentsList = paymentManager.listPayments(customerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light_light, 1));
        recyclerView.setAdapter(adapter);
    }

    private void setupPaymentButtons(View view) {
        cuteToolbar = view.findViewById(R.id.settlement_cute_toolbar);

        CuteButton oldInvoiceBtn = new CuteButton();
        oldInvoiceBtn.setIcon(R.drawable.ic_old_invoice_white_36dp);
        oldInvoiceBtn.setTitle(R.string.open_invoices);
        oldInvoiceBtn.setOnClickListener(() -> {
            OldInvoiceHeaderViewManager oldInvoiceHeaderViewManager = new OldInvoiceHeaderViewManager(getContext());
            List<OldInvoiceHeaderViewModel> headerViewModels = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId);
            if (headerViewModels.size() > 0) {
                InvoiceSelectionDialog invoiceSelectionDialog = new InvoiceSelectionDialog();
                invoiceSelectionDialog.setCustomerId(customerId);
                invoiceSelectionDialog.onInvoiceSelected = this::refreshSettlement;
                invoiceSelectionDialog.show(getChildFragmentManager(), "003d46f5-d86c-4e2a-a651-77c49fabb27c");
            } else {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                cuteMessageDialog.setPositiveButton(R.string.ok, null);
                cuteMessageDialog.setIcon(Icon.Info);
                cuteMessageDialog.setMessage(R.string.there_is_no_not_settled_invoice);
                cuteMessageDialog.show();
            }
        });
        oldInvoiceBtn.setEnabled(() -> !isConfirmed() && sysConfigMap.compare(ConfigKey.AllowSurplusInvoiceSettlement, true));

        CuteButton checkButton = new CuteButton();
        checkButton.setIcon(R.drawable.ic_cheque_white_36dp);
        checkButton.setTitle(R.string.cheque);
        checkButton.setOnClickListener(() -> {
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && sysConfigMap.compare(ConfigKey.DealerAdvanceCreditControl, true) && !sysConfigMap.isNullOrEmpty(ConfigKey.DealerCreditDetails)) {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                cuteMessageDialog.setTitle(R.string.error);
                cuteMessageDialog.setIcon(Icon.Error);
                cuteMessageDialog.setMessage(getString(R.string.just_cash_and_pos) + "\n\r" + sysConfigMap.getStringValue(ConfigKey.DealerCreditDetails, ""));
                cuteMessageDialog.setNegativeButton(R.string.ok, null);
                cuteMessageDialog.show();
            } else if (sysConfigMap.compare(ConfigKey.AdvancedCreditControl, true) && customer.ErrorMessage != null && !customer.ErrorMessage.isEmpty()) {
                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                cuteMessageDialog.setTitle(R.string.error);
                cuteMessageDialog.setIcon(Icon.Error);
                cuteMessageDialog.setMessage(getString(R.string.just_cash_and_pos) + "\n\r" + customer.ErrorMessage);
                cuteMessageDialog.setNegativeButton(R.string.ok, null);
                cuteMessageDialog.show();
            } else {
                CheckDialog checkDialog = new CheckDialog();
                Currency totalPayment = paymentManager.getTotalPaid(customerId);
                checkDialog.setArguments(customerId, customerPayment.getTotalAmount(false),
                        customerPayment.getTotalAmount(false).subtract(totalPayment), null, null);
                checkDialog.setCallBack(() -> refreshSettlement());
                checkDialog.show(getChildFragmentManager(), "CheckDialog");
            }
        });
        checkButton.setEnabled(() -> !isConfirmed() && validPaymentTypes.contains(PaymentType.Check));

        CuteButton poseButton = new CuteButton();
        poseButton.setIcon(R.drawable.ic_debit_card_white_36dp);
        poseButton.setTitle(R.string.card_reader);
        poseButton.setOnClickListener(() -> {
            CardReaderDialog dialog = new CardReaderDialog();

            Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(customerId);
            Currency total=Currency.ZERO;
            if ( customerCallOrderModels.size()>1){

            }else {
                total  = customerCallOrderModels.get(0).TotalAmountNutImmediate.subtract(returnVa);
            }
            Currency totalPayment = paymentManager.getTotalPaid(customerId);
            dialog.setArguments(customerId, customerPayment.getTotalAmount(false),
                    customerPayment.getTotalAmount(false).subtract(totalPayment),
                    null, null);
            dialog.preSetRemainedAmount();
            dialog.show(getChildFragmentManager(), "b20d52e2-5b3b-42d0-b3c7-e9060cf24b74");
            dialog.setCallBack(() -> refreshSettlement());
        });
        poseButton.setEnabled(() -> !isConfirmed() && validPaymentTypes.contains(PaymentType.Card));

        CuteButton cashButton = new CuteButton();
        cashButton.setIcon(R.drawable.ic_cash_white_36dp);
        cashButton.setTitle(R.string.cash);
        cashButton.setOnClickListener(() -> {
            CashPaymentDialog dialog = new CashPaymentDialog();
            PaymentModel paymentModel = paymentManager.getCashPayment(customerId);
            Currency totalPayment = paymentManager.getTotalPaid(customerId);
            if (paymentModel == null)
                dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), null, null);
            else
                dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
            dialog.setCallBack(this::refreshSettlement);
            dialog.preSetRemainedAmount();
            dialog.show(getChildFragmentManager(), "789bec9a-8f17-43af-a7bc-1b3e37caf070");
        });
        cashButton.setEnabled(() -> !isConfirmed() && validPaymentTypes.contains(PaymentType.Cash));

        CuteButton discountBtn = new CuteButton();
        discountBtn.setIcon(R.drawable.ic_settlement_discount_white_36dp);
        discountBtn.setTitle(R.string.settlement_discount);
        discountBtn.setOnClickListener(() -> {
            SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
            if (!SysConfigManager.compare(settlementAllocation, true)) {
                OldSettlementDiscountDialog dialog = new OldSettlementDiscountDialog();
                PaymentModel paymentModel = paymentManager.getDiscountPayment(customerId);
                Currency totalPayment = paymentManager.getTotalPaid(customerId);
                if (paymentModel == null)
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), null, null);
                else
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                dialog.show(getChildFragmentManager(), "f37574cc-8726-41a3-83fd-680969b5b114");
                dialog.setCallBack(this::refreshSettlement);
            } else {
                SettlementDiscountDialog dialog = new SettlementDiscountDialog();
                PaymentModel paymentModel = paymentManager.getDiscountPayment(customerId);
                Currency totalPayment = paymentManager.getTotalPaid(customerId);
                if (paymentModel == null)
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), null, null);
                else
                    dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                dialog.show(getChildFragmentManager(), "f37574cc-8726-41a3-83fd-680969b5b114");
                dialog.setCallBack(this::refreshSettlement);
            }
        });
        discountBtn.setEnabled(() -> {
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                return false;
            return !isConfirmed() && validPaymentTypes.contains(PaymentType.Discount) && sysConfigMap.compare(ConfigKey.AllowDiscountOnSettlement, true);
        });


        CuteButton debtButton = new CuteButton();
        debtButton.setIcon(R.drawable.ic_debt_white_36dp);
        debtButton.setTitle(R.string.pay_from_credit);
        debtButton.setOnClickListener(() -> {
            PayFromCreditDialog dialog = new PayFromCreditDialog();
            PaymentModel paymentModel = paymentManager.getCreditPayment(customerId);
            Currency totalPayment = paymentManager.getTotalPaid(customerId);
            if (paymentModel == null)
                dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), null, null);
            else
                dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
            dialog.setCallBack(SettlementFragment.this::refreshSettlement);
            dialog.show(getChildFragmentManager(), "6120d029-4485-4d89-8226-8832c9e7324c");
        });
        debtButton.setEnabled(() -> !isConfirmed() && sysConfigMap.compare(ConfigKey.AllowSettlementWithCredit, true) && customer.CustomerRemain.compareTo(Currency.ZERO) < 0);

        CuteButton doneButton = new CuteButton();
        doneButton.setIcon(R.drawable.ic_done_white_36dp);
        doneButton.setTitle(R.string.save_payment);
        doneButton.setOnClickListener(this::saveSettlement);
        doneButton.setEnabled(() -> !isConfirmed());

        cuteToolbar.setButtons(oldInvoiceBtn, checkButton, poseButton, cashButton, discountBtn, debtButton, doneButton);
    }

    private void refreshSettlement() {
        updateCustomerPayment();
        ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
        SysConfigModel configModel = new SysConfigManager(getContext()).read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
        validPaymentTypes = validPayTypeManager.getValidPayTypes(customerCallOrderModels, customerPayment.getSelectedOldInvoices(), SysConfigManager.compare(configModel, true));
        paymentsList = paymentManager.listPayments(customerId);
        adapter.notifyDataSetChanged();
        Currency totalPayment = paymentManager.getTotalPaid(customerId);
        PaymentModel discountPaymentModel = paymentManager.getDiscountPayment(customerId);
        Currency discountAmount = discountPaymentModel != null ? discountPaymentModel.Amount : Currency.ZERO;
        totalPaymentPairedItems.setValue(HelperMethods.currencyToString(totalPayment.subtract(discountAmount)));

//        netAmountPairedItems.setValue(HelperMethods.currencyToString(customerPayment.getTotalAmount(false)));

        remainingPairedItems.setValue(HelperMethods.currencyToString(customerPayment.getTotalAmount(false).subtract(totalPayment)));
        invoiceAmountPairedItems.setValue(HelperMethods.currencyToString(paymentManager.getTotalAmountNutCheque(customerId)));
        oldInvoicesAmountPairedItems.setValue(HelperMethods.currencyToString(paymentManager.getTotalAmountNutCash(customerId)));
        returnAmountPairedItems.setValue(HelperMethods.currencyToString(paymentManager.getTotalAmountNutImmediate(customerId)));
    //    returnAmountPairedItems.setValue(HelperMethods.currencyToString(customerPayment.getReturnAmount()));
        discountPairedItems.setValue(HelperMethods.currencyToString(discountAmount));
        Currency customerRemain;
        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());
        CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(customerId);
        if (customerRemainPerLineModel != null)
            customerRemain = customerRemainPerLineModel.CustRemAmount;
        else
            customerRemain = customer.CustomerRemain;
        oldRemainItems.setValue(HelperMethods.currencyToString(customerRemain));
        Currency creditPayment = paymentManager.getCreditPayment(customerId) != null ? paymentManager.getCreditPayment(customerId).Amount : Currency.ZERO;

        /**
         * باقی مانده مبلغ
         */
//        oldRemainAfterPaymentItems.setValue(HelperMethods.currencyToString(customerRemain
//                .add(customerPayment.getTotalAmount(false)
//                        .subtract(customerPayment.getOldInvoicesAmount())
//                        .subtract(totalPayment.subtract(creditPayment)))));


        List<PaymentTypeOrderModel> paymentTypeOrderModels =
                new ValidPayTypeManager(getContext()).getPaymentTypeOrders(customerCallOrderModels, null);


        if (paymentTypeOrderModels!=null&&paymentTypeOrderModels.size()>0) {
            PaymentTypeOrderModel paymentTypeOrderModel = paymentTypeOrderModels.get(0);
            if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString())) {
                Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(customerId);
                Currency total = Currency.ZERO;
                if (customerCallOrderModels.size()>1){
                    for (CustomerCallOrderModel item:
                    customerCallOrderModels) {
                        total = item.TotalAmountNutImmediate.add(total).subtract(returnVa);
                    }
                }else {
                    total = customerCallOrderModels.get(0).TotalAmountNutImmediate.subtract(returnVa);
                }

                oldRemainAfterPaymentItems.setValue(HelperMethods.currencyToString(customerRemain
                        .add(total
                                .subtract(customerPayment.getOldInvoicesAmount())
                                .subtract(totalPayment.subtract(creditPayment)))));

                netAmountPairedItems.setValue(HelperMethods.currencyToString(total));
            }

            if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString())) {
                Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(customerId);

                Currency total = Currency.ZERO;
                if (customerCallOrderModels.size()>1){
                    for (CustomerCallOrderModel item:
                            customerCallOrderModels) {
                        total = item.TotalAmountNutCash.add(total).subtract(returnVa);
                    }
                }else {
                    total = customerCallOrderModels.get(0).TotalAmountNutCash.subtract(returnVa);
                }

               // Currency total = customerCallOrderModels.get(0).TotalAmountNutCash.subtract(returnVa);
                oldRemainAfterPaymentItems.setValue(HelperMethods.currencyToString(customerRemain
                        .add(total
                                .subtract(customerPayment.getOldInvoicesAmount())
                                .subtract(totalPayment.subtract(creditPayment)))));
                netAmountPairedItems.setValue(HelperMethods.currencyToString(total));
            }

            if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCH.toString())) {
                Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(customerId);

                Currency total = Currency.ZERO;
                if (customerCallOrderModels.size()>1){
                    for (CustomerCallOrderModel item:
                            customerCallOrderModels) {
                        total = item.TotalAmountNutCheque.add(total).subtract(returnVa);
                    }
                }else {
                    total = customerCallOrderModels.get(0).TotalAmountNutCheque.subtract(returnVa);
                }
                //Currency total = customerCallOrderModels.get(0).TotalAmountNutCheque.subtract(returnVa);
                oldRemainAfterPaymentItems.setValue(HelperMethods.currencyToString(customerRemain
                        .add(total
                                .subtract(customerPayment.getOldInvoicesAmount())
                                .subtract(totalPayment.subtract(creditPayment)))));
                netAmountPairedItems.setValue(HelperMethods.currencyToString(total));
            }
        }

        usanceDayItems.setValue(String.valueOf(paymentManager.getUsanceDay(customerId)));
        usanceRefItems.setValue(String.valueOf(paymentManager.getUsanceRef(customerId)));
        usancePayItems.setValue(HelperMethods.doubleToString(paymentManager.calculateUsancePay(customerId)));
        cuteToolbar.refresh();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView amountTextView;
        private final ImageView paymentImageView;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            paymentImageView = itemView.findViewById(R.id.payment_image_view);
        }

        String getString(@StringRes int resId) {
            return itemView.getContext().getString(resId);
        }

        public void bind(int position) {
            final PaymentModel paymentModel = paymentsList.get(position);
            if (!isConfirmed()) {
                itemView.setOnLongClickListener(view -> {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setPositiveButton(R.string.yes, view1 -> {
                        try {
                            paymentManager.deletePayment(paymentModel.UniqueId, customerId);
                            refreshSettlement();

                        } catch (Exception ex) {
                            Timber.e(ex);
                            CuteMessageDialog errorDialog = new CuteMessageDialog(getContext());
                            errorDialog.setIcon(Icon.Error);
                            errorDialog.setMessage(R.string.error_deleting_payment);
                            errorDialog.setTitle(R.string.error);
                            errorDialog.setPositiveButton(R.string.ok, null);
                            errorDialog.show();
                        }
                    });
                    dialog.setNegativeButton(R.string.no, null);
                    dialog.setTitle(R.string.delete_payment);
                    dialog.setMessage(R.string.are_you_sure);
                    dialog.setIcon(Icon.Warning);
                    dialog.show();
                    return true;
                });
                itemView.setOnClickListener(view -> {
                    if (paymentModel.PaymentType.equals(PaymentType.Check)) {
                        CheckDialog checkDialog = new CheckDialog();
                        Currency totalPayment = paymentManager.getTotalPaid(customerId);
                        checkDialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                        checkDialog.setCallBack(new PaymentDialogCallBack() {
                            @Override
                            public void saved() {
                                refreshSettlement();
                            }
                        });
                        checkDialog.show(getChildFragmentManager(), "1fea1ed0-9ac3-47da-ac1f-225082ee7083");
                    } else if (paymentModel.PaymentType.equals(PaymentType.Credit)) {
                        PayFromCreditDialog dialog = new PayFromCreditDialog();
                        Currency totalPayment = paymentManager.getTotalPaid(customerId);
                        dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                        dialog.setCallBack(new PaymentDialogCallBack() {
                            @Override
                            public void saved() {
                                refreshSettlement();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "d005c5a8-23ce-4f4b-a847-9b9b387d99cf");
                    } else if (paymentModel.PaymentType.equals(PaymentType.Discount)) {
                        SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
                        if (!SysConfigManager.compare(settlementAllocation, true)) {
                            OldSettlementDiscountDialog dialog = new OldSettlementDiscountDialog();
                            Currency totalPayment = paymentManager.getTotalPaid(customerId);
                            dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                            dialog.setCallBack(new PaymentDialogCallBack() {
                                @Override
                                public void saved() {
                                    refreshSettlement();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "f62588ae-4700-42d1-a569-f56f6995e113");
                        } else {
                            SettlementDiscountDialog dialog = new SettlementDiscountDialog();
                            Currency totalPayment = paymentManager.getTotalPaid(customerId);
                            dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                            dialog.setCallBack(new PaymentDialogCallBack() {
                                @Override
                                public void saved() {
                                    refreshSettlement();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "f62588ae-4700-42d1-a569-f56f6995e113");
                        }
                    } else if (paymentModel.PaymentType.equals(PaymentType.Cash)) {
                        CashPaymentDialog dialog = new CashPaymentDialog();
                        Currency totalPayment = paymentManager.getTotalPaid(customerId);

                        dialog.setArguments(customerId, customerPayment.getTotalAmount(false),
                                customerPayment.getTotalAmount(false).subtract(totalPayment),
                                paymentModel.UniqueId, null);
                        dialog.setCallBack(new PaymentDialogCallBack() {
                            @Override
                            public void saved() {
                                refreshSettlement();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "88605706-8407-4bdf-81d3-d3813a8c33cb");
                    } else if (paymentModel.PaymentType.equals(PaymentType.Card)) {
                        CardReaderDialog dialog = new CardReaderDialog();
                        Currency totalPayment = paymentManager.getTotalPaid(customerId);
                        dialog.setArguments(customerId, customerPayment.getTotalAmount(false), customerPayment.getTotalAmount(false).subtract(totalPayment), paymentModel.UniqueId, null);
                        dialog.setCallBack(new PaymentDialogCallBack() {
                            @Override
                            public void saved() {
                                refreshSettlement();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "6e46af70-ed1e-41d4-8a0b-7be289ac03c4");
                    }
                });
            }
            String chqDateString = DateHelper.toString(paymentModel.ChqDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            if (paymentModel.PaymentType.equals(PaymentType.Check)) {
                titleTextView.setText(
                        getString(R.string.check_account_number) + " : " +
                                paymentModel.CheckAccountNumber + " , " +
                                getString(R.string.check_serial_number) + " : " +
                                paymentModel.CheckNumber + " , " +
                                getString(R.string.check_date) + " : " +
                                chqDateString);
                amountTextView.setText(HelperMethods.currencyToString(paymentModel.Amount));
                paymentImageView.setImageResource(R.drawable.ic_cheque_white_18dp);
            } else if (paymentModel.PaymentType.equals(PaymentType.Card)) {
                titleTextView.setText(
                        getString(R.string.ref_number) + " : " +
                                paymentModel.Ref + " , " +
                                getString(R.string.payment_date) + " : " +
                                chqDateString);
                amountTextView.setText(HelperMethods.currencyToString(paymentModel.Amount));
                paymentImageView.setImageResource(R.drawable.ic_debit_card_white_18dp);
            } else if (paymentModel.PaymentType.equals(PaymentType.Cash)) {
                titleTextView.setText(R.string.cash_payment);
                amountTextView.setText(HelperMethods.currencyToString(paymentModel.Amount));
                paymentImageView.setImageResource(R.drawable.ic_cash_white_18dp);
            } else if (paymentModel.PaymentType.equals(PaymentType.Credit)) {
                titleTextView.setText(R.string.pay_from_credit);
                amountTextView.setText(HelperMethods.currencyToString(paymentModel.Amount));
                paymentImageView.setImageResource(R.drawable.ic_debt_white_18dp);
            } else if (paymentModel.PaymentType.equals(PaymentType.Discount)) {
                double f = paymentModel.Amount.doubleValue() / customerPayment.getTotalAmount(false).doubleValue() * 100;
                titleTextView.setText(new DecimalFormat("##.#").format(f) + " " + getString(R.string.percent) + " " + getString(R.string.settlement_discount));
                amountTextView.setText(HelperMethods.currencyToString(paymentModel.Amount));
                paymentImageView.setImageResource(R.drawable.ic_settlement_discount_white_18dp);
            }
        }
    }

    private void saveSettlementFailed(Context context, String error) {
        try {
            new CustomerCallManager(context).removePaymentCall(customerId);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.payment_control_error);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.exit, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getVaranegarActvity().popFragment();
                }
            });
            dialog.setNegativeButton(R.string.edit_settlement, null);
            dialog.show();
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private void printFailed(Context context, String error) {
        try {
            new CustomerCallManager(context).removePaymentCall(customerId);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.payment_control_error);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e1) {
            Timber.e(e1);
        }
    }

    private void changePaymentType(ThirdPartyPaymentTypes paymentType, boolean print) {
        CustomerCallOrderModel customerCallOrderModel = customerCallOrderModels.get(0);
        PaymentTypeOrderModel newPayType = new PaymentOrderTypeManager(getContext()).get(paymentType.toString());
        SaveOrderUtility saveOrderUtility = new SaveOrderUtility(getContext());
        if (customerCallOrderModels.size()>1){
            for (CustomerCallOrderModel item:
            customerCallOrderModels) {
                saveOrderUtility.setOldPaymentTypeId(item.OrderPaymentTypeUniqueId);
                item.OrderPaymentTypeUniqueId = newPayType.UniqueId;
                try {
                    new CustomerCallOrderManager(getContext()).update(item);
                    saveOrderUtility.setCallback(saveOrderCallBack(print, item, saveOrderUtility));
                    saveOrderUtility.saveOrderWithProgressDialog(item);
                } catch (Exception ex) {
                    Timber.e(ex);
                    if (print)
                        printFailed(getContext(), getString(R.string.error_saving_request));
                    else
                        showErrorDialog();
                }
            }
        }else {

            saveOrderUtility.setOldPaymentTypeId(customerCallOrderModel.OrderPaymentTypeUniqueId);
            customerCallOrderModel.OrderPaymentTypeUniqueId = newPayType.UniqueId;
            try {
                new CustomerCallOrderManager(getContext()).update(customerCallOrderModel);
                saveOrderUtility.setCallback(saveOrderCallBack(print, customerCallOrderModel, saveOrderUtility));
                saveOrderUtility.saveOrderWithProgressDialog(customerCallOrderModel);
            } catch (Exception ex) {
                Timber.e(ex);
                if (print)
                    printFailed(getContext(), getString(R.string.error_saving_request));
                else
                    showErrorDialog();
            }
        }
    }
}
