package com.varanegar.vaslibrary.ui.report.review;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.TourStatusSummaryViewModel;
import com.varanegar.vaslibrary.ui.report.review.adapter.TourReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.supervisor.SupervisorApi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 9/6/2018.
 */

public class TourReviewReportFragment extends BaseReviewReportFragment<TourStatusSummaryViewModel> {

    private Map<OptionId, TourStatusOption> tourStatusOptionHashMap = new HashMap<>();

    private void setTourStatusOptionHashMap(Map<OptionId, TourStatusOption> tourStatusOptionHashMap) {
        this.tourStatusOptionHashMap = tourStatusOptionHashMap;
    }

    @Override
    protected Call<List<TourStatusSummaryViewModel>> reportApi() {
        return new SupervisorApi(getContext()).tour(0,
                tourStatusOptionHashMap.get(OptionId.ReadyToSend) != null && tourStatusOptionHashMap.get(OptionId.ReadyToSend).value,
                tourStatusOptionHashMap.get(OptionId.Sent) != null && tourStatusOptionHashMap.get(OptionId.Sent).value,
                tourStatusOptionHashMap.get(OptionId.InProgress) != null && tourStatusOptionHashMap.get(OptionId.InProgress).value,
                tourStatusOptionHashMap.get(OptionId.Received) != null && tourStatusOptionHashMap.get(OptionId.Received).value,
                tourStatusOptionHashMap.get(OptionId.Finished) != null && tourStatusOptionHashMap.get(OptionId.Finished).value,
                tourStatusOptionHashMap.get(OptionId.Canceled) != null && tourStatusOptionHashMap.get(OptionId.Canceled).value,
                tourStatusOptionHashMap.get(OptionId.Deactivated) != null && tourStatusOptionHashMap.get(OptionId.Deactivated).value,
                DateHelper.toString(getStartDate(), DateFormat.MicrosoftDateTime, Locale.US), DateHelper.toString(getEndDate(), DateFormat.MicrosoftDateTime, Locale.US));
    }

    @Override
    protected SimpleReportAdapter<TourStatusSummaryViewModel> createAdapter() {
        return new TourReviewReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return getString(R.string.tour_report);
    }

    @Override
    protected String isEnabled() {
        if (tourStatusOptionHashMap.size() > 0)
            return null;
        else
            return getString(R.string.empty_filter_tour_statuse);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tourStatusOptionHashMap.put(OptionId.ReadyToSend, new TourStatusOption(OptionId.ReadyToSend, getString(R.string.ready_to_send), true));
        tourStatusOptionHashMap.put(OptionId.Sent, new TourStatusOption(OptionId.Sent, getString(R.string.sent), true));
        tourStatusOptionHashMap.put(OptionId.InProgress, new TourStatusOption(OptionId.InProgress, getString(R.string.receiving_information), true));
        tourStatusOptionHashMap.put(OptionId.Received, new TourStatusOption(OptionId.Received, getString(R.string.received), true));
        tourStatusOptionHashMap.put(OptionId.Finished, new TourStatusOption(OptionId.Finished, getString(R.string.ended), true));
        tourStatusOptionHashMap.put(OptionId.Canceled, new TourStatusOption(OptionId.Canceled, getString(R.string.refused), true));
        tourStatusOptionHashMap.put(OptionId.Deactivated, new TourStatusOption(OptionId.Deactivated, getString(R.string.disabled), true));
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.check_boxes).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.check_boxes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TourReviewReportStatuseFragment fragment = new TourReviewReportStatuseFragment();
                fragment.setCallBack(tourStatusOptionHashMap, new TourReviewReportStatuseFragment.TourStatues() {
                    @Override
                    public void choicesTourStatues(Map<OptionId, TourStatusOption> tourStatusOptionHashMap) {
                        setTourStatusOptionHashMap(tourStatusOptionHashMap);
                        isEnabled();
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "OrderSelectionDialog");
            }
        });
        onAdapterCallback = new OnAdapterCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess() {
                getAdapter().getAdapter().sort(new Comparator<TourStatusSummaryViewModel>() {
                    @Override
                    public int compare(TourStatusSummaryViewModel t1, TourStatusSummaryViewModel t2) {
                        if (t1.TourNo < t2.TourNo) return -1;
                        else if (t1.TourNo > t2.TourNo) return 1;
                        else return 0;
                    }
                });
                getAdapter().refresh();
            }

        };
    }
}
