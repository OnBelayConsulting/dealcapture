package com.onbelay.dealcapture.organization.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.assembler.OrganizationAssembler;
import com.onbelay.dealcapture.organization.assembler.OrganizationRoleSnapshotAssemblerFactory;
import com.onbelay.dealcapture.organization.assembler.OrganizationRoleSummaryAssembler;
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

@Service
@Transactional
public class OrganizationServiceBean extends BaseDomainService implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationRoleRepository organizationRoleRepository;

    @Override
    public QuerySelectedPage findOrganizationIds(DefinedQuery definedQuery) {
        List<Integer> ids = organizationRepository.findOrganizationIds(definedQuery);
        return new QuerySelectedPage(
                ids,
                definedQuery.getOrderByClause());
    }

    @Override
    public List<OrganizationSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<Organization> organizations = organizationRepository.fetchByIds(selectedPage);
        OrganizationAssembler assembler = new OrganizationAssembler();
        return assembler.assemble(organizations);
    }

    @Override
    public TransactionResult save(List<OrganizationSnapshot> snapshots) {
        ArrayList<EntityId> ids = new ArrayList<>();
        for (OrganizationSnapshot snapshot : snapshots ) {
            TransactionResult childResult = save(snapshot);
            if (childResult.getEntityId() != null)
                ids.add(childResult.getEntityId());
        }
        return new TransactionResult(ids);
    }
    @Override
    public TransactionResult save(OrganizationSnapshot snapshot) {
        if (snapshot.getEntityState() == EntityState.NEW) {
            Organization organization =  new Organization(snapshot);
            return new TransactionResult(organization.generateEntityId());
        } else if (snapshot.getEntityState() == EntityState.MODIFIED) {
            Organization organization = organizationRepository.load(snapshot.getEntityId());
            organization.updateWith(snapshot);
            return new TransactionResult(organization.generateEntityId());
        } else if (snapshot.getEntityState() == EntityState.DELETE) {
            Organization organization = organizationRepository.load(snapshot.getEntityId());
            organization.delete();
            return new TransactionResult(organization.generateEntityId());
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
    public QuerySelectedPage findOrganizationRoleIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                organizationRoleRepository.findOrganizationRoleIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<OrganizationRoleSummary> findOrganizationRoleSummariesByIds(QuerySelectedPage selectedPage) {

        List<OrganizationRole> roles = organizationRoleRepository.fetchByIds(selectedPage);
        OrganizationRoleSummaryAssembler assembler = new OrganizationRoleSummaryAssembler();

        return assembler.assemble(roles);
    }
}
