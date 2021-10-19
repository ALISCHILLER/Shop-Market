package com.varanegar.framework.base.questionnaire.controls.optionscontrol;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class RadioControl extends OptionsFormControl {

    public RadioControl(@NonNull Context context, @NonNull List<OptionControl> options, @NonNull String title, @NonNull UUID uniqueId) {
        super(context, options, title, uniqueId);
    }

    @Override
    protected SelectionRecyclerAdapter<OptionControl> createAdapter(AppCompatActivity activity) {
        return new SelectionRecyclerAdapter<>(activity, options, false);
    }
}
