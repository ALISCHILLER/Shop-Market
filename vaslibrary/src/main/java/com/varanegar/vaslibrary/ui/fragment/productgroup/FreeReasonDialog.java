package com.varanegar.vaslibrary.ui.fragment.productgroup;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;

/**
 * Created by A.Torabi on 7/26/2017.
 */

public class FreeReasonDialog extends CuteAlertDialog {
    SelectionRecyclerAdapter<FreeReasonModel> adapter;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_select_a_free_reason);
        View view = inflater.inflate(R.layout.dialog_free_reason, viewGroup, true);
        BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.free_reason_recycler_view);
        adapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), new FreeReasonManager(getContext()).getAll(), false);
        recyclerView.setAdapter(adapter);
    }

    public interface OnReasonSelected {
        void run(FreeReasonModel freeReasonModel);
    }

    public OnReasonSelected onReasonSelected;

    @Override
    public void ok() {
        if (adapter.getSelectedItem() != null) {
            if (onReasonSelected != null)
                onReasonSelected.run(adapter.getSelectedItem());
            dismiss();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setPositiveButton(R.string.ok, null)
                    .setTitle(R.string.error)
                    .setMessage(R.string.please_select_a_free_reason);
            builder.show();
        }

    }

    @Override
    public void cancel() {

    }
}
