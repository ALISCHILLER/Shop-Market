package com.varanegar.framework.base.questionnaire;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class FormAdapter {
    public ArrayList<FormControl> getControls() {
        return controls;
    }

    private ArrayList<FormControl> controls = new ArrayList<>();
    protected AppCompatActivity activity;

    public FormAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void addControl(FormControl control) {
        this.controls.add(control);
    }

    public void removeControl(final UUID controlId) {
        Linq.removeFirst(controls, new Linq.Criteria<FormControl>() {
            @Override
            public boolean run(FormControl item) {
                return item.uniqueId.equals(controlId);
            }
        });
    }

    public int getCount() {
        return controls.size();
    }

    public FormControl getItem(int position) {
        return controls.size() > position && position > 0 ? controls.get(position) : null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return controls.get(position).getView(activity, parent);
    }

    public boolean validate() {
        final boolean[] error = {false};
        Linq.forEach(controls, new Linq.Consumer<FormControl>() {
            @Override
            public void run(FormControl item) {
                String err = item.validate();
                if (err != null) {
                    item.setError(err);
                    error[0] = true;
                } else
                    item.clearError();
            }
        });
        return !error[0];
    }

    public void addControls(List<FormControl> controls) {
        this.controls.addAll(controls);
    }

    public boolean isChanged() {
        return Linq.exists(controls, new Linq.Criteria<FormControl>() {
            @Override
            public boolean run(FormControl item) {
                return item.isChanged();
            }
        });
    }
}
