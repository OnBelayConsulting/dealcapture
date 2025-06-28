package com.onbelay.dealcapture.organization.subscribe.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationSubscriptionSnapshot extends AbstractSnapshot {

    private OrganizationSubscriptionDetail detail = new OrganizationSubscriptionDetail();

    public OrganizationSubscriptionDetail getDetail() {
        return detail;
    }

    public void setDetail(OrganizationSubscriptionDetail detail) {
        this.detail = detail;
    }
}
