package com.onbelay.dealcapture.organization.model;

import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;

public class OrganizationFixture {

    public static Organization createOrganization(String shortName) {
        OrganizationSnapshot snapshot = new OrganizationSnapshot();
        snapshot.getDetail().setShortName(shortName);
        snapshot.getDetail().setLegalName(shortName);
        snapshot.getDetail().setExternalReferenceId(1);
        return new Organization(snapshot);
    }

}
