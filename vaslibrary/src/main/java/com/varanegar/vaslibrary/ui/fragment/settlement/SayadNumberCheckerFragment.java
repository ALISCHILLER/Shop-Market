package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.print.SelectPrinterDialog;


/**
 * Created by e.hashemzadeh on 10/9/2021.
 */
public class SayadNumberCheckerFragment extends CuteDialog {
    SayadNumberCheckerViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.MatchParent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sayad_number_checker, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(SayadNumberCheckerViewModel.class);
        view.findViewById(R.id.success).setOnClickListener(view1 -> {
            viewModel.setSayadNumberCheckerLiveData(true);
            dismiss();
        });
        view.findViewById(R.id.failure).setOnClickListener(view1 -> {
            viewModel.setSayadNumberCheckerLiveData(false);
            dismiss();
        });
        return view;
    }

}