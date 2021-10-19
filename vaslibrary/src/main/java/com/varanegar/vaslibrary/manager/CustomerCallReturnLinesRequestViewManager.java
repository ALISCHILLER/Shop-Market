package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesRequestView;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesRequestViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesRequestViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/11/2017.
 */

public class CustomerCallReturnLinesRequestViewManager extends BaseManager<CustomerCallReturnLinesRequestViewModel> {
    public CustomerCallReturnLinesRequestViewManager(@NonNull Context context) {
        super(context, new CustomerCallReturnLinesRequestViewModelRepository());
    }

    public List<CustomerCallReturnLinesRequestViewModel> getCustomerLines(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(CustomerCallReturnLinesRequestView.CustomerCallReturnLinesRequestViewTbl)
                .whereAnd(Criteria.equals(CustomerCallReturnLinesRequestView.CustomerUniqueId, customerId.toString()));

        return getItems(query);
    }

    public List<CustomerCallReturnLinesRequestViewModel> getLines(@NonNull UUID customerId, @Nullable UUID backOfficeInvoiceId) {
        Query query = new Query();
        query.from(CustomerCallReturnLinesRequestView.CustomerCallReturnLinesRequestViewTbl)
                .whereAnd(Criteria.equals(CustomerCallReturnLinesRequestView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerCallReturnLinesRequestView.InvoiceId, backOfficeInvoiceId == null ? null : backOfficeInvoiceId.toString())));

        return getItems(query);
    }

    public List<CustomerCallReturnLinesRequestViewModel> getLines(@NonNull UUID customerId, boolean withRef) {
        if (withRef) {
            Query query = new Query();
            query.from(CustomerCallReturnLinesRequestView.CustomerCallReturnLinesRequestViewTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturnLinesRequestView.CustomerUniqueId, customerId.toString())
                            .and(Criteria.notIsNull(CustomerCallReturnLinesRequestView.InvoiceId)));
            return getItems(query);
        } else {
            Query query = new Query();
            query.from(CustomerCallReturnLinesRequestView.CustomerCallReturnLinesRequestViewTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturnLinesRequestView.CustomerUniqueId, customerId.toString())
                            .and(Criteria.isNull(CustomerCallReturnLinesRequestView.InvoiceId)));
            return getItems(query);
        }
    }

    public List<CustomerCallReturnLinesRequestViewModel> getLines(@NonNull UUID productId) {
        Query query = new Query();
        query.from(CustomerCallReturnLinesRequestView.CustomerCallReturnLinesRequestViewTbl)
                .whereAnd(Criteria.equals(CustomerCallReturnLinesRequestView.ProductId, productId.toString()));
        return getItems(query);
    }

    @NonNull
    public OrderAmount calculateTotalAmount(UUID customerId, boolean withRef) {
        List<CustomerCallReturnLinesRequestViewModel> returnLinesViewModels = getLines(customerId, withRef);
        Currency totalNetAmount = Currency.ZERO;
        Currency totalRequestAmount = Currency.ZERO;
        Currency totalDiscountAmount = Currency.ZERO;
        Currency totalAddAmount = Currency.ZERO;
        Currency totalAdd1Amount = Currency.ZERO;
        Currency totalAdd2Amount = Currency.ZERO;
        Currency totalTaxAmount = Currency.ZERO;
        Currency totalChargeAmount = Currency.ZERO;
        Currency totalAddOtherAmount = Currency.ZERO;
        for (CustomerCallReturnLinesRequestViewModel line :
                returnLinesViewModels) {
            if (!line.IsFreeItem) {
                Currency requestAmount;
//                if (line.IsPromoLine)
//                    requestAmount = line.TotalRequestNetAmount == null ? Currency.ZERO : line.TotalRequestNetAmount;
//                else
//                    requestAmount = line.TotalRequestAmount == null ? Currency.ZERO : line.TotalRequestAmount;
//                Currency requestDis1Amount = line.TotalRequestDiscount == null ? Currency.ZERO : line.TotalRequestDiscount;
//                Currency requestAdd1Amount = line.TotalRequestAdd1Amount == null ? Currency.ZERO : line.TotalRequestAdd1Amount;
//                Currency requestAdd2Amount = line.TotalRequestAdd2Amount == null ? Currency.ZERO : line.TotalRequestAdd2Amount;
//                Currency requestChargeAmount = line.TotalRequestCharge == null ? Currency.ZERO : line.TotalRequestCharge;
//                Currency requestTaxAmount = line.TotalRequestTax == null ? Currency.ZERO : line.TotalRequestTax;
//                totalRequestAmount = totalRequestAmount.add(requestAmount);
//                totalDiscountAmount = totalDiscountAmount.add(requestDis1Amount);
//                totalAddAmount = totalAddAmount.add(requestAdd1Amount).add(requestAdd2Amount).add(requestChargeAmount).add(requestTaxAmount);
//                totalAdd1Amount = totalAdd1Amount.add(requestAdd1Amount);
//                totalAdd2Amount = totalAdd2Amount.add(requestAdd2Amount);
//                totalTaxAmount = totalTaxAmount.add(requestTaxAmount);
//                totalChargeAmount = totalChargeAmount.add(requestChargeAmount);
//                totalNetAmount = totalNetAmount.add(requestAmount)
//                        .subtract(requestDis1Amount)
//                        .add(requestAdd1Amount)
//                        .add(requestAdd2Amount)
//                        .add(requestChargeAmount)
//                        .add(requestTaxAmount);

                if (!line.IsPromoLine) {
                    requestAmount = line.TotalRequestAmount == null ? Currency.ZERO : line.TotalRequestAmount;
                    Currency requestDis1Amount = line.TotalRequestDis1Amount == null ? Currency.ZERO : line.TotalRequestDis1Amount;
                    Currency requestDis2Amount = line.TotalRequestDis2Amount == null ? Currency.ZERO : line.TotalRequestDis2Amount;
                    Currency requestDis3Amount = line.TotalRequestDis3Amount == null ? Currency.ZERO : line.TotalRequestDis3Amount;
                    Currency requestDisOtherAmount = line.TotalRequestDisOtherAmount == null ? Currency.ZERO : line.TotalRequestDisOtherAmount;
                    Currency requestAdd1Amount = line.TotalRequestAdd1Amount == null ? Currency.ZERO : line.TotalRequestAdd1Amount;
                    Currency requestAdd2Amount = line.TotalRequestAdd2Amount == null ? Currency.ZERO : line.TotalRequestAdd2Amount;
                    Currency requestChargeAmount = line.TotalRequestCharge == null ? Currency.ZERO : line.TotalRequestCharge;
                    Currency requestTaxAmount = line.TotalRequestTax == null ? Currency.ZERO : line.TotalRequestTax;
                    Currency requestAddOtherAmount = line.TotalRequestAddOtherAmount == null ? Currency.ZERO : line.TotalRequestAddOtherAmount;
                    totalRequestAmount = totalRequestAmount.add(requestAmount);
                    totalDiscountAmount = totalDiscountAmount.add(requestDis1Amount).add(requestDis2Amount).add(requestDis3Amount).add(requestDisOtherAmount);
                    totalAddAmount = totalAddAmount.add(requestAdd1Amount).add(requestAdd2Amount).add(requestAddOtherAmount).add(requestChargeAmount).add(requestTaxAmount);
                    totalAdd1Amount = totalAdd1Amount.add(requestAdd1Amount);
                    totalAdd2Amount = totalAdd2Amount.add(requestAdd2Amount);
                    totalAddOtherAmount = totalAddOtherAmount.add(requestAddOtherAmount);
                    totalTaxAmount = totalTaxAmount.add(requestTaxAmount);
                    totalChargeAmount = totalChargeAmount.add(requestChargeAmount);
                    totalNetAmount = totalNetAmount.add(requestAmount)
                            .subtract(requestDis1Amount)
                            .subtract(requestDis2Amount)
                            .subtract(requestDis3Amount)
                            .subtract(requestDisOtherAmount)
                            .add(requestAdd1Amount)
                            .add(requestAdd2Amount)
                            .add(requestAddOtherAmount)
                            .add(requestChargeAmount)
                            .add(requestTaxAmount);
                }
            }
        }
        OrderAmount orderAmount = new OrderAmount();
        orderAmount.NetAmount = totalNetAmount;
        orderAmount.AddAmount = totalAddAmount;
        orderAmount.DiscountAmount = totalDiscountAmount;
        orderAmount.TotalAmount = totalRequestAmount;
        orderAmount.Add1Amount = totalAdd1Amount;
        orderAmount.Add2Amount = totalAdd2Amount;
        orderAmount.AddOtherAmount = totalAddOtherAmount;
        orderAmount.TaxAmount = totalTaxAmount;
        orderAmount.ChargeAmount = totalChargeAmount;
        return orderAmount;
    }

    @NonNull
    public OrderAmount calculateTotalAmount(UUID customerId, @Nullable UUID backOfficeInvoiceId) {
        List<CustomerCallReturnLinesRequestViewModel> returnLinesViewModels;
        if (backOfficeInvoiceId != null)
            returnLinesViewModels = getLines(customerId, backOfficeInvoiceId);
        else
            returnLinesViewModels = getLines(customerId, false);

        Currency totalNetAmount = Currency.ZERO;
        Currency totalRequestAmount = Currency.ZERO;
        Currency totalDiscountAmount = Currency.ZERO;
        Currency totalAddAmount = Currency.ZERO;
        Currency totalAdd1Amount = Currency.ZERO;
        Currency totalAdd2Amount = Currency.ZERO;
        Currency totalAddOtherAmount = Currency.ZERO;
        Currency totalTaxAmount = Currency.ZERO;
        Currency totalChargeAmount = Currency.ZERO;
        for (CustomerCallReturnLinesRequestViewModel line :
                returnLinesViewModels) {
            if (!line.IsFreeItem) {
                Currency requestAmount;
                if (line.IsPromoLine)
                    requestAmount = line.TotalRequestNetAmount == null ? Currency.ZERO : line.TotalRequestNetAmount;
                else
                    requestAmount = line.TotalRequestAmount == null ? Currency.ZERO : line.TotalRequestAmount;
                Currency requestDis1Amount = line.TotalRequestDis1Amount == null ? Currency.ZERO : line.TotalRequestDis1Amount;
                Currency requestDis2Amount = line.TotalRequestDis2Amount == null ? Currency.ZERO : line.TotalRequestDis2Amount;
                Currency requestDis3Amount = line.TotalRequestDis3Amount == null ? Currency.ZERO : line.TotalRequestDis3Amount;
                Currency requestDisOtherAmount = line.TotalRequestDisOtherAmount == null ? Currency.ZERO : line.TotalRequestDisOtherAmount;
                Currency requestAdd1Amount = line.TotalRequestAdd1Amount == null ? Currency.ZERO : line.TotalRequestAdd1Amount;
                Currency requestAdd2Amount = line.TotalRequestAdd2Amount == null ? Currency.ZERO : line.TotalRequestAdd2Amount;
                Currency requestAddOtherAmount = line.TotalRequestAddOtherAmount == null ? Currency.ZERO : line.TotalRequestAddOtherAmount;
                Currency requestChargeAmount = line.TotalRequestCharge == null ? Currency.ZERO : line.TotalRequestCharge;
                Currency requestTaxAmount = line.TotalRequestTax == null ? Currency.ZERO : line.TotalRequestTax;
                totalRequestAmount = totalRequestAmount.add(requestAmount);
                totalDiscountAmount = totalDiscountAmount.add(requestDis1Amount).add(requestDis2Amount).add(requestDis3Amount).add(requestDisOtherAmount);
                totalAddAmount = totalAddAmount.add(requestAdd1Amount).add(requestAdd2Amount).add(requestChargeAmount).add(requestTaxAmount).add(requestAddOtherAmount);
                totalAdd1Amount = totalAdd1Amount.add(requestAdd1Amount);
                totalAdd2Amount = totalAdd2Amount.add(requestAdd2Amount);
                totalAddOtherAmount = totalAddOtherAmount.add(requestAddOtherAmount);
                totalTaxAmount = totalTaxAmount.add(requestTaxAmount);
                totalChargeAmount = totalChargeAmount.add(requestChargeAmount);
                totalNetAmount = totalNetAmount.add(requestAmount)
                        .subtract(requestDis1Amount).subtract(requestDis2Amount).subtract(requestDis3Amount).subtract(requestDisOtherAmount)
                        .add(requestAdd1Amount)
                        .add(requestAdd2Amount)
                        .add(requestAddOtherAmount)
                        .add(requestChargeAmount)
                        .add(requestTaxAmount);

            }
        }
        OrderAmount orderAmount = new OrderAmount();
        orderAmount.NetAmount = totalNetAmount;
        orderAmount.AddAmount = totalAddAmount;
        orderAmount.DiscountAmount = totalDiscountAmount;
        orderAmount.TotalAmount = totalRequestAmount;
        orderAmount.Add1Amount = totalAdd1Amount;
        orderAmount.Add2Amount = totalAdd2Amount;
        orderAmount.AddOtherAmount = totalAddOtherAmount;
        orderAmount.TaxAmount = totalTaxAmount;
        orderAmount.ChargeAmount = totalChargeAmount;
        return orderAmount;
    }
}
