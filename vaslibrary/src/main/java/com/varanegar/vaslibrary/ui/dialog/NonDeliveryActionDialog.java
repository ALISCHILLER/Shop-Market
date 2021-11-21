package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/26/2017.
 */

public class NonDeliveryActionDialog extends CuteAlertDialog {

    SelectionRecyclerAdapter<NoSaleReasonModel> selectionAdapter;
    private UUID customerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        customerId = UUID.fromString(bundle.getString("CustomerUniqueId"));
        CustomerManager manager = new CustomerManager(getContext());
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_select_a_reason_of_no_delivery);
        View view = inflater.inflate(R.layout.fragment_non_order, viewGroup, true);
        BaseRecyclerView nonOrderList = (BaseRecyclerView) view.findViewById(R.id.non_order_list_view);
        NoSaleReasonManager manager = new NoSaleReasonManager(getContext());
       // final List<NoSaleReasonModel> noSalesReasons = manager.getNonDeliveryReason();
        final List<NoSaleReasonModel> noSalesReasons = manager.getDistReturnReason();
        selectionAdapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), noSalesReasons, false);
        nonOrderList.setAdapter(selectionAdapter);
    }

    @Override
    public void ok() {
        NoSaleReasonModel noSaleReasonModel = selectionAdapter.getSelectedItem();
        if (noSaleReasonModel == null) {
            Toast.makeText(getContext(), R.string.please_select_a_reason_of_no_delivery, Toast.LENGTH_SHORT).show();
            return;
        }
        getDialog().dismiss();

        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        try {
            CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(getContext());
            List<CustomerCallInvoiceModel> invoiceModels = callInvoiceManager.getCustomerCallInvoices(customerId);
            List<UUID> invoiceIds = Linq.map(invoiceModels, new Linq.Map<CustomerCallInvoiceModel, UUID>() {
                @Override
                public UUID run(CustomerCallInvoiceModel item) {
                    return item.UniqueId;
                }
            });
            customerCallManager.saveCompleteLackOfDelivery(customerId,invoiceIds, noSaleReasonModel.UniqueId);

            Timber.i("successfully update customer status to non-deliver");
            if (onOrderStatusChanged != null)
                onOrderStatusChanged.onChanged();
        } catch (Exception e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void cancel() {

    }

    public OnOrderStatusChanged onOrderStatusChanged;

    public interface OnOrderStatusChanged {
        void onChanged();
    }
}
