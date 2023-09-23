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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.shared.PriceIndexDetail;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;

@Entity
@Table(name = "PRICE_INDEX")
@NamedQueries({
    @NamedQuery(
       name = PriceIndexRepositoryBean.FIND_BY_NAME,
       query = "SELECT priceIndex " +
			   "  FROM PriceIndex priceIndex " +
       	     "   WHERE priceIndex.detail.name = :name ")
    
})
public class PriceIndex extends TemporalAbstractEntity {

	private Integer id;
	
	private PricingLocation pricingLocation;
	
	private PriceIndexDetail detail = new PriceIndexDetail();
	
	private PriceIndex benchmarkPriceIndex;
	
	private PriceIndex basePriceIndex;
	
	
	protected PriceIndex() {
		
	}
	
	public PriceIndex(PriceIndexSnapshot snapshot) {
		createWith(snapshot);
		save();
	}
	
	
	@Override
	@Transient
	public String getEntityName() {
		return "PriceIndex";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceIndexGen", sequenceName="PRICE_INDEX_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceIndexGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer priceIndexId) {
		this.id = priceIndexId;
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
	public PriceIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(PriceIndexDetail detail) {
		this.detail = detail;
	}
	
	protected void createWith(PriceIndexSnapshot snapshot) {
		super.createWith(snapshot);
		detail.setDefaults();
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		save();
	}
	
	public void updateWith(PriceIndexSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		update();
	}
	

	public void updatePricesWith(List<PriceCurveSnapshot> prices) {
		PriceCurve price = null;
		for (PriceCurveSnapshot s : prices) {
			if (s.getEntityState() == EntityState.NEW) {
				price = new PriceCurve(this, s);
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
			if (this.basePriceIndex == null)
				throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		}
		
	}
	
	private void setAssociations(PriceIndexSnapshot snapshot) {
		
		if (snapshot.getBenchmarkIndexId() != null) {
			this.benchmarkPriceIndex = getPricingIndexRepository().load(snapshot.getBenchmarkIndexId());
		}
		
		if (snapshot.getBaseIndexId() != null) {
			basePriceIndex = getPricingIndexRepository().load(snapshot.getBaseIndexId());
		}
		
		if (snapshot.getPricingLocationId() != null) {
			this.pricingLocation = getLocationRepository().load(snapshot.getPricingLocationId());
		}
	}
	

	@ManyToOne
	@JoinColumn(name ="BENCH_PRICE_INDEX_ID")
	public PriceIndex getBenchmarkPricingIndex() {
		return benchmarkPriceIndex;
	}

	public void setBenchmarkPricingIndex(PriceIndex indexBenchmarkedTo) {
		this.benchmarkPriceIndex = indexBenchmarkedTo;
	}

	@ManyToOne
	@JoinColumn(name ="BASE_PRICE_INDEX_ID")
	public PriceIndex getBasePricingIndex() {
		return basePriceIndex;
	}

	public void setBasePricingIndex(PriceIndex indexIsBasedOn) {
		this.basePriceIndex = indexIsBasedOn;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		PriceIndexAudit audit = PriceIndexAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PriceIndexAudit.findRecentHistory(this);
	}
	
	@Transient
	protected PriceIndexRepositoryBean getPricingIndexRepository() {
		return (PriceIndexRepositoryBean) ApplicationContextFactory.getBean(PriceIndexRepositoryBean.BEAN_NAME);
	}
	
	@Transient
	protected PriceCurveRepositoryBean getPriceRepository() {
		return (PriceCurveRepositoryBean) ApplicationContextFactory.getBean(PriceCurveRepositoryBean.BEAN_NAME);
	}
	
	@Transient
	protected PricingLocationRepository getLocationRepository() {
		return (PricingLocationRepository) ApplicationContextFactory.getBean(PricingLocationRepository.BEAN_NAME);
	}
}
