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

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexAudit;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
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
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexDetail;

@Entity
@Table(name = "POSITION_RISK_FACTOR_MAP_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PositionRiskFactorMappingAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM PositionRiskFactorMappingAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.positionRiskFactorMapping = :mapping")
})
public class PositionRiskFactorMappingAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PositionRiskFactorMappingAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PositionRiskFactorMappingDetail detail = new PositionRiskFactorMappingDetail();

	private PositionRiskFactorMapping positionRiskFactorMapping;

	private DealPosition dealPosition;

	private PriceRiskFactor priceRiskFactor;

	private FxRiskFactor fxRiskFactor;


	protected PositionRiskFactorMappingAudit() {
		
	}


	protected static PositionRiskFactorMappingAudit create(PositionRiskFactorMapping mapping) {
		PositionRiskFactorMappingAudit audit = new PositionRiskFactorMappingAudit();
		audit.positionRiskFactorMapping = mapping;
		audit.copyFrom(mapping);
		return audit;
	}


	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceIndexAuditGen", sequenceName="PRICE_INDEX_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceIndexAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer priceIndexId) {
		this.id = priceIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PositionRiskFactorMapping getPositionRiskFactorMapping() {
		return positionRiskFactorMapping;
	}

	protected void setPositionRiskFactorMapping(PositionRiskFactorMapping positionRiskFactorMapping) {
		this.positionRiskFactorMapping = positionRiskFactorMapping;
	}

	@Embedded
	public PositionRiskFactorMappingDetail getDetail() {
		return detail;
	}

	public void setDetail(PositionRiskFactorMappingDetail detail) {
		this.detail = detail;
	}

	@ManyToOne()
	@JoinColumn(name = "DEAL_POSITION_ID")
	public DealPosition getDealPosition() {
		return dealPosition;
	}

	public void setDealPosition(DealPosition dealPosition) {
		this.dealPosition = dealPosition;
	}

	@ManyToOne()
	@JoinColumn(name = "PRICE_RISK_FACTOR_ID")
	public PriceRiskFactor getPriceRiskFactor() {
		return priceRiskFactor;
	}

	public void setPriceRiskFactor(PriceRiskFactor priceRiskFactor) {
		this.priceRiskFactor = priceRiskFactor;
	}

	@ManyToOne()
	@JoinColumn(name = "FX_RISK_FACTOR_ID")
	public FxRiskFactor getFxRiskFactor() {
		return fxRiskFactor;
	}

	public void setFxRiskFactor(FxRiskFactor fxRiskFactor) {
		this.fxRiskFactor = fxRiskFactor;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return positionRiskFactorMapping;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PositionRiskFactorMapping mapping = (PositionRiskFactorMapping) entity;
		this.detail.copyFrom(mapping.getDetail());
		this.dealPosition = mapping.getDealPosition();
		this.priceRiskFactor = mapping.getPriceRiskFactor();
		this.fxRiskFactor = mapping.getFxRiskFactor();
	}


	public static PositionRiskFactorMappingAudit findRecentHistory(PositionRiskFactorMapping mapping) {
		String[] parmNames = {"mapping", "date" };
		Object[] parms =     {mapping,   DateUtils.getValidToDateTime()};

		return (PositionRiskFactorMappingAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
