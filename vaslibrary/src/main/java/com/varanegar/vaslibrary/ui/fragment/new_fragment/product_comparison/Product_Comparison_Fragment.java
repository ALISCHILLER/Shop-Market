package com.varanegar.vaslibrary.ui.fragment.new_fragment.product_comparison;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;

import java.util.List;
import java.util.UUID;

public class Product_Comparison_Fragment extends VaranegarFragment {
    BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> orderAdapter;
    BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> orderOldAdapter;
    private UUID customerId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setArguments(UUID customerId){
        addArgument("9c497998-18e6-4be9-ad80-984fcfb2169c", customerId.toString());
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("eb6d7315-0bd0-420c-bf80-e0257c7b153a", customerId.toString());
    }
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(
                R.layout.layout_product_comparison_fragment, container, false);
        customerId = UUID.fromString(requireArguments()
                .getString("9c497998-18e6-4be9-ad80-984fcfb2169c"));
        initializeTableView();
        return  view;
    }
    private void initializeTableView() {


    }
}
