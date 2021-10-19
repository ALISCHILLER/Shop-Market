package com.varanegar.vaslibrary.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.Target.TargetMasterManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;
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
 * Created by g.aliakbar on 18/04/2018.
 */

public abstract class CustomerTargetReportDetailContentFragment extends VaranegarFragment {

    private ReportAdapter<TargetReportDetailViewModel> adapter;;
    private ReportView targetReportDetail;
    private Bundle savedInstanceState;
    private UUID targetUniqueId;
    private UUID targetBaseUniqueId;
    private UUID targetTypeUniqueId;
    private String customerId;
    private ProgressDialog progressDialogWait;
    private RadioButton rbAmount;
    private List<TargetReportDetailViewModel> reportDetailViewModelList;

    protected abstract VaranegarFragment getContentFragment();



    public UUID getSelectedId() {
        try {
            return UUID.fromString(getArguments().getString("a129ef75-77ce-432a-8982-6bcab0bf7b51"));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        TargetMasterModel targetMasterModel = new TargetMasterManager(getContext()).getItem( TargetMasterManager.getFilterTargets(UserManager.readFromFile(getContext()).UniqueId, getSelectedId()));
        String targetReportText = targetMasterModel.Title + "  " + targetMasterModel.FromPDate + " - " + targetMasterModel.ToPDate + "  " + TargetBase.getName(getContext(), targetMasterModel.TargetBaseUniqueId) + " - " + TargetType.getName(getContext(), targetMasterModel.TargetTypeUniqueId);

        targetUniqueId = targetMasterModel.UniqueId;
        targetBaseUniqueId = targetMasterModel.TargetBaseUniqueId;
        targetTypeUniqueId =  targetMasterModel.TargetTypeUniqueId;
        customerId = getSelectedId().toString();

        this.savedInstanceState = savedInstanceState;

        View view = inflater.inflate(R.layout.fragment_target_report_detail_customer, viewGroup, false);
        targetReportDetail = (ReportView) view.findViewById(R.id.target_report_detail);

        view.findViewById(R.id.content_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeContent(getContentFragment());
            }
        });


        RadioButton rbOnHandy = view.findViewById(R.id.targetReport_onHandyQty);
        rbAmount = view.findViewById(R.id.targetReport_amount);
        RadioGroup radioGroup = view.findViewById(R.id.targetReport_radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(adapter != null)
                {
                    setData();
                }
            }
        });

        TextView title = view.findViewById(R.id.targetTitle_text);
        title.setText(targetReportText);

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
        targetApi.runWebRequest(targetApi.getTargetDetail(UserManager.readFromFile(getContext()).UniqueId.toString(), targetUniqueId.toString(),customerId), new WebCallBack<List<TargetReportDetailViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<TargetReportDetailViewModel> result, Request request) {

                if (result != null) {
                    progressDialogWait.dismiss();
                    reportDetailViewModelList = result;

                    setData();

                }else {
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
                String err = WebApiErrorBody.log(error,getContext());

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

                Timber.e(t,"TargetDetailReport");

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

        if(reportDetailViewModelList != null) {

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

                    if(rbAmount.isChecked()) {
                        columns.add(bind(entity, TargetReportDetailView.TargetAmount, getString(R.string.target)).setDecimalFormat("##"));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmount, getString(R.string.achieved_in_period)).setWeight(2));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmountPercent, getString(R.string.achieved_in_day_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AchievementAmountPercentInDay, getString(R.string.achieved_in_period_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.SaleAverage, getString(R.string.sales_average_in_days_remain)).setWeight(2));
                    }else{
                        columns.add(bind(entity, TargetReportDetailView.TargetQty, getString(R.string.target)).setDecimalFormat("##"));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQty, getString(R.string.achieved_in_period)).setWeight(2));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQtyPercent, getString(R.string.achieved_in_day_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AchievementQtyPercentInDay, getString(R.string.achieved_in_period_percent)).setSuffix(" %").setWeight(1));
                        columns.add(bind(entity, TargetReportDetailView.AverageQty, getString(R.string.sales_average_in_days_remain)).setWeight(2));
                    }
                }
            };
            adapter.create(reportDetailViewModelList, savedInstanceState);
            adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
            targetReportDetail.setAdapter(adapter);
        }

    }
}
