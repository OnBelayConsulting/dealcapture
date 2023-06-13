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
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.shared.PricingLocationDetail;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;

import javax.persistence.*;

@Entity
@Table(name = "PRICING_LOCATION")
@NamedQueries({
    @NamedQuery(
       name = PricingLocationRepositoryBean.FIND_PRICING_LOCATION_BY_NAME,
       query = "SELECT pricingLocation " +
			   "  FROM PricingLocation pricingLocation " +
       	     "   WHERE pricingLocation.detail.name = :name ")
    
})
public class PricingLocation extends TemporalAbstractEntity {

	private Integer id;

	private PricingLocationDetail detail = new PricingLocationDetail();
	

	protected PricingLocation() {
	}
	
	
	
	public PricingLocation(PricingLocationDetail detailIn) {
		this.detail.copyFrom(detailIn);
		save();
	}

	public PricingLocation(PricingLocationSnapshot snapshot) {
		createWith(snapshot);
	}


	protected void createWith(PricingLocationSnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		save();
	}
	
	public void updateWith(PricingLocationSnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		update();
	}
	
	protected void validate() throws OBValidationException {
		detail.validate();
	}

	@Override
	public EntityId generateEntityId() {
		return new EntityId(
				getId(),
				detail.getName(),
				detail.getDescription(),
				getIsExpired());
	}
    

	
	@Override
	@Transient
	public String getEntityName() {
		return "PricingLocation";
	}
	
	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PricingLocationGen", sequenceName="PRICING_LOCATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PricingLocationGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingLocationId) {
		this.id = pricingLocationId;
	}

	@Embedded
	public PricingLocationDetail getDetail() {
		return detail;
	}

	public void setDetail(PricingLocationDetail detail) {
		this.detail = detail;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		return PricingLocationAudit.create(this);
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PricingLocationAudit.findRecentHistory(this);
	}

}
