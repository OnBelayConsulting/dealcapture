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

import java.util.List;

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
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.shared.PricingIndexDetail;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;

@Entity
@Table(name = "PRICING_INDEX")
@NamedQueries({
    @NamedQuery(
       name = PricingIndexRepositoryBean.FIND_BY_NAME,
       query = "SELECT pricingIndex " +
			   "  FROM PricingIndex pricingIndex " +
       	     "   WHERE pricingIndex.detail.name = :name ")
    
})
public class PricingIndex extends TemporalAbstractEntity {

	private Integer id;
	
	private PricingLocation pricingLocation;
	
	private PricingIndexDetail detail = new PricingIndexDetail();
	
	private PricingIndex benchmarkPricingIndex;
	
	private PricingIndex basePricingIndex;
	
	
	protected PricingIndex() {
		
	}
	
	public PricingIndex(PricingIndexSnapshot snapshot) {
		createWith(snapshot);
		save();
	}
	
	
	@Override
	@Transient
	public String getEntityName() {
		return "PricingIndex";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PricingIndexGen", sequenceName="PRICING_INDEX_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PricingIndexGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}

	@ManyToOne
	@JoinColumn(name ="PRICING_LOCATION_ID")
	public PricingLocation getPricingLocation() {
		return pricingLocation;
	}

	private void setPricingLocation(PricingLocation pricingLocation) {
		this.pricingLocation = pricingLocation;
	}

	@Override
	public EntityId generateEntityId() {
		return new EntityId(
				getId(),
				detail.getName(),
				detail.getDescription(),
				getIsExpired());
	}
	
	@Embedded
	public PricingIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(PricingIndexDetail detail) {
		this.detail = detail;
	}
	
	protected void createWith(PricingIndexSnapshot snapshot) {
		super.createWith(snapshot);
		detail.setDefaults();
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		save();
	}
	
	public void updateWith(PricingIndexSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		update();
	}
	

	public void updatePricesWith(List<IndexPriceSnapshot> prices) {
		IndexPrice price = null;
		for (IndexPriceSnapshot s : prices) {
			if (s.getEntityState() == EntityState.NEW) {
				price = new IndexPrice(this, s);
			} if (s.getEntityState() == EntityState.MODIFIED) {
				price = getPriceRepository().load(s.getEntityId());
				price.updateWith(s);
			}  if (s.getEntityState() == EntityState.DELETE) {
				price = getPriceRepository().load(s.getEntityId());
				price.delete();
			}
		}
	}
	

	
	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.pricingLocation == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICING_LOCATION.getCode());
		
		if (detail.getIndexType() == IndexType.BASIS) {
			if (this.basePricingIndex == null)
				throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		}
		
	}
	
	private void setAssociations(PricingIndexSnapshot snapshot) {
		
		if (snapshot.getBenchmarkIndexId() != null) {
			this.benchmarkPricingIndex = getPricingIndexRepository().load(snapshot.getBenchmarkIndexId());
		}
		
		if (snapshot.getBaseIndexId() != null) {
			basePricingIndex = getPricingIndexRepository().load(snapshot.getBaseIndexId());
		}
		
		if (snapshot.getPricingLocationId() != null) {
			this.pricingLocation = getLocationRepository().load(snapshot.getPricingLocationId());
		}
	}
	

	@ManyToOne
	@JoinColumn(name ="BENCH_PRICING_INDEX_ID")
	public PricingIndex getBenchmarkPricingIndex() {
		return benchmarkPricingIndex;
	}

	public void setBenchmarkPricingIndex(PricingIndex indexBenchmarkedTo) {
		this.benchmarkPricingIndex = indexBenchmarkedTo;
	}

	@ManyToOne
	@JoinColumn(name ="BASE_PRICING_INDEX_ID")
	public PricingIndex getBasePricingIndex() {
		return basePricingIndex;
	}

	public void setBasePricingIndex(PricingIndex indexIsBasedOn) {
		this.basePricingIndex = indexIsBasedOn;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		PricingIndexAudit audit = PricingIndexAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PricingIndexAudit.findRecentHistory(this);
	}
	
	@Transient
	protected PricingIndexRepositoryBean getPricingIndexRepository() {
		return (PricingIndexRepositoryBean) ApplicationContextFactory.getBean(PricingIndexRepositoryBean.BEAN_NAME);
	}
	
	@Transient
	protected IndexPriceRepositoryBean getPriceRepository() {
		return (IndexPriceRepositoryBean) ApplicationContextFactory.getBean(IndexPriceRepositoryBean.BEAN_NAME);
	}
	
	@Transient
	protected PricingLocationRepository getLocationRepository() {
		return (PricingLocationRepository) ApplicationContextFactory.getBean(PricingLocationRepository.BEAN_NAME);
	}
}
