package com.varanegar.vaslibrary.ui.dialog.new_dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.List;

public class Ml_dialog2 extends CuteDialogWithToolbar {
    private View view;
    private BaseRecyclerAdapter<CustomerGroupSimilarProductsalesReportModel> adapter;
    private List<CustomerGroupSimilarProductsalesReportModel> productModel;
    CustomerSumMoneyAndWeightReportModel _sumMoneyAndWeightReportModels;
    public void setValues(
            List<CustomerGroupSimilarProductsalesReportModel> ProductModel
            ,CustomerSumMoneyAndWeightReportModel sumMoneyAndWeightReportModels
    ) {
        this.productModel=ProductModel;
        this._sumMoneyAndWeightReportModels=sumMoneyAndWeightReportModels;
    }
    private InsertPinDialog.OnResult onResult;


    public interface OnResult {
        void done() ;
        void failed(String error);
    }


    public void setOnResult(InsertPinDialog.OnResult onResult){
        this.onResult = onResult;
    }
    private class CenterSysConfigViewHolder extends BaseViewHolder<CustomerGroupSimilarProductsalesReportModel> {

        public CenterSysConfigViewHolder(View itemView, BaseRecyclerAdapter<CustomerGroupSimilarProductsalesReportModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            ((TextView) itemView.findViewById(R.id.center_name_id))
                    .setText(recyclerAdapter.get(position).ProductName+" "+recyclerAdapter.get(position).ProductCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerAdapter.runItemClickListener(position);
                }
            });
        }
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_ml_dialog2, container, false);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        BaseRecyclerView centerRecycler = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view);
        adapter = new BaseRecyclerAdapter<CustomerGroupSimilarProductsalesReportModel>(getVaranegarActvity(), productModel) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.center_owners_row,
                        parent, false);
                return new Ml_dialog2.CenterSysConfigViewHolder(itemView, adapter, getContext());
            }
        };
        centerRecycler.setAdapter(adapter);
        okTv.setOnClickListener(view1 -> {
            onResult.done();
            dismiss();
        });
        return view;
    }
}