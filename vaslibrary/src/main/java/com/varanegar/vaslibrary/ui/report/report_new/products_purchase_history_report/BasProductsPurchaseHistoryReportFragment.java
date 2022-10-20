package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.ProductsPurchaseHistoryReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.ui.report.review.BaseReviewReportFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;

public abstract class BasProductsPurchaseHistoryReportFragment <T extends
        ProductsPurchaseHistoryReportViewModel> extends VaranegarFragment {

    private ReportView reportView;
    private ProgressDialog progressDialog;
    private Date startDate;
    private Date endDate;
    private PairedItems startDatePairedItems;
    private PairedItems endDatePairedItems;

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
        return Objects.requireNonNull(UserManager.readFromFile(getContext())).UniqueId;
    }

    protected String getStartDateString() {
        return DateHelper.toString(getStartDate(), DateFormat.Date,
                VasHelperMethods.getSysConfigLocale(getContext()));
    }

    protected String getEndDateString() {
        return DateHelper.toString(getEndDate(), DateFormat.Date,
                VasHelperMethods.getSysConfigLocale(getContext()));
    }

    protected Date getStartDate() {
        return startDate == null ? DateHelper.Min : startDate;
    }

    protected Date getEndDate() {
        return endDate == null ? new Date() : endDate;
    }

    protected abstract Call<List<T>> reportApi();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_balance_report, container, false);
        view.findViewById(R.id.buttonReport).setOnClickListener(view1 -> setupAdapter());
        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);

        view.findViewById(R.id.start_calendar_image_view).setOnClickListener(view12 ->
                DateHelper.showDatePicker(Objects.requireNonNull(getVaranegarActvity()),
                VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
            if (calendar.getTime().after(new Date())) {
                showErrorDialog(getString(R.string.date_could_not_be_after_now));
                return;
            }
            if (endDate != null && endDate.before(calendar.getTime())) {
                showErrorDialog(getString(R.string.end_date_could_not_be_before_start_date));
                return;
            }
            startDate = calendar.getTime();
            startDatePairedItems.setValue(DateHelper.toString
                    (startDate, DateFormat.Date,
                            VasHelperMethods.getSysConfigLocale(getContext())));
        }));
        view.findViewById(R.id.end_calendar_image_view).setOnClickListener(view13 ->
                DateHelper.showDatePicker(Objects.requireNonNull(getVaranegarActvity()),
                        VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
                    if (calendar.getTime().after(new Date())) {
                        showErrorDialog(getString(R.string.date_could_not_be_after_now));
                        return;
                    }
                    if (startDate != null && startDate.after(calendar.getTime())) {
                        showErrorDialog(getString(R.string.start_date_could_not_be_after_end_date));
                        return;
                    }
                    endDate = calendar.getTime();
                    endDatePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Date,
                            VasHelperMethods.getSysConfigLocale(getContext())));
                }));
        reportView = view.findViewById(R.id.review_report_view);
        SimpleToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnBackClickListener(view14 ->
                Objects.requireNonNull(getVaranegarActvity()).popFragment());
        toolbar.setOnMenuClickListener(view15 ->
                Objects.requireNonNull(getVaranegarActvity()).openDrawer());
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
        progressDialog.setMessage(requireContext().getString(R.string.downloading_data));
        progressDialog.show();
        ReportApi invoiceReportApi = new ReportApi(getContext());
        invoiceReportApi.runWebRequest(reportApi(), new WebCallBack<List<T>>() {
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