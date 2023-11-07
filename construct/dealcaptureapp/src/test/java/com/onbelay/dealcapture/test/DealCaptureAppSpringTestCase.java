package com.onbelay.dealcapture.test;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*", "com.onbelay.dealcapture.*"})
@EntityScan(basePackages = {"com.onbelay.*"})
@TestPropertySource( locations="classpath:application-integrationtest.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DealCaptureAppSpringTestCase extends DealCaptureSpringTestCase {

}
