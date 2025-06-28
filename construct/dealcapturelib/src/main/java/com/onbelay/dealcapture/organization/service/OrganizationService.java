package com.onbelay.dealcapture.organization.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummary;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;

import java.util.List;

public interface OrganizationService {

    OrganizationSnapshot load(EntityId entityId);

    TransactionResult save(OrganizationSnapshot snapshot);

    TransactionResult save(List<OrganizationSnapshot> snapshots);

    TransactionResult saveOrganizationRoles(
            EntityId organizationId,
            List<OrganizationRoleSnapshot> snapshots);

    List<OrganizationRoleSnapshot> fetchOrganizationRoles(EntityId organizationId);

    QuerySelectedPage findOrganizationIds(DefinedQuery definedQuery);

    List<OrganizationSnapshot> findByIds(QuerySelectedPage selectedPage);

    QuerySelectedPage findOrganizationRoleIds(DefinedQuery definedQuery);

    QuerySelectedPage findOrganizationIdsFromOrganizationRoleType(DefinedQuery definedQuery);

    List<OrganizationRoleSummary> findOrganizationRoleSummariesByIds(QuerySelectedPage selectedPage);

    List<OrganizationRoleSummary> findOrganizationRoleSummariesLikeShortName(
            String shortName,
            OrganizationRoleType organizationRoleType);


    OrganizationSnapshot findByExternalReference(Integer externalReferenceId);
}
