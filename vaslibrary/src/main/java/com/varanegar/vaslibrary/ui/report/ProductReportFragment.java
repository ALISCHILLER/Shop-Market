package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.CustomTotalView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductReportViewManager;
import com.varanegar.vaslibrary.manager.UnitManager;
import com.varanegar.vaslibrary.model.productreportview.ProductReportView;
import com.varanegar.vaslibrary.model.productreportview.ProductReportViewModel;
import com.varanegar.vaslibrary.model.productreportview.ProductReportViewModelRepository;
import com.varanegar.vaslibrary.model.unit.UnitModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by s.foroughi on 17/01/2017.
 */

public class ProductReportFragment extends VaranegarFragment {
    ReportAdapter<ProductReportViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_sales_report, container, false);
        ReportView salesReport = (ReportView) view.findViewById(R.id.product_sales_report);

        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new SimpleReportAdapter<ProductReportViewModel>((MainVaranegarActivity) getActivity(), ProductReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final ProductReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, ProductReportView.ProductCode, getString(R.string.product_code)).setSortable().setFilterable().setFrizzed());
                columns.add(bind(entity, ProductReportView.ProductName, getString(R.string.product_name)).setSortable().setFilterable().setWeight(3));
                columns.add(bind(entity, ProductReportView.TotalQty, getString(R.string.total_qty)).calcTotal());
                columns.add(bind(entity, ProductReportView.ConvertFactor, getString(R.string.sale_qty)).setWeight(3).calcTotal().setCustomViewHolder(new CustomViewHolder<ProductReportViewModel>() {
                    @Override
                    public void onBind(View view, final ProductReportViewModel entity) {
                        List<BaseUnit> units = VasHelperMethods.chopTotalQty(entity.TotalQty, entity.UnitName, entity.ConvertFactor, null, null);
                        if (units.size() > 0) {
                            new QtyView().build((LinearLayout) view, units);
                        } else {
                            ((LinearLayout) view).removeAllViews();
                        }
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                        return new LinearLayout(inflater.getContext());
                    }
                }).setCustomTotalView(new CustomTotalView() {
                    @Override
                    public void onBind(View view, List entities) {
                        HashMap<UUID, BigDecimal> units = new HashMap<>();
                        for (int i = 0; i < entities.size(); i++) {
                            ProductReportViewModel productReportViewModel = (ProductReportViewModel) entities.get(i);
                            List<BaseUnit> unitList = VasHelperMethods.totalQtyUnits(productReportViewModel.TotalQty, productReportViewModel.UnitId, productReportViewModel.ConvertFactor, null, null);
                            for (BaseUnit unit :
                                    unitList) {
                                if (units.containsKey(unit.ProductUnitId)) {
                                    BigDecimal value = units.get(unit.ProductUnitId);
                                    units.put(unit.ProductUnitId, new BigDecimal(unit.value).add(value));
                                } else {
                                    units.put(unit.ProductUnitId, new BigDecimal(unit.value));
                                }
                            }
                        }
                        List<BaseUnit> finalUnits = new ArrayList<>();
                        Iterator<Map.Entry<UUID, BigDecimal>> it = units.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<UUID, BigDecimal> pair = it.next();
                            BaseUnit unit = new BaseUnit();
                            unit.value = HelperMethods.bigDecimalToDouble(pair.getValue());
                            UnitManager unitManager = new UnitManager(getContext());
                            UnitModel u = unitManager.getItem(pair.getKey());
                            if (u != null) {
                                unit.Name = u.UnitName;
                                finalUnits.add(unit);
                            }
                            it.remove();
                        }
                        if (finalUnits.size() > 0) {
                            new QtyView().build((LinearLayout) view, finalUnits);
                        } else {
                            ((LinearLayout) view).removeAllViews();
                        }
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                        return new LinearLayout(inflater.getContext());
                    }
                }));
                columns.add(bind(entity, ProductReportView.TotalWeight, getString(R.string.totalsale_controling_weight_kg)).setSortable().calcTotal().setWeight(2));
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.setPagingSize(500);
        adapter.create(new ProductReportViewModelRepository(), ProductReportViewManager.getAll(), savedInstanceState);
        salesReport.setAdapter(adapter);
        return view;

    }


}

