package com.onbelay.dealcapture.organization.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationSnapshot extends AbstractSnapshot {

    private CompanyRoleSnapshot companyRoleSnapshot;
    private CounterpartyRoleSnapshot counterpartyRoleSnapshot;

    private OrganizationDetail detail = new OrganizationDetail();

    public OrganizationSnapshot() {
    }

    public OrganizationSnapshot(EntityId entityId) {
        super(entityId);
    }

    public OrganizationSnapshot(String errorCode) {
        super(errorCode);
    }

    public OrganizationSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public OrganizationSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public OrganizationDetail getDetail() {
        return detail;
    }

    public void setDetail(OrganizationDetail detail) {
        this.detail = detail;
    }

    public CompanyRoleSnapshot getCompanyRoleSnapshot() {
        return companyRoleSnapshot;
    }

    public void setCompanyRoleSnapshot(CompanyRoleSnapshot companyRoleSnapshot) {
        this.companyRoleSnapshot = companyRoleSnapshot;
    }

    public CounterpartyRoleSnapshot getCounterpartyRoleSnapshot() {
        return counterpartyRoleSnapshot;
    }

    public void setCounterpartyRoleSnapshot(CounterpartyRoleSnapshot counterpartyRoleSnapshot) {
        this.counterpartyRoleSnapshot = counterpartyRoleSnapshot;
    }
}
