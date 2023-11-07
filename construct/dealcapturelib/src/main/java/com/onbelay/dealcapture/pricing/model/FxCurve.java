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
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.snapshot.CurveDetail;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import jakarta.persistence.*;

@Entity
@Table(name = "FX_CURVE")
@NamedQueries({
    @NamedQuery(
       name = FxCurveRepositoryBean.FETCH_FX_BY_FX_DATE_OBS_DATE,
       query = "SELECT fx "
       		+ "   FROM FxCurve fx " +
       	     "   WHERE fx.fxIndex.id = :fxIndexId "
       	     + "   AND fx.detail.curveDate = :fxDate "
       	     + "   AND fx.detail.observedDateTime = "
       	     + "    (SELECT MAX(searchFx.detail.observedDateTime)"
       	     + "       FROM FxCurve searchFx"
       	     + "      WHERE searchFx.fxIndex.id = :fxIndexId"
       	     + "        AND searchFx.detail.curveDate = :fxDate"
       	     + "        AND searchFx.detail.observedDateTime <= :currentDateTime"
       	     + "     )  ")
    
})
public class FxCurve extends TemporalAbstractEntity {
	
	private CurveDetail detail = new CurveDetail();

	private Integer id;

	private FxIndex fxIndex;
	
	
	protected FxCurve() {
	}
	
	public FxCurve(
			FxIndex fxIndex,
			FxCurveSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		this.fxIndex = fxIndex;
		save();
	}
	
	@Override
	@Transient
	public String getEntityName() {
		return "FxCurve";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxCurveGen", sequenceName="FX_CURVE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxCurveGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer fxIndexId) {
		this.id = fxIndexId;
	}


	@Embedded
	public CurveDetail getDetail() {
		return detail;
	}


	public void setDetail(CurveDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="FX_INDEX_ID")
	public FxIndex getFxIndex() {
		return fxIndex;
	}


	protected void setFxIndex(FxIndex fxIndex) {
		this.fxIndex = fxIndex;
	}
	
	
	public void updateWith(FxCurveSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		update();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.fxIndex == null)
			throw new OBValidationException(PricingErrorCode.MISSING_FX_INDEX.getCode());
		
	}
	
	
	@Override
	protected AuditAbstractEntity createHistory() {
		FxCurveAudit audit = FxCurveAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return FxCurveAudit.findRecentHistory(this);
	}
}
