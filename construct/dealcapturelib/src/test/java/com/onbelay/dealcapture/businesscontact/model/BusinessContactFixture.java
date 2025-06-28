package com.onbelay.dealcapture.businesscontact.model;


import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;

public class BusinessContactFixture {

    public static BusinessContact createCounterpartyTrader(String firstName, String lastName, String email) {
        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.getDetail().setFirstName(firstName);
        snapshot.getDetail().setLastName(lastName);
        snapshot.getDetail().setEmail(email);
        snapshot.getDetail().setIsAdministrator(false);
        snapshot.getDetail().setIsCompanyTrader(false);
        snapshot.getDetail().setIsCounterpartyTrader(true);

        BusinessContact contact = new BusinessContact();
        contact.createWith(snapshot);
        return contact;
    }


    public static BusinessContact createCompanyTrader(String firstName, String lastName, String email) {
        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.getDetail().setFirstName(firstName);
        snapshot.getDetail().setLastName(lastName);
        snapshot.getDetail().setEmail(email);
        snapshot.getDetail().setIsAdministrator(false);
        snapshot.getDetail().setIsCompanyTrader(true);
        snapshot.getDetail().setIsCounterpartyTrader(false);

        BusinessContact contact = new BusinessContact();
        contact.createWith(snapshot);
        return contact;
    }


    public static BusinessContact createAdministrator(String firstName, String lastName, String email) {
        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.getDetail().setFirstName(firstName);
        snapshot.getDetail().setLastName(lastName);
        snapshot.getDetail().setEmail(email);
        snapshot.getDetail().setIsAdministrator(true);
        snapshot.getDetail().setIsCompanyTrader(false);
        snapshot.getDetail().setIsCounterpartyTrader(false);

        BusinessContact contact = new BusinessContact();
        contact.createWith(snapshot);
        return contact;
    }
}
