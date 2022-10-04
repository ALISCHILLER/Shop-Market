package com.varanegar.vaslibrary.ui.viewholders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atp on 12/11/2016.
 */
public class CustomerSummaryMultipanViewHolder extends BaseViewHolder<CustomerPathViewModel> {
    private final ImageView callImageView;
    private final TextView customerCodeTextView;
    private final TextView customerTelTextView;
    private final TextView customerMobileTextView;
    private final TextView totalTextView;

    public static List<CustomerCallModel> calls;
    private TextView storeNameTextView;
    private TextView customerNameTextView;
    private TextView customerAddressTextView;
    private TextView customerStatusTextView;
    private TextView codenaghsh_text_view,codenaghsh_text;
    private LinearLayout descriptionLayout;
    private TextView descriptionTextView;
    private BackOfficeType backOfficeType;
    private LinearLayout codenaghsh_layout;
    //private ImageUrlViewDialog customerImageView;

    public CustomerSummaryMultipanViewHolder(View itemView,
                                             BaseRecyclerAdapter<CustomerPathViewModel>
                                                     recyclerAdapter,
                                             BackOfficeType backOfficeType) {
        super(itemView, recyclerAdapter, recyclerAdapter.getActivity());
        customerNameTextView = (TextView) itemView.findViewById(R.id.customer_name_text_view);
        customerAddressTextView = (TextView) itemView.findViewById(R.id.customer_address_text_view);
        customerStatusTextView = (TextView) itemView.findViewById(R.id.customer_status_text_view);
        //customerImageView = (ImageUrlViewDialog) itemView.findViewById(R.id.customer_image_view);
        callImageView = (ImageView) itemView.findViewById(R.id.call_image_view);
        storeNameTextView = (TextView) itemView.findViewById(R.id.store_name_text_view);
        customerCodeTextView = (TextView) itemView.findViewById(R.id.customer_code_text_view);
        customerTelTextView = (TextView) itemView.findViewById(R.id.customer_tel_text_view);
        customerMobileTextView = (TextView) itemView.findViewById(R.id.customer_mobile_text_view);
        totalTextView = (TextView) itemView.findViewById(R.id.customer_total_order_text_view);
        codenaghsh_text_view = (TextView) itemView.findViewById(R.id.codenaghsh_text_view);
        codenaghsh_text = (TextView) itemView.findViewById(R.id.codenaghsh_text);
        codenaghsh_layout = (LinearLayout) itemView.findViewById(R.id.codenaghsh_layout);
        descriptionLayout = itemView.findViewById(R.id.description_layout);
        descriptionTextView = itemView.findViewById(R.id.order_description_text_view);
        this.backOfficeType = backOfficeType;
    }

    @Override
    public void bindView(final int position) {

        final CustomerPathViewModel customerModel = recyclerAdapter.get(position);
        customerNameTextView.setText(customerModel.IsNewCustomer ? customerModel.CustomerName
                + " " + getContext().getString(R.string.new_customer) : customerModel.CustomerName);
        customerAddressTextView.setText(customerModel.Address);
        customerCodeTextView.setText(customerModel.CustomerCode);
        customerMobileTextView.setText(customerModel.Mobile);
        customerTelTextView.setText(customerModel.Phone);
        storeNameTextView.setText(customerModel.StoreName);
        String codeNagesh_Str=customerModel.CodeNaghsh;








        if(calls==null)
            calls = new ArrayList();

        List<CustomerCallModel> customerCalls = Linq.findAll(calls,
                new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CustomerId.equals(customerModel.UniqueId);
            }
        });

        setStatus(customerCalls);

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)){
            codenaghsh_layout.setVisibility(View.VISIBLE);
            if (codeNagesh_Str!=null&&!codeNagesh_Str.isEmpty()&&!codeNagesh_Str.equals("")){
                codenaghsh_text_view.setText(customerModel.CodeNaghsh);
                codenaghsh_text.setTextColor(getContext().getResources().getColor(R.color.blue));
            }else if(codeNagesh_Str==null){
                codenaghsh_text_view.setText("");
                codenaghsh_text.setTextColor(getContext().getResources().getColor(R.color.red));
            }
        }

        if (descriptionLayout != null) {
            if (backOfficeType == BackOfficeType.ThirdParty &&
                    VaranegarApplication.is(VaranegarApplication.AppId.Dist) &&
                    customerModel.InvoiceComments != null && !
                    customerModel.InvoiceComments.isEmpty()) {
                descriptionLayout.setVisibility(View.VISIBLE);
                descriptionTextView.setText(customerModel.InvoiceComments);
            } else {
                descriptionLayout.setVisibility(View.GONE);
            }
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAdapter.runItemClickListener(position);
            }
        });
        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customerModel.Mobile));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int result = recyclerAdapter.getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (result == PackageManager.PERMISSION_DENIED)
                        recyclerAdapter.getActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 43);
                    else
                        getContext().startActivity(intent);
                } else
                    getContext().startActivity(intent);
            }
        });
//        int selected = recyclerAdapter.getSelectedPosition();
//        if (selected == position) {
//            TypedValue typedValue = new TypedValue();
//            int[] colorPrimaryLightAttr = new int[]{com.varanegar.framework.R.attr.colorPrimaryLight};
//            int index = 0;
//            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, colorPrimaryLightAttr);
//            int color = a.getColor(index, Color.LTGRAY);
//            a.recycle();
//            itemView.setBackgroundColor(color);
//        } else {
//            if (position % 2 == 1)
//                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light_light));
//            else
//                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
//        }
        totalTextView.setText(HelperMethods.currencyToString(customerModel.TotalOrderAmount));
        if (!customerModel.IsActive) {
            customerNameTextView.setPaintFlags(customerNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            customerAddressTextView.setPaintFlags(customerAddressTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            storeNameTextView.setPaintFlags(storeNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            customerCodeTextView.setPaintFlags(customerCodeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            customerMobileTextView.setPaintFlags(customerMobileTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            customerTelTextView.setPaintFlags(customerTelTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            customerNameTextView.setPaintFlags(customerNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            customerAddressTextView.setPaintFlags(customerAddressTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            storeNameTextView.setPaintFlags(storeNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            customerCodeTextView.setPaintFlags(customerCodeTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            customerMobileTextView.setPaintFlags(customerMobileTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            customerTelTextView.setPaintFlags(customerTelTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void setStatus(List<CustomerCallModel> calls) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        customerStatusTextView.setText(callManager.getName(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor)));
        customerStatusTextView.setBackgroundColor(callManager.getColor(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor)));
        boolean isConfirmed = callManager.isConfirmed(calls);
        if (isConfirmed)
            customerStatusTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
        else
            customerStatusTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}
