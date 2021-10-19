package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.validvalue.Compare;
import com.varanegar.framework.validation.annotations.validvalue.CompareValueChecker;
import com.varanegar.framework.validation.annotations.validvalue.Operator;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CreditPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo.InvoicePaymentInfoLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/12/2017.
 */

public class PayFromCreditDialog extends PaymentDialog {
    @NotEmpty
    @Compare(value = "0", operator = Operator.MoreThan)
    public PairedItemsEditable amountPairedItemsEditable;
    private PairedItems customerRemainingPairedItems;
    private Context context;
    private InvoicePaymentInfoLayout invoicePaymentInfoLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onValidationSucceeded() {
        try {
            savePayment();
        } catch (ParseException e) {
            Timber.e(e);
            showErrorMessage(R.string.amount_is_not_valid);
        }
    }

    @Nullable
    private Currency getCustomerRemaining() {
        CustomerManager customerManager = new CustomerManager(getContext());
        CustomerModel customerModel = customerManager.getItem(getCustomerId());
        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());
        CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(customerModel.UniqueId);
        if (customerRemainPerLineModel != null && customerRemainPerLineModel.CustRemAmount != null)
            return customerRemainPerLineModel.CustRemAmount;
        if (customerModel == null)
            return null;
        return customerModel.CustomerRemain;
    }

    private void savePayment() throws ParseException {
        PaymentManager paymentManager = new PaymentManager(context);
        CreditPayment payment = new CreditPayment();
        payment.Amount = Currency.parse(amountPairedItemsEditable.getValue());
        payment.Date = new Date();
        Currency remaining = getCustomerRemaining().multiply(new Currency(-1));
        if (remaining == null) {
            showErrorMessage(R.string.customer_remaining_amount_is_not_found);
        } else {
            if (payment.Amount.compareTo(remaining) == 1) {
                showErrorMessage(R.string.the_amount_is_larger_than_customer_remaining);
            } else {
                if (becomesGreaterThanTotal(payment.Amount))
                    showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
                else {
                    try {
                        paymentManager.saveCreditPayment(payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                        runCallBack();
                        dismiss();
                    } catch (Exception ex) {
                        Timber.e(ex);
                        showErrorMessage(R.string.error_saving_request);
                    }
                }
            }
        }

    }

    private boolean becomesGreaterThanTotal(Currency amount) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            PaymentManager paymentManager = new PaymentManager(getContext());
            Currency total = paymentManager.getTotalPaid(getCustomerId());
            PaymentModel paymentModel = paymentManager.getCreditPayment(getCustomerId());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, false) && !backOfficeType.equals(BackOfficeType.ThirdParty)) {
                if (paymentModel == null) {
                    total = total.add(amount);
                    return total.compareTo(getTotalAmount()) == 1;
                } else {
                    total = total.subtract(paymentModel.Amount).add(amount);
                    return total.compareTo(getTotalAmount()) == 1;
                }
            } else return false;
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            return false;
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(R.string.error);
            if (error.getViolation().equals(NotEmptyChecker.class))
                errorMessage = getString(R.string.not_empty);
            if (error.getViolation().equals(CompareValueChecker.class))
                errorMessage = getString(R.string.amount_is_not_valid);

            if (error.getField() instanceof PairedItemsEditable) {
                ((PairedItemsEditable) error.getField()).setError(errorMessage);
                ((PairedItemsEditable) error.getField()).requestFocus();
            } else {
                showErrorMessage(errorMessage);
            }
        }
    }

    void refreshInvoicePaymentInfoLayout() {
        String amount = amountPairedItemsEditable.getValue();
        if (amount != null && !amount.isEmpty()) {
            try {
                invoicePaymentInfoLayout.setSettlementAmount(Currency.parse(amount));
            } catch (Exception ex) {
                invoicePaymentInfoLayout.setSettlementAmount(Currency.ZERO);
            }
        }
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(getString(R.string.pay_from_credit) + " (" + getString(R.string.total_remained) + " : " + VasHelperMethods.currencyToString(getRemainedAmount()) + ")");
        View view = inflater.inflate(R.layout.pay_from_credit_dialog, viewGroup, true);
        invoicePaymentInfoLayout = view.findViewById(R.id.invoices_info_layout);
        SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
        int count = invoicePaymentInfoLayout.setArguments(getVaranegarActvity(), getCustomerId(), PaymentType.Credit, getPaymentId());
        if (count <= 1 || !SysConfigManager.compare(settlementAllocation, true))
            invoicePaymentInfoLayout.setVisibility(View.GONE);
        customerRemainingPairedItems = (PairedItems) view.findViewById(R.id.customer_remaining_paired_items);
        amountPairedItemsEditable = (PairedItemsEditable) view.findViewById(R.id.amount_paired_items_editable);
        if (getPaymentId() != null) {
            PaymentManager paymentManager = new PaymentManager(context);
            PaymentModel paymentModel = paymentManager.getPaymentById(getPaymentId(), PaymentType.Credit);
            if (paymentModel != null) {
                amountPairedItemsEditable.setValue(HelperMethods.currencyToString(paymentModel.Amount));
            }
        }
        amountPairedItemsEditable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfoLayout();
            }
        });
        customerRemainingPairedItems.setValue(HelperMethods.currencyToString(getCustomerRemaining()));
    }

    @Override
    public void ok() {
        validator.validate(PayFromCreditDialog.this);
    }

    @Override
    public void cancel() {
        dismiss();
    }
}
