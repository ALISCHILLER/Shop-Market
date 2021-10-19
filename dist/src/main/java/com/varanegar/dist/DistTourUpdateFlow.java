package com.varanegar.dist;

import android.content.Context;

import com.varanegar.vaslibrary.manager.DistributionManager;
import com.varanegar.vaslibrary.manager.updatemanager.SimpleTourAsyncTask;
import com.varanegar.vaslibrary.manager.updatemanager.TourAsyncTask;
import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;

import java.util.List;

/**
 * Created by A.Jafarzadeh on 2/19/2018.
 */

public class DistTourUpdateFlow extends TourUpdateFlow {
    public DistTourUpdateFlow(Context context) {
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
                return R.string.distribution_tour;
            }

            @Override
            public int queueId() {
                return 5;
            }
        });
    }
}
