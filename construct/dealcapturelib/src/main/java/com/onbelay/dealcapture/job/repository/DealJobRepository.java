package com.onbelay.dealcapture.job.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.job.model.DealJob;

import java.util.List;

public interface DealJobRepository {
    public static final String BEAN_NAME = "dealJobRepository";

    DealJob load(EntityId entityId);


    List<Integer> findJobIds(DefinedQuery definedQuery);

    List<DealJob> fetchByIds(QuerySelectedPage page);
}
