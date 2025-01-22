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
@Table(name = "INTEREST_CURVE_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = InterestCurveAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT indexInterestAudit " +
			   "  FROM InterestCurveAudit indexInterestAudit " +
       		    "WHERE indexInterestAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND indexInterestAudit.interestCurve = :interestCurve")
})
public class InterestCurveAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "InterestCurveAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private InterestCurve interestCurve;
	
	private InterestIndex interestIndex;

	private CurveDetail detail = new CurveDetail();
	
	
	protected InterestCurveAudit() {
		
	}
	
	protected static InterestCurveAudit create(InterestIndex interestIndex) {
		InterestCurveAudit audit = new InterestCurveAudit();
		audit.interestIndex = interestIndex;
		audit.copyFrom(interestIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="InterestCurveAuditGen", sequenceName="INTEREST_CURVE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "InterestCurveAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public InterestCurve getInterestCurve() {
		return interestCurve;
	}

	private void setInterestCurve(InterestCurve interestCurve) {
		this.interestCurve = interestCurve;
	}

	@ManyToOne
	@JoinColumn(name ="INTEREST_INDEX_ID")
	public InterestIndex getInterestIndex() {
		return interestIndex;
	}

	private void setInterestIndex(InterestIndex interestIndex) {
		this.interestIndex = interestIndex;
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
		return interestCurve;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		InterestCurve interestCurve = (InterestCurve) entity;
		this.interestIndex = interestCurve.getInterestIndex();
		this.detail.copyFrom(interestCurve.getDetail());
	}

	public static InterestCurveAudit create(InterestCurve interestCurveIn) {
		InterestCurveAudit audit = new InterestCurveAudit();
		audit.setInterestCurve(interestCurveIn);
		audit.copyFrom(interestCurveIn);
		return audit;
	}


	public static InterestCurveAudit findRecentHistory(InterestCurve interestCurve) {
		String[] parmNames = {"interestCurve", "date" };
		Object[] parms =     {interestCurve,   DateUtils.getValidToDateTime()};

		return (InterestCurveAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
