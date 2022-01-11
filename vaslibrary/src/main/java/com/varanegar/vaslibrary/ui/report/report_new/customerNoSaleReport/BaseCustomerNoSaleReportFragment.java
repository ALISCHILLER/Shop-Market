package com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;

import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.ui.report.review.BaseReviewReportFragment;
import com.varanegar.vaslibrary.util.multispinnerfilter.KeyPairBoolData;
import com.varanegar.vaslibrary.util.multispinnerfilter.MultiSpinnerListener;
import com.varanegar.vaslibrary.util.multispinnerfilter.MultiSpinnerSearch;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;

public abstract class BaseCustomerNoSaleReportFragment <T extends CustomerModel> extends VaranegarFragment {

    private ReportView reportView;
    private ProgressDialog progressDialog;
    private Date startDate;
    private Date endDate;
    private PairedItems startDatePairedItems;
    private PairedItems endDatePairedItems;
    private PairedItemsSpinner<String> product_group_spinner;
    protected BaseReviewReportFragment.OnAdapterCallback onAdapterCallback;

    private List<String>  product_group=new ArrayList<>();

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

    protected String getStartDateString() {
        Date d=new Date();
      //  String date = DateHelper.toString(d, DateFormat.Date, Locale.getDefault());
        JalaliCalendar calendar = new JalaliCalendar();
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));
        int MONTH = calendar.get(Calendar.MONTH)+1;
        String data=YEAR+"/"+MONTH+"/"+"01";
        return data;
    }
    protected List<String> getProduct_group() {
        return product_group;
    }
    protected String getEndDateString() {
        Date d=new Date();
        String date = DateHelper.toString(d, DateFormat.Date, Locale.getDefault());

        return date;
    }

    protected Date getStartDate() {
        return startDate == null ? DateHelper.Min : startDate;
    }

    protected Date getEndDate() {
        return endDate == null ? new Date() : endDate;
    }

    protected abstract Call<List<String>> reportApi();

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
        View view = inflater.inflate(R.layout.fragment_customer_no_sale_report, container, false);

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAdapter();
            }
        });



      ///  product_group_spinner=view.findViewById(R.id.product_group_spinner);



        ProductGroupManager ProductGroupManager = new ProductGroupManager(getContext());
        List<ProductGroupModel> catalogModels = ProductGroupManager.getParentItems(ProductType.isForReturn);


        /**
         *لیست ویزیتورها
         */
        MultiSpinnerSearch multiSelectSpinnerWithSearch = view.findViewById(R.id.multipleItemSelectionSpinnerr);

        // Pass true If you want searchView above the list. Otherwise false. default = true.
        multiSelectSpinnerWithSearch.setSearchEnabled(true);
        multiSelectSpinnerWithSearch.setHintText("لیست کالاها");
        //A text that will display in clear text button
        multiSelectSpinnerWithSearch.setClearText("پاک کردن لیست");
        // A text that will display in search hint.
        multiSelectSpinnerWithSearch.setSearchHint("جستجو");
        // Set text that will display when search result not found...
        multiSelectSpinnerWithSearch.setEmptyTitle("Not Data Found!");
        // If you will set the limit, this button will not display automatically.
        multiSelectSpinnerWithSearch.setShowSelectAllButton(true);
        final List<KeyPairBoolData> listArray1 = new ArrayList<>();
        List<String> list =new ArrayList<>();

        for (int i=0;i<catalogModels.size();i++){
            //list.add(catalogModels.get(i).ProductGroupName);

            list.clear();
            product_group.clear();
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(catalogModels.get(i).UniqueId);
            h.setName(catalogModels.get(i).ProductGroupName);
            h.setSelected(false);
            listArray1.add(h);

        }
        multiSelectSpinnerWithSearch.setItems(listArray1, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> selectedItems) {
                //The followings are selected items.
                for (int i = 0; i < selectedItems.size(); i++) {
                    list.add(String.valueOf(selectedItems.get(i).getId()));
                    product_group.add(String.valueOf(selectedItems.get(i).getId()));
                }
            }

            @Override
            public void onClear() {

            }
        });



//        product_group_spinner.setup(getChildFragmentManager(), list, new SearchBox.SearchMethod<String>() {
//            @Override
//            public boolean onSearch(String item, String text) {
//                String searchKey = VasHelperMethods.persian2Arabic(text);
//                return item.contains(searchKey);
//            }
//        });
//
//        product_group_spinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(int position, String item) {
//                String data = String.valueOf(catalogModels.get(position).UniqueId);
//                if (data != null) {
//                    product_group.add(data);
//                }
//            }
//        });

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
//        if ((getEndDate().getTime() / 1000) - (getStartDate().getTime() / 1000) > 3600 * 24 * 30) {
//            showErrorDialog(getString(R.string.time_span_should_be_smaller_than_a_month));
//            return;
//        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getContext().getString(R.string.downloading_data));
        progressDialog.show();
        ReportApi invoiceReportApi = new ReportApi(getContext());
        invoiceReportApi.runWebRequest(reportApi(), new WebCallBack<List<String>>() {

            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<String> result, Request request) {
                Log.e("report", String.valueOf(result));
                MainVaranegarActivity activity = getVaranegarActvity();
                List<CustomerModel> customerModels = new CustomerManager(getContext()).getAll();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    adapter = createAdapter();
                    if (result.size()>0) {
                        List<T> list_no_sale = null;
                        for (String list : result) {
                            for (CustomerModel customerModel : customerModels) {
                                if (list.equals(customerModel.UniqueId)) {
                                    list_no_sale.add((T) customerModel);
                                }
                            }
                        }
                        adapter.create((List<T>) list_no_sale, null);
                    }
                    adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                    if (result.size()<=0) {
                        adapter.create((List<T>) customerModels, null);
                    }
                    reportView.setAdapter(adapter);
                    if (onAdapterCallback != null)
                        onAdapterCallback.onSuccess();
                    dismissProgressDialog();
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
                    dismissProgressDialog();
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    showErrorDialog(getString(R.string.connection_to_server_failed));
                    if (onAdapterCallback != null)
                        onAdapterCallback.onFailure();
                    dismissProgressDialog();
                }
            }
        });
    }

    protected abstract String getTitle();

    protected abstract String isEnabled();

}