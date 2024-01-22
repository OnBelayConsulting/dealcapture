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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import java.time.LocalDate;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;


public class DealSummary {

	private EntityId dealId;
	private String ticketNo;
	private LocalDate startDate;
	private String companyName;
	private String counterpartyName;
	private String dealStatusValue;
	private String positionGenerationStatusValue;
	private String positionGenerationIdentifier;
	
	
	
	public DealSummary(
			Integer dealKey,
			String ticketNo, 
			LocalDate startDate, 
			String companyName,
			String counterpartyName,
			String dealStatusValue,
			String positionGenerationStatusValue,
			String positionGenerationIdentifier) {
		
		super();
		this.dealId = new EntityId(dealKey, ticketNo, ticketNo, false);
		this.ticketNo = ticketNo;
		this.startDate = startDate;
		this.companyName = companyName;
		this.counterpartyName = counterpartyName;
		this.dealStatusValue = dealStatusValue;
		this.positionGenerationStatusValue = positionGenerationStatusValue;
		this.positionGenerationIdentifier = positionGenerationIdentifier;
	}
	
	public EntityId getDealId() {
		return dealId;
	}
	public String getTicketNo() {
		return ticketNo;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getCounterpartyName() {
		return counterpartyName;
	}
	
	public DealStatusCode getDealStatusCode() {
		return DealStatusCode.lookUp(dealStatusValue);
	}

	public PositionGenerationStatusCode getPositionGenerationStatusCode() {
		return PositionGenerationStatusCode.lookUp(positionGenerationStatusValue);
	}

	public String getPositionGenerationIdentifier() {
		return positionGenerationIdentifier;
	}
}
