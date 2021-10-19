package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 4/3/2017.
 */

public class ReturnTypeDialog extends CuteAlertDialog {
    RadioButton withRefRadioButton;
    RadioButton withoutRefRadioButton;
    RadioButton withRefFromRequestRadioButton;
    RadioButton withoutRefFromRequestRadioButton;
    RadioButton withRefReplacementRegistrationRadioButton;
    RadioButton withoutRefReplacementRegistrationRadioButton;
    boolean withRef = false;
    boolean withoutRef = false;
    boolean withRefFromRequest = false;
    boolean withoutRefFromRequest = false;
    boolean WithoutRefReplacementRegistration = false;
    boolean WithRefReplacementRegistration = false;
    public OnTypeSelected onTypeSelected;
    private String customerId;

    public interface OnTypeSelected {
        void run(UUID returnType, boolean isFromRequest);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            customerId = bundle.getString("f4c5f58c-c422-4186-b5d0-5693de811403", null);
        getTypes();
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(getString(R.string.choose_return_type));
        View view = inflater.inflate(R.layout.dialog_return_type, viewGroup);
        withRefRadioButton = (RadioButton) view.findViewById(R.id.with_reference);
        withoutRefRadioButton = (RadioButton) view.findViewById(R.id.without_reference);
        withRefFromRequestRadioButton = (RadioButton) view.findViewById(R.id.with_reference_from_request);
        withoutRefFromRequestRadioButton = (RadioButton) view.findViewById(R.id.without_reference_from_request);
        withoutRefReplacementRegistrationRadioButton = (RadioButton) view.findViewById(R.id.without_reference_replacement_registration);
        withRefReplacementRegistrationRadioButton = (RadioButton) view.findViewById(R.id.with_reference_replacement_registration);
        if (withRef)
            withRefRadioButton.setVisibility(View.VISIBLE);
        if (withoutRef)
            withoutRefRadioButton.setVisibility(View.VISIBLE);
        if (withRefFromRequest)
            withRefFromRequestRadioButton.setVisibility(View.VISIBLE);
        if (withoutRefFromRequest)
            withoutRefFromRequestRadioButton.setVisibility(View.VISIBLE);
        if (WithoutRefReplacementRegistration)
            withoutRefReplacementRegistrationRadioButton.setVisibility(View.VISIBLE);
        if (WithRefReplacementRegistration)
            withRefReplacementRegistrationRadioButton.setVisibility(View.VISIBLE);

    }


    @Override
    public void ok() {
        if (!withoutRefRadioButton.isChecked() && !withRefRadioButton.isChecked() && !withRefFromRequestRadioButton.isChecked() && !withoutRefFromRequestRadioButton.isChecked() && !withoutRefReplacementRegistrationRadioButton.isChecked() && !withRefReplacementRegistrationRadioButton.isChecked()) {
            Toast.makeText(getContext(), R.string.please_select_return_type, Toast.LENGTH_SHORT).show();
            return;
        }
        if (onTypeSelected != null) {
            if (withoutRefRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithoutRef, false);
                dismiss();
            } else if (withRefRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithRef, false);
                dismiss();
            } else if (withRefFromRequestRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithRef, true);
                dismiss();
            } else if (withoutRefFromRequestRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithoutRef, true);
                dismiss();
            } else if (withoutRefReplacementRegistrationRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithoutRefReplacementRegistration, true);
                dismiss();
            } else if (withRefReplacementRegistrationRadioButton.isChecked()) {
                onTypeSelected.run(ReturnType.WithRefReplacementRegistration, true);
                dismiss();
            }
        }
    }

    @Override
    public void cancel() {
    }

    void getTypes() {
        CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getActivity());
        List<UUID> returnTypes = callReturnManager.getEnabledReturnTypes(customerId != null ? UUID.fromString(customerId) : null);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel config = sysConfigManager.read(ConfigKey.ScientificVisit, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.HotSales) || !SysConfigManager.compare(config, true))
            withRef = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRef);
                }
            });
        withoutRef = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
            @Override
            public boolean run(UUID item) {
                return item.equals(ReturnType.WithoutRef);
            }
        });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.HotSales) || !SysConfigManager.compare(config, true))
            withRefFromRequest = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRefFromRequest);
                }
            });
        withoutRefFromRequest = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
            @Override
            public boolean run(UUID item) {
                return item.equals(ReturnType.WithoutRefFromRequest);
            }
        });
        WithoutRefReplacementRegistration = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
            @Override
            public boolean run(UUID item) {
                return item.equals(ReturnType.WithoutRefReplacementRegistration);
            }
        });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.HotSales) || !SysConfigManager.compare(config, true))
            WithRefReplacementRegistration = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRefReplacementRegistration);
                }
            });
    }
}
