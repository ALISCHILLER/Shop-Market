package com.varanegar.framework.util.report.filter.string;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.varanegar.framework.R;
import com.varanegar.framework.util.report.filter.FilterDialog;

/**
 * Created by atp on 12/31/2016.
 */
public class StringFilterDialog extends FilterDialog {
    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setTitle(getString(R.string.filter));
        View view = inflater.inflate(R.layout.fragment_string_filter_dialog, null);
        final EditText filterEditText = (EditText) view.findViewById(R.id.filter_edit_text);
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = filterEditText.getText().toString();
                if (text == null || text.isEmpty()) {
                    filterEditText.setError(getString(R.string.filter_is_mandatory));
                    filterEditText.requestFocus();
                } else {
                    StringFilter filter = new StringFilter();
                    filter.text = text;
                    if (onFilterChange != null) {
                        onFilterChange.run(filter);
                        dismiss();
                    }
                }
            }
        });

        return view;
    }
}
