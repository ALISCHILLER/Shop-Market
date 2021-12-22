package com.varanegar.vaslibrary.ui.report.report_new;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportView;
import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportViewModel;

public class ProductInvoiceReportAdapter extends SimpleReportAdapter<ProductInvoiveBalanceReportViewModel> {

    public ProductInvoiceReportAdapter(MainVaranegarActivity activity) {
        super(activity, ProductInvoiveBalanceReportViewModel.class);
    }

    public ProductInvoiceReportAdapter(VaranegarFragment fragment) {
        super(fragment, ProductInvoiveBalanceReportViewModel.class);
    }
    @Override
    public void bind(ReportColumns columns,ProductInvoiveBalanceReportViewModel entity){
        columns.add(bind(entity, ProductInvoiveBalanceReportView.CustomerBackOfficeCode,"کد مشتری").setWeight(2));
        columns.add(bind(entity, ProductInvoiveBalanceReportView.CustomerName,"نام مشتری").setWeight(1).setFrizzed());
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
