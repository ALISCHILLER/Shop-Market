package com.varanegar.dist.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.report.CustomTotalView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UnitManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyView;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyView;
import com.varanegar.vaslibrary.model.sysconfig.SysConfig;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.unit.UnitModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;
import com.varanegar.vaslibrary.ui.report.WarehouseReportAdapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class DistWarehouseReportAdapter extends SimpleReportAdapter<DistWarehouseProductQtyViewModel> {
    SysConfigModel showStockLevel;
    private BackOfficeType backOfficeType;

    public DistWarehouseReportAdapter(MainVaranegarActivity activity, SysConfigModel showStockLevel, BackOfficeType backOfficeType) {
        super(activity, DistWarehouseProductQtyViewModel.class);
        this.showStockLevel = showStockLevel;
        this.backOfficeType = backOfficeType;
    }

    @Override
    public void bind(ReportColumns columns, DistWarehouseProductQtyViewModel entity) {
        super.bind(columns, entity);
        columns.add(bind(entity, DistWarehouseProductQtyView.ProductCode, activity.getString(R.string.product_code)).setSortable().setFilterable().setWeight(1.5f).setFrizzed());
        columns.add(bind(entity, DistWarehouseProductQtyView.ProductName, activity.getString(R.string.product_name)).setSortable().setFilterable().setWeight(2f).setFrizzed());
        if (!backOfficeType.equals(BackOfficeType.ThirdParty)) {
            columns.add(bind(entity, DistWarehouseProductQtyView.OnHandQty, activity.getString(R.string.onhand_qty_init)).setFilterable().setWeight(1).setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.OnHandQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
            columns.add(bind(entity, DistWarehouseProductQtyView.TotalReturnedQty, activity.getString(R.string.retuned_from_dist)).setFilterable().setWeight(1f).setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.TotalReturnedQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
            columns.add(bind(entity, DistWarehouseProductQtyView.WellReturnQty, activity.getString(R.string.well_returned)).setFilterable().setWeight(1f).setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.WellReturnQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
            columns.add(bind(entity, DistWarehouseProductQtyView.WasteReturnQty, activity.getString(R.string.waste_returned)).setFilterable().setWeight(1f).setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.WasteReturnQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
        } else {
            columns.add(bind(entity, DistWarehouseProductQtyView.OnHandQty, activity.getString(R.string.onhand_qty_init)).setFilterable().setWeight(1.5f).calcTotal().setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.OnHandQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }).setCustomTotalView(new CustomTotalView() {
                @Override
                public void onBind(View view, List entities) {
                    HashMap<UUID, BigDecimal> units = new HashMap<>();
                    for (int i = 0; i < entities.size(); i++) {
                        DistWarehouseProductQtyViewModel distWarehouseProductQtyViewModel = (DistWarehouseProductQtyViewModel) entities.get(i);
                        List<BaseUnit> unitList = VasHelperMethods.totalQtyUnits(distWarehouseProductQtyViewModel.OnHandQty, distWarehouseProductQtyViewModel.ProductUnitId, distWarehouseProductQtyViewModel.ConvertFactor, null, null);
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
                        UnitManager unitManager = new UnitManager(getActivity());
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
            columns.add(bind(entity, DistWarehouseProductQtyView.TotalReturnedQty, activity.getString(R.string.retuned_from_dist)).setFilterable().setWeight(1.5f).calcTotal().setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.TotalReturnedQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }).setCustomTotalView(new CustomTotalView() {
                @Override
                public void onBind(View view, List entities) {
                    HashMap<UUID, BigDecimal> units = new HashMap<>();
                    for (int i = 0; i < entities.size(); i++) {
                        DistWarehouseProductQtyViewModel distWarehouseProductQtyViewModel = (DistWarehouseProductQtyViewModel) entities.get(i);
                        List<BaseUnit> unitList = VasHelperMethods.totalQtyUnits(distWarehouseProductQtyViewModel.TotalReturnedQty, distWarehouseProductQtyViewModel.ProductUnitId, distWarehouseProductQtyViewModel.ConvertFactor, null, null);
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
                        UnitManager unitManager = new UnitManager(getActivity());
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
            columns.add(bind(entity, DistWarehouseProductQtyView.WellReturnQty, activity.getString(R.string.well_returned)).setFilterable().setWeight(1.5f).calcTotal().setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.WellReturnQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }).setCustomTotalView(new CustomTotalView() {
                @Override
                public void onBind(View view, List entities) {
                    HashMap<UUID, BigDecimal> units = new HashMap<>();
                    for (int i = 0; i < entities.size(); i++) {
                        DistWarehouseProductQtyViewModel distWarehouseProductQtyViewModel = (DistWarehouseProductQtyViewModel) entities.get(i);
                        List<BaseUnit> unitList = VasHelperMethods.totalQtyUnits(distWarehouseProductQtyViewModel.WellReturnQty, distWarehouseProductQtyViewModel.ProductUnitId, distWarehouseProductQtyViewModel.ConvertFactor, null, null);
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
                        UnitManager unitManager = new UnitManager(getActivity());
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
            columns.add(bind(entity, DistWarehouseProductQtyView.WasteReturnQty, activity.getString(R.string.waste_returned)).setFilterable().setWeight(1.5f).calcTotal().setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    textView.setText(entity.WasteReturnQtyView);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }).setCustomTotalView(new CustomTotalView() {
                @Override
                public void onBind(View view, List entities) {
                    HashMap<UUID, BigDecimal> units = new HashMap<>();
                    for (int i = 0; i < entities.size(); i++) {
                        DistWarehouseProductQtyViewModel distWarehouseProductQtyViewModel = (DistWarehouseProductQtyViewModel) entities.get(i);
                        List<BaseUnit> unitList = VasHelperMethods.totalQtyUnits(distWarehouseProductQtyViewModel.WasteReturnQty, distWarehouseProductQtyViewModel.ProductUnitId, distWarehouseProductQtyViewModel.ConvertFactor, null, null);
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
                        UnitManager unitManager = new UnitManager(getActivity());
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
        }
        columns.add(bind(entity, DistWarehouseProductQtyView.TotalWeight, activity.getString(R.string.weight)).setWeight(1f).calcTotal().setCustomViewHolder(new CustomViewHolder<DistWarehouseProductQtyViewModel>() {
            @Override
            public void onBind(View view, DistWarehouseProductQtyViewModel entity) {
                TextView textView = view.findViewById(R.id.on_hand_qty);
                DecimalFormat df = new DecimalFormat("#,###.#");
                df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.getDefault()));
                textView.setText(df.format(entity.TotalWeight));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
            }
        }));

    }
}
