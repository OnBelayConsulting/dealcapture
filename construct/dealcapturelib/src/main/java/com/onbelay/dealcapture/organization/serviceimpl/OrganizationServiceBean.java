package com.onbelay.dealcapture.organization.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.organization.assembler.OrganizationAssembler;
import com.onbelay.dealcapture.organization.assembler.OrganizationRoleSnapshotAssemblerFactory;
import com.onbelay.dealcapture.organization.assembler.OrganizationRoleSummaryAssembler;
import com.onbelay.dealcapture.organization.enums.OrganizationErrorCode;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.model.OrganizationRole;
import com.onbelay.dealcapture.organization.repository.OrganizationRepository;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummary;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrganizationServiceBean extends BaseDomainService implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationRoleRepository organizationRoleRepository;

    @Override
    public OrganizationSnapshot load(EntityId entityId) {
        Organization organization = organizationRepository.load(entityId);
        if (organization == null)
            throw new OBRuntimeException(OrganizationErrorCode.MISSING_ORGANIZATION.getCode());

        OrganizationAssembler assembler = new OrganizationAssembler();
        return assembler.assemble(organization);
    }

    @Override
    public QuerySelectedPage findOrganizationIds(DefinedQuery definedQuery) {
        List<Integer> ids = organizationRepository.findOrganizationIds(definedQuery);
        return new QuerySelectedPage(
                ids,
                definedQuery.getOrderByClause());
    }

    @Override
    public QuerySelectedPage findOrganizationIdsFromOrganizationRoleType(DefinedQuery definedQuery) {
        List<Integer> ids = organizationRoleRepository.findOrganizationRoles(definedQuery)
                .stream()
                .map( c -> c.getOrganization().getId()).toList();

        return new QuerySelectedPage(ids, definedQuery.getOrderByClause());
    }

    @Override
    public List<OrganizationSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<Organization> organizations = organizationRepository.fetchByIds(selectedPage);
        OrganizationAssembler assembler = new OrganizationAssembler();
        return assembler.assemble(organizations);
    }

    @Override
    public TransactionResult save(List<OrganizationSnapshot> snapshots) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (OrganizationSnapshot snapshot : snapshots ) {
            TransactionResult childResult = save(snapshot);
            if (childResult.getId() != null)
                ids.add(childResult.getId());
        }
        return new TransactionResult(ids);
    }
    @Override
    public TransactionResult save(OrganizationSnapshot snapshot) {
        if (snapshot.getEntityState() == EntityState.NEW) {
            Organization organization =  new Organization(snapshot);
            return new TransactionResult(organization.getId());
        } else if (snapshot.getEntityState() == EntityState.MODIFIED) {
            Organization organization = organizationRepository.load(snapshot.getEntityId());
            organization.updateWith(snapshot);
            return new TransactionResult(organization.getId());
        } else if (snapshot.getEntityState() == EntityState.DELETE) {
            Organization organization = organizationRepository.load(snapshot.getEntityId());
            organization.delete();
        }
        return new TransactionResult();
    }


    @Override
    public TransactionResult saveOrganizationRoles(
            EntityId organizationId,
            List<OrganizationRoleSnapshot> snapshots) {

        Organization organization = organizationRepository.load(organizationId);
        return new TransactionResult(
                organization.saveOrganizationRoles(snapshots));
    }

    @Override
    public List<OrganizationRoleSnapshot> fetchOrganizationRoles(EntityId organizationId) {
        Organization organization = organizationRepository.load(organizationId);

        List<OrganizationRole> roles = organization.getOrganizationRoles();
        return OrganizationRoleSnapshotAssemblerFactory.assemble(roles);
    }

    @Override
    public OrganizationSnapshot findByExternalReference(Integer externalReferenceId) {
        Organization organization = organizationRepository.findByExternalReference(externalReferenceId);
        if (organization == null)
            return null;
        OrganizationAssembler assembler = new OrganizationAssembler();
        return assembler.assemble(organization);
    }

    @Override
    public QuerySelectedPage findOrganizationRoleIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                organizationRoleRepository.findOrganizationRoleIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<OrganizationRoleSummary> findOrganizationRoleSummariesLikeShortName(
            String shortName,
            OrganizationRoleType organizationRoleType) {
        return organizationRoleRepository.findAllLikeShortName(
                shortName,
                organizationRoleType);
    }

    @Override
    public List<OrganizationRoleSummary> findOrganizationRoleSummariesByIds(QuerySelectedPage selectedPage) {

        List<OrganizationRole> roles = organizationRoleRepository.fetchByIds(selectedPage);
        OrganizationRoleSummaryAssembler assembler = new OrganizationRoleSummaryAssembler();

        return assembler.assemble(roles);
    }
}
