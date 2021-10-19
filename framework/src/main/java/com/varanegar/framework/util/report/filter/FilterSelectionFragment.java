package com.varanegar.framework.util.report.filter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.report.filter.date.DateFilterDialog;
import com.varanegar.framework.util.report.filter.integer.IntegerFilterDialog;
import com.varanegar.framework.util.report.filter.string.StringFilterDialog;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by atp on 1/7/2017.
 */
public class FilterSelectionFragment extends PopupFragment {
    public void setFields(@NonNull ArrayList<FilterField> fields) {
        this.fields = fields;
    }

    private Locale locale;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    private ArrayList<FilterField> fields = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters_select_field, container, false);
        ListView listView = (ListView) view.findViewById(R.id.filter_field_selection_list_view);
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, fields);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final FilterField selectedField = (FilterField) adapter.getItem(i);
                FilterDialog filterDialog = null;
                if (selectedField != null) {
                    if (selectedField.type == FieldType.Integer) {
                        filterDialog = new IntegerFilterDialog();
                    } else if (selectedField.type == FieldType.String) {
                        filterDialog = new StringFilterDialog();
                    } else if (selectedField.type == FieldType.Float) {
                        filterDialog = new IntegerFilterDialog();
                    } else if (selectedField.type == FieldType.Date) {
                        DateFilterDialog dateFilterDialog = new DateFilterDialog();
                        dateFilterDialog.setLocale(locale);
                        filterDialog = dateFilterDialog;
                    }
                    if (filterDialog != null) {
                        filterDialog.onFilterChange = new FilterDialog.OnFilterChangeHandler() {
                            @Override
                            public void run(Filter filter) {
                                filter.columnIndex = selectedField.columnIndex;
                                filter.filterIndex = i;
                                VaranegarApplication.getInstance().save("0e34cf19-4c5b-482f-bf73-55841fc442a9", filter);
                                getVaranegarActvity().popFragment();
                            }
                        };
                        filterDialog.show(getVaranegarActvity().getSupportFragmentManager(), "FilterDialog");
                    }
                }
            }
        });

        return view;
    }

}
