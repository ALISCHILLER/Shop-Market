package com.varanegar.vaslibrary.ui.report;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportView;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModelRepository;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.print.SellAndReceivePrint.SaleOrRequestPrintHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class SalesOrRequestReportFragment extends VaranegarFragment {

    private ReportAdapter<RequestReportViewModel> adapter;
    private TextView titleTextView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_or_request_report_layout, container, false);
        titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        ReportView reportView = (ReportView) view.findViewById(R.id.report_view);
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        view.findViewById(R.id.menu_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });

        adapter = new SimpleReportAdapter<RequestReportViewModel>((MainVaranegarActivity) getActivity(), RequestReportViewModel.class) {
            @Override
            public void bind(final ReportColumns columns, final RequestReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, RequestReportView.CustomerCode, getString(R.string.customer_code)).setSortable().setFilterable().setFrizzed());
                columns.add(bind(entity, RequestReportView.CustomerName, getString(R.string.customer_name)).setSortable().setFilterable().setWeight(2).setFrizzed());
                columns.add(bind(entity, RequestReportView.StoreName, getString(R.string.request_report_store_name)).setSortable().setFilterable().setWeight(2));
                columns.add(bind(entity, RequestReportView.TotalOrderNetAmount, getString(R.string.total_order_net_amount)).setSortable().calcTotal().setFilterable().setWeight(1.5f));
                if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                    columns.add(bind(entity, RequestReportView.Discount, getString(R.string.discount_amount)).setSortable().calcTotal().setFilterable().setWeight(1.5f));
                }
                columns.add(bind(entity, RequestReportView.PaymentTypeBaseName, getString(R.string.payment_type)).setSortable().setFilterable().setWeight(1.5f));
                columns.add(bind(entity, RequestReportView.LocalPaperNo, getString(R.string.request_report_local_paper_no)).setSortable().setFilterable().setWeight(1.5f));
                columns.add(bind(entity, RequestReportView.CallType, getString(R.string.is_sent)).setWeight(1).setCustomViewHolder(new CustomViewHolder<RequestReportViewModel>() {
                    @Override
                    public void onBind(View view, RequestReportViewModel entity) {
                        if (entity.CallType != null && entity.CallType.length() > 0) {
                            TextView textView = (TextView) view.findViewById(R.id.request_report_statuse);
                            String[] callTypeSplits = entity.CallType.split(":");
                            String[] confirmStatuseSplits = entity.ConfirmStatus.split(":");
                            CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
                            List<CustomerCallModel> calls = new ArrayList<>();
                            for (int i = 0; i < callTypeSplits.length; i++) {
                                CustomerCallModel customerCallModel = new CustomerCallModel();
                                customerCallModel.CallType = customerCallManager.getCustomerCallTypeFromId(callTypeSplits[i]);
                                if (confirmStatuseSplits.equals("0"))
                                    customerCallModel.ConfirmStatus = false;
                                else
                                    customerCallModel.ConfirmStatus = true;
                                calls.add(customerCallModel);
                            }
                            textView.setText(customerCallManager.getName(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor)));
                            textView.setBackgroundColor(customerCallManager.getColor(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor)));
                            boolean isConfirmed = customerCallManager.isConfirmed(calls);
                            if (isConfirmed)
                                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                            else
                                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                        return inflater.inflate(R.layout.fragment_have_send_or_not, parent, false);
                    }
                }).setWeight(2));
            }

            @Override
            protected ItemContextView<RequestReportViewModel> onCreateContextView() {
                return new SalesOrReportContextView(getAdapter(), getContext());
            }

        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new RequestReportViewModelRepository(), RequestReportViewManager.getAll(), savedInstanceState);
        reportView.setAdapter(adapter);

        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            if (adapter.size() > 0) {
                View printImageBtn = view.findViewById(R.id.print_image_button);
                printImageBtn.setVisibility(View.VISIBLE);
                printImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SaleOrRequestPrintHelper print = new SaleOrRequestPrintHelper(getVaranegarActvity());
                        print.start(null);
                    }
                });
            }
            setTitle(getString(R.string.sales_report));
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            setTitle(getString(R.string.request_repot));
        }

        return view;

    }

    private void setTitle(String string) {
        titleTextView.setText(string);
    }

    class SalesOrReportContextView extends ItemContextView<RequestReportViewModel> {

        public SalesOrReportContextView(BaseRecyclerAdapter<RequestReportViewModel> adapter, Context context) {
            super(adapter, context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
            UUID orderId = adapter.get(position).OrderUniqueId;
            View view = inflater.inflate(R.layout.fragment_request_report_lines, viewGroup, false);
            ReportAdapter<CustomerCallOrderOrderViewModel> orderAdapter = new SimpleReportAdapter<CustomerCallOrderOrderViewModel>((MainVaranegarActivity) getVaranegarActvity(), CustomerCallOrderOrderViewModel.class) {
                @Override
                public void bind(ReportColumns columns, CustomerCallOrderOrderViewModel entity) {
                    bindRowNumber(columns);
                    columns.add(bind(entity, CustomerCallOrderOrderView.ProductCode, getString(R.string.product_code)).setSortable());
                    columns.add(bind(entity, CustomerCallOrderOrderView.ProductName, getString(R.string.product_name)).setSortable().setWeight(2));
                    columns.add(bind(entity, CustomerCallOrderOrderView.TotalQty, getString(R.string.count_label)).setSortable());
                    if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                        columns.add(bind(entity, CustomerCallOrderOrderView.UnitPrice, getString(R.string.unit_price)));
                    }
                    columns.add(bind(entity, CustomerCallOrderOrderView.RequestAmount, getString(R.string.value)).setSortable().calcTotal());
                    columns.add(bind(entity, CustomerCallOrderOrderView.TotalWeight, getString(R.string.totalsale_controling_weight_kg)).setSortable().calcTotal().setWeight(2));
                }
            };
            ReportView RequestLinesReport = (ReportView) view.findViewById(R.id.request_report_lines);
            orderAdapter.create(new CustomerCallOrderOrderViewModelRepository(), new CustomerCallOrderOrderViewManager(getContext()).getLinesQuery(orderId, null), null);
            RequestLinesReport.setAdapter(orderAdapter);
            return view;
        }
    }
}