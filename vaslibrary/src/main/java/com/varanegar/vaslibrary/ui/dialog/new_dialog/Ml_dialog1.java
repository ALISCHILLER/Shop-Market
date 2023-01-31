package com.varanegar.vaslibrary.ui.dialog.new_dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.customerLastBill.CustomerLastBillModel;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ml_dialog1 extends CuteDialogWithToolbar {
    private BaseRecyclerAdapter<CustomerLastBillModel> adapter;
    private BaseRecyclerAdapter<CustomerLastBillModel> adapter1;
    private BaseRecyclerAdapter<CustomerLastBillModel> adapter2;
    private List<CustomerLastBillModel> productModel;
    private List<CustomerLastBillModel> productModel1 =new ArrayList<>();
    private List<CustomerLastBillModel> productModel2=new ArrayList<>();
    private List<CustomerLastBillModel> productModel3=new ArrayList<>();
    private String _customerName;
    private LinearLayout centers_linear,centers_linear1,centers_linear2;

    public void setValues(List<CustomerLastBillModel> ProductModel, String customerName) {
        this.productModel = ProductModel;
        this._customerName = customerName;
    }

    private InsertPinDialog.OnResult onResult;


    public interface OnResult {
        void done();

        void failed(String error);
    }


    public void setOnResult(InsertPinDialog.OnResult onResult) {
        this.onResult = onResult;
    }

    private static class CenterSysConfigViewHolder extends BaseViewHolder<CustomerLastBillModel> {

        public CenterSysConfigViewHolder(View itemView, BaseRecyclerAdapter<CustomerLastBillModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            if (recyclerAdapter.get(position).Type.equals("ZTAN")
                    || recyclerAdapter.get(position).Type.equals("ZTFI")) {

                ((TextView) itemView.findViewById(R.id.product_name_id))
                        .setText(Objects.requireNonNull(recyclerAdapter.get(position)).ProductName + " " +
                                Objects.requireNonNull(recyclerAdapter.get(position)).ProductCode);

                ((TextView) itemView.findViewById(R.id.product_numbr_id))
                        .setText("EA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_EA
                                + "/" + "KA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_KAR
                        );
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerAdapter.runItemClickListener(position);
                    }
                });
            }
        }
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_ml_dialog1, container, false);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText("فاکتور قبلی مشتری:" + _customerName + " ");
        BaseRecyclerView centerRecycler = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view);
        BaseRecyclerView center_recycler_view1 = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view1);
        BaseRecyclerView center_recycler_view2 = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view2);
        LinearLayout centers_linear = (LinearLayout) view.findViewById(R.id.centers_linear);
        LinearLayout centers_linear1 = (LinearLayout) view.findViewById(R.id.centers_linear1);
        LinearLayout centers_linear2 = (LinearLayout) view.findViewById(R.id.centers_linear2);


        if (productModel!=null) {
            for (CustomerLastBillModel lastBillModel : productModel) {
                if (lastBillModel.Type.equals("ZTAN")
                        || lastBillModel.Type.equals("ZTFI")) {
                    productModel1.add(lastBillModel);
                    centers_linear.setVisibility(View.VISIBLE);
                }
                if (lastBillModel.Type.equals("ZREN")
                        || lastBillModel.Type.equals("ZRFI")) {
                    productModel2.add(lastBillModel);
                    centers_linear1.setVisibility(View.VISIBLE);
                }
                if (lastBillModel.TypeSum.equals("X")) {
                    productModel3.add(lastBillModel);
                    centers_linear2.setVisibility(View.VISIBLE);
                }

            }
        }else {
            dismiss();
        }

        adapter = new BaseRecyclerAdapter<CustomerLastBillModel>(getVaranegarActvity(), productModel1) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder(itemView, adapter, getContext());
            }
        };


        adapter1 = new BaseRecyclerAdapter<CustomerLastBillModel>(getVaranegarActvity(), productModel2) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder1(itemView, adapter1, getContext());
            }
        };
        adapter2 = new BaseRecyclerAdapter<CustomerLastBillModel>(getVaranegarActvity(), productModel3) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_center_owners_row,
                        parent, false);
                return new CenterSysConfigViewHolder2(itemView, adapter2, getContext());
            }
        };
        centerRecycler.setAdapter(adapter);
        center_recycler_view1.setAdapter(adapter1);
        center_recycler_view2.setAdapter(adapter2);
        okTv.setOnClickListener(view1 -> {
            dismiss();
            onResult.done();

        });
        return view;
    }


    private class CenterSysConfigViewHolder1 extends BaseViewHolder<CustomerLastBillModel> {

        public CenterSysConfigViewHolder1(View itemView, BaseRecyclerAdapter<CustomerLastBillModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            if (recyclerAdapter.get(position).Type.equals("ZREN")
                    || recyclerAdapter.get(position).Type.equals("ZRFI")) {

                ((TextView) itemView.findViewById(R.id.product_name_id))
                        .setText(Objects.requireNonNull(recyclerAdapter.get(position)).ProductName + " " +
                                Objects.requireNonNull(recyclerAdapter.get(position)).ProductCode);

                ((TextView) itemView.findViewById(R.id.product_numbr_id))
                        .setText("EA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_EA
                                + "/" + "KA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_KAR
                        );
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerAdapter.runItemClickListener(position);
                    }
                });
            }
        }
    }


    private class CenterSysConfigViewHolder2 extends BaseViewHolder<CustomerLastBillModel> {

        public CenterSysConfigViewHolder2(View itemView, BaseRecyclerAdapter<CustomerLastBillModel>
                recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            if (recyclerAdapter.get(position).TypeSum.equals("X")) {

                ((TextView) itemView.findViewById(R.id.product_name_id))
                        .setText(Objects.requireNonNull(recyclerAdapter.get(position)).ProductName + " " +
                                Objects.requireNonNull(recyclerAdapter.get(position)).ProductCode);

                ((TextView) itemView.findViewById(R.id.product_numbr_id))
                        .setText("EA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_SUM_EA
                                + "/" + "KA" + Objects.requireNonNull(recyclerAdapter.get(position)).ProductQTY_SUM_KAR
                        );
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerAdapter.runItemClickListener(position);
                    }
                });
            }
        }
    }
}
