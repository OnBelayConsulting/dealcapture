package com.onbelay.dealcapture.organization.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummaryCollection;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshotCollection;

import java.util.List;

public interface OrganizationRestAdapter {

    TransactionResult save(OrganizationSnapshot snapshot);

    OrganizationSnapshotCollection find(
            String queryText,
            String orgRoleType,
            Integer start,
            Integer limit);

    OrganizationRoleSummaryCollection findSummaries(
            String queryText,
            Integer start,
            Integer limit);


    OrganizationRoleSummaryCollection findSummariesLikeShortName(
            String shortName,
            OrganizationRoleType roleType,
            Integer limit);


    OrganizationSnapshot get(Integer id);

    TransactionResult saveRoles(
            EntityId organizationId,
            List<OrganizationRoleSnapshot> snapshots);
}
