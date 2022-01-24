package com.varanegar.vaslibrary.ui.report.report_new.invoice_balance;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportView;

public class ProductInvoiceReportAdapter extends SimpleReportAdapter<ProductInvoiveBalanceReportModel> {

    public ProductInvoiceReportAdapter(MainVaranegarActivity activity) {
        super(activity, ProductInvoiveBalanceReportModel.class);
    }

    public ProductInvoiceReportAdapter(VaranegarFragment fragment) {
        super(fragment, ProductInvoiveBalanceReportModel.class);
    }
    @Override
    public void bind(ReportColumns columns, ProductInvoiveBalanceReportModel entity){
        columns.add(bind(entity, ProductInvoiveBalanceReportView.CustomerBackOfficeCode,"کد مشتری").setWeight(1.5f));
        columns.add(bind(entity, ProductInvoiveBalanceReportView.CustomerName,"نام مشتری").setWeight(2).setFrizzed());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.InvoiceNumber,"شماره فاکتور").setFrizzed());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.InvoiceShmsiDate,"تاریخ فاکتور").setFrizzed());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.InvoiceOverDue,"مدت"));
        columns.add(bind(entity,ProductInvoiveBalanceReportView.InvoiceFinalPrice,"مبلغ نهایی فاکتور").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.PaidPose,"مبلغ حواله").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.PaidCash,"مبلغ نقد").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.PaidCheck,"مبلغ چک").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.IvoiceRemain,"مبلغ مانده فاکتور").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductInvoiveBalanceReportView.UsancePaid,"راس تسویه").sendToDetail().setWeight(1.5f).calcTotal());
    }
}
