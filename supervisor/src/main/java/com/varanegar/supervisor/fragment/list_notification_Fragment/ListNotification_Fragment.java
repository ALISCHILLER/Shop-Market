package com.varanegar.supervisor.fragment.list_notification_Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_ModelRepository;
import com.varanegar.supervisor.fragment.menuTools_Fragment.MenuToolsAdapter;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.supervisor.webapi.model_new.send_pincode.PinRequestViewModel;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToParty;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;


public class ListNotification_Fragment  extends IMainPageFragment
        implements ListNotificationAdapter.ItemClickListener {
    private RecyclerView list_request;
    private List<PinRequest_Model> pinRequestModels;
    private ListNotificationAdapter listNotificationAdapter;
    private ProgressDialog progressDialog;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_notifaction_layout
                ,container,false);
        list_request=view.findViewById(R.id.recycler_list_request);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataPinRequest();
        listsetdata();
    }

    public void listsetdata(){
        listNotificationAdapter=new ListNotificationAdapter(getActivity(),pinRequestModels);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list_request.setLayoutManager(llm);
        list_request.setAdapter(listNotificationAdapter);
        listNotificationAdapter.addItemClickListener(this);
    }

    @Override
    public void onItemClick(String status, PinRequest_Model pinRequest_model) {
        if (status.equals("Approve")){
            SupervisorApi supervisorApi=new SupervisorApi(getActivity());
            PinRequestViewModel pinRequestViewModel=new PinRequestViewModel();
            pinRequestViewModel.CustomerId=pinRequest_model.customerId;
            pinRequestViewModel.CustomerCallOrderId=pinRequest_model.customer_call_order;
            pinRequestViewModel.PinType=pinRequest_model.pinType;
            pinRequestViewModel.DealerId=pinRequest_model.dealerId;
            startProgressDialog();
          supervisorApi.runWebRequest(supervisorApi.SendPinrapprove(pinRequestViewModel),
                  new WebCallBack<String>() {
              @Override
              protected void onFinish() {
                  stopProgressDialog();
              }

              @Override
              protected void onSuccess(String result, Request request) {
                  updateDataPinRequest("Visible",pinRequest_model);
              }

              @Override
              protected void onApiFailure(ApiError error, Request request) {
                  String err = WebApiErrorBody.log(error, getContext());
                  if (isResumed()) {
                      showErrorDialog(err);

                  }
              }

              @Override
              protected void onNetworkFailure(Throwable t, Request request) {
                  if (isResumed()) {
                      showErrorDialog(getString(com.varanegar.vaslibrary.R.string.network_error));

                  }
              }
          });
        }else if(status.equals("Reject")){
            updateDataPinRequest("Visible",pinRequest_model);
        }
    }
    private void getDataPinRequest(){

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = simpleDateFormat.format(now);
        PinRequest_ModelRepository pinRequest_modelRepository = new PinRequest_ModelRepository();

        Query q = new Query();

        q.from(PinRequest_.PinRequest_Tbl).whereAnd(Criteria.equals(PinRequest_.date,date));
        pinRequestModels=pinRequest_modelRepository.getItems(q);
    }

    private void updateDataPinRequest(String conditionRequest,PinRequest_Model pinRequest_model){
        PinRequest_ModelRepository pinRequest_modelRepository = new PinRequest_ModelRepository();
        pinRequest_model.Status=conditionRequest;
        pinRequest_modelRepository.insertOrUpdate(pinRequest_model);
        getDataPinRequest();
        listsetdata();
    }

    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
            dialog.show();
        }
    }

    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(com.varanegar.vaslibrary.R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }
}
