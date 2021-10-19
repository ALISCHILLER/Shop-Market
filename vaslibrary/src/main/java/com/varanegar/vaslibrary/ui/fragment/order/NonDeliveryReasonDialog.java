package com.varanegar.vaslibrary.ui.fragment.order;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/1/2018.
 */

public class NonDeliveryReasonDialog extends CuteAlertDialog {
    SelectionRecyclerAdapter<NoSaleReasonModel> selectionAdapter;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_select_a_reason_of_no_delivery);
        View view = inflater.inflate(R.layout.fragment_non_order, viewGroup, true);
        BaseRecyclerView nonOrderList = (BaseRecyclerView) view.findViewById(R.id.non_order_list_view);
        NoSaleReasonManager manager = new NoSaleReasonManager(getContext());
        final List<NoSaleReasonModel> noSalesReasons = manager.getNonOrderDeliveryReason();
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
        if (onItemSelected != null)
            onItemSelected.onChanged(noSaleReasonModel.UniqueId);
    }

    @Override
    public void cancel() {

    }

    public OnItemSelected onItemSelected;

    public interface OnItemSelected {
        void onChanged(UUID reasonUniqueId);
    }
}
