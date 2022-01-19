package com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model.CustomerModelView;
import com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model.CustomerNoSaleModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportView;


public class CustomerNoSaleReportAdapter extends SimpleReportAdapter<CustomerNoSaleModel> {
    public CustomerNoSaleReportAdapter(MainVaranegarActivity activity) {
        super(activity, CustomerNoSaleModel.class);
    }

    public CustomerNoSaleReportAdapter(VaranegarFragment fragment) {
        super(fragment, CustomerNoSaleModel.class);
    }

    @Override
    public void bind(ReportColumns columns, CustomerNoSaleModel entity){
        columns.add(bind(entity, CustomerModelView.CustomerCode,"کد مشتری").setWeight(1.5f).setFrizzed());
        columns.add(bind(entity, CustomerModelView.CustomerName,"نام مشتری").setWeight(2.5f).setFrizzed());
        columns.add(bind(entity, CustomerModelView.StoreName,"نام فروشگاه").setWeight(1.5f));

        columns.add(bind(entity, CustomerModelView.CustomerActivity,"فعالیت مشتری").setWeight(1.5f));
        columns.add(bind(entity, CustomerModelView.CustomerCategory,"گروه مشتری").setWeight(1.5f));
        columns.add(bind(entity, CustomerModelView.Phone,"شماره تماس").setWeight(1.5f));
        columns.add(bind(entity, CustomerModelView.Mobile,"شماره موبایل").setWeight(1.5f));
        columns.add(bind(entity, CustomerModelView.Address,"آدرس فروشگاه").setWeight(3.5f));
    }
}
