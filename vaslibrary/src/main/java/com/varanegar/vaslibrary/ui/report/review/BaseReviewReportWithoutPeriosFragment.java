package com.varanegar.vaslibrary.ui.report.review;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportViewModel;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;

/**
 * Created by e.hashemzadeh on 20/06/21.
 */

public abstract class BaseReviewReportWithoutPeriosFragment<T extends ReviewReportViewModel> extends VaranegarFragment {

    private ReportView reportView;
    private ProgressDialog progressDialog;

    protected BaseReviewReportFragment.OnAdapterCallback onAdapterCallback;

    public SimpleReportAdapter<T> getAdapter() {
        return adapter;
    }

    private SimpleReportAdapter<T> adapter;


    public interface OnAdapterCallback {

        void onFailure();

        void onSuccess();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

    protected UUID getDealerId() {
        return UserManager.readFromFile(getContext()).UniqueId;
    }

    protected abstract Call<List<T>> reportApi();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    protected abstract SimpleReportAdapter<T> createAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_report_without_period_layout, container, false);
        setupAdapter();
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAdapter();
            }
        });

        reportView = view.findViewById(R.id.review_report_view);
        SimpleToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().popFragment();
            }
        });
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().openDrawer();
            }
        });
        toolbar.setTitle(getTitle());
        return view;
    }

    private void setupAdapter() {
        String error = isEnabled();
        if (error != null) {
            showErrorDialog(error);
            return;
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getContext().getString(R.string.downloading_data));
        progressDialog.show();
        ReviewReportApi reviewReportApi = new ReviewReportApi(getContext());
        reviewReportApi.runWebRequest(reportApi(), new WebCallBack<List<T>>() {
            @Override
            protected void onFinish() {
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(List<T> result, Request request) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    adapter = createAdapter();
                    adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                    adapter.create(result, null);
                    reportView.setAdapter(adapter);
                    if (onAdapterCallback != null)
                        onAdapterCallback.onSuccess();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    showErrorDialog(err);
                    if (onAdapterCallback != null)
                        onAdapterCallback.onFailure();
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    showErrorDialog(getString(R.string.connection_to_server_failed));
                    if (onAdapterCallback != null)
                        onAdapterCallback.onFailure();
                }
            }
        });
    }

    protected abstract String getTitle();

    protected abstract String isEnabled();
}
