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
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerProfileErrorCode;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table (name = "POWER_PROFILE")
@NamedQueries({
		@NamedQuery(
				name = PowerProfileRepositoryBean.FETCH_ASSIGNED_POWER_PROFILES,
				query = "SELECT profile " +
						"  FROM PowerProfile profile " +
						" WHERE profile.detail.positionGenerationIdentifier = :positionGenerationIdentifier " +
					"  ORDER BY profile.detail.name "),
		@NamedQuery(
				name = PowerProfileRepositoryBean.FIND_BY_NAME,
				query = "SELECT profile " +
						"  FROM PowerProfile profile " +
					"     WHERE profile.detail.name = :name ")
})
public class PowerProfile extends TemporalAbstractEntity {

	private Integer id;

	private PriceIndex settledPriceIndex;

	private PriceIndex restOfMonthPriceIndex;

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
		if (snapshot.getChangedMappings().isEmpty())
			savePowerProfileIndexMappings(snapshot.getIndexMappings());
		else
			processIndexMappings(snapshot.getChangedMappings());
		if (snapshot.getChangedProfileDays().isEmpty())
			savePowerProfileDays(snapshot.getProfileDays());
		else
			processPowerProfileDayMap(snapshot.getChangedProfileDays());
		updateRelationships(snapshot);
		validateDays();
	}

	public void updateWith(PowerProfileSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		if (snapshot.getChangedMappings().isEmpty())
			savePowerProfileIndexMappings(snapshot.getIndexMappings());
		else
			processIndexMappings(snapshot.getChangedMappings());
		if (snapshot.getChangedProfileDays().isEmpty())
			savePowerProfileDays(snapshot.getProfileDays());
		else
			processPowerProfileDayMap(snapshot.getChangedProfileDays());

		updateRelationships(snapshot);
		update();
	}

	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		detail.validate();
	}

	private void validateDays() throws OBValidationException {
		Map<PowerFlowCode, PowerProfileIndexMapping> codeMap = fetchPowerProfileIndexMappings()
				.stream()
				.collect(
						Collectors.toMap(c-> c.getDetail().getPowerFlowCode(), c-> c ));
		for (PowerProfileDay day : fetchPowerProfileDays()) {
			for (int i=1; i < 25; i++){
				if (day.getDetail().getPowerFlowCode(i).isHourly() == false)
					throw new OBValidationException(PowerProfileErrorCode.INVALID_POWER_PROFILE_CODE.getCode());

				if (day.getDetail().getPowerFlowCode(i) != PowerFlowCode.NONE &&
				 		codeMap.containsKey(day.getDetail().getPowerFlowCode(i)) == false)
					throw new OBValidationException(PowerProfileErrorCode.MISSING_POWER_PROFILE_MAPPING.getCode());
			}

		}
	}

	private void updateRelationships(PowerProfileSnapshot snapshot) {
		if (snapshot.getSettledPriceIndexId() != null) {
			this.settledPriceIndex = getPriceIndexRepository().load(snapshot.getSettledPriceIndexId());
		}
		if (snapshot.getRestOfMonthPriceIndexId() != null) {
			this.restOfMonthPriceIndex = getPriceIndexRepository().load(snapshot.getRestOfMonthPriceIndexId());
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

	@ManyToOne
	@JoinColumn(name = "END_OF_MTH_PRICE_INDEX_ID")
	public PriceIndex getRestOfMonthPriceIndex() {
		return restOfMonthPriceIndex;
	}

	public void setRestOfMonthPriceIndex(PriceIndex endOfMonthPriceIndex) {
		this.restOfMonthPriceIndex = endOfMonthPriceIndex;
	}

	/*
	Update the collection of index mappings from a new list of changed mappings by adding updating or deleting.
	 */
	private void processIndexMappings(List<PowerProfileIndexMappingSnapshot> mappings) {
		Map<PowerFlowCode, PowerProfileIndexMappingSnapshot> changedMap = new HashMap<>();
		mappings.forEach(mapping -> {
			changedMap.put(mapping.getDetail().getPowerFlowCode(), mapping);
		});

		Map<PowerFlowCode, PowerProfileIndexMapping> existingMap = new HashMap<>();

		fetchPowerProfileIndexMappings().forEach(mapping -> {
			existingMap.put(mapping.getDetail().getPowerFlowCode(), mapping);
		});

		for (PowerFlowCode code : existingMap.keySet()) {
			PowerProfileIndexMappingSnapshot snapshot = changedMap.get(code);
			if (snapshot == null) {
				PowerProfileIndexMapping existing = existingMap.get(code);
				existing.delete();
			}
		}

		for (PowerFlowCode key : changedMap.keySet()) {
			PowerProfileIndexMapping existing = existingMap.get(key);
			if (existing == null) {
				savePowerProfileIndexMapping(changedMap.get(key));
			} else {
				existing.updateWith(changedMap.get(key));
			}
		}

	}

	private void processPowerProfileDayMap(List<PowerProfileDaySnapshot> powerProfileDays) {
		Map<Integer, PowerProfileDaySnapshot> changedDayMap = new HashMap<>();
		powerProfileDays.forEach(powerProfileDay -> {
			changedDayMap.put(powerProfileDay.getDetail().getDayOfWeek(), powerProfileDay);
		});

		Map<Integer, PowerProfileDay> existingDayMap = new HashMap<>();

		fetchPowerProfileDays().forEach(powerProfileDay -> {
			existingDayMap.put(powerProfileDay.getDetail().getDayOfWeek(), powerProfileDay);
		});

		for (Integer key : existingDayMap.keySet()) {
			PowerProfileDaySnapshot snapshot = changedDayMap.get(key);
			if (snapshot == null) {
				PowerProfileDay existing = existingDayMap.get(key);
				existing.delete();
			}
		}

		for (Integer key : changedDayMap.keySet()) {
			PowerProfileDay existing = existingDayMap.get(key);
			if (existing == null) {
				savePowerProfileDay(changedDayMap.get(key));
			} else {
				existing.updateWith(changedDayMap.get(key));
			}
		}
	}

	protected Integer savePowerProfileDay(PowerProfileDaySnapshot snapshot) {
		switch (snapshot.getEntityState()) {

			case NEW -> {
				PowerProfileDay day = PowerProfileDay.create(this, snapshot);
				return day.getId();
			}
			case MODIFIED -> {
				PowerProfileDay day = getPowerProfileDayRepository().load(snapshot.getEntityId());
				day.updateWith(snapshot);
				return day.getId();
			}
			case DELETE -> {
				PowerProfileDay day = getPowerProfileDayRepository().load(snapshot.getEntityId());
				day.delete();
				return day.getId();
			}
		}
		return null;

	}

	public List<Integer> savePowerProfileDays(List<PowerProfileDaySnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (PowerProfileDaySnapshot snapshot : snapshots) {
			Integer id = savePowerProfileDay(snapshot);
			if (id != null) {
				ids.add(id);
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
			Integer id = savePowerProfileIndexMapping(snapshot);
			if (id != null) {
				ids.add(id);
			}
		}
		return ids;
	}

	public Integer savePowerProfileIndexMapping(PowerProfileIndexMappingSnapshot snapshot) {
		if (snapshot.getDetail().getPowerFlowCode().isHourly() == false)
			throw new OBValidationException(PowerProfileErrorCode.INVALID_POWER_PROFILE_CODE.getCode());
		switch (snapshot.getEntityState()) {

			case NEW -> {
				PowerProfileIndexMapping mapping = PowerProfileIndexMapping.create(this, snapshot);
				return mapping.getId();
			}
			case MODIFIED -> {
				PowerProfileIndexMapping mapping = getPowerProfileIndexMappingRepository().load(snapshot.getEntityId());
				mapping.updateWith(snapshot);
				return mapping.getId();
			}
			case DELETE -> {
				PowerProfileIndexMapping mapping = getPowerProfileIndexMappingRepository().load(snapshot.getEntityId());
				mapping.delete();
				return mapping.getId();
			}
		}
		return null;
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
