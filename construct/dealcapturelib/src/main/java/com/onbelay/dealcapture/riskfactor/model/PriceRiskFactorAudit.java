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
package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.RiskFactorDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "PRICE_RISK_FACTOR_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PriceRiskFactorAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT riskFactorAudit " +
			   "  FROM PriceRiskFactorAudit riskFactorAudit " +
       		    "WHERE riskFactorAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND riskFactorAudit.priceRiskFactor = :riskFactor")
})
public class PriceRiskFactorAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PriceRiskFactorAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	
	private PriceIndex priceIndex;
	private PriceRiskFactor priceRiskFactor;
	private RiskFactorDetail detail = new RiskFactorDetail();
	
	protected PriceRiskFactorAudit() {
		
	}
	
	protected PriceRiskFactorAudit(PriceRiskFactor priceRiskFactor) {
		this.priceRiskFactor = priceRiskFactor;
	}

	public static PriceRiskFactorAudit create(
			PriceRiskFactor riskFactor) {
		
		PriceRiskFactorAudit audit = new PriceRiskFactorAudit(riskFactor);
		audit.priceIndex = riskFactor.getIndex();
		audit.detail.copyFrom(riskFactor.getDetail());
		audit.save();
		return audit;
		
	}
	
	

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceRiskFactorAuditGen", sequenceName="PRICE_RISK_FACTOR_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceRiskFactorAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer riskFactorAuditId) {
		this.id = riskFactorAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PriceRiskFactor getPriceRiskFactor() {
		return priceRiskFactor;
	}

	public void setPriceRiskFactor(PriceRiskFactor riskFactor) {
		this.priceRiskFactor = riskFactor;
	}

	@ManyToOne()
	@JoinColumn(name = "PRICE_INDEX_ID")
	public PriceIndex getPriceIndex() {
		return this.priceIndex;
	}

	public void setPriceIndex(final PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}

	@Embedded
	public RiskFactorDetail getDetail() {
		return detail;
	}

	public void setDetail(RiskFactorDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return priceRiskFactor;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PriceRiskFactor riskFactor = (PriceRiskFactor) entity;
		this.priceIndex = riskFactor.getIndex();
		this.detail.copyFrom(riskFactor.getDetail());
	}


	public static PriceRiskFactorAudit findRecentHistory(PriceRiskFactor riskFactor) {
		String[] parmNames = {"riskFactor", "date" };
		Object[] parms =     {riskFactor,   DateUtils.getValidToDateTime()};

		return (PriceRiskFactorAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
