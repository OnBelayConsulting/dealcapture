package com.onbelay.dealcapture.organization.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummaryCollection;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshotCollection;

public interface OrganizationRestAdapter {

    TransactionResult save(OrganizationSnapshot snapshot);

    OrganizationSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    OrganizationRoleSummaryCollection findSummaries(
            String queryText,
            Integer start,
            Integer limit);
}
