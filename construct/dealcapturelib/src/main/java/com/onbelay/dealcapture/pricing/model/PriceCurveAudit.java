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
import com.onbelay.dealcapture.pricing.shared.PriceCurveDetail;

@Entity
@Table(name = "PRICE_CURVE_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PriceCurveAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT indexPriceAudit " +
			   "  FROM PriceCurveAudit indexPriceAudit " +
       		    "WHERE indexPriceAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND indexPriceAudit.priceCurve = :priceCurve")
})
public class PriceCurveAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PriceCurveAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PriceCurve priceCurve;
	
	private PriceIndex priceIndex;

	private PriceCurveDetail detail = new PriceCurveDetail();
	
	
	protected PriceCurveAudit() {
		
	}
	
	protected static PriceCurveAudit create(PriceIndex priceIndex) {
		PriceCurveAudit audit = new PriceCurveAudit();
		audit.priceIndex = priceIndex;
		audit.copyFrom(priceIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceCurveAuditGen", sequenceName="PRICE_CURVE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceCurveAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PriceCurve getPriceCurve() {
		return priceCurve;
	}

	private void setPriceCurve(PriceCurve priceCurve) {
		this.priceCurve = priceCurve;
	}

	@ManyToOne
	@JoinColumn(name ="PRICE_INDEX_ID")
	public PriceIndex getPriceIndex() {
		return priceIndex;
	}

	private void setPriceIndex(PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}

	@Embedded
	public PriceCurveDetail getDetail() {
		return detail;
	}

	private void setDetail(PriceCurveDetail detail) {
		this.detail = detail;
	}


	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return priceCurve;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PriceCurve priceCurve = (PriceCurve) entity;
		this.priceIndex = priceCurve.getPriceIndex();
		this.detail.copyFrom(priceCurve.getDetail());
	}

	public static PriceCurveAudit create(PriceCurve priceCurveIn) {
		PriceCurveAudit audit = new PriceCurveAudit();
		audit.setPriceCurve(priceCurveIn);
		audit.copyFrom(priceCurveIn);
		return audit;
	}


	public static PriceCurveAudit findRecentHistory(PriceCurve priceCurve) {
		String[] parmNames = {"priceCurve", "date" };
		Object[] parms =     {priceCurve,   DateUtils.getValidToDateTime()};

		return (PriceCurveAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
