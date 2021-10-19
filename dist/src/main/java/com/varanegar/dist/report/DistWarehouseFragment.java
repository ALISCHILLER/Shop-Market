package com.varanegar.dist.report;

import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.report.WarehouseProductQtyFragment;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class DistWarehouseFragment extends WarehouseProductQtyFragment {

    @Override
    public ReportAdapter createAdapter(SysConfigModel showStockLevel, BackOfficeType backOfficeType) {
        return new DistWarehouseReportAdapter(getVaranegarActvity(), showStockLevel, backOfficeType);
    }
}
