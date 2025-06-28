package com.onbelay.dealcapture.businesscontact.subscribe.converter;


import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.businesscontact.subscribe.snapshot.BusinessContactSubscriptionSnapshot;

public class BusinessContactSubscriberConverter {

    public BusinessContactSnapshot convert(BusinessContactSubscriptionSnapshot snapshotIn) {
        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.getDetail().setExternalReferenceId(snapshotIn.getEntityId().getId());

        if (snapshotIn.getDetail().getFirstName() != null)
            snapshot.getDetail().setFirstName(snapshotIn.getDetail().getFirstName());

        if (snapshotIn.getDetail().getLastName() != null)
            snapshot.getDetail().setLastName(snapshotIn.getDetail().getLastName());

        if (snapshotIn.getDetail().getEmail() != null)
            snapshot.getDetail().setEmail(snapshotIn.getDetail().getEmail());

        if (snapshotIn.getDetail().getIsCompanyTrader() != null)
            snapshot.getDetail().setIsCompanyTrader(snapshotIn.getDetail().getIsCompanyTrader());

        if (snapshotIn.getDetail().getIsCounterpartyTrader() != null)
            snapshot.getDetail().setIsCounterpartyTrader(snapshotIn.getDetail().getIsCounterpartyTrader());

        if (snapshotIn.getDetail().getIsAdministrator() != null)
            snapshot.getDetail().setIsAdministrator(snapshotIn.getDetail().getIsAdministrator());

        return snapshot;
    }

}
