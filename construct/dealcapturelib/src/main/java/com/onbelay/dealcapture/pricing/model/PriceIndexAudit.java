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
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "PRICE_INDEX_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PriceIndexAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT priceIndexAudit " +
			   "  FROM PriceIndexAudit priceIndexAudit " +
       		    "WHERE priceIndexAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND priceIndexAudit.priceIndex = :priceIndex")
})
public class PriceIndexAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PriceIndexAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PriceIndex priceIndex;

	private PricingLocation pricingLocation;
	
	private PriceIndexDetail detail = new PriceIndexDetail();
	
	private PriceIndex benchmarkPriceIndex;
	
	private PriceIndex basePriceIndex;
	
	
	protected PriceIndexAudit() {
		
	}
	
	protected static PriceIndexAudit create(PriceIndex priceIndex) {
		PriceIndexAudit audit = new PriceIndexAudit();
		audit.priceIndex = priceIndex;
		audit.copyFrom(priceIndex);
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
	public PriceIndex getPriceIndex() {
		return priceIndex;
	}

	private void setPriceIndex(PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}

	@ManyToOne
	@JoinColumn(name ="PRICING_LOCATION_ID")
	public PricingLocation getPricingLocation() {
		return pricingLocation;
	}

	private void setPricingLocation(PricingLocation pricingLocation) {
		this.pricingLocation = pricingLocation;
	}

	@Embedded
	public PriceIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(PriceIndexDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="BENCH_PRICE_INDEX_ID")
	public PriceIndex getBenchmarkPricingIndex() {
		return benchmarkPriceIndex;
	}

	private void setBenchmarkPricingIndex(PriceIndex indexBenchmarkedTo) {
		this.benchmarkPriceIndex = indexBenchmarkedTo;
	}

	@ManyToOne
	@JoinColumn(name ="BASE_PRICE_INDEX_ID")
	public PriceIndex getBasePricingIndex() {
		return basePriceIndex;
	}

	private void setBasePricingIndex(PriceIndex indexIsBasedOn) {
		this.basePriceIndex = indexIsBasedOn;
	}


	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return priceIndex;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PriceIndex priceIndex = (PriceIndex) entity;
		this.pricingLocation = priceIndex.getPricingLocation();
		this.basePriceIndex = priceIndex.getBasePricingIndex();
		this.benchmarkPriceIndex = priceIndex.getBenchmarkPricingIndex();
		this.detail.copyFrom(priceIndex.getDetail());
	}


	public static PriceIndexAudit findRecentHistory(PriceIndex priceIndex) {
		String[] parmNames = {"priceIndex", "date" };
		Object[] parms =     {priceIndex,   DateUtils.getValidToDateTime()};

		return (PriceIndexAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
