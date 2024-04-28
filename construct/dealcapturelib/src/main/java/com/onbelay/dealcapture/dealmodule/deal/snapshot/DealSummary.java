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

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;


public class DealSummary {

	private Integer dealId;
	private Integer powerProfileId;
	private String ticketNo;
	private LocalDate startDate;
	private LocalDate endDate;
	private DealTypeCode dealTypeCode;
	private BuySellCode buySellCode;
	private CurrencyCode reportingCurrencyCode;
	private BigDecimal volumeQuantity;
	private UnitOfMeasureCode volumeUnitOfMeasureCode;
	private FrequencyCode volumeFrequencyCode;
	private CurrencyCode settlementCurrencyCode;

	public DealSummary(
			Integer dealId,
			Integer powerProfileId,
			String ticketNo,
			LocalDate startDate,
			LocalDate endDate,
			String dealTypeCodeValue,
			String buySellCodeValue,
			String reportingCurrencyCodeValue,
			BigDecimal volumeQuantity,
			String volumeUnitOfMeasureCodeValue,
			String volumeFrequencyCodeValue,
			String settlementCurrencyCodeValue) {
		
		super();
		this.powerProfileId = powerProfileId;
		this.dealId = dealId;
		this.ticketNo = ticketNo;
		this.startDate = startDate;
		this.endDate = endDate;
		dealTypeCode = DealTypeCode.lookUp(dealTypeCodeValue);
		this.buySellCode = BuySellCode.lookUp(buySellCodeValue);
		this.reportingCurrencyCode = CurrencyCode.lookUp(reportingCurrencyCodeValue);
		this.volumeQuantity = volumeQuantity;
		this.volumeUnitOfMeasureCode = UnitOfMeasureCode.lookUp(volumeUnitOfMeasureCodeValue);
		this.volumeFrequencyCode = FrequencyCode.lookUp(volumeFrequencyCodeValue);
		this.settlementCurrencyCode = CurrencyCode.lookUp(settlementCurrencyCodeValue);
	}

	public Integer getDealId() {
		return dealId;
	}

	public Integer getPowerProfileId() {
		return powerProfileId;
	}

	public boolean hasPowerProfile() {
		return powerProfileId != null;
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

	public FrequencyCode getVolumeFrequencyCode() {
		return volumeFrequencyCode;
	}
}
