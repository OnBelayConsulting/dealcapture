package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;

public interface DealJobRunner {

    public void execute(DealJobRequestPublication publication);
}
