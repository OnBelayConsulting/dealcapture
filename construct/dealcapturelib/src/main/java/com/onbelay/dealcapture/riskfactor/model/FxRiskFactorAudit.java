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
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.RiskFactorDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "FX_RISK_FACTOR_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = FxRiskFactorAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT riskFactorAudit " +
			   "  FROM FxRiskFactorAudit riskFactorAudit " +
       		    "WHERE riskFactorAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND riskFactorAudit.fxRiskFactor = :riskFactor")
})
public class FxRiskFactorAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "FxRiskFactorAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	
	private FxIndex fxIndex;
	private FxRiskFactor fxRiskFactor;
	private RiskFactorDetail detail = new RiskFactorDetail();
	
	protected FxRiskFactorAudit() {
		
	}
	
	protected FxRiskFactorAudit(FxRiskFactor fxRiskFactor) {
		this.fxRiskFactor = fxRiskFactor;
	}

	public static FxRiskFactorAudit create(
			FxRiskFactor riskFactor) {
		
		FxRiskFactorAudit audit = new FxRiskFactorAudit(riskFactor);
		audit.fxIndex = riskFactor.getIndex();
		audit.detail.copyFrom(riskFactor.getDetail());
		audit.save();
		return audit;
		
	}
	
	

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxRiskFactorAuditGen", sequenceName="FX_RISK_FACTOR_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxRiskFactorAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer riskFactorAuditId) {
		this.id = riskFactorAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public FxRiskFactor getFxRiskFactor() {
		return fxRiskFactor;
	}

	public void setFxRiskFactor(FxRiskFactor riskFactor) {
		this.fxRiskFactor = riskFactor;
	}

	@ManyToOne()
	@JoinColumn(name = "FX_INDEX_ID")
	public FxIndex getFxIndex() {
		return this.fxIndex;
	}

	public void setFxIndex(final FxIndex fxIndex) {
		this.fxIndex = fxIndex;
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
		return fxRiskFactor;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		FxRiskFactor riskFactor = (FxRiskFactor) entity;
		this.fxIndex = riskFactor.getIndex();
		this.detail.copyFrom(riskFactor.getDetail());
	}


	public static FxRiskFactorAudit findRecentHistory(FxRiskFactor riskFactor) {
		String[] parmNames = {"riskFactor", "date" };
		Object[] parms =     {riskFactor,   DateUtils.getValidToDateTime()};

		return (FxRiskFactorAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
