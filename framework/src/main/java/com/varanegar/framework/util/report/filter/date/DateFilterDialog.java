package com.varanegar.framework.util.report.filter.date;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.varanegar.framework.R;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.framework.util.report.filter.FilterDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by atp on 3/5/2017.
 */

public class DateFilterDialog extends FilterDialog {
    TextView dateTextView;
    DateFilter dateFilter;
    Spinner operatorSpinner;
    private Locale locale;
    public void setLocale(Locale locale){
        this.locale = locale;
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_filter_diaog, null);
        operatorSpinner = (Spinner) view.findViewById(R.id.operator_spinner);
        DateFilter.DateFilterOperator[] operators = new DateFilter.DateFilterOperator[]{DateFilter.DateFilterOperator.Equals, DateFilter.DateFilterOperator.After, DateFilter.DateFilterOperator.Before};
        operatorSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, operators));
        dateTextView = (TextView) view.findViewById(R.id.date_text_view);
        Calendar calendar;
        dateFilter = new DateFilter();
        if (locale == null)
            locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (language.equals("fa"))
            calendar = new JalaliCalendar();
        else
            calendar = new GregorianCalendar();
        dateFilter.value = calendar.getTime();
        dateTextView.setText(DateHelper.toString(calendar, DateFormat.Date));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language.equals("fa")) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog();
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            processDate(new JalaliCalendar(year, monthOfYear, dayOfMonth));
                        }
                    });
                    datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
                } else {
                    DatePickerFragment datePickerDialog = new DatePickerFragment();
                    datePickerDialog.onDateSet = new DatePickerFragment.OnDateSet() {
                        @Override
                        public void run(int year, int month, int day) {
                            processDate(new GregorianCalendar(year, month, day));
                        }
                    };
                    datePickerDialog.show(getFragmentManager(), "timePicker");
                }
            }
        });

        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter.operator = (DateFilter.DateFilterOperator) operatorSpinner.getSelectedItem();
                if (onFilterChange != null) {
                    dismiss();
                    onFilterChange.run(dateFilter);
                }
            }
        });
        return view;
    }

    void processDate(Calendar calendar) {
        dateTextView.setText(DateHelper.toString(calendar, DateFormat.Date));
        dateFilter.value = calendar.getTime();
    }
}
