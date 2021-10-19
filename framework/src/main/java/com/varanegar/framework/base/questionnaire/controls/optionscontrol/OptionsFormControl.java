package com.varanegar.framework.base.questionnaire.controls.optionscontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public abstract class OptionsFormControl extends FormControl {
    public List<OptionControl> options;
    private TextView errorTextView;
    private boolean isChanged;

    @Override
    public boolean isValueChanged() {
        return isChanged;
    }

    public OptionsFormControl(@NonNull Context context, @NonNull List<OptionControl> options, @NonNull String title, @NonNull UUID uniqueId) {
        super(context, title, uniqueId);
        this.options = options;
    }

    @Override
    protected void onCreateContentView(@NonNull AppCompatActivity activity, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_control_options, parent, true);
        final SelectionRecyclerAdapter<OptionControl> selectionRecyclerAdapter = createAdapter(activity);
        for (int i = 0; i < options.size(); i++) {
            OptionControl option = options.get(i);
            if (option.selected) {
                selectionRecyclerAdapter.select(i);
            }
        }
        selectionRecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<OptionControl>() {
            @Override
            public void run(int position) {
                isChanged = true;
                Linq.forEach(options, new Linq.Consumer<OptionControl>() {
                    @Override
                    public void run(OptionControl item) {
                        item.selected = false;
                    }
                });
                for (int p :
                        selectionRecyclerAdapter.getSelectedPositions()) {
                    options.get(p).selected = true;
                }
                if (getOptionsAttachmentStatus())
                    showAttachmentButton();
                else
                    hideAttachmentButton();
            }
        });
        BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.options_recycler_view);
        recyclerView.setAdapter(selectionRecyclerAdapter);
        errorTextView = (TextView) view.findViewById(R.id.error_text_view);
    }

    protected boolean getOptionsAttachmentStatus() {
        for (OptionControl option :
                options) {
            if (option.selected && option.hasAttachment) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setError(String err) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(err);
    }

    @Override
    public void clearError() {
        errorTextView.setText("");
        errorTextView.setVisibility(View.GONE);
    }

    protected abstract SelectionRecyclerAdapter<OptionControl> createAdapter(AppCompatActivity activity);

    @Override
    public void deserializeValue(@Nullable String value) {
        if (value == null)
            return;
        Gson gson = new GsonBuilder().create();
        List<OptionControl> controls =
                gson.fromJson(value, new TypeToken<List<OptionControl>>() {
                }.getType());
        if (controls == null)
            return;
        for (final OptionControl control :
                controls) {
            OptionControl option = Linq.findFirst(this.options, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.name.equals(control.name);
                }
            });
            if (option != null)
                option.selected = control.selected;
        }

    }

    @Nullable
    @Override
    public String serializeValue() {
        Gson gson = new GsonBuilder().create();
        String value = gson.toJson(options);
        return value;
    }
}
