package com.onbelay.dealcapture.organization.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationRoleSummaryDetail {

    private String shortName;
    private String legalName;
    private String organizationRoleStatusValue;


    private String getOrganizationRoleStatusValue() {
        return organizationRoleStatusValue;
    }

    private void setOrganizationRoleStatusValue(String organizationRoleStatusValue) {
        this.organizationRoleStatusValue = organizationRoleStatusValue;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    @Transient
    @JsonIgnore
    public OrganizationRoleStatus getStatus() {
        return OrganizationRoleStatus.lookUp(organizationRoleStatusValue);
    }

    public void setStatus(OrganizationRoleStatus status) {
        this.organizationRoleStatusValue = status.getCode();
    }

    public void copyFrom(OrganizationRoleSummaryDetail copy) {

        if (copy.legalName != null)
            this.legalName = copy.legalName;

        if (copy.shortName != null)
            this.shortName = copy.shortName;

        if (copy.organizationRoleStatusValue != null)
            organizationRoleStatusValue = copy.organizationRoleStatusValue;
    }

    @JsonIgnore
    public void setDefaults() {
        organizationRoleStatusValue = OrganizationRoleStatus.PENDING.getCode();

    }


}
