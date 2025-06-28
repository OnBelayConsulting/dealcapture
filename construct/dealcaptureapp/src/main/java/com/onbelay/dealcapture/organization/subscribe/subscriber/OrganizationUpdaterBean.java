package com.onbelay.dealcapture.organization.subscribe.subscriber;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.subscribe.converter.OrganizationSubscriberConverter;
import com.onbelay.dealcapture.organization.subscribe.snapshot.OrganizationSubscriptionSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrganizationUpdaterBean implements OrganizationUpdater {

    @Autowired
    private OrganizationService organizationService;

    @Override
    public TransactionResult updateOrganizations(List<OrganizationSubscriptionSnapshot> snapshotsIn) {
        OrganizationSubscriberConverter converter = new OrganizationSubscriberConverter();
        List<OrganizationSnapshot> snapshots = new ArrayList<>();

        for (OrganizationSubscriptionSnapshot snapshotIn: snapshotsIn) {
            OrganizationSnapshot found = organizationService.findByExternalReference(snapshotIn.getEntityId().getId());
            if (found != null) {
                OrganizationSnapshot updateMe = converter.convert(snapshotIn);
                updateMe.setEntityId(found.getEntityId());
                if (snapshotIn.getEntityState() == EntityState.DELETE)
                    updateMe.setEntityState(EntityState.DELETE);
                else
                    updateMe.setEntityState(EntityState.MODIFIED);
                snapshots.add(updateMe);
            } else {
                if (snapshotIn.getEntityState() != EntityState.DELETE) {
                    OrganizationSnapshot createMe = converter.convert(snapshotIn);
                    createMe.setEntityState(EntityState.NEW);
                    snapshots.add(createMe);
                }
            }
        }

        return organizationService.save(snapshots);
    }
}
