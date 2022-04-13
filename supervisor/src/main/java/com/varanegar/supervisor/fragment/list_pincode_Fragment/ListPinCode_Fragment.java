package com.varanegar.supervisor.fragment.list_pincode_Fragment;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.DataManager;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_ModelRepository;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPin;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPinModel;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPinModelRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListPinCode_Fragment extends IMainPageFragment {
    private RecyclerView list_request;
    private FloatingActionButton fab;
    private List<CustomerPinModel> customerPinModel;
    private ListPinCodeAdapter  listPinCodeAdapter;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_pin_code_layout
                ,container,false);
        list_request=view.findViewById(R.id.recycler_list_request);
        fab=view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataPinRequest();
        listsetdata();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void getDataPinRequest(){
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = simpleDateFormat.format(now);
        CustomerPinModelRepository pinModelRepository = new CustomerPinModelRepository();
        Query q = new Query();
        q.from(CustomerPin.CustomerPinTbl);
        customerPinModel=pinModelRepository.getItems(q);
    }
    public void listsetdata(){
        listPinCodeAdapter=new ListPinCodeAdapter(getActivity(),customerPinModel);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list_request.setLayoutManager(llm);
        list_request.setAdapter(listPinCodeAdapter);
    }

    public void restData(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.downloading_data));
        progressDialog.show();
        DataManager dataManager=new DataManager(getContext());
        dataManager.getCustomerPin2(new DataManager.Callback() {
            @Override
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        getDataPinRequest();
                        listsetdata();
                    } catch (Exception ignored) {

                    }
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            }
        });
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
