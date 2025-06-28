package com.onbelay.dealcapture.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.model.OrganizationFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;


@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*", "com.onbelay.dealcapture.*"})
@EntityScan(basePackages = {"com.onbelay.*"})
@TestPropertySource( locations="classpath:application-integrationtest.properties")
@SpringBootTest
public class DealCaptureSpringTestCase extends TransactionalSpringTestCase {

    protected Organization myOrganization;

    protected BusinessContact myBusinessContact;

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public void setUp() {
        super.setUp();
        myOrganization = OrganizationFixture.createOrganization("OnBelay");
        myBusinessContact = BusinessContactFixture.createCompanyTrader("Joe", "Lastman", "lastman@gmail.com");
    }
}
