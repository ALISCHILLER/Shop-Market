package com.varanegar.vaslibrary.ui.report;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.ReturnReasonManager;
import com.varanegar.vaslibrary.manager.ReturnReportViewManager;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportView;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportViewModel;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportViewModelRepository;
import com.varanegar.vaslibrary.print.reportsprint.ReturnReportPrintHelper;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */

public class ReturnReportFragment extends VaranegarFragment {
    ReportAdapter<ReturnReportViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_returns_report, container, false);
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

        adapter = new SimpleReportAdapter<ReturnReportViewModel>((MainVaranegarActivity) getActivity(), ReturnReportViewModel.class) {
            @Override
            protected ItemContextView<ReturnReportViewModel> onCreateContextView() {
                return new ReturnReportViewContextView(getAdapter(), getActivity());
            }

            @Override
            public void bind(ReportColumns columns, final ReturnReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, ReturnReportView.ProductCode, getString(R.string.product_code)).setSortable().setFilterable());
                columns.add(bind(entity, ReturnReportView.ProductName, getString(R.string.product_name)).setSortable().setFilterable().setWeight(2));
                columns.add(bind(entity, ReturnReportView.TotalReturnQty, activity.getString(R.string.returned_qty_label)).setWeight(2).setCustomViewHolder(new CustomViewHolder<ReturnReportViewModel>() {
                    @Override
                    public void onBind(View view, ReturnReportViewModel entity) {
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
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                        return new LinearLayout(inflater.getContext());
                    }
                }));
                columns.add(bind(entity, ReturnReportView.TotalReturnQty, getString(R.string.total_return_qty)).setWeight(1.5f).setSortable().calcTotal().setFilterable());

                if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                    columns.add(bind(entity, ReturnReportView.RequestUnitPrice, getString(R.string.unit_price)).setSortable().calcTotal().setFilterable());
                    columns.add(bind(entity, ReturnReportView.TotalRequestAmount, getString(R.string.return_report_amount)).setSortable().calcTotal().setFilterable());
                }
                columns.add(bind(entity, ReturnReportView.TotalWeight, getString(R.string.totalsale_controling_weight_kg)).setWeight(1.5f).setSortable().calcTotal().setFilterable());

            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new ReturnReportViewModelRepository(), ReturnReportViewManager.getAll(), savedInstanceState);
        salesReport.setAdapter(adapter);

        ImageView print = view.findViewById(R.id.return_report_print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.size() > 0) {
                    ReturnReportPrintHelper print = new ReturnReportPrintHelper(getVaranegarActvity());
                    print.start(null);
                }
            }
        });

        return view;
    }


    class ReturnReportViewContextView extends ItemContextView<ReturnReportViewModel> {


        private List<ReturnReasonModel> reasonModels;

        public ReturnReportViewContextView(BaseRecyclerAdapter<ReturnReportViewModel> adapter, Context context) {
            super(adapter, context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
            View view = inflater.inflate(R.layout.return_report_context_view, viewGroup, false);
            ReturnReportViewModel returnViewModel = adapter.get(position);
            ((TextView) view.findViewById(R.id.product_name_text_view)).setText(returnViewModel.ProductName);
            ((TextView) view.findViewById(R.id.total_return_text_view)).setText(HelperMethods.bigDecimalToString(returnViewModel.TotalReturnQty));

            ReturnReasonManager returnReasonManager = new ReturnReasonManager(getActivity());
            reasonModels = returnReasonManager.getAll();

            CustomerCallReturnLinesViewManager manager = new CustomerCallReturnLinesViewManager(getContext());
            List<CustomerCallReturnLinesViewModel> list = manager.getLines(returnViewModel.ProductId, null);

            BaseRecyclerAdapter<CustomerCallReturnLinesViewModel> adapter = new BaseRecyclerAdapter<CustomerCallReturnLinesViewModel>(getVaranegarActvity(), list) {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.return_report_item_row_layout, parent, false);
                    return new CalcItemViewHolder(view, this, getActivity());
                }
            };

            BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.return_report_recycler_view);
            recyclerView.setAdapter(adapter);
            return view;
        }

        class CalcItemViewHolder extends BaseViewHolder<CustomerCallReturnLinesViewModel> {
            private final TextView returnReasonTextView;
            private final LinearLayout qtyLayout;
            private final TextView returnQtyTextView;
            private final TextView returnTypeTextView;
            private final TextView invoiceNoTextView;
            private final TextView amountTextView;

            public CalcItemViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallReturnLinesViewModel> recyclerAdapter, Context context) {
                super(itemView, recyclerAdapter, context);
                returnReasonTextView = (TextView) itemView.findViewById(R.id.return_reason_text_view);
                returnTypeTextView = (TextView) itemView.findViewById(R.id.return_type_text_view);
                returnQtyTextView = (TextView) itemView.findViewById(R.id.return_qty_text_view);
                qtyLayout = (LinearLayout) itemView.findViewById(R.id.return_qty_layout);
                invoiceNoTextView = (TextView) itemView.findViewById(R.id.invoice_no_text_view);
                amountTextView = (TextView) itemView.findViewById(R.id.amount_text_view);

            }

            @Override
            public void bindView(int position) {
                final CustomerCallReturnLinesViewModel returnLinesViewModel = recyclerAdapter.get(position);
                ReturnReasonModel returnReasonModel = Linq.findFirst(reasonModels, new Linq.Criteria<ReturnReasonModel>() {
                    @Override
                    public boolean run(ReturnReasonModel item) {
                        return item.UniqueId.equals(returnLinesViewModel.ReturnReasonId);
                    }
                });
                returnReasonTextView.setText(returnReasonModel.ReturnReasonName);
                if (ReturnType.Well.equals(returnLinesViewModel.ReturnProductTypeId))
                    returnTypeTextView.setText(R.string.well);
                else
                    returnTypeTextView.setText(R.string.waste);

                if (returnLinesViewModel.SaleNo == null)
                    invoiceNoTextView.setText(R.string.return_without_reference);
                else
                    invoiceNoTextView.setText(String.valueOf(returnLinesViewModel.SaleNo));

                amountTextView.setText(HelperMethods.currencyToString(returnLinesViewModel.TotalRequestAmount));
                returnQtyTextView.setText(HelperMethods.bigDecimalToString(returnLinesViewModel.TotalReturnQty));
                List<BaseUnit> units = VasHelperMethods.chopTotalQty(returnLinesViewModel.TotalReturnQty, returnLinesViewModel.UnitName, returnLinesViewModel.ConvertFactor, null, null);
                new QtyView().build(qtyLayout, units);
            }
        }
    }
}

