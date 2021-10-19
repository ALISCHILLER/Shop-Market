package com.varanegar.vaslibrary.ui.report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreView;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreViewModel;

/**
 * Created by A.Jafarzadeh on 7/16/2017.
 */

public class PreviewReport extends SimpleReportAdapter<TempReportPreViewModel> {
    public PreviewReport(MainVaranegarActivity activity) {
        super(activity,TempReportPreViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, TempReportPreViewModel entity) {
        columns.add(bind(entity, TempReportPreView.ProductCode, activity.getString(com.varanegar.vaslibrary.R.string.product_code)).setSortable().setFilterable());
        columns.add(bind(entity, TempReportPreView.ProductName, activity.getString(com.varanegar.vaslibrary.R.string.product_name)).setSortable().setFilterable());
        columns.add(bind(entity, TempReportPreView.UnitPrice, activity.getString(com.varanegar.vaslibrary.R.string.unit_price)));
        columns.add(bind(entity, TempReportPreView.Qty, activity.getString(com.varanegar.vaslibrary.R.string.qty)));
        columns.add(bind(entity, TempReportPreView.Discont, activity.getString(R.string.discount_amount)));
        columns.add(bind(entity, TempReportPreView.Tax, activity.getString(R.string.adds)));
//        columnTemplates.add(bind(entity, CustomerCallOrderLinesOrderView.InvoiceQty, activity.getString(com.varanegar.vaslibrary.R.string.qty)));
//        columnTemplates.add(bind(entity, , "ناخالص"));
//        columnTemplates.add(bind(entity, null, "تخفیف"));
//        columnTemplates.add(bind(entity, null, "اضافات"));
//        columnTemplates.add(bind(entity, null, "خالص"));
    }
}
