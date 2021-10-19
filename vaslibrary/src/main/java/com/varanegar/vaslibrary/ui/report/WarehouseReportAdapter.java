package com.varanegar.vaslibrary.ui.report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyView;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public abstract class WarehouseReportAdapter extends SimpleReportAdapter<WarehouseProductQtyViewModel> {
    public WarehouseReportAdapter(MainVaranegarActivity activity) {
        super(activity, WarehouseProductQtyViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, WarehouseProductQtyViewModel entity) {
        bindRowNumber(columns);
        columns.add(bind(entity, WarehouseProductQtyView.ProductCode, activity.getString(R.string.product_code)).setSortable().setFilterable().setWeight(1.5f).setFrizzed());
        columns.add(bind(entity, WarehouseProductQtyView.ProductName, activity.getString(R.string.product_name)).setSortable().setFilterable().setWeight(2f));
    }
}
