package com.varanegar.framework.util.jobscheduler;

public class JobPair {
    Job job;
    Long time;

    public JobPair(Job job) {
        this.job = job;
        this.time = 0L;
    }
}
