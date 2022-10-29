package com.varanegar.vaslibrary.ui.drawer;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.processbutton.iml.SubmitProcessButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.LocaleHelper;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.UserDialogPreferences;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.validation.annotations.Length;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.ManageSpaceActivity;
import com.varanegar.vaslibrary.base.SelectLanguageDialog;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.personnel.ChangePasswordViewModel;
import com.varanegar.vaslibrary.webapi.personnel.UserApi;

import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by A.Torabi on 6/14/2018.
 */

public class UserProfileFragment extends VaranegarFragment implements ValidationListener {
    Validator validator = new Validator();
    @NotEmpty
    public PairedItemsEditable oldPass;
    @NotEmpty
    @Length(min = 4, max = 20)
    public PairedItemsEditable pass1;
    @NotEmpty
    @Length(min = 4, max = 20)
    public PairedItemsEditable pass2;
    private UserModel userModel;
    private SubmitProcessButton btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_layout, container, false);
        ((SimpleToolbar) view.findViewById(R.id.toolbar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().popFragment();
            }
        });
        userModel = UserManager.readFromFile(getContext());
        PairedItems userNamePairedItems = view.findViewById(R.id.user_name_paired_items);
        PairedItems loginDatePairedItems = view.findViewById(R.id.login_date_paired_items);
        userNamePairedItems.setValue(userModel.UserName);
        loginDatePairedItems.setValue(DateHelper.toString(userModel.LoginDate, DateFormat.Complete, Locale.getDefault()));

        oldPass = view.findViewById(R.id.old_pass);
        pass1 = view.findViewById(R.id.pass1);
        pass2 = view.findViewById(R.id.pass2);
        btn = view.findViewById(R.id.submit_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate(UserProfileFragment.this);
            }
        });

        view.findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getVaranegarActvity(), ManageSpaceActivity.class);
                getVaranegarActvity().startActivity(intent);
/*                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.UserDialogPreference, SysConfigManager.cloud);
                UserDialogPreferences.clearPreferences(getContext());
                UserDialogPreferences.setPreferenceAvailability(getContext(), SysConfigManager.compare(sysConfigModel, true));*/
            }
        });


        view.findViewById(R.id.change_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectLanguageDialog dialog = new SelectLanguageDialog();
                dialog.onLanguageSelected = new SelectLanguageDialog.OnLanguageSelected() {
                    @Override
                    public void onSelected(Locale local) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (local != null && activity != null && !activity.isFinishing()) {
                            LocaleHelper.setPreferredLocal(getContext(), local);
                            LocaleHelper.setLocale(getContext(), local.getLanguage());
                            Intent intent = activity.getIntent();
                            getVaranegarActvity().finish();
                            startActivity(intent);
                        }
                    }
                };
                dialog.show(getChildFragmentManager(), "SelectLanguageDialog");
            }
        });
        return view;
    }

    @Override
    public void onValidationSucceeded() {
        if (!pass1.getValue().equals(pass2.getValue())) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.passwords_are_not_equal);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            return;
        }
        ChangePasswordViewModel changePasswordViewModel = new ChangePasswordViewModel();
        changePasswordViewModel.ConfirmPassword = pass2.getValue();
        changePasswordViewModel.NewPassword = pass1.getValue();
        changePasswordViewModel.OldPassword = oldPass.getValue();
        changePasswordViewModel.UserId = userModel.UniqueId;
        UserApi userApi = new UserApi(getContext());
        btn.setProgress(1);
        setEnabled(false);
        userApi.runWebRequest(userApi.changePass(changePasswordViewModel), new WebCallBack<ResponseBody>() {
            @Override
            protected void onFinish() {
                btn.setProgress(0);
                setEnabled(true);
            }

            @Override
            protected void onSuccess(ResponseBody result, Request request) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setMessage(R.string.password_changed);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.setIcon(Icon.Success);
                dialog.show();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                showError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                showError(getString(R.string.connection_to_server_failed));
            }
        });
    }

    private void showError(String error) {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setTitle(R.string.error);
        dialog.setMessage(error);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.setIcon(Icon.Error);
        dialog.show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(R.string.error);
            if (error.getViolation().equals(NotEmptyChecker.class))
                errorMessage = getString(R.string.not_empty);

            if (error.getField() instanceof PairedItemsEditable) {
                ((PairedItemsEditable) error.getField()).setError(errorMessage);
                ((PairedItemsEditable) error.getField()).requestFocus();
            } else
                getVaranegarActvity().showSnackBar(errorMessage, MainVaranegarActivity.Duration.Short);
        }
    }

    private void setEnabled(boolean enabled) {
        btn.setEnabled(enabled);
        pass1.setEnabled(enabled);
        pass2.setEnabled(enabled);
        oldPass.setEnabled(enabled);
    }

}
