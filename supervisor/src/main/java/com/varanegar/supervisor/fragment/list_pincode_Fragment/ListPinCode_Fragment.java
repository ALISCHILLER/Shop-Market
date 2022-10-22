package com.varanegar.supervisor.fragment.list_pincode_Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.DataManager;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPin;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPinModel;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPinModelRepository;

import java.util.List;

public class ListPinCode_Fragment extends IMainPageFragment {

    private RecyclerView recyclerViewPins;
    private FloatingActionButton fab;
    private List<CustomerPinModel> customerPinModel;
    private EditText editTextSearch;


    //---------------------------------------------------------------------------------------------- onCreateContentView
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_pin_code_layout, container, false);
    }
    //---------------------------------------------------------------------------------------------- onCreateContentView


    //---------------------------------------------------------------------------------------------- onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setListeners();
        getAllPing();
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- init
    private void init(@NonNull View view) {
        editTextSearch = view.findViewById(R.id.editTextSearch);
        fab = view.findViewById(R.id.fab);
        recyclerViewPins = view.findViewById(R.id.recycler_list_request);
    }
    //---------------------------------------------------------------------------------------------- init


    //---------------------------------------------------------------------------------------------- setListeners
    private void setListeners() {
        fab.setOnClickListener(v -> restData());
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty())
                    getAllPing();
                else
                    getDataPinFilter(s.toString());
            }
        });
    }
    //---------------------------------------------------------------------------------------------- setListeners


    //---------------------------------------------------------------------------------------------- getDataPinFilter
    private void getDataPinFilter(String search) {
        Query q = new Query();
        q.from(CustomerPin.CustomerPinTbl)
                .whereOr(Criteria.contains(CustomerPin.CustomerName, search))
                .whereOr(Criteria.contains(CustomerPin.CustomerCode, search));
        getDataPinRequest(q);
    }
    //---------------------------------------------------------------------------------------------- getDataPinFilter


    //---------------------------------------------------------------------------------------------- getAllPing
    private void getAllPing() {
        Query q = new Query();
        q.from(CustomerPin.CustomerPinTbl);
        getDataPinRequest(q);
    }
    //---------------------------------------------------------------------------------------------- getAllPing


    //---------------------------------------------------------------------------------------------- getDataPinRequest
    private void getDataPinRequest(Query q) {
        CustomerPinModelRepository pinModelRepository = new CustomerPinModelRepository();
        customerPinModel = pinModelRepository.getItems(q);
        listSetData();
    }
    //---------------------------------------------------------------------------------------------- getDataPinRequest


    //---------------------------------------------------------------------------------------------- listSetData
    public void listSetData() {
        ListPinCodeAdapter listPinCodeAdapter = new ListPinCodeAdapter(getActivity(), customerPinModel);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPins.setLayoutManager(llm);
        recyclerViewPins.setAdapter(listPinCodeAdapter);
    }
    //---------------------------------------------------------------------------------------------- listSetData


    //---------------------------------------------------------------------------------------------- restData
    public void restData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.downloading_data));
        progressDialog.show();
        DataManager dataManager = new DataManager(getContext());
        dataManager.getCustomerPin2(new DataManager.Callback() {
            @Override
            public void onSuccess() {
                if (progressDialog.isShowing()) {
                    try {
                        editTextSearch.setText("");
                        getAllPing();
                        progressDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            }
        });
    }
    //---------------------------------------------------------------------------------------------- restData


    //---------------------------------------------------------------------------------------------- showError
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
    //---------------------------------------------------------------------------------------------- showError
}
