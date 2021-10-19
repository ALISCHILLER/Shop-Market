package com.varanegar.warmsales;

import android.content.Context;

import com.varanegar.vaslibrary.manager.DistributionManager;
import com.varanegar.vaslibrary.manager.updatemanager.SimpleTourAsyncTask;
import com.varanegar.vaslibrary.manager.updatemanager.TourAsyncTask;
import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;

import java.util.List;

public class WarmSalesTourUpdateFlow extends TourUpdateFlow {
    public WarmSalesTourUpdateFlow(Context context) {
        super(context);
    }

    @Override
    protected void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {
        super.addAsyncTasks(tasks, call);
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                DistributionManager distributionManager = new DistributionManager(getContext());
                distributionManager.sync(call);
            }

            @Override
            public String name() {
                return "DistributionData";
            }

            @Override
            public int group() {
                return R.string.distribution_data;
            }

            @Override
            public int queueId() {
                return 5;
            }
        });
    }
}
