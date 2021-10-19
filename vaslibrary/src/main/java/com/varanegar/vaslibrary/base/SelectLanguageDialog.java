package com.varanegar.vaslibrary.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by A.Torabi on 1/8/2019.
 */

public class SelectLanguageDialog extends CuteAlertDialog {

    public OnLanguageSelected onLanguageSelected;
    private SelectionRecyclerAdapter<LocalModel> adapter;

    public interface OnLanguageSelected {
        void onSelected(Locale local);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_choose_language);
        View view = inflater.inflate(R.layout.select_language_layout, viewGroup, true);
        BaseRecyclerView recyclerView = view.findViewById(R.id.options_base_recycler_view);
        final List<LocalModel> supportedLanguages = new ArrayList<>();
        supportedLanguages.add(LocalModel.Arabic);
        supportedLanguages.add(LocalModel.English);
        supportedLanguages.add(LocalModel.Persian);
        adapter = new SelectionRecyclerAdapter<LocalModel>(getVaranegarActvity(), supportedLanguages, false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void ok() {
        if (adapter.getSelectedItem() != null) {
            if (onLanguageSelected != null) {
                LocalModel localModel = adapter.getSelectedItem();
                onLanguageSelected.onSelected(localModel.getLocal());
            }
            dismiss();
        }
    }

    @Override
    public void cancel() {

    }
}