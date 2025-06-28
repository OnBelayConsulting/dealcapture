package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.dealcapture.job.enums.JobTypeCode;

public interface DealJobRunnerFactory {

    DealJobRunner getRunner(JobTypeCode code);

}
