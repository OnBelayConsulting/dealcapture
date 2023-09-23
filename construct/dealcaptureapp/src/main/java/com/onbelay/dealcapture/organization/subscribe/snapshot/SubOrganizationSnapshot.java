package com.onbelay.dealcapture.organization.subscribe.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubOrganizationSnapshot extends AbstractSnapshot {

    private SubOrganizationDetail detail = new SubOrganizationDetail();

    public SubOrganizationDetail getDetail() {
        return detail;
    }

    public void setDetail(SubOrganizationDetail detail) {
        this.detail = detail;
    }
}
