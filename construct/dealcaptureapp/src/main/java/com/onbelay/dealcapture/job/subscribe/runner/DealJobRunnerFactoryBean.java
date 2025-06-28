package com.onbelay.dealcapture.job.subscribe.runner;


import com.onbelay.dealcapture.job.enums.JobTypeCode;

import java.util.HashMap;
import java.util.Map;

public class DealJobRunnerFactoryBean implements DealJobRunnerFactory {

    private Map<JobTypeCode, DealJobRunner> runners = new HashMap<>();

    @Override
    public DealJobRunner getRunner(JobTypeCode code) {
        return runners.get(code);
    }

    public void addRunner(JobTypeCode code, DealJobRunner runner) {
        runners.put(code, runner);
    }
}
