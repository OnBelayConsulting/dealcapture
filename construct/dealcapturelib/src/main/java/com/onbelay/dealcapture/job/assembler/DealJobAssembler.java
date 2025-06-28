package com.onbelay.dealcapture.job.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.job.model.DealJob;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;

import java.util.List;

public class DealJobAssembler  extends EntityAssembler {

    public DealJobSnapshot assemble(DealJob job) {
        DealJobSnapshot snapshot = new DealJobSnapshot();
        super.setEntityAttributes(job, snapshot);
        if (job.getDependsOnJobId() != null) {
            snapshot.setDependsOnId(new EntityId(job.getDependsOnJobId()));
        }

        snapshot.getDetail().copyFrom(job.getDetail());
        return snapshot;
    }

    public List<DealJobSnapshot> assemble(List<DealJob> jobs) {
        return jobs
                .stream()
                .map(this::assemble)
                .toList();
    }

}
