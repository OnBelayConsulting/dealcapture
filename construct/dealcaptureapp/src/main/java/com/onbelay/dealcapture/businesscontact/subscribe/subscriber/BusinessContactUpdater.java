package com.onbelay.dealcapture.businesscontact.subscribe.subscriber;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.businesscontact.subscribe.snapshot.BusinessContactSubscriptionSnapshot;

import java.util.List;

public interface BusinessContactUpdater {

    TransactionResult updateBusinessContacts(List<BusinessContactSubscriptionSnapshot> snapshots);

}
