package com.onbelay.dealcapture.businesscontact.subscribe.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessContactSubscriptionDetail {

    private String firstName;
    private String lastName;

    private String email;

    private Boolean isCompanyTrader;
    private Boolean isCounterpartyTrader;
    private Boolean isAdministrator;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Boolean getIsCompanyTrader() {
        return isCompanyTrader;
    }

    public void setIsCompanyTrader(Boolean companyTrader) {
        isCompanyTrader = companyTrader;
    }

    public Boolean getIsCounterpartyTrader() {
        return isCounterpartyTrader;
    }

    public void setIsCounterpartyTrader(Boolean counterpartyTrader) {
        isCounterpartyTrader = counterpartyTrader;
    }

    public Boolean getIsAdministrator() {
        return isAdministrator;
    }

    public void setIsAdministrator(Boolean administrator) {
        isAdministrator = administrator;
    }


}
