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

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDayDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "POWER_PROFILE_DAY_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PowerProfileDayAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM PowerProfileDayAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.powerProfileDay = :powerProfileDay")
})
public class PowerProfileDayAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PowerProfileDayAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PowerProfile powerProfile;
	
	private PowerProfileDay powerProfileDay;
	
	private PowerProfileDayDetail detail = new PowerProfileDayDetail();

	public static PowerProfileDayAudit create(PowerProfileDay powerProfileDay) {
		PowerProfileDayAudit audit = new PowerProfileDayAudit();
		audit.setPowerProfileDay(powerProfileDay);
		audit.setPowerProfile(powerProfileDay.getPowerProfile());
		audit.copyFrom(powerProfileDay);
		return audit;
	}
	
	protected PowerProfileDayAudit() {

	}

	protected PowerProfileDayAudit(PowerProfileDay powerProfileDay) {
		this.powerProfileDay = powerProfileDay;
		this.powerProfile = powerProfileDay.getPowerProfile();
		this.detail.copyFrom(powerProfileDay.getDetail());
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileDayAuditGen", sequenceName="POWER_PROFILE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileDayAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealQuantityAuditId) {
		this.id = dealQuantityAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="POWER_PROFILE_ID")
	public PowerProfile getPowerProfile() {
		return powerProfile;
	}

	public void setPowerProfile(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PowerProfileDay getPowerProfileDay() {
		return powerProfileDay;
	}

	public void setPowerProfileDay(PowerProfileDay powerProfileDay) {
		this.powerProfileDay = powerProfileDay;
	}

	@Embedded
	public PowerProfileDayDetail getDetail() {
		return detail;
	}

	private void setDetail(PowerProfileDayDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return powerProfileDay;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PowerProfileDay powerProfileDay = (PowerProfileDay) entity;
		this.detail.copyFrom(powerProfileDay.getDetail());
	}


	public static PowerProfileDayAudit findRecentHistory(PowerProfileDay powerProfileDay) {
		String[] parmNames = {"powerProfileDay", "date" };
		Object[] parms =     {powerProfileDay,   DateUtils.getValidToDateTime()};

		return (PowerProfileDayAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
