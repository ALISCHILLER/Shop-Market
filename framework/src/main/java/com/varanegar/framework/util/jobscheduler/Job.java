package com.varanegar.framework.util.jobscheduler;

import android.content.Context;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public interface Job {
    /**
     * Minimum supported interval is 60 seconds
     *
     * @return
     */
    public abstract Long getInterval();

    /**
     * Whatever you need to do as job started
     *
     * @param context
     */
    public abstract void run(Context context);
}
