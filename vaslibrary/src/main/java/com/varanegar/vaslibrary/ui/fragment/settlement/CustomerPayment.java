package com.varanegar.vaslibrary.ui.fragment.settlement;

import androidx.annotation.NonNull;

import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 12/31/2017.
 */

public class CustomerPayment {
    @NonNull
    public Currency getInvoiceAmount() {
        return InvoiceAmount == null ? Currency.ZERO : InvoiceAmount;
    }

    @NonNull
    public Currency getOldInvoicesAmount() {
        return OldInvoicesAmount == null ? Currency.ZERO : OldInvoicesAmount;
    }

    @NonNull
    public Currency getReturnAmount() {
        return ReturnAmount == null ? Currency.ZERO : ReturnAmount;
    }

    @NonNull
    public Currency getTotalAmount(boolean fastPayment) {
        if (fastPayment)
            return getInvoiceAmount().subtract(getReturnAmount());
        else
            return getInvoiceAmount().add(getOldInvoicesAmount()).subtract(getReturnAmount());
    }

    private Currency InvoiceAmount = Currency.ZERO;
    private Currency OldInvoicesAmount = Currency.ZERO;
    private Currency ReturnAmount = Currency.ZERO;
    private List<OldInvoiceHeaderViewModel> selectedOldInvoices = new ArrayList<>();

    @NonNull
    public List<OldInvoiceHeaderViewModel> getSelectedOldInvoices() {
        return selectedOldInvoices == null ? new ArrayList<OldInvoiceHeaderViewModel>() : selectedOldInvoices;
    }


    public CustomerPayment(Currency invoiceAmount, Currency returnAmount, Currency oldInvoiceAmount, List<OldInvoiceHeaderViewModel> selectedOldInvoices) {
        this.InvoiceAmount = invoiceAmount;
        this.ReturnAmount = returnAmount;
        this.OldInvoicesAmount = oldInvoiceAmount;
        this.selectedOldInvoices = selectedOldInvoices;
    }


}
