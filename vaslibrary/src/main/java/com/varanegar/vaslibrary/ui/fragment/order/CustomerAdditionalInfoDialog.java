package com.varanegar.vaslibrary.ui.fragment.order;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.customer.CustomerBarcodeModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerAdditionalInfoModel;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

public class CustomerAdditionalInfoDialog extends CuteDialogWithToolbar {

    private BaseRecyclerView baseRecyclerView;
    private TextView errorTextView;
    private ProgressBar progressBar;


    @Override
    public void onResume() {
        super.onResume();
        if (Connectivity.isConnected(getContext())) {
            CustomerApi customerApi = new CustomerApi(getContext());
            String customerId = getArguments().getString("CUSTOMER_ID");
            customerApi.runWebRequest(customerApi.getCustomerAdditionalInfo(customerId), new WebCallBack<List<CustomerAdditionalInfoModel>>() {
                @Override
                protected void onFinish() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                protected void onSuccess(List<CustomerAdditionalInfoModel> result, Request request) {
                    BaseRecyclerAdapter<CustomerAdditionalInfoModel> adapter = new BaseRecyclerAdapter<CustomerAdditionalInfoModel>(getVaranegarActvity(), result) {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_addtional_info_row, parent, false);
                            return new BaseViewHolder<CustomerAdditionalInfoModel>(view, this, getContext()) {
                                @Override
                                public void bindView(int position) {
                                    CustomerAdditionalInfoModel item = recyclerAdapter.get(position);
                                    PairedItems pairedItems = itemView.findViewById(R.id.data_paired_items);
                                    pairedItems.setTitle(item.Title);
                                    pairedItems.setValue(item.Value);
                                }
                            };
                        }
                    };
                    baseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    baseRecyclerView.setAdapter(adapter);
                    errorTextView.setVisibility(View.GONE);
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    if (errorTextView != null) {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText(err);
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t.getMessage());
                    if (errorTextView != null) {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText(R.string.network_error);
                    }
                }
            });
        } else if (errorTextView != null) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(R.string.please_connect_to_network);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.customer_additional_fields_layout, container, false);
        baseRecyclerView = view.findViewById(R.id.items_recycler_view);
        errorTextView = view.findViewById(R.id.error_text_view);
        progressBar = view.findViewById(R.id.progress_bar);
        setTitle(R.string.additional_data);
        return view;
    }
}
