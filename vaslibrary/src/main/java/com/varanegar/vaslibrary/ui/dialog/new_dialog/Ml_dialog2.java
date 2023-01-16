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

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.List;

public class Ml_dialog2 extends CuteDialogWithToolbar {
    private View view;
    private BaseRecyclerAdapter<CustomerXMounthSaleReportModel> adapter;
    private List<CustomerXMounthSaleReportModel> productModel;
    CustomerSumMoneyAndWeightReportModel _sumMoneyAndWeightReportModels;
    private String _customerName;
    private String _dataSaleOfDays;
    private long _numberOfDays;
    public void setValues(
            List<CustomerXMounthSaleReportModel> ProductModel
            ,CustomerSumMoneyAndWeightReportModel sumMoneyAndWeightReportModels,
            String CustomerName,String dataSaleOfDays,long numberOfDays)
    {
        this.productModel=ProductModel;
        this._sumMoneyAndWeightReportModels=sumMoneyAndWeightReportModels;
        this._customerName=CustomerName;
        this._dataSaleOfDays=dataSaleOfDays;
        this._numberOfDays=numberOfDays;

    }
    private InsertPinDialog.OnResult onResult;


    public interface OnResult {
        void done() ;
        void failed(String error);
    }


    public void setOnResult(InsertPinDialog.OnResult onResult){
        this.onResult = onResult;
    }
    private class CenterSysConfigViewHolder extends BaseViewHolder<CustomerXMounthSaleReportModel> {

        public CenterSysConfigViewHolder(View itemView, BaseRecyclerAdapter<CustomerXMounthSaleReportModel>
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
        TextView txt_weight = view.findViewById(R.id.txt_weight);
        TextView txt_money = view.findViewById(R.id.txt_money);
        TextView txt_numberOfDays = view.findViewById(R.id.txt_numberOfDays);
        TextView text_title = view.findViewById(R.id.text_title);
        LinearLayout centers_linear = view.findViewById(R.id.centers_linear);

        if (productModel!=null &&productModel.size()==0) {
            text_title.setVisibility(View.GONE);
            centers_linear.setVisibility(View.GONE);
        }
        if(productModel!=null &&productModel.size()>0) {
            text_title.setText("کالا خریداری شده سه ماه قبلی مشتری تعداد کالا :"+""+productModel.size());
        }
        if (_sumMoneyAndWeightReportModels!=null) {
            String money= HelperMethods.currencyToString(Currency.valueOf(Double.parseDouble(_sumMoneyAndWeightReportModels.Money_Sum)));
            String weight= HelperMethods.currencyToString(Currency.valueOf(Double.parseDouble(_sumMoneyAndWeightReportModels.Weight_Sum)));
            txt_money.setText("متوسط خربد سه ماه ریال:" +money);
            txt_weight.setText("متوسط خربد سه ماه وزن:"+weight);
        }else {
            txt_money.setVisibility(View.GONE);
            txt_weight.setVisibility(View.GONE);
        }

        if (_numberOfDays > 0 && _numberOfDays > 15) {
            txt_numberOfDays.setText(" " + _customerName + " "
                    + "بیشتر از" + _numberOfDays + " روز خرید نکرده است تاریخ آخرین خرید " + _dataSaleOfDays);
        }else {
            txt_numberOfDays.setVisibility(View.GONE);
        }
        BaseRecyclerView centerRecycler = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view);
        adapter = new BaseRecyclerAdapter<CustomerXMounthSaleReportModel>(getVaranegarActvity(), productModel) {
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