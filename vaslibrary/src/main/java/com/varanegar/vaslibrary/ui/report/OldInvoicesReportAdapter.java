package com.varanegar.vaslibrary.ui.report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;

/**
 * Created by atp on 4/4/2017.
 */

public class OldInvoicesReportAdapter extends SimpleReportAdapter<OldInvoiceHeaderViewModel> {
    public OldInvoicesReportAdapter(MainVaranegarActivity activity) {
        super(activity,OldInvoiceHeaderViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, OldInvoiceHeaderViewModel entity) {
        columns.add(bind(entity, OldInvoiceHeaderView.InvoiceNo, activity.getString(R.string.invoice_no)).setSortable().setFilterable());
        columns.add(bind(entity, OldInvoiceHeaderView.InvoiceDate, activity.getString(R.string.invoice_date)).setSortable().setFilterable());
        columns.add(bind(entity, OldInvoiceHeaderView.Amount, activity.getString(R.string.invoice_amount)).setSortable().calcTotal().setFilterable().sendToDetail());
    }
}
