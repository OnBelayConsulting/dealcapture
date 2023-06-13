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


public class DealSummary {

	private EntityId dealId;
	private String ticketNo;
	private LocalDate startDate;
	private String companyName;
	private String counterpartyName;
	
	
	
	
	public DealSummary(
			Integer dealKey,
			String ticketNo, 
			LocalDate startDate, 
			String companyName,
			String counterpartyName) {
		
		super();
		this.dealId = new EntityId(dealKey, ticketNo, ticketNo, false);
		this.ticketNo = ticketNo;
		this.startDate = startDate;
		this.companyName = companyName;
		this.counterpartyName = counterpartyName;
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
	
	
	
	
}
