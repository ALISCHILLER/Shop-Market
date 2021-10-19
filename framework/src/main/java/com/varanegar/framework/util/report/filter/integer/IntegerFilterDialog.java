package com.varanegar.framework.util.report.filter.integer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.varanegar.framework.R;
import com.varanegar.framework.util.report.filter.FilterDialog;

/**
 * Created by atp on 12/31/2016.
 */
public class IntegerFilterDialog extends FilterDialog {

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_int_filter_dialog, null);
        final Spinner operatorSpinner = (Spinner) view.findViewById(R.id.operator_spinner);
        IntFilter.IntFilterOperator[] operators = new IntFilter.IntFilterOperator[]{IntFilter.IntFilterOperator.Equals, IntFilter.IntFilterOperator.GreaterThan, IntFilter.IntFilterOperator.LessThan};
        operatorSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, operators));
        final EditText filterEditText = (EditText) view.findViewById(R.id.filter_edit_text);
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterEditText.getText().toString().isEmpty()) {
                    filterEditText.setError(getString(R.string.filter_is_mandatory));
                    filterEditText.requestFocus();
                } else {
                    IntFilter filter = new IntFilter();
                    filter.operator = (IntFilter.IntFilterOperator) operatorSpinner.getSelectedItem();
                    filter.value = Integer.parseInt(filterEditText.getText().toString());
                    if (onFilterChange != null) {
                        dismiss();
                        onFilterChange.run(filter);
                    }
                }
            }
        });
        return view;
    }
}
