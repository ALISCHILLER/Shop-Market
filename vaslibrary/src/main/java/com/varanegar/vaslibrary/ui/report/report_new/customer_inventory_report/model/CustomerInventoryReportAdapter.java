package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.CustomerPurchaseHistoryView;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;

public class CustomerInventoryReportAdapter extends SimpleReportAdapter<PCustomerInventoryReportModel> {
    public CustomerInventoryReportAdapter(MainVaranegarActivity activity) {
        super(activity, PCustomerInventoryReportModel.class);
    }

    public CustomerInventoryReportAdapter(VaranegarFragment fragment) {
        super(fragment, PCustomerInventoryReportModel.class);
    }


    @Override
    public void bind(ReportColumns columns, PCustomerInventoryReportModel entity){
        columns.add(bind(entity, CustomerInventoryReportView.customerCode,"کد مشتری").setWeight(0.8f).setFrizzed());
        columns.add(bind(entity, CustomerInventoryReportView.customerName,"نام مشتری").setWeight(1.5f).setFrizzed());
        columns.add(bind(entity, CustomerInventoryReportView.saleDate,"تاریخ فروش").setWeight(0.8f));
        columns.add(bind(entity, CustomerInventoryReportView.productCode,"کد کالا").setWeight(0.7f));
        columns.add(bind(entity, CustomerInventoryReportView.productName,"نام کالا").setWeight(1.5f));
        columns.add(bind(entity, CustomerInventoryReportView.productGroupName,"گروه کالا").setWeight(0.7f));
        columns.add(bind(entity, CustomerInventoryReportView.inventoryCustomer,"موجودی مشتری").setWeight(0.8f));
        columns.add(bind(entity, CustomerInventoryReportView.tedadKochektarinVahed,"تعداد کوچکترین واحد").setWeight(1));
        columns.add(bind(entity, CustomerInventoryReportView.productionDate,"تاریخ تولید").setWeight(0.8f));
        columns.add(bind(entity, CustomerInventoryReportView.personnelName,"نام پرسنل").setWeight(1.5f));
        columns.add(bind(entity, CustomerInventoryReportView.pathTitle,"عنوان مسیر").setWeight(1f));
        columns.add(bind(entity, CustomerInventoryReportView.delear,"نام فروشنده").setWeight(1.5f));

//        columns.add(bind(entity, CustomerInventoryReportView.productGroupNameID,"نام مشتری").setWeight(2));
    }
}
