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
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileIndexMappingRepository;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "POWER_PROFILE")
@NamedQueries({
		@NamedQuery(
				name = PowerProfileRepositoryBean.FETCH_ASSIGNED_POWER_PROFILES,
				query = "SELECT profile " +
						"  FROM PowerProfile profile " +
						"   WHERE profile.detail.positionGenerationIdentifier = :positionGenerationIdentifier " +
						"ORDER BY profile.detail.name ")
})
public class PowerProfile extends TemporalAbstractEntity {

	private Integer id;

	private PriceIndex settledPriceIndex;

	private PowerProfileDetail detail = new PowerProfileDetail();
	
	protected PowerProfile() {
		
	}
	
	
	public static PowerProfile create(PowerProfileSnapshot snapshot) {
		
		PowerProfile profile = new PowerProfile();
		profile.createWith(snapshot);
		return profile;
		
	}

	protected void createWith(PowerProfileSnapshot snapshot) {
		super.createWith(snapshot);
		detail.setDefaults();
		detail.copyFrom(snapshot.getDetail());
		save();
		savePowerProfileDays(snapshot.getProfileDays());
		savePowerProfileIndexMappings(snapshot.getIndexMappings());
		updateRelationships(snapshot);
	}

	public void updateWith(PowerProfileSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		savePowerProfileDays(snapshot.getProfileDays());
		savePowerProfileIndexMappings(snapshot.getIndexMappings());
		updateRelationships(snapshot);
		update();
	}

	private void updateRelationships(PowerProfileSnapshot snapshot) {
		if (snapshot.getSettledPriceIndexId() != null) {
			this.settledPriceIndex = getPriceIndexRepository().load(snapshot.getSettledPriceIndexId());
		}
	}

	@ManyToOne
	@JoinColumn(name = "SETTLED_PRICE_INDEX_ID")
	public PriceIndex getSettledPriceIndex() {
		return settledPriceIndex;
	}

	public void setSettledPriceIndex(PriceIndex settledPriceIndex) {
		this.settledPriceIndex = settledPriceIndex;
	}

	protected void addPowerProfileDay(PowerProfileDay powerProfileDay) {
		powerProfileDay.setPowerProfile(this);
		powerProfileDay.save();
	}


	public List<Integer> savePowerProfileDays(List<PowerProfileDaySnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (PowerProfileDaySnapshot snapshot : snapshots) {
			switch (snapshot.getEntityState()) {

				case NEW -> {
					PowerProfileDay day = PowerProfileDay.create(this, snapshot);
					ids.add(day.getId());
				}
				case MODIFIED -> {
					PowerProfileDay day = getPowerProfileDayRepository().load(snapshot.getEntityId());
					day.updateWith(snapshot);
					ids.add(day.getId());
				}
				case DELETE -> {
					PowerProfileDay day = getPowerProfileDayRepository().load(snapshot.getEntityId());
					day.delete();
				}
			}
		}
		return ids;
	}


	public List<PowerProfileDay> fetchPowerProfileDays() {
		return getPowerProfileDayRepository().fetchPowerProfileDays(this);
	}

	public List<Integer> savePowerProfileIndexMappings(List<PowerProfileIndexMappingSnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (PowerProfileIndexMappingSnapshot snapshot : snapshots) {
			switch (snapshot.getEntityState()) {

				case NEW -> {
					PowerProfileIndexMapping mapping = PowerProfileIndexMapping.create(this, snapshot);
					ids.add(mapping.getId());
				}
				case MODIFIED -> {
					PowerProfileIndexMapping mapping = getPowerProfileIndexMappingRepository().load(snapshot.getEntityId());
					mapping.updateWith(snapshot);
					ids.add(mapping.getId());
				}
				case DELETE -> {
					PowerProfileIndexMapping mapping = getPowerProfileIndexMappingRepository().load(snapshot.getEntityId());
					mapping.delete();
				}
			}
		}
		return ids;
	}

	public void addPowerProfileIndexMapping(PowerProfileIndexMapping powerProfileIndexMapping) {
		powerProfileIndexMapping.setPowerProfile(this);
		powerProfileIndexMapping.save();
	}

	public List<PowerProfileIndexMapping> fetchPowerProfileIndexMappings() {
		return getPowerProfileIndexMappingRepository().findByPowerProfile(this);
	}

	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileGen", sequenceName="POWER_PROFILE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer PowerProfileId) {
		this.id = PowerProfileId;
	}


	
	@Override
	@Transient
	public String getEntityName() {
		return "PowerProfile";
	}


	@Embedded
	public PowerProfileDetail getDetail() {
		return detail;
	}


	private void setDetail(PowerProfileDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return PowerProfileAudit.create(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PowerProfileAudit.findRecentHistory(this);
	}
	
	@Transient
	protected static PowerProfileRepository getPowerProfileRepository() {
		return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepositoryBean.BEAN_NAME);
	}


	@Transient
	protected static PriceIndexRepository getPriceIndexRepository() {
		return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
	}


	@Transient
	protected static PowerProfileDayRepository getPowerProfileDayRepository() {
		return (PowerProfileDayRepository) ApplicationContextFactory.getBean(PowerProfileDayRepository.BEAN_NAME);
	}

	@Transient
	protected static PowerProfileIndexMappingRepository getPowerProfileIndexMappingRepository() {
		return (PowerProfileIndexMappingRepository) ApplicationContextFactory.getBean(PowerProfileIndexMappingRepository.BEAN_NAME);
	}
}
