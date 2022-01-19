package com.varanegar.supervisor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.supervisor.webapi.VisitorVisitInfoViewModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.QuestionnaireUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/8/2018.
 */

public class SettingsFragment extends IMainPageFragment {
    private static final int REQUEST_PHONE_CALL = 190;
    private MainFragment parentFrag;
    private PieChart pieChart;
    private View errorLayout;
    private View loadingLayout;
    private View reportLayout;
    private PairedItemsSpinner<VisitorModel> visitorNameSpinner;
    private ImageView phoneImageView;
    private PairedItems customersCountPairedItems;
    private PairedItems visitedCountPairedItems;
    private PairedItems totalAmountPairedItems;
    private PairedItems visitCountPairedItems;
    private PairedItems orderCountPairedItems;
    private PairedItems nonOrderCountPairedItems;
    private PairedItems nonVisitCount;
    private PairedItems returnCountPairedItems;
    private ConstraintLayout updatingQuestionnaire;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_settings_layout, container, false);

        view.findViewById(R.id.menu_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsSlidingDialog dialog = new SettingsSlidingDialog();
                dialog.show(getChildFragmentManager(), "SettingsSlidingDialog");
            }
        });

        pieChart = view.findViewById(R.id.pie_chart);
        errorLayout = view.findViewById(R.id.error_layout);
        loadingLayout = view.findViewById(R.id.loading_layout);
        reportLayout = view.findViewById(R.id.report_layout);
        phoneImageView = view.findViewById(R.id.phone_image_view);
        visitorNameSpinner = view.findViewById(R.id.visitor_name_paired_items);
        customersCountPairedItems = view.findViewById(R.id.customers_count_paired_items);
        visitedCountPairedItems = view.findViewById(R.id.visited_count_paired_items);
        totalAmountPairedItems = view.findViewById(R.id.total_amount_paired_items);
        visitCountPairedItems = view.findViewById(R.id.visit_count_paired_items);
        orderCountPairedItems = view.findViewById(R.id.order_count_paired_items);
        nonOrderCountPairedItems = view.findViewById(R.id.non_order_count_paired_items);
        nonVisitCount = view.findViewById(R.id.non_visit_count_paired_items);
        returnCountPairedItems = view.findViewById(R.id.return_count_paired_items);
        updatingQuestionnaire = view.findViewById(R.id.updating_questionnaire_layout);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentFrag = ((MainFragment) getParentFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    private void start() {
        List<VisitorModel> visitorModels = new VisitorManager(getContext()).getAll();
        if (visitorModels.size() == 0) {
            getData();
        } else {
            populateSpinner(visitorModels);
        }
    }

    private void populateSpinner(List<VisitorModel> visitorModels) {
        VisitorModel visitorModel = new VisitorModel();
        visitorModel.Name = getString(R.string.all_visitors);
        visitorModels.add(0, visitorModel);
        visitorNameSpinner.setup(getChildFragmentManager(), visitorModels, new SearchBox.SearchMethod<VisitorModel>() {
            @Override
            public boolean onSearch(VisitorModel item, String text) {
                String searchKey = VasHelperMethods.persian2Arabic(text);
                return item.Name.contains(searchKey);
            }
        });
        final VisitorModel selectedVisitor = VisitorFilter.read(getContext());
        if (selectedVisitor.UniqueId == null)
            visitorNameSpinner.selectItem(0);
        else {
            int index = Linq.findFirstIndex(visitorModels, new Linq.Criteria<VisitorModel>() {
                @Override
                public boolean run(VisitorModel item) {
                    return item.UniqueId != null && item.UniqueId.equals(selectedVisitor.UniqueId);
                }
            });
            if (index >= 0)
                visitorNameSpinner.selectItem(index);
        }
        visitorNameSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<VisitorModel>() {
            @Override
            public void onItemSelected(int position, VisitorModel item) {
                VisitorFilter.save(getContext(), item);
                getVisitorsVisitInfo();
            }
        });
        phoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisitorModel v = visitorNameSpinner.getSelectedItem();
                if (v != null && v.UniqueId != null) {
                    if (v.Phone != null && !v.Phone.isEmpty()) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Activity activity = getActivity();
                                if (activity != null && activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + v.Phone));
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + v.Phone));
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    } else
                        showError(R.string.visitor_mobile_number_is_not_available);

                } else
                    showError(R.string.please_select_a_visitor);

            }
        });
        updatingQuestionnaire.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final MainVaranegarActivity activity = getVaranegarActvity();
                if (activity == null || activity.isFinishing())
                    return;
                if (!Connectivity.isConnected(activity)) {
                    ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                    connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
                    return;
                }
                startProgress(com.varanegar.vaslibrary.R.string.please_wait, com.varanegar.vaslibrary.R.string.updating_questionnaire);
                PingApi pingApi = new PingApi();
                pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
                    @Override
                    public void done(String ipAddress) {
                        QuestionnaireUpdateFlow flow = new QuestionnaireUpdateFlow(activity);
                        flow.syncQuestionnaire(new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                finishProgress();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(com.varanegar.vaslibrary.R.string.updating_questionnaire_completed);
                                    dialog.setTitle(com.varanegar.vaslibrary.R.string.done);
                                    dialog.setIcon(Icon.Success);
                                    dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            protected void onFailure(String error) {
                                finishProgress();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(error);
                                    dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                    dialog.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void failed() {
                        finishProgress();
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(activity);
                            dialog.setMessage(com.varanegar.vaslibrary.R.string.network_error);
                            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                            dialog.show();
                        }
                    }
                });
            }
        });
        getVisitorsVisitInfo();
    }

    private void getVisitorsVisitInfo() {
        VisitorModel visitorModel = VisitorFilter.read(getContext());
        loadingLayout.setVisibility(View.VISIBLE);
        reportLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        final UserModel userModel = UserManager.readFromFile(getContext());
        SupervisorApi api = new SupervisorApi(getContext());
        api.runWebRequest(api.getVisitorsVisitInfo(userModel.UniqueId, visitorModel.UniqueId), new WebCallBack<List<VisitorVisitInfoViewModel>>() {
            @Override
            protected void onFinish() {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing())
                    loadingLayout.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(List<VisitorVisitInfoViewModel> result, Request request) {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing()) {
                    reportLayout.setVisibility(View.VISIBLE);
                    if (result.size() == 1) {
                        pieChart.setVisibility(View.VISIBLE);
                        VisitorVisitInfoViewModel viewModel = result.get(0);
                        List<PieEntry> entries = new ArrayList<>();

                        entries.add(new PieEntry(viewModel.NoOrder, getString(R.string.lack_of_order_count)));
                        entries.add(new PieEntry(viewModel.NoVisit, getString(R.string.lack_of_visit_count)));
                        entries.add(new PieEntry(viewModel.Ordered, getString(R.string.order_count)));

                        PieDataSet pieDataSet = new PieDataSet(entries, "");
                        pieDataSet.setValueTextSize(20);
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        Description description = new Description();
                        description.setText("");
                        pieChart.setDescription(description);
                        pieChart.animateY(1500);
                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);

                        customersCountPairedItems.setValue(Integer.toString(viewModel.customerCount));
                        visitedCountPairedItems.setValue(Integer.toString(viewModel.VisitedCustomerCount));
                        totalAmountPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.TotalAmount));
                        visitCountPairedItems.setValue(Integer.toString(viewModel.Visited));
                        orderCountPairedItems.setValue(Integer.toString(viewModel.Ordered));
                        nonOrderCountPairedItems.setValue(Integer.toString(viewModel.NoOrder));
                        nonVisitCount.setValue(Integer.toString(viewModel.NoVisit));
                        returnCountPairedItems.setValue(Integer.toString(viewModel.ReturnCount));
                    }
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing()) {
                    errorLayout.setVisibility(View.VISIBLE);
                    WebApiErrorBody.log(error, activity);
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing()) {
                    errorLayout.setVisibility(View.VISIBLE);
                    Timber.e(t);
                }
            }
        });
    }

    private void getData() {
        parentFrag.disableTab();
        startProgress(R.string.please_wait, R.string.downloading_data);
        DataManager.getVisitor(getContext(), new DataManager.Callback() {
            @Override
            public void onSuccess() {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing()) {
                    parentFrag.enableTab();
                    List<VisitorModel> visitorModels = new VisitorManager(activity).getAll();
                    if (visitorModels.size() > 0)
                        populateSpinner(visitorModels);
                    finishProgress();

                //    getQuestionnaire();
                }
            }

            @Override
            public void onError(String error) {
                Activity activity = getActivity();
                if (isResumed() && activity != null && !activity.isFinishing()) {
                    parentFrag.enableTab();
                    finishProgress();
                    showError(error);
                }
            }
        });
    }

    private void showError(@StringRes int error) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void showError(String error) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }



}