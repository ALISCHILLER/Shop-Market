package com.varanegar.vaslibrary.ui.dialog;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.vaslibrary.R;

/**
 * Created by e.hashemzadeh on 20/06/30.
 */

public class VirtualTourDialog extends CuteDialogWithToolbar {

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.virtual_tour_dialog, container, false);
        final PairedItemsEditable tourNo = view.findViewById(R.id.tour_no_paired_items);
        final PairedItemsEditable passWord = view.findViewById(R.id.password_paired_items);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView cancelTv = view.findViewById(R.id.cancel_text_view);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok = true;
                if (tourNo.getValue() == null || tourNo.getValue().equalsIgnoreCase("")) {
                    tourNo.setError(getString(R.string.full_tour_no));
                    ok = false;
                }
                if (passWord.getValue() == null || passWord.getValue().equalsIgnoreCase("")) {
                    passWord.setError(getString(R.string.full_password));
                    ok = false;
                }
                if (ok) {
                    try {
                        if (passWord.getValue().equals("Vn" + getContext().getPackageManager().getPackageInfo(getContext().getApplicationInfo().packageName, 0).versionName)) {
                            tourNoListener listener = (tourNoListener) getTargetFragment();
                            listener.setTourNo(tourNo.getValue());
                            dismiss();
                        } else
                            passWord.setError(getString(R.string.wrong_password));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    public interface tourNoListener {
        void setTourNo(String tourNo);
    }

}
