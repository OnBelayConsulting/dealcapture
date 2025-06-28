package com.onbelay.dealcapture.businesscontact.subscribe.subscriber;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.businesscontact.service.BusinessContactService;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.businesscontact.subscribe.converter.BusinessContactSubscriberConverter;
import com.onbelay.dealcapture.businesscontact.subscribe.snapshot.BusinessContactSubscriptionSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessContactUpdaterBean implements BusinessContactUpdater {

    @Autowired
    private BusinessContactService businessContactService;

    @Override
    public TransactionResult updateBusinessContacts(List<BusinessContactSubscriptionSnapshot> snapshotsIn) {
        BusinessContactSubscriberConverter converter = new BusinessContactSubscriberConverter();
        List<BusinessContactSnapshot> snapshots = new ArrayList<>();

        for (BusinessContactSubscriptionSnapshot snapshotIn: snapshotsIn) {
            BusinessContactSnapshot found = businessContactService.findByExternalReference(snapshotIn.getEntityId().getId());
            if (found != null) {
                BusinessContactSnapshot updateMe = converter.convert(snapshotIn);
                updateMe.setEntityId(found.getEntityId());
                if (snapshotIn.getEntityState() == EntityState.DELETE)
                    updateMe.setEntityState(EntityState.DELETE);
                else
                    updateMe.setEntityState(EntityState.MODIFIED);
                snapshots.add(updateMe);
            } else {
                if (snapshotIn.getEntityState() != EntityState.DELETE) {
                    BusinessContactSnapshot createMe = converter.convert(snapshotIn);
                    createMe.setEntityState(EntityState.NEW);
                    snapshots.add(createMe);
                }
            }
        }

        return businessContactService.save(snapshots);
    }
}
