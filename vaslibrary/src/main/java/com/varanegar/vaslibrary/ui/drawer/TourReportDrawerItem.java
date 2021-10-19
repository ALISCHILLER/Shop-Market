package com.varanegar.vaslibrary.ui.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.drawer.BaseDrawerItem;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.DistributionCustomerCallManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.tour.TourModel;

/**
 * Created by atp on 1/29/2017.
 */

public abstract class TourReportDrawerItem extends BaseDrawerItem {

    public TourReportDrawerItem(Context context) {
        super(context);
    }

    public TourReportDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TourReportDrawerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        View view = inflate(getContext(), R.layout.drawer_tour_report_item, this);
        TourModel tourModel = new TourManager(getContext()).loadTour();
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            DistributionCustomerCallManager distributionCustomerCallManager = new DistributionCustomerCallManager(getContext());
            ((TextView) view.findViewById(R.id.tour_no_text_view)).setText(getContext().getString(R.string.tour_report) + " " + String.valueOf(tourModel.TourNo) + " - " + getContext().getString(R.string.dist_no) + " " + distributionCustomerCallManager.getDistNumbers());
        } else {
            if (tourModel != null)
                ((TextView) view.findViewById(R.id.tour_no_text_view)).setText(getContext().getString(R.string.tour_report) + " " + String.valueOf(tourModel.TourNo));
        }

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TourReportDrawerItem.this.onClick();
            }
        });
    }

    protected abstract void onClick();
}
