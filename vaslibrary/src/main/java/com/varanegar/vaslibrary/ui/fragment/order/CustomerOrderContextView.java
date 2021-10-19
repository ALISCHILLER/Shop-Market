package com.varanegar.vaslibrary.ui.fragment.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;

public class CustomerOrderContextView extends ItemContextView<CustomerCallOrderOrderViewModel> {

    CustomerOrderContextView(BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> adapter, Context context) {
        super(adapter, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
        CustomerCallOrderOrderViewModel orderLine = adapter.get(position);
        View view = inflater.inflate(R.layout.customer_order_context_view, viewGroup, false);
        if (orderLine != null) {
            ((TextView) view.findViewById(R.id.product_name_text_view)).setText(orderLine.ProductName);
            ((TextView) view.findViewById(R.id.product_code_text_view)).setText(orderLine.ProductCode);
            String price = orderLine.UnitPrice == null ? getContext().getString(R.string.free) : orderLine.UnitPrice.toString();
            ((TextView) view.findViewById(R.id.product_price_text_view)).setText(price);
            if (orderLine.IsRequestFreeItem)
                view.findViewById(R.id.free_reason_layout).setVisibility(View.VISIBLE);
            else
                view.findViewById(R.id.free_reason_layout).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.free_reason_text_view)).setText(orderLine.FreeReasonName);
        }
        return view;
    }
}
