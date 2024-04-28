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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import jakarta.persistence.*;

@Entity
@Table (name = "POWER_PROFILE_IDX_MAP")
@NamedQueries({
		@NamedQuery(
				name = PowerProfileIndexMappingRepositoryBean.FETCH_BY_POWER_PROFILE,
				query = "SELECT mapping " +
						"  FROM PowerProfileIndexMapping mapping " +
						"   WHERE mapping.powerProfile = :powerProfile " +
						"ORDER BY mapping.detail.powerFlowCodeValue ")
})
public class PowerProfileIndexMapping extends TemporalAbstractEntity {

	private Integer id;

	private PowerProfile powerProfile;

	private PriceIndex priceIndex;

	private PowerProfileIndexMappingDetail detail = new PowerProfileIndexMappingDetail();

	protected PowerProfileIndexMapping() {
		
	}
	
	
	public static PowerProfileIndexMapping create(
			PowerProfile powerProfile, 
			PowerProfileIndexMappingSnapshot snapshot) {
		
		PowerProfileIndexMapping mapping = new PowerProfileIndexMapping();
		mapping.createWith(powerProfile, snapshot);
		return mapping;
		
	}

	protected void createWith(
			PowerProfile powerProfile,
			PowerProfileIndexMappingSnapshot snapshot) {
		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		updateRelationships(snapshot);
		powerProfile.addPowerProfileIndexMapping(this);
	}

	public void updateWith(PowerProfileIndexMappingSnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		updateRelationships(snapshot);
		update();
	}

	private void updateRelationships(PowerProfileIndexMappingSnapshot snapshot) {
		if (snapshot.getPriceIndexId() != null)
			this.priceIndex = getPriceIndexRepository().load(snapshot.getPriceIndexId());
	}

	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileIdxMappingGen", sequenceName="POWER_PROFILE_IDX_MAP_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileIdxMappingGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer PowerProfileId) {
		this.id = PowerProfileId;
	}


	@ManyToOne()
	@JoinColumn(name = "POWER_PROFILE_ID")
	public PowerProfile getPowerProfile() {
		return powerProfile;
	}

	public void setPowerProfile(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
	}


	@ManyToOne()
	@JoinColumn(name = "PRICE_INDEX_ID")
	public PriceIndex getPriceIndex() {
		return priceIndex;
	}

	public void setPriceIndex(PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}

	@Override
	@Transient
	public String getEntityName() {
		return "PowerProfileIndexMapping";
	}


	@Embedded
	public PowerProfileIndexMappingDetail getDetail() {
		return detail;
	}


	private void setDetail(PowerProfileIndexMappingDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return PowerProfileIndexMappingAudit.create(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PowerProfileIndexMappingAudit.findRecentHistory(this);
	}
	
	@Transient
	protected static PowerProfileRepository getPowerProfileRepository() {
		return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepositoryBean.BEAN_NAME);
	}


	@Transient
	protected static PriceIndexRepository getPriceIndexRepository() {
		return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
	}

}
