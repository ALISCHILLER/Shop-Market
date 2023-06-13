package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.varanegar.framework.util.HelperMethods;
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
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CashPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
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

public class CashPaymentDialog extends PaymentDialog {
    @NotEmpty
    @Compare(value = "0", operator = Operator.MoreThan)
    public PairedItemsEditable amountPairedItemsEditable;
    private InvoicePaymentInfoLayout invoicePaymentInfoLayout;
    private boolean preset;
    private List<CustomerCallOrderModel> customerCallOrderModels;
    @Override
    public void onValidationSucceeded() {
        try {
            savePayment();
        } catch (ParseException e) {
            showErrorMessage(R.string.amount_is_not_valid);
        }
    }

    private void savePayment() throws ParseException {
        PaymentManager paymentManager = new PaymentManager(getContext());
        CashPayment payment = new CashPayment();
        payment.Amount = Currency.parse(amountPairedItemsEditable.getValue());
        payment.Date = new Date();
        if (becomesGreaterThanTotal(payment.Amount))
            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
        else {
            try {
                paymentManager.saveCashPayment(payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                runCallBack();
                dismiss();
            } catch (Exception ex) {
                showErrorMessage(R.string.error_saving_request);
            }
        }
    }

    private boolean becomesGreaterThanTotal(Currency amount) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            PaymentManager paymentManager = new PaymentManager(getContext());
            Currency total = paymentManager.getTotalPaid(getCustomerId());
            PaymentModel paymentModel = paymentManager.getCashPayment(getCustomerId());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, false) && !backOfficeType.equals(BackOfficeType.ThirdParty)) {
                if (paymentModel == null) {
                    total = total.add(amount);
                    return total.compareTo(getTotalAmount()) > 0;
                } else {
                    total = total.subtract(paymentModel.Amount).add(amount);
                    return total.compareTo(getTotalAmount()) > 0;
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
        if (getCustomerRemainAmount() != null)
            setTitle(getString(R.string.cash_payment) + " (" + getString(R.string.total_remained) + VasHelperMethods.currencyToString(getRemainedAmount()) + "  -  " + getString(R.string.old_remain_amount) + getCustomerRemainAmount() + ")");
        else
            setTitle(getString(R.string.cash_payment) + " (" + getString(R.string.total_remained) + VasHelperMethods.currencyToString(getRemainedAmount()) + ")");
        View view = inflater.inflate(R.layout.cash_payment_dialog, viewGroup, true);
        invoicePaymentInfoLayout = view.findViewById(R.id.invoices_info_layout);
        SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
        int count = invoicePaymentInfoLayout.setArguments(getVaranegarActvity(), getCustomerId(), PaymentType.Cash, getPaymentId());
        PaymentManager paymentManager = new PaymentManager(getContext());
        if (count <= 1 || !SysConfigManager.compare(settlementAllocation, true))
            invoicePaymentInfoLayout.setVisibility(View.GONE);
        amountPairedItemsEditable = view.findViewById(R.id.amount_paired_items_editable);


        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(getCustomerId());
        Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(getCustomerId());
        Currency total = customerCallOrderModels.get(0).TotalAmountNutImmediate.subtract(returnVa);


        if (getPaymentId() != null) {

            PaymentModel paymentModel = paymentManager.getPaymentById(getPaymentId(), PaymentType.Cash);
            if (paymentModel != null) {
                amountPairedItemsEditable.setValue(HelperMethods.currencyToString(paymentModel.Amount));
            }
        } else {
            if (preset) {
                if (new SysConfigManager(getContext()).readOwnerKeys().DataOwnerKey.equalsIgnoreCase("caf5a390-cbe1-435b-bdde-1f682e004693"))
                    amountPairedItemsEditable.setValue(getCustomerRemainAmount() != null ? VasHelperMethods.currencyToString(getRemainedAmount().add(getCustomerRemainAmount())) : VasHelperMethods.currencyToString(getRemainedAmount()));
                else {
                    if (getRemainedAmount().intValue() < 0)
                        amountPairedItemsEditable.setValue("0");
                    else
                        amountPairedItemsEditable.setValue(VasHelperMethods.currencyToString(total));
                }
                refreshInvoicePaymentInfoLayout();
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
    }

    @Override
    public void ok() {
        validator.validate(CashPaymentDialog.this);
    }

    @Override
    public void cancel() {
        dismiss();
    }

    public void preSetRemainedAmount() {
        this.preset = true;
    }
}
