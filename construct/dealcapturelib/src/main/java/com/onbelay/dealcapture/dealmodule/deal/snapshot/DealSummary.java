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

import java.math.BigDecimal;
import java.time.LocalDate;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;


public class DealSummary {

	private EntityId dealId;
	private String ticketNo;
	private LocalDate startDate;
	private LocalDate endDate;
	private DealTypeCode dealTypeCode;
	private BuySellCode buySellCode;
	private CurrencyCode reportingCurrencyCode;
	private CurrencyCode costCurrencyCode;
	private BigDecimal volumeQuantity;
	private UnitOfMeasureCode volumeUnitOfMeasureCode;
	private CurrencyCode settlementCurrencyCode;

	public DealSummary(
			Integer dealId,
			String ticketNo,
			LocalDate startDate,
			LocalDate endDate,
			String dealTypeCodeValue,
			String buySellCodeValue,
			String reportingCurrencyCodeValue,
			String costCurrencyCodeValue,
			BigDecimal volumeQuantity,
			String volumeUnitOfMeasureCodeValue,
			String settlementCurrencyCodeValue) {
		
		super();
		this.dealId = new EntityId(dealId, ticketNo, ticketNo, false);
		this.ticketNo = ticketNo;
		this.startDate = startDate;
		this.endDate = endDate;
		dealTypeCode = DealTypeCode.lookUp(dealTypeCodeValue);
		this.buySellCode = BuySellCode.lookUp(buySellCodeValue);
		this.reportingCurrencyCode = CurrencyCode.lookUp(reportingCurrencyCodeValue);
		this.costCurrencyCode = CurrencyCode.lookUp(costCurrencyCodeValue);
		this.volumeQuantity = volumeQuantity;
		this.volumeUnitOfMeasureCode = UnitOfMeasureCode.lookUp(volumeUnitOfMeasureCodeValue);
		this.settlementCurrencyCode = CurrencyCode.lookUp(settlementCurrencyCodeValue);
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

	public LocalDate getEndDate() {
		return endDate;
	}

	public DealTypeCode getDealTypeCode() {
		return dealTypeCode;
	}

	public BigDecimal getVolumeQuantity() {
		return volumeQuantity;
	}

	public UnitOfMeasureCode getVolumeUnitOfMeasureCode() {
		return volumeUnitOfMeasureCode;
	}

	public BuySellCode getBuySellCode() {
		return buySellCode;
	}

	public CurrencyCode getReportingCurrencyCode() {
		return reportingCurrencyCode;
	}

	public CurrencyCode getSettlementCurrencyCode() {
		return settlementCurrencyCode;
	}

	public CurrencyCode getCostCurrencyCode() {
		return costCurrencyCode;
	}
}
