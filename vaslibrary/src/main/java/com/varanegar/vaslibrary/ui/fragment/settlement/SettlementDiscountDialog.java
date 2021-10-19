package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.validvalue.CompareValueChecker;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.DiscountPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo.InvoicePaymentInfoLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class SettlementDiscountDialog extends PaymentDialog {

    private Currency amount;
    private Currency remained;
    private Context context;
    private PaymentModel existingPaymentModel;
    private InvoicePaymentInfoLayout invoicePaymentInfoLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
        context = getContext();
    }

    @Override
    public void onValidationSucceeded() {
//        if (amount.doubleValue() > getMaxAmount().doubleValue()) {
//            CuteMessageDialog dialog = new CuteMessageDialog(context);
//            dialog.setIcon(Icon.Error);
//            dialog.setTitle(R.string.error);
//            dialog.setMessage(R.string.doscount_value_larger_than_max);
//            dialog.setPositiveButton(R.string.ok, null);
//            dialog.show();
//        } else {
        try {
            savePayment();
        } catch (ParseException e) {
            showErrorMessage(R.string.amount_is_not_valid);
        }
//        }
    }

    private void savePayment() throws ParseException {
        PaymentManager paymentManager = new PaymentManager(context);
        DiscountPayment discountPayment = new DiscountPayment();
        List<InvoicePaymentInfoViewModel> invoicePaymentInfoViewModels = invoicePaymentInfoLayout.getItems();
        Currency totalAmount = Currency.ZERO;
        for (InvoicePaymentInfoViewModel viewModel :
                invoicePaymentInfoViewModels) {
            totalAmount = totalAmount.add(viewModel.PaidAmount);
        }
        discountPayment.Amount = totalAmount;
        discountPayment.Date = new Date();
        if (becomesGreaterThanTotal(discountPayment.Amount))
            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
        else if (discountPayment.Amount.compareTo(Currency.ZERO) <= 0)
            showErrorMessage(R.string.amount_is_not_valid);
        else {
            try {
                paymentManager.saveDiscountPayment(discountPayment, getCustomerId(), invoicePaymentInfoViewModels);
                runCallBack();
                dismiss();
            } catch (Exception ex) {
                showErrorMessage(R.string.error_saving_request);
            }
        }
    }

    private boolean becomesGreaterThanTotal(Currency amount) {
        PaymentManager paymentManager = new PaymentManager(context);
        Currency total = paymentManager.getTotalPaid(getCustomerId());
        PaymentModel paymentModel = paymentManager.getDiscountPayment(getCustomerId());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, false)) {
            if (paymentModel == null) {
                total = total.add(amount);
                return total.compareTo(getTotalAmount()) == 1;
            } else {
                total = total.subtract(paymentModel.Amount).add(amount);
                return total.compareTo(getTotalAmount()) == 1;
            }
        } else return false;
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
                CuteMessageDialog dialog = new CuteMessageDialog(context);
                dialog.setIcon(Icon.Error);
                dialog.setMessage(errorMessage);
                dialog.setTitle(R.string.error_saving_request);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        }
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(getString(R.string.settlement_discount) + " (" + getString(R.string.total_remained) + " : " + VasHelperMethods.currencyToString(getRemainedAmount()) + ")");
        setSizingPolicy(SizingPolicy.WrapContent);
        View view = inflater.inflate(R.layout.settlement_discount_dialog, viewGroup, true);
        invoicePaymentInfoLayout = view.findViewById(R.id.invoices_info_layout);
        SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
        if (!SysConfigManager.compare(settlementAllocation, true))
            invoicePaymentInfoLayout.setVisibility(View.GONE);
        invoicePaymentInfoLayout.setArguments(getVaranegarActvity(), getCustomerId(), PaymentType.Discount, getPaymentId());

        PaymentManager paymentManager = new PaymentManager(context);
        Currency total = paymentManager.getTotalPaid(getCustomerId());
        remained = getTotalAmount().subtract(total);

        if (getPaymentId() != null)
            existingPaymentModel = paymentManager.getItem(getPaymentId());

//        if (getPaymentId() == null) {
//            Currency maxAmount = getMaxAmount();
//            // maxAmount is the biggest possible discount value in accordance with total amount and other factors
//            // But we also need to consider the paid amount to suggest a value that does not exceed total amount
//            // Therefore we calculate maxAmount2, This is a maximum considering other payments that already have been saved.
//            Currency maxAmount2 = remained.compareTo(maxAmount) == -1 ? remained : maxAmount;
//            double roundedTotalAmount = round(remained.doubleValue(), maxAmount2.doubleValue());
//            amount = Currency.valueOf(remained.doubleValue() - roundedTotalAmount);
//            remainingAmountPairedItems.setValue(HelperMethods.currencyToString(Currency.valueOf(roundedTotalAmount)));
//            amountPairedItemsEditable.setValue(HelperMethods.currencyToString(amount));
//            maxDiscountAmountPairedItems.setValue(HelperMethods.currencyToString(maxAmount));
//        } else {
//            Currency maxAmount = getMaxAmount();
//            amount = existingPaymentModel.Amount;
//            double roundedTotalAmount = remained.subtract(amount).doubleValue();
//            remainingAmountPairedItems.setValue(HelperMethods.currencyToString(Currency.valueOf(roundedTotalAmount)));
//            amountPairedItemsEditable.setValue(HelperMethods.currencyToString(amount));
//            maxDiscountAmountPairedItems.setValue(HelperMethods.currencyToString(maxAmount));
//        }
    }

//    @NonNull
//    public Currency getMaxAmount() {
//        // todo: 12/5/2017 calculate real value
//        return new Currency(1350);
//    }

    static double round(double number, double max) {
        int temp = (int) number;
        while (temp > max) {
            int length = String.valueOf(temp).length() - 1;
            int t = (int) Math.pow(10, length);
            temp = temp - t;
        }
        return number - temp;
    }

    static double getRightDigits(double number, int numberOfDigits) {
        double t = Math.pow(10, numberOfDigits);
        double rightDigits = number - Math.floor(number / t) * t;
        return rightDigits;
    }

    @Override
    public void ok() {
        validator.validate(SettlementDiscountDialog.this);
    }

    @Override
    public void cancel() {

    }
}
