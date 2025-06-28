package com.onbelay.dealcapture.job.publish.snapshot;

public class DealJobRequestPublication {
    private Integer jobId;

    public DealJobRequestPublication() {
    }

    public DealJobRequestPublication(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
}
