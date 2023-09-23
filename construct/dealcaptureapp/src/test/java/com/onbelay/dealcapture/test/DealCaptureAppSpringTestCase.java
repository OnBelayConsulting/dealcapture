package com.onbelay.dealcapture.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.model.OrganizationFixture;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*", "com.onbelay.dealcapture.*"})
@EntityScan(basePackages = {"com.onbelay.*"})
@RunWith(SpringRunner.class)
@TestPropertySource( locations="classpath:application-integrationtest.properties")
@Ignore("Do not run *TestCase classes with JUnit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DealCaptureAppSpringTestCase extends TransactionalSpringTestCase {

    private static KeycloakContainer keycloak;

    static {
        keycloak = new KeycloakContainer().withRealmImportFile("realm-export.json");
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        Integer port = keycloak.getFirstMappedPort();
        String issuer_uri = "http://localhost:" + port + "/realms/master";
        registry.add("spring.security.oauth2.client.provider.okta.issuer-uri", () -> issuer_uri);

        String token_uri = "http://localhost:" + port + "/realms/master/protcol/openid-connect/token";
        registry.add("spring.security.oauth2.client.provider.okta.token-uri", () -> token_uri);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/master");
    }


    protected Organization myOrganization;

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public void setUp() {
        super.setUp();
        myOrganization = OrganizationFixture.createOrganization("OnBelay");
    }

}
