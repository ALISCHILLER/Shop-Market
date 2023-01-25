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
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.List;
import java.util.Objects;

public class Ml_dialog1 extends CuteDialogWithToolbar {
    private BaseRecyclerAdapter<CustomerXMounthSaleReportModel> adapter;
    private BaseRecyclerAdapter<CustomerXMounthSaleReportModel> adapter1;
    private BaseRecyclerAdapter<CustomerXMounthSaleReportModel> adapter2;
    private List<CustomerXMounthSaleReportModel> productModel;
    private String _customerName;
    public void setValues(List<CustomerXMounthSaleReportModel> ProductModel,String customerName) {
        this.productModel=ProductModel;
        this._customerName=customerName;
    }
    private InsertPinDialog.OnResult onResult;


    public interface OnResult {
        void done() ;
        void failed(String error);
    }


    public void setOnResult(InsertPinDialog.OnResult onResult){
        this.onResult = onResult;
    }
    private static class CenterSysConfigViewHolder extends BaseViewHolder<CustomerXMounthSaleReportModel> {

        public CenterSysConfigViewHolder(View itemView, BaseRecyclerAdapter<CustomerXMounthSaleReportModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(Objects.requireNonNull(recyclerAdapter.get(position)).ProductName+" "+
                            Objects.requireNonNull(recyclerAdapter.get(position)).ProductCode);

            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(Objects.requireNonNull(recyclerAdapter.get(position)).ProductName+" "+
                            Objects.requireNonNull(recyclerAdapter.get(position)).ProductCode);
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

        View view = inflater.inflate(R.layout.layout_ml_dialog1, container, false);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText("کالا های خریداری نشده این فاکتور براساس فاکتورهای سه ماه قبلی "+" "
                +_customerName+" "+"تعداد کالا:"+productModel.size());
        BaseRecyclerView centerRecycler = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view);
        BaseRecyclerView center_recycler_view1 = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view1);
        BaseRecyclerView center_recycler_view2 = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view2);
        adapter = new BaseRecyclerAdapter<CustomerXMounthSaleReportModel>(getVaranegarActvity(), productModel) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder(itemView, adapter, getContext());
            }
        };



        adapter1 = new BaseRecyclerAdapter<CustomerXMounthSaleReportModel>(getVaranegarActvity(), productModel) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder1(itemView, adapter, getContext());
            }
        };
        adapter2 = new BaseRecyclerAdapter<CustomerXMounthSaleReportModel>(getVaranegarActvity(), productModel) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder2(itemView, adapter, getContext());
            }
        };
        centerRecycler.setAdapter(adapter);
        center_recycler_view1.setAdapter(adapter1);
        center_recycler_view2.setAdapter(adapter2);
        okTv.setOnClickListener(view1 -> {
            onResult.done();
            dismiss();
        });
        return view;
    }



    private class CenterSysConfigViewHolder1 extends BaseViewHolder<CustomerXMounthSaleReportModel> {

        public CenterSysConfigViewHolder1(View itemView, BaseRecyclerAdapter<CustomerXMounthSaleReportModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(recyclerAdapter.get(position).ProductName+" "+recyclerAdapter.get(position).ProductCode);

            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(recyclerAdapter.get(position).ProductName+" "+recyclerAdapter.get(position).ProductCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerAdapter.runItemClickListener(position);
                }
            });
        }
    }


    private class CenterSysConfigViewHolder2 extends BaseViewHolder<CustomerXMounthSaleReportModel> {

        public CenterSysConfigViewHolder2(View itemView, BaseRecyclerAdapter<CustomerXMounthSaleReportModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(recyclerAdapter.get(position).ProductName+" "+recyclerAdapter.get(position).ProductCode);

            ((TextView) itemView.findViewById(R.id.product_name_id))
                    .setText(recyclerAdapter.get(position).ProductName+" "+recyclerAdapter.get(position).ProductCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerAdapter.runItemClickListener(position);
                }
            });
        }
    }
}
