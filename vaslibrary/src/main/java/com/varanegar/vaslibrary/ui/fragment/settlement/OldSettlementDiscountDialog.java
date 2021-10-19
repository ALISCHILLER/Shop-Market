package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.validvalue.Compare;
import com.varanegar.framework.validation.annotations.validvalue.CompareValueChecker;
import com.varanegar.framework.validation.annotations.validvalue.Operator;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.DiscountPayment;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.AllowableRoundSettlementDigit;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.AllowableRoundSettlementDigitDist;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.SettlementDiscountPercent;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class OldSettlementDiscountDialog extends PaymentDialog {
    @NotEmpty
    @Compare(value = "0", operator = Operator.MoreThan)
    public PairedItemsEditable amountPairedItemsEditable;
    public PairedItems maxDiscountAmountPairedItems;
    private PairedItems remainingAmountPairedItems;
    private Currency amount;
    private Currency remained;
    private Context context;
    private PaymentModel existingPaymentModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
        context = getContext();
    }

    @Override
    public void onValidationSucceeded() {
        if (amount.doubleValue() > getMaxAmount().doubleValue()) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.doscount_value_larger_than_max);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } else {
            try {
                savePayment();
            } catch (ParseException e) {
                showErrorMessage(R.string.amount_is_not_valid);
            }
        }
    }

    private void savePayment() throws ParseException {
        PaymentManager paymentManager = new PaymentManager(context);
        DiscountPayment discountPayment = new DiscountPayment();
        discountPayment.Amount = Currency.parse(amountPairedItemsEditable.getValue());
        discountPayment.Date = new Date();
        if (becomesGreaterThanTotal(discountPayment.Amount))
            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
        else {
            try {
                paymentManager.saveDiscountPayment(discountPayment, getCustomerId(), null);
                runCallBack();
                dismiss();
            } catch (Exception ex) {
                showErrorMessage(R.string.error_saving_request);
            }
        }
    }

    private boolean becomesGreaterThanTotal(Currency amount) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            PaymentManager paymentManager = new PaymentManager(context);
            Currency total = paymentManager.getTotalPaid(getCustomerId());
            PaymentModel paymentModel = paymentManager.getDiscountPayment(getCustomerId());
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
        View view = inflater.inflate(R.layout.old_settlement_discount_dialog, viewGroup, true);
        amountPairedItemsEditable = (PairedItemsEditable) view.findViewById(R.id.amount_paired_items_editable);
        maxDiscountAmountPairedItems = (PairedItems) view.findViewById(R.id.max_discount_amount_paired_items);
        remainingAmountPairedItems = (PairedItems) view.findViewById(R.id.remaining_amount_paired_items);

        PaymentManager paymentManager = new PaymentManager(context);
        Currency total = paymentManager.getTotalPaid(getCustomerId());
        remained = getTotalAmount().subtract(total);

        if (getPaymentId() != null)
            existingPaymentModel = paymentManager.getItem(getPaymentId());
        amountPairedItemsEditable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                try {
                    if (text.isEmpty())
                        amount = Currency.ZERO;
                    else
                        amount = Currency.parse(text);
                    if (getPaymentId() == null)
                        remainingAmountPairedItems.setValue(HelperMethods.currencyToString(remained.subtract(amount)));
                    else
                        remainingAmountPairedItems.setValue(HelperMethods.currencyToString(remained.add(existingPaymentModel.Amount).subtract(amount)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        if (getPaymentId() == null) {
            Currency maxAmount = getMaxAmount();
            // maxAmount is the biggest possible discount value in accordance with total amount and other factors
            // But we also need to consider the paid amount to suggest a value that does not exceed total amount
            // Therefore we calculate maxAmount2, This is a maximum considering other payments that already have been saved.
//            Currency maxAmount2 = remained.compareTo(maxAmount) == -1 ? remained : maxAmount;
//            double roundedTotalAmount = round(remained.doubleValue(), maxAmount2.doubleValue());
//            amount = Currency.valueOf(remained.doubleValue() - roundedTotalAmount);
            remainingAmountPairedItems.setValue(HelperMethods.currencyToString(remained));
//            amountPairedItemsEditable.setValue(HelperMethods.currencyToString(Currency.ZERO));
            maxDiscountAmountPairedItems.setValue(HelperMethods.currencyToString(getMaxAmount()));
        } else {
            Currency maxAmount = getMaxAmount();
            amount = existingPaymentModel.Amount;
//            double roundedTotalAmount = remained.subtract(amount).doubleValue();
            remainingAmountPairedItems.setValue(HelperMethods.currencyToString(remained.subtract(amount)));
            amountPairedItemsEditable.setValue(HelperMethods.currencyToString(amount));
            maxDiscountAmountPairedItems.setValue(HelperMethods.currencyToString(getMaxAmount()));
        }
    }

    @NonNull
    private Currency getMaxAmount() {
        SysConfigModel percent, round;
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        percent = sysConfigManager.read(SettlementDiscountPercent, SysConfigManager.cloud);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            round = sysConfigManager.read(AllowableRoundSettlementDigitDist, SysConfigManager.cloud);
        else
            round = sysConfigManager.read(AllowableRoundSettlementDigit, SysConfigManager.cloud);

        Currency totalAmount = getTotalAmount();
        Currency currency = totalAmount.multiply(SysConfigManager.getCurrencyValue(percent, Currency.ZERO));
        Currency finalCurrency;
        try {
            if (percent.Value.equals("-1"))
                finalCurrency = totalAmount;
            else if (currency.compareTo(Currency.ZERO) == 0)
                finalCurrency = SysConfigManager.getCurrencyValue(round, Currency.ZERO);
            else
                finalCurrency = currency.divide(new Currency(100)).add(SysConfigManager.getCurrencyValue(round, Currency.ZERO));
        } catch (Exception e) {
            Timber.d("No back office");
            return Currency.ZERO;
        }
        return finalCurrency;
    }

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
        validator.validate(OldSettlementDiscountDialog.this);
    }

    @Override
    public void cancel() {

    }
}
