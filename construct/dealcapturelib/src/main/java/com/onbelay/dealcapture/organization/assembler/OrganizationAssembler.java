package com.onbelay.dealcapture.organization.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class OrganizationAssembler extends EntityAssembler {

    public OrganizationSnapshot assemble(Organization organization) {
        OrganizationSnapshot snapshot = new OrganizationSnapshot();
        super.setEntityAttributes(organization, snapshot);
        snapshot.getDetail().copyFrom(organization.getDetail());
        return snapshot;
    }

    public List<OrganizationSnapshot> assemble(List<Organization> organizations) {
        return organizations
                .stream()
                .map(c-> assemble(c))
                .collect(Collectors.toList());
    }


}
