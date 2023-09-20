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
import com.onbelay.dealcapture.pricing.shared.PriceCurveDetail;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;

import jakarta.persistence.*;

@Entity
@Table(name = "PRICE_CURVE")
@NamedQueries({
    @NamedQuery(
       name = PriceCurveRepositoryBean.FETCH_PRICE_BY_PRICE_DATE_OBS_DATE,
       query = "SELECT price "
       		+ "   FROM PriceCurve price " +
       	     "   WHERE price.priceIndex.id = :priceIndexId "
       	     + "   AND price.detail.priceDate = :priceDate "
       	     + "   AND price.detail.observedDateTime = "
       	     + "    (SELECT MAX(searchPrice.detail.observedDateTime)"
       	     + "       FROM PriceCurve searchPrice"
       	     + "      WHERE searchPrice.priceIndex.id = :priceIndexId"
       	     + "        AND searchPrice.detail.priceDate = :priceDate"
       	     + "        AND searchPrice.detail.observedDateTime <= :currentDateTime"
       	     + "     )  ")
    
})
public class PriceCurve extends TemporalAbstractEntity {
	
	private PriceCurveDetail detail = new PriceCurveDetail();

	private Integer id;

	private PriceIndex priceIndex;
	
	
	protected PriceCurve() {
	}
	
	public PriceCurve(
			PriceIndex priceIndex,
			PriceCurveSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		this.priceIndex = priceIndex;
		save();
	}
	
	@Override
	@Transient
	public String getEntityName() {
		return "PriceCurve";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceCurveGen", sequenceName="PRICE_CURVE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceCurveGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer priceIndexId) {
		this.id = priceIndexId;
	}


	@Embedded
	public PriceCurveDetail getDetail() {
		return detail;
	}


	public void setDetail(PriceCurveDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="PRICE_INDEX_ID")
	public PriceIndex getPriceIndex() {
		return priceIndex;
	}


	protected void setPriceIndex(PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}
	
	
	public void updateWith(PriceCurveSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		update();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.priceIndex == null)
			throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		
	}
	
	
	@Override
	protected AuditAbstractEntity createHistory() {
		PriceCurveAudit audit = PriceCurveAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PriceCurveAudit.findRecentHistory(this);
	}
}
