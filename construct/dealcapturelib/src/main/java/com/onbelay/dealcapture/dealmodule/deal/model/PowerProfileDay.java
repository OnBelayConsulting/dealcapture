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
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDayDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDayPrivateDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import jakarta.persistence.*;

@Entity
@Table (name = "POWER_PROFILE_DAY")
@NamedQueries({
		@NamedQuery(
				name = PowerProfileDayRepositoryBean.FETCH_POWER_PROFILE_DAYS,
				query = "SELECT powerProfileDay " +
						"  FROM PowerProfileDay powerProfileDay " +
						"   WHERE powerProfileDay.powerProfile = :powerProfile " +
						"ORDER BY powerProfileDay.detail.dayOfWeek ")
})
public class PowerProfileDay extends TemporalAbstractEntity {

	private Integer id;

	private PowerProfile powerProfile;

	private PowerProfileDayPrivateDetail detail = new PowerProfileDayPrivateDetail();

	protected PowerProfileDay() {
		
	}
	
	
	public static PowerProfileDay create(
			PowerProfile powerProfile, 
			PowerProfileDaySnapshot snapshot) {
		
		PowerProfileDay profile = new PowerProfileDay();
		profile.createWith(powerProfile, snapshot);
		return profile;
		
	}

	protected void createWith(
			PowerProfile powerProfile,
			PowerProfileDaySnapshot snapshot) {
		super.createWith(snapshot);
		getDetail().setDefaults();
		detail.copyFrom(snapshot.getDetail());
		powerProfile.addPowerProfileDay(this);
	}

	public void updateWith(PowerProfileDaySnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		update();
	}

	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileDayGen", sequenceName="POWER_PROFILE_DAY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileDayGen")
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

	@Override
	@Transient
	public String getEntityName() {
		return "PowerProfileDay";
	}


	@Embedded
	public PowerProfileDayPrivateDetail getDetail() {
		return detail;
	}


	private void setDetail(PowerProfileDayPrivateDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return PowerProfileDayAudit.create(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return PowerProfileDayAudit.findRecentHistory(this);
	}
	
	@Transient
	protected static PowerProfileRepository getPowerProfileRepository() {
		return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepositoryBean.BEAN_NAME);
	}

	public int calculateHours() {
		int hours = 0;
		for (int i=1; i < 25; i++) {
			if (getDetail().getPowerFlowCode(i) != PowerFlowCode.NONE)
				hours++;
		}
		return hours;
	}
}
