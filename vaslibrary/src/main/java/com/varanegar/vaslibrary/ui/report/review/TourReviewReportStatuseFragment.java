package com.varanegar.vaslibrary.ui.report.review;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.vaslibrary.R;

import java.util.HashMap;
import java.util.Map;

import static com.varanegar.vaslibrary.ui.report.review.OptionId.*;

/**
 * Created by A.Jafarzadeh on 9/8/2018.
 */

public class TourReviewReportStatuseFragment extends CuteAlertDialog {

    private CheckBox readyToSentChB, sentChb, receivingInformationChB, receivedChB, endedChB, refusedChB, disabledChB;
    private Map<OptionId, TourStatusOption> tourStatusOptionHashMap = new HashMap<>();
    private TourStatues tourStatues;

    public void setCallBack(Map<OptionId, TourStatusOption> tourStatusOptionHashMap, @NonNull TourStatues tourStatues) {
        this.tourStatues = tourStatues;
        this.tourStatusOptionHashMap = tourStatusOptionHashMap;
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_review_report_statuse, viewGroup, true);
        setTitle(R.string.tour_statuse_filter);
        readyToSentChB = view.findViewById(R.id.ready_to_send);
        sentChb = view.findViewById(R.id.sent);
        receivingInformationChB = view.findViewById(R.id.receiving_information);
        receivedChB = view.findViewById(R.id.received);
        endedChB = view.findViewById(R.id.ended);
        refusedChB = view.findViewById(R.id.refused);
        disabledChB = view.findViewById(R.id.disabled);
        for (TourStatusOption tourStatusOption : tourStatusOptionHashMap.values()) {
            switch (tourStatusOption.id) {
                case ReadyToSend :
                    readyToSentChB.setChecked(true);
                    break;
                case Sent:
                    sentChb.setChecked(true);
                    break;
                case InProgress:
                    receivingInformationChB.setChecked(true);
                    break;
                case Received:
                    receivedChB.setChecked(true);
                    break;
                case Finished:
                    endedChB.setChecked(true);
                    break;
                case Canceled:
                    refusedChB.setChecked(true);
                    break;
                case Deactivated:
                    disabledChB.setChecked(true);
                    break;
            }
        }
    }

    private void setTourStatusOptionHashMap() {
        if (readyToSentChB.isChecked()) {
            tourStatusOptionHashMap.remove(ReadyToSend);
            tourStatusOptionHashMap.put(ReadyToSend, new TourStatusOption(ReadyToSend, getString(R.string.ready_to_send), true));
        } else
            tourStatusOptionHashMap.remove(ReadyToSend);

        if (sentChb.isChecked()) {
            tourStatusOptionHashMap.remove(Sent);
            tourStatusOptionHashMap.put(Sent, new TourStatusOption(Sent, getString(R.string.sent), true));
        } else
            tourStatusOptionHashMap.remove(Sent);

        if (receivingInformationChB.isChecked()) {
            tourStatusOptionHashMap.remove(InProgress);
            tourStatusOptionHashMap.put(InProgress, new TourStatusOption(InProgress, getString(R.string.receiving_information), true));
        } else
            tourStatusOptionHashMap.remove(InProgress);

        if (receivedChB.isChecked()) {
            tourStatusOptionHashMap.remove(Received);
            tourStatusOptionHashMap.put(Received, new TourStatusOption(Received, getString(R.string.received), true));
        } else
            tourStatusOptionHashMap.remove(Received);

        if (endedChB.isChecked()) {
            tourStatusOptionHashMap.remove(Finished);
            tourStatusOptionHashMap.put(Finished, new TourStatusOption(Finished, getString(R.string.ended), true));

        } else
            tourStatusOptionHashMap.remove(Finished);

        if (refusedChB.isChecked()) {
            tourStatusOptionHashMap.remove(Canceled);
            tourStatusOptionHashMap.put(Canceled, new TourStatusOption(Canceled, getString(R.string.refused), true));
        } else
            tourStatusOptionHashMap.remove(Canceled);

        if (disabledChB.isChecked()) {
            tourStatusOptionHashMap.remove(Deactivated);
            tourStatusOptionHashMap.put(Deactivated, new TourStatusOption(Deactivated, getString(R.string.disabled), true));
        } else
            tourStatusOptionHashMap.remove(Deactivated);
    }

    @Override
    public void ok() {
        dismiss();
        setTourStatusOptionHashMap();
        if (tourStatues != null)
            tourStatues.choicesTourStatues(tourStatusOptionHashMap);
    }

    @Override
    public void cancel() {

    }

    public interface TourStatues {
        void choicesTourStatues(Map<OptionId, TourStatusOption> tourStatusOptionHashMap);
    }
}
