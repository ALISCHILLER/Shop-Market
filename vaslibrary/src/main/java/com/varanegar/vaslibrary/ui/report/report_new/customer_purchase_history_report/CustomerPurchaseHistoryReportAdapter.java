package com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.CustomerPurchaseHistoryView;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;

public class CustomerPurchaseHistoryReportAdapter extends SimpleReportAdapter<PCustomerPurchaseHistoryViewModel> {
    public CustomerPurchaseHistoryReportAdapter(MainVaranegarActivity activity) {
        super(activity, PCustomerPurchaseHistoryViewModel.class);
    }

    public CustomerPurchaseHistoryReportAdapter(VaranegarFragment fragment) {
        super(fragment, PCustomerPurchaseHistoryViewModel.class);
    }


    @Override
    public void bind(ReportColumns columns, PCustomerPurchaseHistoryViewModel entity){
        columns.add(bind(entity, CustomerPurchaseHistoryView.CustomerBackOfficeCode,"کد مشتری").setWeight(2).setFrizzed());
        columns.add(bind(entity, CustomerPurchaseHistoryView.CustomerName,"نام مشتری").setWeight(2).setFrizzed());
        columns.add(bind(entity, CustomerPurchaseHistoryView.InvoiceShamsiDate,"تاریخ").setWeight(1));
        columns.add(bind(entity, CustomerPurchaseHistoryView.InvoiceNumber,"شماره فاکتور").setWeight(1));
        columns.add(bind(entity, CustomerPurchaseHistoryView.ProductBackOfficeCode,"کد کالا").setWeight(1));
        columns.add(bind(entity, CustomerPurchaseHistoryView.ProductName,"نام کالا").setWeight(2.5f));
        columns.add(bind(entity, CustomerPurchaseHistoryView.Count_CA,"تعداد کارتن").setWeight(1));
        columns.add(bind(entity, CustomerPurchaseHistoryView.Count_EA,"تعداد بسته").setWeight(1));
        columns.add(bind(entity, CustomerPurchaseHistoryView.Price,"فی ").sendToDetail().setWeight(1).calcTotal());
        columns.add(bind(entity, CustomerPurchaseHistoryView.Amount,"مبلغ خالص").sendToDetail().setWeight(1.5f).calcTotal());
    }
}
