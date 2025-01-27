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
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexDetail;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INTEREST_INDEX")
@NamedQueries({
    @NamedQuery(
       name = InterestIndexRepositoryBean.FIND_BY_NAME,
       query = "SELECT interestIndex " +
			   "  FROM InterestIndex interestIndex " +
       	     "   WHERE interestIndex.detail.name = :name "),
	@NamedQuery(
		name = InterestIndexRepositoryBean.FIND_BY_IS_RISK_FREE_RATE,
		query = "SELECT interestIndex " +
				"  FROM InterestIndex interestIndex " +
			  "   WHERE interestIndex.detail.isRiskFreeRate = :isRiskFreeRate "),

    @NamedQuery(
       name = InterestIndexRepositoryBean.FIND_UNEXPIRED_INDICES,
       query = "SELECT interestIndex " +
			   "  FROM InterestIndex interestIndex " +
			   " WHERE interestIndex.isExpired = false " +
       	  "   ORDER BY interestIndex.detail.name ")
})
public class InterestIndex extends TemporalAbstractEntity {

	private Integer id;

	private InterestIndexDetail detail = new InterestIndexDetail();

	protected InterestIndex() {
		
	}
	
	public static InterestIndex create(InterestIndexSnapshot snapshot) {
		InterestIndex index = new InterestIndex();
		index.createWith(snapshot);
		return index;
	}
	
	
	@Override
	@Transient
	public String getEntityName() {
		return "InterestIndex";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="InterestIndexGen", sequenceName="INTEREST_INDEX_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "InterestIndexGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer interestIndexId) {
		this.id = interestIndexId;
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
	public InterestIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(InterestIndexDetail detail) {
		this.detail = detail;
	}

	protected void createWith(InterestIndexSnapshot snapshot) {
		super.createWith(snapshot);
		detail.setDefaults();
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		save();
	}
	
	public void updateWith(InterestIndexSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		setAssociations(snapshot);
		update();
	}
	

	public List<Integer> saveInterestCurves(List<InterestCurveSnapshot> interests) {

		ArrayList<Integer> ids = new ArrayList<>();
		for (InterestCurveSnapshot s : interests) {
			if (s.getEntityState() == EntityState.NEW) {
				InterestCurve interest = InterestCurve.newInterestCurve(this, s);
				ids.add(interest.getId());
			} if (s.getEntityState() == EntityState.MODIFIED) {
				InterestCurve interest = getInterestCurveRepository().load(s.getEntityId());
				interest.updateWith(s);
				ids.add(interest.getId());
			}  if (s.getEntityState() == EntityState.DELETE) {
				InterestCurve interest = getInterestCurveRepository().load(s.getEntityId());
				interest.delete();
			}
		}
		return ids;
	}

	protected void addInterestCurve(InterestCurve curve) {
		curve.setInterestIndex(this);
		curve.save();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		validateIsRiskFreeRateAndName();
	}
	
	private void setAssociations(InterestIndexSnapshot snapshot) {
	}

	private void validateIsRiskFreeRateAndName() throws OBValidationException {
		List<InterestIndex> indices = getInterestIndexRepository().findNonExpiredInterestIndexes();
		for (InterestIndex index : indices) {
			if (index.getId().equals(getId()) == false) {
				if (detail.getIsRiskFreeRate() == true) {
					if (index.getDetail().getIsRiskFreeRate()) {
						throw new OBValidationException(PricingErrorCode.INVALID_INTEREST_INDEX_IS_RISK_FREE_RATE.getCode());
					}
				}
				if (detail.getName().equals(index.getDetail().getName())) {
					throw new OBValidationException(PricingErrorCode.DUPLICATE_INTEREST_INDEX_NAME.getCode());
				}
			}
		}
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		InterestIndexAudit audit = InterestIndexAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return InterestIndexAudit.findRecentHistory(this);
	}
	
	@Transient
	protected InterestCurveRepositoryBean getInterestCurveRepository() {
		return (InterestCurveRepositoryBean) ApplicationContextFactory.getBean(InterestCurveRepositoryBean.BEAN_NAME);
	}

	@Transient
	protected InterestIndexRepositoryBean getInterestIndexRepository() {
		return (InterestIndexRepositoryBean) ApplicationContextFactory.getBean(InterestIndexRepositoryBean.BEAN_NAME);
	}
}
