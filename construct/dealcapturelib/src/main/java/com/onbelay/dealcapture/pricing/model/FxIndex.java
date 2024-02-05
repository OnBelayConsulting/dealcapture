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
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexDetail;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FX_INDEX")
@NamedQueries({
    @NamedQuery(
       name = FxIndexRepositoryBean.FIND_BY_NAME,
       query = "SELECT fxIndex " +
			   "  FROM FxIndex fxIndex " +
       	     "   WHERE fxIndex.detail.name = :name "),
    
    @NamedQuery(
       name = FxIndexRepositoryBean.FIND_BY_FROM_TO_CURRENCIES,
       query = "SELECT fxIndex " +
			   "  FROM FxIndex fxIndex " +
       	     "   WHERE fxIndex.detail.fromCurrencyCodeValue = :from " +
			   "   AND fxIndex.detail.toCurrencyCodeValue = :to " +
 		   "  ORDER BY fxIndex.detail.frequencyCodeValue " ),
    @NamedQuery(
       name = FxIndexRepositoryBean.FETCH_FX_INDEX_REPORTS,
       query = "SELECT new com.onbelay.dealcapture.pricing.snapshot.FxIndexReport(" +
			   "	fxIndex.id," +
			   "	fxIndex.detail.frequencyCodeValue," +
			   "	fxIndex.detail.toCurrencyCodeValue," +
			   "	fxIndex.detail.fromCurrencyCodeValue" +
			   ") " +
			   "  FROM FxIndex fxIndex " +
       	     "   WHERE fxIndex.id in (:indexIds) "),
    @NamedQuery(
       name = FxIndexRepositoryBean.LOAD_ALL,
       query = "SELECT fxIndex " +
			   "  FROM FxIndex fxIndex " +
       	  "   ORDER BY fxIndex.detail.name ")
})
public class FxIndex extends TemporalAbstractEntity {

	private Integer id;
	
	private FxIndexDetail detail = new FxIndexDetail();
	
	
	
	protected FxIndex() {
		
	}
	
	public static FxIndex create(FxIndexSnapshot snapshot) {
		FxIndex index = new FxIndex();
		index.createWith(snapshot);
		return index;
	}
	
	
	@Override
	@Transient
	public String getEntityName() {
		return "FxIndex";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxIndexGen", sequenceName="FX_INDEX_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxIndexGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer fxIndexId) {
		this.id = fxIndexId;
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
	public FxIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(FxIndexDetail detail) {
		this.detail = detail;
	}
	
	protected void createWith(FxIndexSnapshot snapshot) {
		super.createWith(snapshot);
		detail.setDefaults();
		detail.copyFrom(snapshot.getDetail());

		if (detail.getName() == null)
			detail.setName(detail.composeName());
		if (detail.getDescription() == null)
			detail.setDescription(detail.getName());

		setAssociations(snapshot);
		save();
	}
	
	public void updateWith(FxIndexSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		update();
	}


	public FxRiskFactor getFxRiskFactor(LocalDate marketDate) {
		return getFxRiskFactorRepository().fetchByMarketDate(generateEntityId(), marketDate);
	}

	public List<FxRiskFactor> getFxRiskFactorsByDates(
			LocalDate fromMarketDate,
			LocalDate toMarketDate) {

		return getFxRiskFactorRepository().fetchByDatesInclusive(
				generateEntityId(),
				toMarketDate,
				fromMarketDate);
	}


	public List<Integer> saveFxRiskFactors(List<FxRiskFactorSnapshot> factors) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (FxRiskFactorSnapshot snapshot : factors) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				FxRiskFactor factor = FxRiskFactor.create(this, snapshot);
				ids.add(factor.getId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				FxRiskFactor factor = getFxRiskFactorRepository().load(snapshot.getEntityId());
				factor.updateWith(snapshot);
				ids.add(factor.getId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				FxRiskFactor factor = getFxRiskFactorRepository().load(snapshot.getEntityId());
				factor.delete();
			}
		}
		return ids;
	}

	
	protected void validate() throws OBValidationException {
		detail.validate();
		

	}
	
	private void setAssociations(FxIndexSnapshot snapshot) {
	}


	public void add(FxRiskFactor fxRiskFactor) {
		fxRiskFactor.setIndex(this);
		fxRiskFactor.save();
	}

	public List<Integer> saveFxCurves(List<FxCurveSnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (FxCurveSnapshot snapshot : snapshots) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				FxCurve curve = new FxCurve(this, snapshot);
				ids.add(curve.getId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				FxCurve curve = getFxCurveRepository().load(snapshot.getEntityId());
				curve.updateWith(snapshot);
				ids.add(curve.getId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				FxCurve curve = getFxCurveRepository().load(snapshot.getEntityId());
				curve.delete();
			}
		}
		return ids;
 	}

	public void addFxCurve(FxCurve curve) {
		curve.setFxIndex(this);
		curve.save();
	}

	public FxRate getCurrentRate(LocalDate marketDate) {
		FxCurve curve = getFxCurveRepository().fetchCurrentFxCurve(generateEntityId(), marketDate);
		if (curve != null)
			return curve.generateFxRate();
		else
			return new FxRate(CalculatedErrorType.ERROR);
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		FxIndexAudit audit = FxIndexAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return FxIndexAudit.findRecentHistory(this);
	}
	
	@Transient
	protected FxIndexRepositoryBean getFxIndexRepository() {
		return (FxIndexRepositoryBean) ApplicationContextFactory.getBean(FxIndexRepositoryBean.BEAN_NAME);
	}

	@Transient
	protected FxCurveRepository getFxCurveRepository() {
		return (FxCurveRepository) ApplicationContextFactory.getBean(FxCurveRepository.BEAN_NAME);
	}

	@Transient
	protected FxRiskFactorRepository getFxRiskFactorRepository() {
		return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
	}
}
