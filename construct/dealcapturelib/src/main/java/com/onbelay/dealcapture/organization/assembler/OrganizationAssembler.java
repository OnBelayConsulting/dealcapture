package com.onbelay.dealcapture.organization.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.snapshot.CompanyRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class OrganizationAssembler extends EntityAssembler {

    public OrganizationSnapshot assemble(Organization organization) {
        OrganizationSnapshot snapshot = new OrganizationSnapshot();
        super.setEntityAttributes(organization, snapshot);
        snapshot.getDetail().copyFrom(organization.getDetail());

        for (OrganizationRoleSnapshot role : OrganizationRoleSnapshotAssemblerFactory.assemble(organization.getOrganizationRoles())) {
            if (role.getOrganizationRoleType() == OrganizationRoleType.COMPANY_ROLE)
                snapshot.setCompanyRoleSnapshot((CompanyRoleSnapshot) role);
            if (role.getOrganizationRoleType() == OrganizationRoleType.COUNTERPARTY_ROLE)
                snapshot.setCounterpartyRoleSnapshot((CounterpartyRoleSnapshot) role);
        }


        return snapshot;
    }

    public List<OrganizationSnapshot> assemble(List<Organization> organizations) {
        return organizations
                .stream()
                .map(c-> assemble(c))
                .collect(Collectors.toList());
    }


}
