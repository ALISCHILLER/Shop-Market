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
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportView;
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportViewModel;
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportViewModelRepository;

/**
 * Created by s.foroughi on 16/01/2017.
 */

public class PaymentReportViewModelManager extends BaseManager<PaymentReportViewModel> {
    public PaymentReportViewModelManager(Context context) {
        super(context, new PaymentReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.select(
                Projection.column(PaymentReportView.CustomerId).as("CustomerId"),
                Projection.column(PaymentReportView.CustomerCode).as("CustomerCode"),
                Projection.column(PaymentReportView.CustomerName).as("CustomerName"),
                Projection.column(PaymentReportView.PaymentId).as("PaymentId"),
                Projection.column(PaymentReportView.InvoiceId).as("InvoiceId"),
                Projection.sum(PaymentReportView.CashAmount).as("CashAmount"),
                Projection.sum(PaymentReportView.ChequeAmount).as("ChequeAmount"),
                Projection.sum(PaymentReportView.CardAmount).as("CardAmount"),
                Projection.sum(PaymentReportView.SettlementDiscountAmount).as("SettlementDiscountAmount"),
                Projection.sum(PaymentReportView.CreditAmount).as("CreditAmount"),
                Projection.column(PaymentReportView.ChqNo).as("ChqNo"),
                Projection.column(PaymentReportView.ChqDate).as("ChqDate"),
                Projection.column(PaymentReportView.BankName).as("BankName"),
                Projection.column(PaymentReportView.ChqBranchName).as("ChqBranchName"),
                Projection.column(PaymentReportView.ChqBranchCode).as("ChqBranchCode"),
                Projection.column(PaymentReportView.ChqAccountNo).as("ChqAccountNo"),
                Projection.column(PaymentReportView.ChqAccountName).as("ChqAccountName"),
                Projection.column(PaymentReportView.FollowNo).as("FollowNo"),
                Projection.column(PaymentReportView.CityName).as("CityName"),
                Projection.sum(PaymentReportView.PaidAmount).as("PaidAmount"),
                Projection.column(PaymentReportView.DealerName).as("DealerName"),
                Projection.column(PaymentReportView.PaymentType).as("PaymentType")
        );
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.groupBy(PaymentReportView.PaymentId);
        return  new Query().from(From.subQuery(query).as("PaymentReportView"));
    }

    public static Query getAll(String dealerName) {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl).whereAnd(Criteria.contains(PaymentReportView.DealerName, dealerName));
        return query;
    }

    @Nullable
    public Currency getTotalCash() {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.sum(PaymentReportView.CashAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    public Currency getTotalCheque() {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.sum(PaymentReportView.ChequeAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    public Currency getTotalCard () {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.sum(PaymentReportView.CardAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);

    }

    public Currency getTotalPayed() {

        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.sum(PaymentReportView.PaidAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    public Currency getTotalChqCount() {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.count(PaymentReportView.ChequeAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    public Currency getTotalSettlementDiscount() {
        Query query = new Query();
        query.from(PaymentReportView.PaymentReportViewTbl);
        query.select(Projection.sum(PaymentReportView.SettlementDiscountAmount));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }
}
