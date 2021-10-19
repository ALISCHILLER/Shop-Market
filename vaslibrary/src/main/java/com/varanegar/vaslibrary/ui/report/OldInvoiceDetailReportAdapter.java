package com.varanegar.vaslibrary.ui.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountView;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountViewModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailView;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailViewModel;

/**
 * Created by atp on 4/5/2017.
 */

public class OldInvoiceDetailReportAdapter extends SimpleReportAdapter<OldInvoiceDetailViewModel> {
    public OldInvoiceDetailReportAdapter(MainVaranegarActivity activity) {
        super(activity, OldInvoiceDetailViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, OldInvoiceDetailViewModel entity) {
        bindRowNumber(columns);
        columns.add(bind(entity, OldInvoiceDetailView.PrizeType, activity.getString(R.string.prize)).setCustomViewHolder(new CustomViewHolder<OldInvoiceDetailViewModel>() {
            @Override
            public void onBind(View view, OldInvoiceDetailViewModel entity) {
                if (entity.PrizeType)
                    view.findViewById(R.id.is_promo_image_view).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.is_promo_image_view).setVisibility(View.INVISIBLE);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.promotion_row_indicate_layout, parent, false);
            }
        }).setWeight(0.4f));
        columns.add(bind(entity, OldInvoiceDetailView.ProductCode, activity.getString(R.string.product_code)));
        columns.add(bind(entity, OldInvoiceDetailView.ProductName, activity.getString(R.string.product_name)).setSortable().setFilterable().setWeight(2));
        columns.add(bind(entity, OldInvoiceDetailView.SaleNo, activity.getString(R.string.invoice_no)).setSortable().setFilterable());
        columns.add(bind(entity, OldInvoiceDetailView.SalePDate, activity.getString(R.string.sale_date)).setSortable());
        columns.add(bind(entity, OldInvoiceDetailView.TotalQty, activity.getString(R.string.total_sales_qty)));
        columns.add(bind(entity, OldInvoiceDetailView.TotalReturnQty, activity.getString(R.string.return_qty)).calcTotal());
        columns.add(bind(entity, OldInvoiceDetailView.TotalRequestAmount, activity.getString(R.string.return_amount)).calcTotal());
    }
}
