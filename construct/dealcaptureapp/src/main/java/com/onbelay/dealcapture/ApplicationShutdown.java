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
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.time.LocalDateTime;

public class ApplicationShutdown implements ApplicationListener<ContextClosedEvent> {
	private static final Logger logger = LogManager.getLogger(ApplicationShutdown.class);
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		LocalDateTime dateTime = LocalDateTime.now();
		logger.info("OnBelay StrongDesign Managed deals terminated on: " + dateTime.toString());
	}
	
	

}
