package com.varanegar.vaslibrary.ui.report.target;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.targetbase.TargetBase;
import com.varanegar.vaslibrary.model.targettype.TargetType;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.target.TargetApi;
import com.varanegar.vaslibrary.webapi.target.TargetReportDetailView;
import com.varanegar.vaslibrary.webapi.target.TargetReportDetailViewModel;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by g.aliakbar on 10/03/2018.
 */

public class TargetReportDetailFragment extends VaranegarFragment {

    private ReportAdapter<TargetReportDetailViewModel> adapter;
    ;
    private ReportView targetReportDetail;
    private Bundle savedInstanceState;
    private UUID targetUniqueId;
    private UUID targetBaseUniqueId;
    private UUID targetTypeUniqueId;
    private String customerId;
    private ProgressDialog progressDialogWait;
    private RadioButton rbAmount;
    private List<TargetReportDetailViewModel> reportDetailViewModelList;


    public void setTarget(@NonNull UUID targetUniqueId, @NonNull String targetReportText, @NonNull UUID targetBaseUniqueId, UUID targetTypeUniqueId, String customerId) {
        Bundle bundle = new Bundle();
        bundle.putString("6d047089-2a26-43ca-a78e-ac3bd67157de", targetUniqueId.toString());
        bundle.putString("975e5b50-a355-4334-882b-a2b68edd8620", targetReportText);
        bundle.putString("b51d5e75-0eb3-4d85-aae7-020cf25efb1d", targetBaseUniqueId.toString());
        bundle.putString("42796194-671e-4e06-8039-1b103636d209", targetTypeUniqueId.toString());
        bundle.putString("821ae6d6-fa4c-4f19-8eea-d60efbea2f57", customerId);
        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        targetUniqueId = UUID.fromString(getStringArgument("6d047089-2a26-43ca-a78e-ac3bd67157de"));
        targetBaseUniqueId = UUID.fromString(getStringArgument("b51d5e75-0eb3-4d85-aae7-020cf25efb1d"));
        targetTypeUniqueId = UUID.fromString(getStringArgument("42796194-671e-4e06-8039-1b103636d209"));
        customerId = getStringArgument("821ae6d6-fa4c-4f19-8eea-d60efbea2f57");
        String targetReportText = getStringArgument("975e5b50-a355-4334-882b-a2b68edd8620");
        this.savedInstanceState = savedInstanceState;

        View view = inflater.inflate(R.layout.fragment_target_report_detail, container, false);
        targetReportDetail = (ReportView) view.findViewById(R.id.target_report_detail);

        RadioButton rbOnHandy = view.findViewById(R.id.targetReport_onHandyQty);
        rbAmount = view.findViewById(R.id.targetReport_amount);
        RadioGroup radioGroup = view.findViewById(R.id.targetReport_radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (adapter != null) {
                    setData();
                }
            }
        });

        SimpleToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        toolbar.setTitle(targetReportText);

        progressDialogWait = new ProgressDialog(getContext());
        progressDialogWait.setTitle(getContext().getString(R.string.please_wait));
        progressDialogWait.setMessage(getContext().getString(R.string.connecting_to_the_server));
        progressDialogWait.setCancelable(false);
        progressDialogWait.show();

        getData();

        return view;
    }

    private void getData() {

        TargetApi targetApi = new TargetApi(getContext());
        targetApi.runWebRequest(targetApi.getTargetDetail(UserManager.readFromFile(getContext()).UniqueId.toString(), targetUniqueId.toString(), customerId), new WebCallBack<List<TargetReportDetailViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<TargetReportDetailViewModel> result, Request request) {
                if (result != null) {
                    progressDialogWait.dismiss();
                    reportDetailViewModelList = result;
                    setData();
                } else {
                    progressDialogWait.dismiss();
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Alert);
                    dialog.setTitle(R.string.alert);
                    dialog.setMessage(R.string.no_data_to_load);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                progressDialogWait.dismiss();
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(err);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t, "TargetDetailReport");
                progressDialogWait.dismiss();
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(R.string.error_connecting_to_server);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        });
    }

    private void setData() {
        if (reportDetailViewModelList != null) {
            adapter = new SimpleReportAdapter<TargetReportDetailViewModel>((MainVaranegarActivity) getActivity(), TargetReportDetailViewModel.class) {
                @Override
                public void bind(ReportColumns columns, TargetReportDetailViewModel entity) {
                    bindRowNumber(columns);

                    if (targetBaseUniqueId.equals(TargetBase.Customer) && customerId.equals("")) {
                        columns.add(bind(entity, TargetReportDetailView.CustomerName, getString(R.string.customer)).setSortable());
                    }
                    if (targetTypeUniqueId.equals(TargetType.Product)) {
                        columns.add(bind(entity, TargetReportDetailView.SubjectTitle, getString(R.string.product)).setWeight(2));
                    }
                    if (targetTypeUniqueId.equals(TargetType.ProductGroup)) {
                        columns.add(bind(entity, TargetReportDetailView.SubjectTitle, getString(R.string.product_group)).setWeight(2));
                    }
                    if (targetTypeUniqueId.equals(TargetType.Total)) {
                        columns.add(bind(entity, TargetReportDetailView.SubjectTitle, getString(R.string.title)).setSortable().setWeight(2));
                    }

                    if (rbAmount.isChecked()) {
                        columns.add(bind(entity, TargetReportDetailView.TargetAmount, getString(R.string.target)).setDecimalFormat("##"));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmount, getString(R.string.achieved_in_period)).setWeight(2));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmountPercent, getString(R.string.achieved_in_day_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmountPercentInDay, getString(R.string.achieved_in_period_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.SaleAverage, getString(R.string.sales_average_in_days_remain)).setWeight(2));
                    } else {
                        columns.add(bind(entity, TargetReportDetailView.TargetQty, getString(R.string.target)).setDecimalFormat("##"));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQty, getString(R.string.achieved_in_period)).setWeight(2));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQtyPercent, getString(R.string.achieved_in_day_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQtyPercentInDay, getString(R.string.achieved_in_period_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AverageQty, getString(R.string.sales_average_in_days_remain)).setWeight(2));
                    }
                }
            };
            adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
            adapter.create(reportDetailViewModelList, savedInstanceState);
            targetReportDetail.setAdapter(adapter);
        }

    }
}
