package com.onbelay.dealcapture.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.organization.model.Organization;
import com.onbelay.dealcapture.organization.model.OrganizationFixture;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*", "com.onbelay.dealcapture.*"})
@EntityScan(basePackages = {"com.onbelay.*"})
@TestPropertySource( locations="classpath:application-integrationtest.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
public class DealCaptureAppSpringTestCase extends TransactionalSpringTestCase {

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer();

//        keycloak = new KeycloakContainer().withRealmImportFile("realm-export.json");

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        Integer port = keycloak.getFirstMappedPort();
        String issuer_uri = "http://localhost:" + port + "/realms/master";
        registry.add("spring.security.oauth2.client.provider.okta.issuer-uri", () -> issuer_uri);

        String token_uri = "http://localhost:" + port + "/realms/master/protocol/openid-connect/token";
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
