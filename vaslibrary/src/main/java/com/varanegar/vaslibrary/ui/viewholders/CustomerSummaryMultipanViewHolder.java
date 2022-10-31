package com.varanegar.vaslibrary.ui.viewholders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
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
import com.varanegar.vaslibrary.manager.customer.CustomerActivityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;

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
    private final TextView storeNameTextView;
    private final TextView customerNameTextView;
    private final TextView customerAddressTextView;
    private final TextView customerStatusTextView;
    private final TextView customerGroupTextView;
    private final TextView codenaghsh_text_view;
    private final TextView codenaghsh_text;
    private final LinearLayout descriptionLayout;
    private final TextView descriptionTextView;
    private final BackOfficeType backOfficeType;
    private final LinearLayout codenaghsh_layout;
    private final TextView customerLevelTextView;
    private final TextView customerActivityTextView;
    //private ImageUrlViewDialog customerImageView;

    //---------------------------------------------------------------------------------------------- CustomerSummaryMultipanViewHolder
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
        customerLevelTextView = itemView.findViewById(R.id.customer_level_text_view);
        customerActivityTextView = itemView.findViewById(R.id.customer_activity_text_view);
        customerGroupTextView = itemView.findViewById(R.id.customer_group_text_view);
        this.backOfficeType = backOfficeType;
    }
    //---------------------------------------------------------------------------------------------- CustomerSummaryMultipanViewHolder


    //---------------------------------------------------------------------------------------------- bindView
    @Override
    public void bindView(final int position) {

        String codeNagesh_Str;
        final CustomerPathViewModel customerModel = recyclerAdapter.get(position);
        if (customerModel == null)
            return;

        customerNameTextView.setText(customerModel.IsNewCustomer ? customerModel.CustomerName
                + " " + getContext().getString(R.string.new_customer) : customerModel.CustomerName);
        customerAddressTextView.setText(customerModel.Address);
        customerCodeTextView.setText(customerModel.CustomerCode);
        customerMobileTextView.setText(customerModel.Mobile);
        customerTelTextView.setText(customerModel.Phone);
        storeNameTextView.setText(customerModel.StoreName);
        codeNagesh_Str = customerModel.CodeNaghsh;


        if (calls == null)
            calls = new ArrayList<>();

        List<CustomerCallModel> customerCalls = Linq.findAll(calls,
                item -> item.CustomerId.equals(customerModel.UniqueId));

        setStatus(customerCalls);

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            codenaghsh_layout.setVisibility(View.VISIBLE);
            if (codeNagesh_Str != null && !codeNagesh_Str.isEmpty()) {
                codenaghsh_text_view.setText(customerModel.CodeNaghsh);
                codenaghsh_text.setTextColor(getContext().getResources().getColor(R.color.blue));
            } else if (codeNagesh_Str == null) {
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
        itemView.setOnClickListener(view -> recyclerAdapter.runItemClickListener(position));

        callImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customerModel.Mobile));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int result = recyclerAdapter.getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE);
                if (result == PackageManager.PERMISSION_DENIED)
                    recyclerAdapter.getActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 43);
                else
                    getContext().startActivity(intent);
            } else
                getContext().startActivity(intent);
        });
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

        setCustomerLevelAndCategoryAndActivity(customerModel);
    }
    //---------------------------------------------------------------------------------------------- bindView


    //---------------------------------------------------------------------------------------------- setStatus
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
    //---------------------------------------------------------------------------------------------- setStatus


    //---------------------------------------------------------------------------------------------- setCustomerLevelAndCategoryAndActivity
    private void setCustomerLevelAndCategoryAndActivity(CustomerPathViewModel customerModel) {
        CustomerLevelManager manager = new CustomerLevelManager(getContext());
        CustomerLevelModel model = manager.getItem(customerModel.CustomerLevelId);
        if (model != null)
            customerLevelTextView.setText(model.CustomerLevelName);
        customerGroupTextView.setText(customerModel.CustomerCategoryName);

        CustomerActivityManager activityManager = new CustomerActivityManager(getContext());
        CustomerActivityModel activityModel = activityManager.getItem(customerModel.CustomerActivityId);
        if (activityModel != null)
            customerActivityTextView.setText(activityModel.toString());
    }
    //---------------------------------------------------------------------------------------------- setCustomerLevelAndCategoryAndActivity


}
