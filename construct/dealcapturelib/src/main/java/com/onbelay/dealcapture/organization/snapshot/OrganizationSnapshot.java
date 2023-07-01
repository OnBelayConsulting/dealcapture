package com.onbelay.dealcapture.organization.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationSnapshot extends AbstractSnapshot {

    private OrganizationDetail detail = new OrganizationDetail();

    public OrganizationDetail getDetail() {
        return detail;
    }

    public void setDetail(OrganizationDetail detail) {
        this.detail = detail;
    }
}
