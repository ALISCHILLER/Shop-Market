package com.varanegar.vaslibrary.ui.viewholders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;

import java.util.List;

/**
 * Created by atp on 12/11/2016.
 */
public class CustomerSummaryViewHolder extends BaseViewHolder<CustomerPathViewModel> {
    private final List<CustomerCallModel> calls;
    private final TextView telTextView;
    private final TextView mobileTextView;
    private final TextView customerCodeTextView;
    private TextView storeNameTextView;
    private TextView customerNameTextView;
    private TextView customerAddressTextView;
    private TextView customerStatusTextView;
    private LinearLayout descriptionLayout;
    private TextView descriptionTextView;
    private BackOfficeType backOfficeType;
    //private ImageView customerImageView;

    public CustomerSummaryViewHolder(View itemView, BaseRecyclerAdapter<CustomerPathViewModel> recyclerAdapter, BackOfficeType backOfficeType) {
        super(itemView, recyclerAdapter, recyclerAdapter.getActivity());
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        calls = callManager.getItems(new Query().from(CustomerCall.CustomerCallTbl));
        customerNameTextView = (TextView) itemView.findViewById(R.id.customer_name_text_view);
        customerAddressTextView = (TextView) itemView.findViewById(R.id.customer_address_text_view);
        customerStatusTextView = (TextView) itemView.findViewById(R.id.customer_status_text_view);
        //customerImageView = (ImageView) itemView.findViewById(R.id.customer_image_view);
        storeNameTextView = (TextView) itemView.findViewById(R.id.store_name_text_view);
        telTextView = (TextView) itemView.findViewById(R.id.tel_text_view);
        mobileTextView = (TextView) itemView.findViewById(R.id.mobile_text_view);
        customerCodeTextView = (TextView) itemView.findViewById(R.id.customer_code_text_view);
        descriptionLayout = itemView.findViewById(R.id.description_layout);
        descriptionTextView = itemView.findViewById(R.id.order_description_text_view);
        this.backOfficeType = backOfficeType;
    }

    @Override
    public void bindView(final int position) {
        final CustomerPathViewModel customerModel = recyclerAdapter.get(position);
        customerNameTextView.setText(customerModel.CustomerName);
        telTextView.setText(customerModel.Phone);
        mobileTextView.setText(customerModel.Mobile);
        telTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customerModel.Phone));
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
        mobileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        customerAddressTextView.setText(customerModel.Address);
        storeNameTextView.setText(customerModel.StoreName);
        customerCodeTextView.setText("(" + customerModel.CustomerCode + ")");
        List<CustomerCallModel> customerCalls = Linq.findAll(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CustomerId.equals(customerModel.UniqueId);
            }
        });

        setStatus(customerCalls);
        if (descriptionLayout != null) {
            if (backOfficeType == BackOfficeType.ThirdParty && customerModel.Comments != null && !customerModel.Comments.isEmpty()) {
                descriptionLayout.setVisibility(View.VISIBLE);
                descriptionTextView.setText(customerModel.Comments);
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
//        int selected = recyclerAdapter.getSelectedPosition();
//        if (selected == position) {
//            TypedValue typedValue = new TypedValue();
//            int[] colorPrimaryLightAttr = new int[]{com.varanegar.framework.R.attr.colorPrimaryLight};
//            int index = 0;
//            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, colorPrimaryLightAttr);
//            int color = a.getColor(index, Color.LTGRAY);
//            a.recycle();
//            itemView.setBackgroundColor(color);
//        } else
//            itemView.setBackgroundColor(Color.WHITE);
        if (!customerModel.IsActive) {
            customerNameTextView.setPaintFlags(customerNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            customerAddressTextView.setPaintFlags(customerAddressTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            storeNameTextView.setPaintFlags(storeNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
