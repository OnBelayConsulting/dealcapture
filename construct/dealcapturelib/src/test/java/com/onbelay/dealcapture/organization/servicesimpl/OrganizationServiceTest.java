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
package com.onbelay.dealcapture.organization.servicesimpl;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationServiceTest extends DealCaptureSpringTestCase {

	@Autowired
	private OrganizationService organizationService;


	public void setUp() {
		super.setUp();

		flush();
	}


	@Test
	public void createOrganization() {

		OrganizationSnapshot snapshot = new OrganizationSnapshot();
		snapshot.getDetail().setLegalName("DDD");
		snapshot.getDetail().setShortName("EEE");
		TransactionResult result  = organizationService.save(snapshot);
		flush();
		clearCache();
		
		assertTrue(result.isSuccessful());
	}
	
}
