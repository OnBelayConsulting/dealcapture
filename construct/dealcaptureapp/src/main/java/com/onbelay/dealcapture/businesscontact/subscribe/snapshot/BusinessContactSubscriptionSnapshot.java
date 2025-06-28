package com.onbelay.dealcapture.businesscontact.subscribe.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessContactSubscriptionSnapshot extends AbstractSnapshot {

    private BusinessContactSubscriptionDetail detail = new BusinessContactSubscriptionDetail();

    public BusinessContactSubscriptionDetail getDetail() {
        return detail;
    }

    public void setDetail(BusinessContactSubscriptionDetail detail) {
        this.detail = detail;
    }
}
