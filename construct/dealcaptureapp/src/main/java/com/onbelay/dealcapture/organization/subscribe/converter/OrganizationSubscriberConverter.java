package com.onbelay.dealcapture.organization.subscribe.converter;

import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.subscribe.snapshot.OrganizationSubscriptionSnapshot;

public class OrganizationSubscriberConverter {

    public OrganizationSnapshot convert(OrganizationSubscriptionSnapshot snapshotIn) {
        OrganizationSnapshot snapshot = new OrganizationSnapshot();
        snapshot.getDetail().setExternalReferenceId(snapshotIn.getEntityId().getId());

        if (snapshotIn.getDetail().getShortName() != null)
            snapshot.getDetail().setShortName(snapshotIn.getDetail().getShortName());

        if (snapshotIn.getDetail().getLegalName() != null)
            snapshot.getDetail().setLegalName(snapshotIn.getDetail().getLegalName());
        return snapshot;
    }

}
