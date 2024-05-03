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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.BenchSettlementRuleCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexDetail;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRICE_INDEX")
@NamedQueries({
    @NamedQuery(
       name = PriceIndexRepositoryBean.FIND_BY_NAME,
       query = "SELECT priceIndex " +
			   "  FROM PriceIndex priceIndex " +
       	     "   WHERE priceIndex.detail.name = :name "),
    
    @NamedQuery(
       name = PriceIndexRepositoryBean.FETCH_PRICE_INDEX_REPORTS,
       query = "SELECT new com.onbelay.dealcapture.pricing.snapshot.PriceIndexReport(" +
			   "	priceIndex.id," +
			   "	priceIndex.detail.indexTypeValue," +
			   "	priceIndex.benchmarkPricingIndex.id," +
			   "	priceIndex.basePricingIndex.id," +
			   "	priceIndex.detail.frequencyCodeValue," +
			   "	priceIndex.detail.currencyCodeValue," +
			   "	priceIndex.detail.unitOfMeasureCodeValue" +
			   ") " +
			   "  FROM PriceIndex priceIndex " +
       	     "   WHERE priceIndex.id in (:indexIds) "),
    @NamedQuery(
       name = PriceIndexRepositoryBean.LOAD_ALL,
       query = "SELECT priceIndex " +
			   "  FROM PriceIndex priceIndex " +
       	  "   ORDER BY priceIndex.detail.name ")
})
public class PriceIndex extends TemporalAbstractEntity {

	private Integer id;
	
	private PricingLocation pricingLocation;
	
	private PriceIndexDetail detail = new PriceIndexDetail();
	
	private PriceIndex benchmarkPriceIndex;
	
	private PriceIndex basePriceIndex;
	
	
	protected PriceIndex() {
		
	}
	
	public static PriceIndex create(PriceIndexSnapshot snapshot) {
		PriceIndex index = new PriceIndex();
		index.createWith(snapshot);
		return index;
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
	

	public List<Integer> savePriceCurves(List<PriceCurveSnapshot> prices) {

		ArrayList<Integer> ids = new ArrayList<>();
		for (PriceCurveSnapshot s : prices) {
			if (s.getEntityState() == EntityState.NEW) {
				PriceCurve price = PriceCurve.newPriceCurve(this, s);
				ids.add(price.getId());
			} if (s.getEntityState() == EntityState.MODIFIED) {
				PriceCurve price = getPriceRepository().load(s.getEntityId());
				price.updateWith(s);
				ids.add(price.getId());
			}  if (s.getEntityState() == EntityState.DELETE) {
				PriceCurve price = getPriceRepository().load(s.getEntityId());
				price.delete();
			}
		}
		return ids;
	}

	protected void addPriceCurve(PriceCurve curve) {
		curve.setPriceIndex(this);
		curve.save();
	}

	public List<PriceRiskFactor> getPriceRiskFactorsByDates(
			LocalDate fromMarketDate,
			LocalDate toMarketDate) {

		return getPriceRiskFactorRepository().fetchByDatesInclusive(
				generateEntityId(),
				toMarketDate,
				fromMarketDate);
	}


	public List<Integer> savePriceRiskFactors(List<PriceRiskFactorSnapshot> factors) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (PriceRiskFactorSnapshot snapshot : factors) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				PriceRiskFactor factor = PriceRiskFactor.create(this, snapshot);
				ids.add(factor.getId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				PriceRiskFactor factor = getPriceRiskFactorRepository().load(snapshot.getEntityId());
				factor.updateWith(snapshot);
				ids.add(factor.getId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				PriceRiskFactor factor = getPriceRiskFactorRepository().load(snapshot.getEntityId());
				factor.delete();
			}
		}
		return ids;
	}

	public void addPriceRiskFactor(PriceRiskFactor factor) {
		factor.setIndex(this);
		factor.save();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.pricingLocation == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICING_LOCATION.getCode());
		
		if (detail.getIndexType() == IndexType.BASIS) {
			if (this.basePriceIndex == null)
				throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		}
		if (detail.getBenchSettlementRuleCode() != BenchSettlementRuleCode.NEVER) {
			if (benchmarkPriceIndex == null)
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
	protected PriceRiskFactorRepository getPriceRiskFactorRepository() {
		return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
	}

	@Transient
	protected PricingLocationRepository getLocationRepository() {
		return (PricingLocationRepository) ApplicationContextFactory.getBean(PricingLocationRepository.BEAN_NAME);
	}
}
