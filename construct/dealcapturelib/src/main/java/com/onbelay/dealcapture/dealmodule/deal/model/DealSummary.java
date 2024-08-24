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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDetail;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;


//@Entity
//@Table(name = "DEAL_SUMMARY_VIEW")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "DEAL_TYPE_CODE")
//@Immutable
@NamedQueries({
		@NamedQuery(
				name = DealRepositoryBean.FETCH_ASSIGNED_DEAL_SUMMARIES,
				query = "SELECT summary " +
				  "        FROM DealSummary summary " +
  				   "      WHERE summary.dealDetail.positionGenerationIdentifier = :identifier " +
						"   AND summary.dealDetail.positionGenerationStatusValue = 'Generating'" +
					"  ORDER BY summary.dealDetail.ticketNo DESC"),
		@NamedQuery(
				name = DealRepositoryBean.FETCH_DEAL_SUMMARIES,
				query = "SELECT summary " +
						"  FROM DealSummary summary " +
						" WHERE summary.id in (:dealIds) " +
					"  ORDER BY summary.dealDetail.ticketNo DESC")
})
public abstract class DealSummary extends AbstractEntity {

	private Integer id;
	private Integer powerProfileId;

	private DealDetail dealDetail = new DealDetail();

	private String dealTypeCodeValue;

	public DealSummary() {
	}

	protected DealSummary(DealTypeCode dealTypeCode) {
		setDealTypeCode(dealTypeCode);
	}

	@Override
	@Id
	@Column(name="ENTITY_ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Transient
	@JsonIgnore
	public DealTypeCode getDealTypeCode() {
		return DealTypeCode.lookUp(dealTypeCodeValue);
	}

	public void setDealTypeCode(DealTypeCode code) {
		this.dealTypeCodeValue = code.getCode();
	}

	@Column(name = "DEAL_TYPE_CODE", insertable = false, updatable = false)
	private String getDealTypeCodeValue() {
		return dealTypeCodeValue;
	}

	private void setDealTypeCodeValue(String dealTypeCodeValue) {
		this.dealTypeCodeValue = dealTypeCodeValue;
	}



	@Column(name="POWER_PROFILE_ID")
	public Integer getPowerProfileId() {
		return powerProfileId;
	}

	public void setPowerProfileId(Integer powerProfileId) {
		this.powerProfileId = powerProfileId;
	}

	@Embedded
	public DealDetail getDealDetail() {
		return dealDetail;
	}

	public void setDealDetail(DealDetail dealDetail) {
		this.dealDetail = dealDetail;
	}

	@Override
	protected void validate() throws OBValidationException {

	}

	@Transient
	public boolean hasPowerProfile() {
		return powerProfileId != null;
	}
}
