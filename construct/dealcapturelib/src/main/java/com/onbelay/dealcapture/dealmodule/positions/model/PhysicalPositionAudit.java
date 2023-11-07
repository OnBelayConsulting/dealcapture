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
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;

@Entity
@Table (name = "PHYSICAL_POSITION_AUDIT")
public class PhysicalPositionAudit extends DealPositionAudit {
	public static final String FIND_AUDIT_BY_TO_DATE = "PhysicalPositionAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	private PhysicalPositionDetail detail = new PhysicalPositionDetail();

	private FxRiskFactor dealPriceFxRiskFactor;

	private PriceRiskFactor marketPriceRiskFactor;
	private FxRiskFactor marketPriceFxRiskFactor;

	protected PhysicalPositionAudit() {
		super.getDealPositionDetail().setDealTypeCode(DealTypeCode.PHYSICAL_DEAL);
	}

	@Embedded
	public PhysicalPositionDetail getDetail() {
		return detail;
	}


	private void setDetail(PhysicalPositionDetail detail) {
		this.detail = detail;
	}



	@ManyToOne
	@JoinColumn(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
	public FxRiskFactor getDealPriceFxRiskFactor() {
		return dealPriceFxRiskFactor;
	}

	public void setDealPriceFxRiskFactor(FxRiskFactor dealPriceFxRiskFactor) {
		this.dealPriceFxRiskFactor = dealPriceFxRiskFactor;
	}

	@ManyToOne
	@JoinColumn(name = "MKT_PRICE_RISK_FACTOR_ID")
	public PriceRiskFactor getMarketPriceRiskFactor() {
		return marketPriceRiskFactor;
	}

	public void setMarketPriceRiskFactor(PriceRiskFactor marketPriceRiskFactor) {
		this.marketPriceRiskFactor = marketPriceRiskFactor;
	}

	@ManyToOne
	@JoinColumn(name = "MKT_PRICE_FX_RISK_FACTOR_ID")
	public FxRiskFactor getMarketPriceFxRiskFactor() {
		return marketPriceFxRiskFactor;
	}

	public void setMarketPriceFxRiskFactor(FxRiskFactor marketPriceFxRiskFactor) {
		this.marketPriceFxRiskFactor = marketPriceFxRiskFactor;
	}

	public static PhysicalPositionAudit create(PhysicalPosition physicalPosition) {

		PhysicalPositionAudit audit = new PhysicalPositionAudit();
		audit.setDealPosition(physicalPosition);
		audit.copyFrom(physicalPosition);
		audit.save();
		return audit;

	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		super.copyFrom(entity);
		PhysicalPosition physicalPosition = (PhysicalPosition) entity;
		this.dealPriceFxRiskFactor = physicalPosition.getDealPriceFxRiskFactor();
		this.marketPriceRiskFactor = physicalPosition.getMarketPriceRiskFactor();
		this.marketPriceFxRiskFactor = physicalPosition.getMarketPriceFxRiskFactor();
		this.detail.copyFrom(physicalPosition.getDetail());
	}

}
