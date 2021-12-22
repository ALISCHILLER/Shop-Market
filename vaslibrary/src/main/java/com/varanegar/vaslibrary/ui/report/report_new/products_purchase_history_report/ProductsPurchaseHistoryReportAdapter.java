package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportView;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.ProductsPurchaseHistoryReportView;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportViewModel;

public class ProductsPurchaseHistoryReportAdapter  extends
        SimpleReportAdapter<TProductsPurchaseHistoryReportViewModel> {
    public ProductsPurchaseHistoryReportAdapter(MainVaranegarActivity activity) {
        super(activity, TProductsPurchaseHistoryReportViewModel.class);
    }

    public ProductsPurchaseHistoryReportAdapter(VaranegarFragment fragment) {
        super(fragment, TProductsPurchaseHistoryReportViewModel.class);
    }


    @Override
    public void bind(ReportColumns columns, TProductsPurchaseHistoryReportViewModel entity) {
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductBackOfficeCode, "کد کالا").setWeight(2).setFrizzed());
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductName, "نام کالا").setWeight(2.5f).setFrizzed());
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductCategoryCode, "کد گروه کالا").setWeight(2));
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductCategoryName, "نام گروه کالا").setWeight(2));
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductNetWeight, "وزن خالص").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, ProductsPurchaseHistoryReportView.ProductNetCount_CA, "تعداد کارتن خالص").sendToDetail().setWeight(1.5f).calcTotal());
    }
}
