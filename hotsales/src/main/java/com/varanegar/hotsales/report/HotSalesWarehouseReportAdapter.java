package com.varanegar.hotsales.report;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyView;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.report.WarehouseReportAdapter;

/**
 * Created by A.Jafarzadeh on 3/11/2018.
 */

public class HotSalesWarehouseReportAdapter extends WarehouseReportAdapter {
    SysConfigModel showStockLevel;
    public HotSalesWarehouseReportAdapter(MainVaranegarActivity activity, SysConfigModel showStockLevel, BackOfficeType backOfficeType) {
        super(activity);
        this.showStockLevel = showStockLevel;
    }

    @Override
    public void bind(ReportColumns columns, WarehouseProductQtyViewModel entity) {
        super.bind(columns, entity);

        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowShowPriceStockReport, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true)) {
            columns.add(bind(entity, WarehouseProductQtyView.SalePrice, activity.getString(com.varanegar.vaslibrary.R.string.product_price)).calcTotal().setWeight(1));
            columns.add(bind(entity, WarehouseProductQtyView.RemainedPriceQty, activity.getString(R.string.remained_price_qty)).calcTotal().setWeight(1.5f));
        }
        columns.add(bind(entity, WarehouseProductQtyView.OnHandQtyView, activity.getString(R.string.onhand_qty_init)).setWeight(1.5f));
        columns.add(bind(entity, WarehouseProductQtyView.RenewQtyView, activity.getString(R.string.renew_qty)).setWeight(1.5f));
        columns.add(bind(entity, WarehouseProductQtyView.TotalQtyView, activity.getString(R.string.sold_qty)).setWeight(1.5f));
        columns.add(bind(entity, WarehouseProductQtyView.RemainedQtyView, activity.getString(R.string.remain_amount)).setWeight(1.5f));
        columns.add(bind(entity, WarehouseProductQtyView.TotalReturnQty, activity.getString(R.string.return_qty)).sendToDetail());
    }
}
