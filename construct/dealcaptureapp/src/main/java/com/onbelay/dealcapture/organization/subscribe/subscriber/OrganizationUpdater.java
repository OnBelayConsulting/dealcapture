package com.onbelay.dealcapture.organization.subscribe.subscriber;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.subscribe.snapshot.OrganizationSubscriptionSnapshot;

import java.util.List;

public interface OrganizationUpdater {

    TransactionResult updateOrganizations(List<OrganizationSubscriptionSnapshot> snapshots);

}
