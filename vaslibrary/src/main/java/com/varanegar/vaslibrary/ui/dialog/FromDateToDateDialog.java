package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by A.Jafarzadeh on 8/13/2018.
 */

public class FromDateToDateDialog extends CuteAlertDialog {

    private ImageView fromDateIv, toDateIv;
    private PairedItems fromDatePi, toDatePi;
    private Date startDate, endDate;
    private StartEndDateCallBack startEndDateCallBack;

    public void setCallBack(@NonNull StartEndDateCallBack startEndDateCallBack) {
        this.startEndDateCallBack = startEndDateCallBack;
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.from_date_to_date_dialog_fragment, viewGroup, true);
        setTitle(R.string.date_filter);
        fromDateIv = view.findViewById(R.id.from_date_iv);
        toDateIv = view.findViewById(R.id.to_date_iv);
        fromDatePi = view.findViewById(R.id.from_date_paired);
        toDatePi = view.findViewById(R.id.to_date_paired);
        fromDateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(getContext()), new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        startDate = calendar.getTime();
                        fromDatePi.setValue(DateHelper.toString(calendar, DateFormat.Date));
                    }
                });
            }
        });
        toDateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(getContext()), new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        endDate = calendar.getTime();
                        toDatePi.setValue(DateHelper.toString(calendar, DateFormat.Date));
                    }
                });
            }
        });
    }

    @Override
    public void ok() {
        if (startDate != null && endDate != null && startEndDateCallBack != null) {
            if (endDate.getTime() < startDate.getTime()) {
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setMessage(R.string.invalid_period);
                dialog.setTitle(R.string.error);
                dialog.setIcon(Icon.Error);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            } else {
                dismiss();
                startEndDateCallBack.setStartAndEndDate(startDate, endDate);
            }
        } else {
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setMessage(R.string.set_complete_information);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }

    }

    @Override
    public void cancel() {

    }

    public interface StartEndDateCallBack {
        void setStartAndEndDate(Date startDate, Date endDate);
    }

}
