package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.CustomerInventoryReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.CustomerInventoryReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.InventoryRequest;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.PCustomerInventoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Create by Mehrdad Latifi on 8/23/2022
 */

public class CustomerInventoryReportFragment <T extends CustomerInventoryReportViewModel> extends VisitFragment {

    private UUID customerId;
    private CustomerModel customer;
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
    private PairedItemsSpinner<ProductGroupModel> filterSpinner;


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
        init(view);
        setListener();
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- getCustomerId
    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }
    //---------------------------------------------------------------------------------------------- getCustomerId


    //---------------------------------------------------------------------------------------------- init
    private void init(@NonNull View view) {
        customerId = UUID.fromString(getStringArgument("67485d97-5f0e-4b1e-9677-0798dec7a587"));
        try {
            CustomerCallManager callManager = new CustomerCallManager(requireContext());
            callManager.unConfirmAllCalls(customerId);
            CustomerManager customerManager = new CustomerManager(getContext());
            customer = customerManager.getItem(customerId);
        } catch (Exception e) {
            onBackPressed();
            Timber.e(e);

        }
        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);
        startCalendarImageView = view.findViewById(R.id.start_calendar_image_view);
        endCalendarImageView = view.findViewById(R.id.end_calendar_image_view);
        reportView = view.findViewById(R.id.review_report_view);
        toolbar = view.findViewById(R.id.toolbar);
        buttonReport = view.findViewById(R.id.buttonReport);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        toolbar.setTitle(getTitle());

    }
    //---------------------------------------------------------------------------------------------- init


    //---------------------------------------------------------------------------------------------- setListener
    private void setListener() {
        if (getVaranegarActvity() == null) {
            onBackPressed();
            return;
        }

        startCalendarImageView.setOnClickListener(view1 -> DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
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

        endCalendarImageView.setOnClickListener(view12 -> DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
            if (calendar.getTime().after(new Date())) {
                showErrorDialog(getString(R.string.date_could_not_be_after_now));
                return;
            }
            if (startDate != null && startDate.after(calendar.getTime())) {
                showErrorDialog(getString(R.string.start_date_could_not_be_after_end_date));
                return;
            }
            endDate = calendar.getTime();
            endDatePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
        }));

        toolbar.setOnBackClickListener(view -> getVaranegarActvity().popFragment());

        toolbar.setOnMenuClickListener(view -> getVaranegarActvity().openDrawer());

        buttonReport.setOnClickListener(view -> {
            if (checkEmpty()) {
                requestForReport();
            }
        });

    }
    //---------------------------------------------------------------------------------------------- setListener


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


    //---------------------------------------------------------------------------------------------- getTitle
    protected String getTitle() {
        return getString(R.string.customerInventoryReport);
    }
    //---------------------------------------------------------------------------------------------- getTitle


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
        invoiceReportApi.runWebRequest(inventoryReportApi(), new WebCallBack<List<PCustomerInventoryReportModel>>() {
            @Override
            protected void onFinish() {
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(List<PCustomerInventoryReportModel> result, Request request) {
                setFilterSpinner(result);
                setReportAdapter(result);
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


    //---------------------------------------------------------------------------------------------- inventoryReportApi
    private Call<List<PCustomerInventoryReportModel>> inventoryReportApi() {
        CustomerPathViewModel pathView = new CustomerPathViewManager(requireContext()).getCustomerPathViewModel(customerId);
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null)
            return null;
        InventoryRequest request = new InventoryRequest(
                getDateString(startDate),
                getDateString(endDate),
                userModel.UniqueId.toString(),
                customerId.toString(),
                pathView.VisitTemplatePathId.toString()
        );
        return new ReportApi(getContext()).inventoryReport(request);
    }
    //---------------------------------------------------------------------------------------------- inventoryReportApi


    //---------------------------------------------------------------------------------------------- getDateString
    private String getDateString(Date date) {
        String dateJalali = DateHelper.toString(date, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        dateJalali = dateJalali.replaceAll("/", "");
        return dateJalali;
    }
    //---------------------------------------------------------------------------------------------- getDateString


    //---------------------------------------------------------------------------------------------- setReportAdapter
    private void setReportAdapter(List<PCustomerInventoryReportModel> items) {
        SimpleReportAdapter<PCustomerInventoryReportModel> adapter = createAdapter();
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(items, null);
        reportView.setAdapter(adapter);
    }
    //---------------------------------------------------------------------------------------------- setReportAdapter


    //---------------------------------------------------------------------------------------------- setFilterSpinner
    private void setFilterSpinner(List<PCustomerInventoryReportModel> items) {
        List<ProductGroupModel> spinnerList = new ProductGroupManager(getContext()).getAll();
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
                filterList(items, item.UniqueId.toString());
            }
            /*VisitorFilter.save(getContext(), item);
            refreshAdapter();*/
        });
    }
    //---------------------------------------------------------------------------------------------- setFilterSpinner


    //---------------------------------------------------------------------------------------------- createAdapter
    private SimpleReportAdapter<PCustomerInventoryReportModel> createAdapter() {
        return new CustomerInventoryReportAdapter(getVaranegarActvity());
    }
    //---------------------------------------------------------------------------------------------- createAdapter


    //---------------------------------------------------------------------------------------------- filterList
    private void filterList(List<PCustomerInventoryReportModel> items, String productGroupNameID) {
        List<PCustomerInventoryReportModel> filter = new ArrayList<>();
        for (PCustomerInventoryReportModel item : items) {
            Log.i("meri", item.productGroupNameID.toUpperCase() + "  /  " + productGroupNameID.toUpperCase());
            if (item.productGroupNameID.toUpperCase().contains(productGroupNameID.toUpperCase()))
                filter.add(item);
        }
        setReportAdapter(filter);
    }
    //---------------------------------------------------------------------------------------------- filterList

}
