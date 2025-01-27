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
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import jakarta.persistence.*;

@Entity
@Table(name = "INTEREST_CURVE")
@NamedQueries({
    @NamedQuery(
       name = InterestCurveRepositoryBean.FETCH_RATE_BY_CURVE_DATE_OBS_DATE,
       query = "SELECT curve "
       		+ "   FROM InterestCurve curve " +
       	     "   WHERE curve.interestIndex.id = :interestIndexId "
       	     + "   AND curve.detail.curveDate = :curveDate "
			 + "   AND curve.detail.hourEnding = :hourEnding "
       	     + "   AND curve.detail.observedDateTime = "
       	     + "    (SELECT MAX(searchInterest.detail.observedDateTime)"
       	     + "       FROM InterestCurve searchInterest"
       	     + "      WHERE searchInterest.interestIndex.id = curve.interestIndex.id "
       	     + "        AND searchInterest.detail.curveDate = curve.detail.curveDate "
		     + "        AND searchInterest.detail.hourEnding = curve.detail.hourEnding "
		    +  "        AND searchInterest.detail.frequencyCodeValue = curve.detail.frequencyCodeValue "
       	     + "        AND searchInterest.detail.observedDateTime <= :currentDateTime"
       	     + "     )" +
			   "ORDER BY curve.detail.frequencyCodeValue  ")

})
public class InterestCurve extends TemporalAbstractEntity {
	
	private CurveDetail detail = new CurveDetail();

	private Integer id;

	private InterestIndex interestIndex;
	
	
	protected InterestCurve() {
	}
	
	public static InterestCurve newInterestCurve(
			InterestIndex interestIndex,
			InterestCurveSnapshot snapshot) {
		InterestCurve interestCurve = new InterestCurve();
		interestCurve.createWith(
				interestIndex,
				snapshot);
		return interestCurve;
	}
	
	@Override
	@Transient
	public String getEntityName() {
		return "InterestCurve";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="InterestCurveGen", sequenceName="INTEREST_CURVE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "InterestCurveGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer interestIndexId) {
		this.id = interestIndexId;
	}



	@Embedded
	public CurveDetail getDetail() {
		return detail;
	}


	public void setDetail(CurveDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="INTEREST_INDEX_ID")
	public InterestIndex getInterestIndex() {
		return interestIndex;
	}


	protected void setInterestIndex(InterestIndex interestIndex) {
		this.interestIndex = interestIndex;
	}
	

	private void createWith(
			InterestIndex interestIndex,
			InterestCurveSnapshot snapshot) {

		detail.setDefaults();
		this.detail.copyFrom(snapshot.getDetail());
		interestIndex.addInterestCurve(this);
	}

	public void updateWith(InterestCurveSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		update();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.interestIndex == null)
			throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		
	}
	
	
	@Override
	protected AuditAbstractEntity createHistory() {
		InterestCurveAudit audit = InterestCurveAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return InterestCurveAudit.findRecentHistory(this);
	}
}
