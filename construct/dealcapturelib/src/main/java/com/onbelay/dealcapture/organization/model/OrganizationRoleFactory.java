package com.onbelay.dealcapture.organization.model;

import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OrganizationRoleFactory {

    private static Map<OrganizationRoleType, Supplier<OrganizationRole>> roleMap = new HashMap<>();

    static {
        roleMap.put(OrganizationRoleType.COMPANY_ROLE, CompanyRole::new);
        roleMap.put(OrganizationRoleType.COUNTERPARTY_ROLE, CounterpartyRole::new);
    }

    public static OrganizationRole newOrganizationRole(OrganizationRoleType type) {
        return roleMap.get(type).get();
    }


}
