package com.varanegar.supervisor.customreport.orderstatus;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.customreport.orderstatus.model.ColumnHeader;
import com.varanegar.supervisor.customreport.orderstatus.model.IOnExpand;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReport;
import com.varanegar.supervisor.customreport.orderstatus.model.orderStatusModel;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

public class OrderReportFragment extends IMainPageFragment {
    private RecyclerView recycler_report;
    private  OrderReportAdapter adapter;
    private IOnExpand expand;
    private ColumnHeader[] headers;
    private HorizontalScrollView scrollView;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_status_report, container, false);
        recycler_report=view.findViewById(R.id.reports_layout);
     scrollView = view.findViewById(R.id.grid_recycler_header);
        Date d=new Date();
        String endData = DateHelper.toString(d, DateFormat.Date, Locale.getDefault());
        JalaliCalendar calendar = new JalaliCalendar();
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));
        int MONTH = calendar.get(Calendar.MONTH)+1;
        String Startdata=YEAR+"/"+"07"+"/"+"01";
        configure(
                new ColumnHeader[]{
                        new ColumnHeader(50, "id"),
                        new ColumnHeader(50, "name"),
                        new ColumnHeader(50, "address"),});

        List<String> dealersId = new ArrayList<>();
        List<VisitorModel> visitorModelss = new VisitorManager(getContext()).getAll();
//       for (int i=0 ;i<visitorModelss.size();i++){
//            dealersId.add(String.valueOf(visitorModelss.get(i).UniqueId));
//        }
        dealersId.add(String.valueOf(visitorModelss.get(0).UniqueId));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_report.setLayoutManager(layoutManager);
        orderStatusModel orderStatusModel =new orderStatusModel();
        orderStatusModel.setDealersId(dealersId);
        orderStatusModel.setStartdata(Startdata);
        orderStatusModel.setEndDate(endData);
        startProgress(R.string.please_wait, R.string.connecting_to_the_server);
        SupervisorApi supervisorApi=new SupervisorApi(getContext());

        supervisorApi.runWebRequest(supervisorApi.OrderStatusReport(orderStatusModel),
                new WebCallBack<List<OrderStatusReport>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<OrderStatusReport> result, Request request) {
                adapter=new OrderReportAdapter(getContext(), result, new IOnExpand() {
                    @Override
                    public void onClick(OrderStatusReport item,int index) {
                        item.setExpand(!item.isExpand());
                        adapter.notifyItemChanged(index);
                    }
                });
                recycler_report.setAdapter(adapter);
                finishProgress();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                if (isResumed()) {
                    finishProgress();
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {

                if (isResumed()) {
                    finishProgress();
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        Timber.e(t);
                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));

                    }
                }
            }
        });
        return view;
    }


    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
            dialog.show();
        }
    }
    public void configure(ColumnHeader[] headers){
        setupGridHeader(headers);
    }

    private void setupGridHeader(ColumnHeader[] headers) {
        this.headers = headers;

      LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.scrollv_header_detail_emp, scrollView, false);
        int i = 0;

        while (i < this.headers.length){
            view.addView(createTextView(headers[i]));
            i++;
        }

      scrollView.addView(view);
    }

    private TextView createTextView(ColumnHeader header) {
        TextView txt = new TextView(getContext());
        txt.setId(Utils.randomInt());
        txt.setText(header.getHeaderText());
        txt.setTextColor(getResources().getColor(R.color.black));
        txt.setLayoutParams(new ViewGroup.LayoutParams(
                Utils.Conversion.dp(header.getWidth(), getResources()),
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return txt;
    }
}
