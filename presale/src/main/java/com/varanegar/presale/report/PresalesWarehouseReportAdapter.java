package com.varanegar.presale.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductBatchOnHandQtyManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyView;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQty;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModel;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.report.WarehouseReportAdapter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class PresalesWarehouseReportAdapter extends WarehouseReportAdapter {
    private final SysConfigModel showUnconfirmedRequests;
    public SysConfigModel showStockLevel;


    public PresalesWarehouseReportAdapter(MainVaranegarActivity activity, SysConfigModel showStockLevel, BackOfficeType backOfficeType) {
        super(activity);
        this.showStockLevel = showStockLevel;
        SysConfigManager sysConfigManager = new SysConfigManager(activity);
        showUnconfirmedRequests = sysConfigManager.read(ConfigKey.ShowInventoryMinusUnconfirmedRequests, SysConfigManager.cloud);
    }

    @Override
    public void bind(ReportColumns columns, WarehouseProductQtyViewModel entity) {
        super.bind(columns, entity);
        if (SysConfigManager.compare(showStockLevel, true)) {
            columns.add(bind(entity, WarehouseProductQtyView.OnHandQty, activity.getString(R.string.onhand_qty_small_unit)).setWeight(2).setSortable().setFilterable().setCustomViewHolder(new CustomViewHolder<WarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, WarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                    SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
                    if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                        textView.setText(String.valueOf(entity.OnHandQty));
                    } else {
                        if (entity.OnHandQty.compareTo(BigDecimal.ZERO) > 0) {
                            textView.setText(R.string.check_sign);
                            textView.setTextColor(activity.getResources().getColor(R.color.green));
                        } else {
                            textView.setText(R.string.multiplication_sign);
                            textView.setTextColor(activity.getResources().getColor(R.color.red));
                        }
                    }
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
            columns.add(bind(entity, WarehouseProductQtyView.OnHandQtyView, activity.getString(R.string.onhand_qty)).setWeight(1.5f).setCustomViewHolder(new CustomViewHolder<WarehouseProductQtyViewModel>() {
                @Override
                public void onBind(View view, WarehouseProductQtyViewModel entity) {
                    TextView textView = view.findViewById(R.id.on_hand_qty);
                    SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                    SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
                    if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                        textView.setText(entity.OnHandQtyView);
                    } else {
                        if (entity.OnHandQty.compareTo(BigDecimal.ZERO) > 0) {
                            textView.setText(R.string.check_sign);
                            textView.setTextColor(activity.getResources().getColor(R.color.green));
                        } else {
                            textView.setText(R.string.multiplication_sign);
                            textView.setTextColor(activity.getResources().getColor(R.color.red));
                        }
                    }
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                }
            }));
        }
        if (SysConfigManager.compare(showUnconfirmedRequests, true))
            columns.add(bind(entity, WarehouseProductQtyView.RemainedAfterReservedQty, activity.getString(R.string.onhand_qty_after_reserve)).setWeight(1));
        columns.add(bind(entity, WarehouseProductQtyView.BatchNo, activity.getString(R.string.batch_numbers)).setWeight(1).setCustomViewHolder(new CustomViewHolder<WarehouseProductQtyViewModel>() {
            @Override
            public void onBind(View view, WarehouseProductQtyViewModel entity) {
                ImageView imageView = view.findViewById(R.id.batch_image_view);
                if (entity.BatchNo != null)
                    imageView.setVisibility(View.VISIBLE);
                else
                    imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.show_batch_product_layout, parent, false);
            }
        }));
    }

    @Override
    protected ItemContextView<WarehouseProductQtyViewModel> onCreateContextView() {
        return new BatchDetailContextview(getAdapter(), getActivity());
    }

    class BatchDetailContextview extends ItemContextView<WarehouseProductQtyViewModel> {

        public BatchDetailContextview(BaseRecyclerAdapter<WarehouseProductQtyViewModel> adapter, Context context) {
            super(adapter, context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
            if (adapter.get(position).BatchNo != null) {
                UUID productId = adapter.get(position).UniqueId;
                View view = inflater.inflate(R.layout.fragment_batch_lines, viewGroup, false);
                ReportAdapter<ProductBatchOnHandQtyModel> adapter = new SimpleReportAdapter<ProductBatchOnHandQtyModel>((MainVaranegarActivity) getActivity(), ProductBatchOnHandQtyModel.class) {
                    @Override
                    public void bind(ReportColumns columns, ProductBatchOnHandQtyModel entity) {
                        bindRowNumber(columns);
                        columns.add(bind(entity, ProductBatchOnHandQty.BatchNo, getActivity().getString(R.string.batch_no)).setSortable());
                        columns.add(bind(entity, ProductBatchOnHandQty.ExpDate, getActivity().getString(R.string.expire_date)).setSortable());
                        if (SysConfigManager.compare(showStockLevel, true))
                            columns.add(bind(entity, ProductBatchOnHandQty.OnHandQty, getActivity().getString(R.string.onhand_qty)).setSortable().setCustomViewHolder(new CustomViewHolder<ProductBatchOnHandQtyModel>() {
                                @Override
                                public void onBind(View view, ProductBatchOnHandQtyModel entity) {
                                    TextView textView = view.findViewById(R.id.on_hand_qty);
                                    SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                                    SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
                                    if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                                        textView.setText(String.valueOf(entity.OnHandQty));
                                    } else {
                                        if (entity.OnHandQty.compareTo(BigDecimal.ZERO) > 0) {
                                            textView.setText(R.string.check_sign);
                                            textView.setTextColor(activity.getResources().getColor(R.color.green));
                                        } else {
                                            textView.setText(R.string.multiplication_sign);
                                            textView.setTextColor(activity.getResources().getColor(R.color.red));
                                        }
                                    }
                                }

                                @Override
                                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                                    return inflater.inflate(R.layout.show_on_hand_qty_layout, parent, false);
                                }
                            }));
                    }
                };
                ReportView batchlinesReport = (ReportView) view.findViewById(R.id.batch_report_lines_view);
                adapter.create(new ProductBatchOnHandQtyModelRepository(), ProductBatchOnHandQtyManager.getProductBatchesQuery(productId), null);
                batchlinesReport.setAdapter(adapter);
                return view;
            } else
                return null;
        }
    }

}
