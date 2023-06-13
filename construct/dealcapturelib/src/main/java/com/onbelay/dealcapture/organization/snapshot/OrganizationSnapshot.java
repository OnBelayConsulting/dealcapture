package com.onbelay.dealcapture.organization.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

public class OrganizationSnapshot extends AbstractSnapshot {

    private OrganizationDetail detail = new OrganizationDetail();

    public OrganizationDetail getDetail() {
        return detail;
    }

    public void setDetail(OrganizationDetail detail) {
        this.detail = detail;
    }
}
