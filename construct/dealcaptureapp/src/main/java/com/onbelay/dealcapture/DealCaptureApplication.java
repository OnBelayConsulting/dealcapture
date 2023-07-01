/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@ComponentScan(basePackages = {"com.onbelay.core.*", "com.onbelay.shared.*","com.onbelay.dealcapture" })
@EntityScan("com.onbelay.*")
@SpringBootApplication
public class DealCaptureApplication implements ApplicationRunner {
	private static final Logger logger = LogManager.getLogger(DealCaptureApplication.class);

	
	public static void main(String[] args) {
		
		new SpringApplicationBuilder(DealCaptureApplication.class)
			.listeners(new ApplicationShutdown())
			.run(args);
		
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("============== DealCapture startup at " + LocalDateTime.now().toString());
		ZonedDateTime zdt = ZonedDateTime.now();
		logger.info("Application timezone: " + zdt.getZone());
		logger.info("Total number of available CPUs " + Runtime.getRuntime().availableProcessors());
	}

}
