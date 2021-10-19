package com.varanegar.vaslibrary.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRef;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRefModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atp on 4/16/2017.
 */

public class ProductReturnWithoutRefListAdapter extends SimpleReportAdapter<ProductReturnWithoutRefModel> {


    private final boolean sortable;

    public ProductReturnWithoutRefListAdapter(MainVaranegarActivity activity, boolean sortable) {
        super(activity, ProductReturnWithoutRefModel.class);
        this.sortable = sortable;
    }

    @Override
    public void bind(ReportColumns columns, ProductReturnWithoutRefModel entity) {
        bindRowNumber(columns);
        if (sortable)
            columns.add(bind(entity, ProductReturnWithoutRef.ProductCode, activity.getString(R.string.product_code)).setSortable().setFilterable());
        else
            columns.add(bind(entity, ProductReturnWithoutRef.ProductCode, activity.getString(R.string.product_code)));
        if (sortable)
            columns.add(bind(entity, ProductReturnWithoutRef.ProductName, activity.getString(R.string.product_name)).setSortable().setFilterable().setWeight(2));
        else
            columns.add(bind(entity, ProductReturnWithoutRef.ProductName, activity.getString(R.string.product_name)).setWeight(2));
        columns.add(bind(entity, ProductReturnWithoutRef.UnitName, activity.getString(R.string.qty)).setWeight(2).setCustomViewHolder(new CustomViewHolder<ProductReturnWithoutRefModel>() {
            @Override
            public void onBind(View view, ProductReturnWithoutRefModel entity) {
                if (entity.UnitName != null
                        && entity.Qty != null
                        && !entity.UnitName.isEmpty()
                        && !entity.Qty.isEmpty()) {
                    String[] allQtys = entity.Qty.split("\\|");
                    String[] allUnitNames = entity.UnitName.split("\\|");
                    String[] unitNames = allUnitNames[0].split(":");
                    List<BaseUnit> units = new ArrayList<>();
                    for (int i = 0; i < unitNames.length; i++) {
                        BaseUnit unit = new BaseUnit();
                        unit.Name = unitNames[i];
                        for (int j = 0; j < allQtys.length; j++) {
                            String[] reasonQtys = allQtys[j].split(":");
                            unit.value += Double.parseDouble(reasonQtys[i]);
                        }
                        units.add(unit);
                    }
                    new QtyView().build((LinearLayout) view, units);
                } else {
                    ((LinearLayout) view).removeAllViews();
                }
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return new LinearLayout(inflater.getContext());
            }
        }));
        if (sortable)
            columns.add(bind(entity, ProductReturnWithoutRef.TotalQty, activity.getString(R.string.total_return_qty)).setSortable().calcTotal().setWeight(1.5f));
        else
            columns.add(bind(entity, ProductReturnWithoutRef.TotalQty, activity.getString(R.string.total_return_qty)).calcTotal().setWeight(1.5f));
        if (sortable)
            columns.add(bind(entity, ProductReturnWithoutRef.TotalRequestAmount, activity.getString(R.string.return_amount)).setSortable().calcTotal());
        else
            columns.add(bind(entity, ProductReturnWithoutRef.TotalRequestAmount, activity.getString(R.string.return_amount)).calcTotal());
    }
}