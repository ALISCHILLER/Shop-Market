package com.varanegar.supervisor.status;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.model_old.CustomerCallViewModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallInfoFragment extends CustomerCallTabFragment {
    private PairedItems visitStatusPairedItems;
    private PairedItems callStatusPairedItems;
    private PairedItems descriptionPairedItems;
    private PairedItems customerCodePairedItems;
    private PairedItems customerNamePairedItems;
    private PairedItems addressPairedItems;
    private PairedItems startTimePairedItems;
    private PairedItems endTimePairedItems;
    private PairedItems visitDurationPairedItems;
    private PairedItems tourNoPairedItems;
    private PairedItems visitorNamePairedItems;

    @Override
    public void refresh(CustomerCallViewModel customerCallViewModel) {
        if (customerCallViewModel == null)
            return;
        tourNoPairedItems.setValue(String.valueOf(customerCallViewModel.TourNo));
        visitorNamePairedItems.setValue(customerCallViewModel.DealerName);
        visitStatusPairedItems.setValue(customerCallViewModel.VisitStatusName);
        descriptionPairedItems.setValue(customerCallViewModel.Description);
        callStatusPairedItems.setValue(customerCallViewModel.CallStatusName);
        customerCodePairedItems.setValue(customerCallViewModel.CustomerCode);
        customerNamePairedItems.setValue(customerCallViewModel.CustomerName);
        startTimePairedItems.setValue(DateHelper.toString(customerCallViewModel.StartTime, DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getContext())));
        endTimePairedItems.setValue(DateHelper.toString(customerCallViewModel.EndTime, DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getContext())));
        addressPairedItems.setValue(customerCallViewModel.Address);
        visitDurationPairedItems.setValue(customerCallViewModel.VisitDurationStr);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.customer_call_info_layout, container, false);
        tourNoPairedItems = view.findViewById(R.id.tour_no_paired_items);
        visitorNamePairedItems = view.findViewById(R.id.visitor_name_paired_items);
        visitStatusPairedItems = view.findViewById(R.id.visit_status_paired_items);
        callStatusPairedItems = view.findViewById(R.id.call_status_paired_items);
        customerCodePairedItems = view.findViewById(R.id.customer_code_paired_items);
        customerNamePairedItems = view.findViewById(R.id.customer_name_paired_items);
        startTimePairedItems = view.findViewById(R.id.start_time_paired_items);
        endTimePairedItems = view.findViewById(R.id.end_time_paired_items);
        visitDurationPairedItems = view.findViewById(R.id.visit_duration_paired_items);
        addressPairedItems = view.findViewById(R.id.address_paired_items);
        descriptionPairedItems = view.findViewById(R.id.description_paired_items);


        return view;
    }
}
