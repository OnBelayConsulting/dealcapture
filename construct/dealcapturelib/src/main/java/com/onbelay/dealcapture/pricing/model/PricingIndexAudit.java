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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.shared.PricingIndexDetail;

@Entity
@Table(name = "PRICING_INDEX_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PricingIndexAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT pricingIndexAudit " +
			   "  FROM PricingIndexAudit pricingIndexAudit " +
       		    "WHERE pricingIndexAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND pricingIndexAudit.pricingIndex = :pricingIndex")
})
public class PricingIndexAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PricingIndexAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PricingIndex pricingIndex;

	private PricingLocation pricingLocation;
	
	private PricingIndexDetail detail = new PricingIndexDetail();
	
	private PricingIndex benchmarkPricingIndex;
	
	private PricingIndex basePricingIndex;
	
	
	protected PricingIndexAudit() {
		
	}
	
	protected static PricingIndexAudit create(PricingIndex pricingIndex) {
		PricingIndexAudit audit = new  PricingIndexAudit();
		audit.pricingIndex = pricingIndex;
		audit.copyFrom(pricingIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PricingIndexAuditGen", sequenceName="PRICING_INDEX_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PricingIndexAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PricingIndex getPricingIndex() {
		return pricingIndex;
	}

	private void setPricingIndex(PricingIndex pricingIndex) {
		this.pricingIndex = pricingIndex;
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
	public PricingIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(PricingIndexDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="BENCH_PRICING_INDEX_ID")
	public PricingIndex getBenchmarkPricingIndex() {
		return benchmarkPricingIndex;
	}

	private void setBenchmarkPricingIndex(PricingIndex indexBenchmarkedTo) {
		this.benchmarkPricingIndex = indexBenchmarkedTo;
	}

	@ManyToOne
	@JoinColumn(name ="BASE_PRICING_INDEX_ID")
	public PricingIndex getBasePricingIndex() {
		return basePricingIndex;
	}

	private void setBasePricingIndex(PricingIndex indexIsBasedOn) {
		this.basePricingIndex = indexIsBasedOn;
	}


	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return pricingIndex;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PricingIndex pricingIndex = (PricingIndex) entity;
		this.pricingLocation = pricingIndex.getPricingLocation();
		this.basePricingIndex = pricingIndex.getBasePricingIndex();
		this.benchmarkPricingIndex = pricingIndex.getBenchmarkPricingIndex();
		this.detail.copyFrom(pricingIndex.getDetail());
	}


	public static PricingIndexAudit findRecentHistory(PricingIndex pricingIndex) {
		String[] parmNames = {"pricingIndex", "date" };
		Object[] parms =     {pricingIndex,   DateUtils.getValidToDateTime()};

		return (PricingIndexAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
