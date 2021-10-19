package com.varanegar.vaslibrary.ui.fragment.picturecustomer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerManager;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerModel;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/29/2017.
 */

public class NoPictureReasonDialog extends CuteAlertDialog {

    private EditText reasonEditText;
    OnReasonSelected onReasonSelected;

    public interface OnReasonSelected {
        void onDone();
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_picture_dialog, viewGroup, true);
        setTitle(R.string.no_picture_reason);
        reasonEditText = (EditText) view.findViewById(R.id.reason_edit_text);

    }

    @Override
    public void ok() {
        String reason = reasonEditText.getText().toString();
        if (reason.isEmpty()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.please_input_reason);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.show();
        } else {
            UUID uniqueId = UUID.fromString(getArguments().getString("cc550edb-f5e1-45d8-8010-1eef6f9a9bfc"));
            PictureCustomerManager manager = new PictureCustomerManager(getContext());
            PictureCustomerModel pictureCustomerModel = manager.getItem(uniqueId);
            if (pictureCustomerModel != null) {
                pictureCustomerModel.NoPictureReason = reason;
                try {
                    manager.update(pictureCustomerModel);
                    Timber.i("no picture reason selected");
                    dismiss();
                    if (onReasonSelected != null)
                        onReasonSelected.onDone();
                } catch (Exception e) {
                    Timber.e(e);
                    getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
                }
            }
        }
    }

    @Override
    public void cancel() {

    }
}
