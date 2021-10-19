package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportView;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportViewModel;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportViewModelRepository;

/**
 * Created by s.foroughi on 18/01/2017.
 */

public class DeliveryReportViewManager extends BaseManager<DeliveryReportViewModel> {
    public DeliveryReportViewManager(Context context) {
        super(context, new DeliveryReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.select(
                Projection.column(DeliveryReportView.CustomerId).as("CustomerId"),
                Projection.column(DeliveryReportView.CustomerCode).as("CustomerCode"),
                Projection.column(DeliveryReportView.CustomerName).as("CustomerName"),
                Projection.constant("").as("OrderUniqueId"),
                Projection.sum(DeliveryReportView.InvoiceNetAmount).as("InvoiceNetAmount"),
                Projection.sum(DeliveryReportView.InvoiceReturnNetAmount).as("InvoiceReturnNetAmount"),
                Projection.column(DeliveryReportView.TotalReturnNetAmount).as("TotalReturnNetAmount"),
                Projection.column(DeliveryReportView.TotalOldInvoiceAmount).as("TotalOldInvoiceAmount"),
                Projection.column(DeliveryReportView.TotalPayAbleAmount).as("TotalPayAbleAmount"),
                Projection.column(DeliveryReportView.ReceiptAmount).as("ReceiptAmount"),
                Projection.sum(DeliveryReportView.CashAmount).as("CashAmount"),
                Projection.sum(DeliveryReportView.ChequeAmount).as("ChequeAmount"),
                Projection.sum(DeliveryReportView.CardAmount).as("CardAmount"),
                Projection.sum(DeliveryReportView.SettlementDiscountAmount).as("SettlementDiscountAmount"),
                Projection.sum(DeliveryReportView.CreditAmount).as("CreditAmount"),
                Projection.constant("").as("DealerName")
                );
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.groupBy(DeliveryReportView.CustomerId);
        return new Query().from(From.subQuery(query).as("DeliveryReportView"));
    }

    public static Query getAll(String dealerName) {
        Query query = new Query();
        query.select(
                Projection.column(DeliveryReportView.CustomerId).as("CustomerId"),
                Projection.column(DeliveryReportView.CustomerCode).as("CustomerCode"),
                Projection.column(DeliveryReportView.CustomerName).as("CustomerName"),
                Projection.constant("").as("OrderUniqueId"),
                Projection.sum(DeliveryReportView.InvoiceNetAmount).as("InvoiceNetAmount"),
                Projection.sum(DeliveryReportView.InvoiceReturnNetAmount).as("InvoiceReturnNetAmount"),
                Projection.column(DeliveryReportView.TotalReturnNetAmount).as("TotalReturnNetAmount"),
                Projection.column(DeliveryReportView.TotalOldInvoiceAmount).as("TotalOldInvoiceAmount"),
                Projection.column(DeliveryReportView.TotalPayAbleAmount).as("TotalPayAbleAmount"),
                Projection.column(DeliveryReportView.ReceiptAmount).as("ReceiptAmount"),
                Projection.sum(DeliveryReportView.CashAmount).as("CashAmount"),
                Projection.sum(DeliveryReportView.ChequeAmount).as("ChequeAmount"),
                Projection.sum(DeliveryReportView.CardAmount).as("CardAmount"),
                Projection.sum(DeliveryReportView.SettlementDiscountAmount).as("SettlementDiscountAmount"),
                Projection.sum(DeliveryReportView.CreditAmount).as("CreditAmount"),
                Projection.column(DeliveryReportView.DealerName).as("DealerName")
        );
        query.from(DeliveryReportView.DeliveryReportViewTbl).whereAnd(Criteria.contains(DeliveryReportView.DealerName, dealerName));
        query.groupBy(Projection.column(DeliveryReportView.CustomerId), Projection.column(DeliveryReportView.DealerName));
        return new Query().from(From.subQuery(query).as("DeliveryReportView"));
    }
    @Nullable
    public Currency getTotalCash() {
        Query query = new Query();
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.select(Projection.sum(DeliveryReportView.CashAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    @Nullable
    public Currency getTotalChq() {
        Query query = new Query();
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.select(Projection.sum(DeliveryReportView.ChequeAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    @Nullable
    public Currency getTotalPos() {
        Query query = new Query();
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.select(Projection.sum(DeliveryReportView.CardAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    @Nullable
    public Currency getTotalReturn() {
        Query query = new Query();
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.select(Projection.sum(DeliveryReportView.TotalReturnNetAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    @Nullable
    public Currency getTotalReceipt() {
        Query query = new Query();
        query.from(DeliveryReportView.DeliveryReportViewTbl);
        query.select(Projection.sum(DeliveryReportView.ReceiptAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }
}




