package com.onbelay.dealcapture.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.model.OrganizationFixture;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*", "com.onbelay.dealcapture.*"})
@EntityScan(basePackages = {"com.onbelay.*"})
@RunWith(SpringRunner.class)
@TestPropertySource( locations="classpath:application-integrationtest.properties")
@SpringBootTest
@Ignore("Do not run *TestCase classes with JUnit")
public class DealCaptureSpringTestCase extends TransactionalSpringTestCase {

    protected Organization myOrganization;

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public void setUp() {
        super.setUp();
        myOrganization = OrganizationFixture.createOrganization("OnBelay");
    }
}
