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
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.snapshot.CurveDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "FX_CURVE_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = FxCurveAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT indexFxAudit " +
			   "  FROM FxCurveAudit indexFxAudit " +
       		    "WHERE indexFxAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND indexFxAudit.fxCurve = :fxCurve")
})
public class FxCurveAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "FxCurveAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private FxCurve fxCurve;
	
	private FxIndex fxIndex;

	private CurveDetail detail = new CurveDetail();
	
	
	protected FxCurveAudit() {
		
	}
	
	protected static FxCurveAudit create(FxIndex fxIndex) {
		FxCurveAudit audit = new FxCurveAudit();
		audit.fxIndex = fxIndex;
		audit.copyFrom(fxIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxCurveAuditGen", sequenceName="FX_CURVE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxCurveAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public FxCurve getFxCurve() {
		return fxCurve;
	}

	private void setFxCurve(FxCurve fxCurve) {
		this.fxCurve = fxCurve;
	}

	@ManyToOne
	@JoinColumn(name ="FX_INDEX_ID")
	public FxIndex getFxIndex() {
		return fxIndex;
	}

	private void setFxIndex(FxIndex fxIndex) {
		this.fxIndex = fxIndex;
	}

	@Embedded
	public CurveDetail getDetail() {
		return detail;
	}

	private void setDetail(CurveDetail detail) {
		this.detail = detail;
	}


	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return fxCurve;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		FxCurve fxCurve = (FxCurve) entity;
		this.fxIndex = fxCurve.getFxIndex();
		this.detail.copyFrom(fxCurve.getDetail());
	}

	public static FxCurveAudit create(FxCurve fxCurveIn) {
		FxCurveAudit audit = new FxCurveAudit();
		audit.setFxCurve(fxCurveIn);
		audit.copyFrom(fxCurveIn);
		return audit;
	}


	public static FxCurveAudit findRecentHistory(FxCurve fxCurve) {
		String[] parmNames = {"fxCurve", "date" };
		Object[] parms =     {fxCurve,   DateUtils.getValidToDateTime()};

		return (FxCurveAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
