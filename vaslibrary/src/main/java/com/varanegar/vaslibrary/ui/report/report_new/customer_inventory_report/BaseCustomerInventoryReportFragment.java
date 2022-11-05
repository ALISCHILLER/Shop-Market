package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.InventoryRequest;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.PCustomerInventoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import okhttp3.Request;
import retrofit2.Call;

/**
 * Create by Mehrdad Latifi on 8/23/2022
 */

public abstract class BaseCustomerInventoryReportFragment<T extends PCustomerInventoryReportModel> extends VisitFragment {

    private Date startDate;
    private Date endDate;
    private PairedItems startDatePairedItems;
    private PairedItems endDatePairedItems;
    private ReportView reportView;
    private ImageView startCalendarImageView;
    private ImageView endCalendarImageView;
    private SimpleToolbar toolbar;
    private Button buttonReport;
    private ProgressDialog progressDialog;
    private MainVaranegarActivity activity;
    private PairedItemsSpinner<ProductGroupModel> filterSpinner;
    private List<T> resultReport;
    private Integer selectPosition = -1;
    private String selectedFilterId;

    protected abstract SimpleReportAdapter<T> createAdapter();

    protected abstract Call<List<T>> reportApi();

    protected abstract String getTitle();

    @NonNull
    protected abstract UUID getCustomerId();


    //---------------------------------------------------------------------------------------------- onCreateView
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_inventory_report, container, false);
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            long start = savedInstanceState.getLong("startDate", 0);
            long end = savedInstanceState.getLong("endDate", 0);
            selectedFilterId = savedInstanceState.getString("selectedFilterId", null);
            selectPosition = savedInstanceState.getInt("selectPosition", -1);
            /*
             * مجبور شدم این کار رو بکنم چون مدل اصلی کل پروژه از Parcelable ارث بری نکرده بود
             * */
            ArrayList<Parcelable> objects = savedInstanceState.getParcelableArrayList("report");
            try {
                resultReport = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    T item = (T) objects.get(i);
                    resultReport.add(item);
                }
            } catch (Exception ignored) {

            }

            if (start != 0 && end != 0) {
                startDate = new Date(start);
                endDate = new Date(end);
            }
        }
        activity = getVaranegarActvity();
        if (activity == null)
            return;

        init(view);
        setListener();
    }
    //---------------------------------------------------------------------------------------------- onViewCreated



    //---------------------------------------------------------------------------------------------- init
    private void init(@NonNull View view) {

        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);
        startCalendarImageView = view.findViewById(R.id.start_calendar_image_view);
        endCalendarImageView = view.findViewById(R.id.end_calendar_image_view);
        reportView = view.findViewById(R.id.review_report_view);
        toolbar = view.findViewById(R.id.toolbar);
        buttonReport = view.findViewById(R.id.buttonReport);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        toolbar.setTitle(getTitle());
        filterSpinner.setVisibility(View.GONE);

        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -30);
            startDate = calendar.getTime();
        }

        if (endDate == null)
            endDate = new Date();

        setDateToDatePairedItems(startDatePairedItems, startDate);

        setDateToDatePairedItems(endDatePairedItems, endDate);

        if (resultReport != null) {
            setFilterSpinner(resultReport);
            setReportAdapter(resultReport);
        }

        if (selectPosition != -1 && resultReport != null) {
            filterSpinner.selectItem(selectPosition);
            filterList(resultReport, selectedFilterId);
        }

    }
    //---------------------------------------------------------------------------------------------- init


    //---------------------------------------------------------------------------------------------- setListener
    private void setListener() {
        if (getVaranegarActvity() == null) {
            onBackPressed();
            return;
        }

        startCalendarImageView.setOnClickListener(v -> clickOnStartCalender());
        endCalendarImageView.setOnClickListener(v -> clickOnEndCalender());

        toolbar.setOnBackClickListener(view -> getVaranegarActvity().popFragment());

        toolbar.setOnMenuClickListener(view -> getVaranegarActvity().openDrawer());

        buttonReport.setOnClickListener(view -> {
            if (checkEmpty()) {
                requestForReport();
            }
        });

    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- clickOnStartCalender
    private void clickOnStartCalender() {
        DateHelper.showDatePicker(activity,
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
                    setDateToDatePairedItems(startDatePairedItems, startDate);
                });
    }
    //---------------------------------------------------------------------------------------------- clickOnStartCalender


    //---------------------------------------------------------------------------------------------- clickOnEndCalender
    private void clickOnEndCalender() {
        DateHelper.showDatePicker(activity,
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
                    setDateToDatePairedItems(endDatePairedItems, endDate);
                });
    }
    //---------------------------------------------------------------------------------------------- clickOnEndCalender


    //---------------------------------------------------------------------------------------------- setDateToDatePairedItems
    private void setDateToDatePairedItems(PairedItems pairedItems, Date date) {
        pairedItems.setValue(DateHelper.toString(
                date,
                DateFormat.Date,
                VasHelperMethods.getSysConfigLocale(getContext())));
    }
    //---------------------------------------------------------------------------------------------- setDateToDatePairedItems


    //---------------------------------------------------------------------------------------------- setCustomerId
    public void setCustomerId(UUID customerId) {
        addArgument("67485d97-5f0e-4b1e-9677-0798dec7a587", customerId.toString());
    }
    //---------------------------------------------------------------------------------------------- setCustomerId


    //---------------------------------------------------------------------------------------------- showErrorDialog
    private void showErrorDialog(String error) {
        if (getContext() == null)
            return;

        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
    //---------------------------------------------------------------------------------------------- showErrorDialog



    //---------------------------------------------------------------------------------------------- checkEmpty
    private boolean checkEmpty() {

        if (startDate == null) {
            showErrorDialog(getString(R.string.startDateEmpty));
            return false;
        }

        if (endDate == null) {
            showErrorDialog(getString(R.string.endDateEmpty));
            return false;
        }

        return true;
    }
    //---------------------------------------------------------------------------------------------- checkEmpty


    //---------------------------------------------------------------------------------------------- requestForReport
    private void requestForReport() {
        filterSpinner.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getString(R.string.downloading_data));
        progressDialog.show();
        ReportApi invoiceReportApi = new ReportApi(getContext());
        invoiceReportApi.runWebRequest(reportApi(),
                new WebCallBack<List<T>>() {
            @Override
            protected void onFinish() {
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(List<T> result, Request request) {
                resultReport = result;
                setFilterSpinner(resultReport);
                setReportAdapter(resultReport);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    showErrorDialog(err);
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    showErrorDialog(getString(R.string.connection_to_server_failed));
                }
            }
        });

    }
    //---------------------------------------------------------------------------------------------- requestForReport


    //---------------------------------------------------------------------------------------------- dismissProgressDialog
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    //---------------------------------------------------------------------------------------------- dismissProgressDialog



    //---------------------------------------------------------------------------------------------- getDateString
    private String getDateString(Date date) {
        String dateJalali = DateHelper.toString(date, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        dateJalali = dateJalali.replaceAll("/", "");
        return dateJalali;
    }
    //---------------------------------------------------------------------------------------------- getDateString


    //---------------------------------------------------------------------------------------------- setReportAdapter
    private void setReportAdapter(List<T> items) {
        SimpleReportAdapter<T> adapter = createAdapter();
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(items, null);
        reportView.setAdapter(adapter);
    }
    //---------------------------------------------------------------------------------------------- setReportAdapter



    //---------------------------------------------------------------------------------------------- setFilterSpinner
    private void setFilterSpinner(List<T> items) {
        List<ProductGroupModel> spinnerList = new ProductGroupManager(requireContext()).getAll();
        ProductGroupModel all = new ProductGroupModel();
        all.ProductGroupName = "همه";
        spinnerList.add(0, all);
        filterSpinner.setVisibility(View.VISIBLE);
        filterSpinner.setup(getChildFragmentManager(), spinnerList, (item, text) -> {
            String searchKey = VasHelperMethods.persian2Arabic(text);
            if (searchKey == null)
                return false;
            return item.ProductGroupName.contains(searchKey);
        });

        filterSpinner.setOnItemSelectedListener((position, item) -> {
            if (position == 0)
                setReportAdapter(items);
            else {
                selectPosition = position;
                assert item.UniqueId != null;
                filterList(items, item.UniqueId.toString());
                selectedFilterId = item.UniqueId.toString();
            }
        });
    }
    //---------------------------------------------------------------------------------------------- setFilterSpinner



    //---------------------------------------------------------------------------------------------- filterList
    private void filterList(List<T> items, String productGroupNameID) {
        List<T> filter = new ArrayList<>();
        for (T item : items) {
            Log.i("meri", item.productGroupNameID.toUpperCase() + "  /  " + productGroupNameID.toUpperCase());
            if (item.productGroupNameID.toUpperCase().contains(productGroupNameID.toUpperCase()))
                filter.add(item);
        }
        setReportAdapter(filter);
    }
    //---------------------------------------------------------------------------------------------- filterList


    //---------------------------------------------------------------------------------------------- getInventoryRequest
    protected InventoryRequest getInventoryRequest() {
        CustomerPathViewModel pathView = new CustomerPathViewManager(requireContext()).getCustomerPathViewModel(getCustomerId());
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null)
            return null;

        return new InventoryRequest(
                getDateString(startDate),
                getDateString(endDate),
                userModel.UniqueId.toString(),
                getCustomerId().toString(),
                pathView.VisitTemplatePathId.toString());
    }
    //---------------------------------------------------------------------------------------------- getInventoryRequest



    //---------------------------------------------------------------------------------------------- onSaveInstanceState
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (startDate != null)
            outState.putLong("startDate", startDate.getTime());
        if (endDate != null)
            outState.putLong("endDate", endDate.getTime());
        if (selectedFilterId != null) {
            outState.putString("selectedFilterId", selectedFilterId);
            outState.putInt("selectPosition", selectPosition);
        }
        outState.putParcelableArrayList("report", (ArrayList<? extends Parcelable>) resultReport);
    }
    //---------------------------------------------------------------------------------------------- onSaveInstanceState


}
