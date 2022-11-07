package com.varanegar.dist.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.varanegar.dist.DistDrawerAdapter;
import com.varanegar.dist.DistTourReportFragment;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import timber.log.Timber;

public class DistCustomersFragment extends CustomersFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new DistDrawerAdapter(getVaranegarActvity()));
        checkVersionIsUpdated();
    }

    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new DistSendTourFragment();
    }

    @Override
    protected TourReportFragment getProfileFragment() {
        return new DistTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new DistCustomerContentFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return null;
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return null;
    }



    //---------------------------------------------------------------------------------------------- checkVersionIsUpdated
    private void checkVersionIsUpdated() {
        if (getContext() == null)
            return;
        try {
            int currentVersion = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionCode;
            SharedPreferences sharedPreferences = getContext()
                    .getSharedPreferences("ApplicationVersion", Context.MODE_PRIVATE);
            int saveVersion = sharedPreferences.getInt("SaveVersion", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("SaveVersion", currentVersion);
            editor.apply();
            if (currentVersion != saveVersion && saveVersion > 0)
                showDialogNewFeatures();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------- checkVersionIsUpdated


    //---------------------------------------------------------------------------------------------- showDialogNewFeatures
    private void showDialogNewFeatures() {
        if (getContext() == null)
            return;

        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getContext().getAssets().open("newFeature.txt")))) {
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Info);
            dialog.setCancelable(false);
            dialog.setTitle(com.varanegar.vaslibrary.R.string.newFeatures);
            dialog.setMessage(text.toString());
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.iUnderstood, view -> dialog.dismiss());
            dialog.show();
        } catch (IOException e) {
            Timber.e("Error reading file new feature " + e.getMessage());
        }
    }
    //---------------------------------------------------------------------------------------------- showDialogNewFeatures


}
