package com.varanegar.vaslibrary.ui.calculator.returncalculator;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.returnReason.ReturnReason;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModelRepository;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.UUID;

/**
 * Created by atp on 4/12/2017.
 */

public class ReturnReasonDialog extends CuteAlertDialog {
    SelectionRecyclerAdapter<ReturnReasonModel> selectionRecyclerAdapter;
    RadioButton healthyRadioButton;
    RadioButton wasteRadioButton;

    public interface OnReasonSelected {
        void run(UUID returnTypeId, ReturnReasonModel returnReasonModel);
    }

    public OnReasonSelected onReasonSelected;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_return_reason, viewGroup, true);
        BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.reasons_base_recycler_view);
        selectionRecyclerAdapter =
                new SelectionRecyclerAdapter<>(getVaranegarActvity(), new ReturnReasonModelRepository(), new Query().from(ReturnReason.ReturnReasonTbl), false);
        selectionRecyclerAdapter.refresh();
        recyclerView.setAdapter(selectionRecyclerAdapter);
        healthyRadioButton = (RadioButton) view.findViewById(R.id.healthy_radio_button);
        wasteRadioButton = (RadioButton) view.findViewById(R.id.waste_radio_button);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel AllowReturnDamagedProductSys = sysConfigManager.read(ConfigKey.AllowReturnDamagedProduct, SysConfigManager.cloud);
        if (SysConfigManager.compare(AllowReturnDamagedProductSys, false))
            wasteRadioButton.setVisibility(View.GONE);
        SysConfigModel AllowReturnIntactProductSys = sysConfigManager.read(ConfigKey.AllowReturnIntactProduct, SysConfigManager.cloud);
        if (SysConfigManager.compare(AllowReturnIntactProductSys, false))
            healthyRadioButton.setVisibility(View.GONE);
        if (healthyRadioButton.getVisibility() == View.GONE && wasteRadioButton.getVisibility() == View.GONE) {
            view.findViewById(R.id.return_type_layout).setVisibility(View.GONE);
            healthyRadioButton.setChecked(true);
        }
    }

    @Override
    public void ok() {
        ReturnReasonModel returnReasonModel = null;
        UUID returnTypeId = null;
        if (!healthyRadioButton.isChecked() && !wasteRadioButton.isChecked()) {
            Toast.makeText(getContext(), R.string.please_select_return_type, Toast.LENGTH_SHORT).show();
            return;
        }
        if (healthyRadioButton.isChecked()) {
            returnTypeId = ReturnType.Well;
        } else {
            returnTypeId = ReturnType.Waste;
        }
        if (selectionRecyclerAdapter.getSelectedPositions().size() > 0) {
            returnReasonModel = selectionRecyclerAdapter.get(selectionRecyclerAdapter.getSelectedPositions().get(0));
        } else {
            getVaranegarActvity().showSnackBar(R.string.please_select_a_reason, MainVaranegarActivity.Duration.Short);
        }
        if (returnReasonModel != null && returnTypeId != null) {
            if (onReasonSelected != null)
                onReasonSelected.run(returnTypeId, returnReasonModel);
            dismiss();
        }
    }

    @Override
    public void cancel() {

    }
}
