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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

@Entity
@Table (name = "POWER_PROFILE_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PowerProfileAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM PowerProfileAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.powerProfile = :powerProfile")
})
public class PowerProfileAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PowerProfileAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PowerProfile powerProfile;

	private PriceIndex settledPriceIndex;

	private PowerProfileDetail detail = new PowerProfileDetail();

	public static PowerProfileAudit create(PowerProfile powerProfile) {
		PowerProfileAudit audit = new PowerProfileAudit();
		audit.setPowerProfile(powerProfile);
		audit.copyFrom(powerProfile);
		return audit;
	}

	protected PowerProfileAudit() {

	}

	protected PowerProfileAudit(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
		this.settledPriceIndex = powerProfile.getSettledPriceIndex();
		this.detail.copyFrom(powerProfile.getDetail());
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileAuditGen", sequenceName="POWER_PROFILE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealQuantityAuditId) {
		this.id = dealQuantityAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PowerProfile getPowerProfile() {
		return powerProfile;
	}

	public void setPowerProfile(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
	}

	@ManyToOne
	@JoinColumn(name = "SETTLED_PRICE_INDEX_ID")
	public PriceIndex getSettledPriceIndex() {
		return settledPriceIndex;
	}

	public void setSettledPriceIndex(PriceIndex settledPriceIndex) {
		this.settledPriceIndex = settledPriceIndex;
	}


	@Embedded
	public PowerProfileDetail getDetail() {
		return detail;
	}

	private void setDetail(PowerProfileDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return powerProfile;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PowerProfile powerProfile = (PowerProfile) entity;
		this.settledPriceIndex = powerProfile.getSettledPriceIndex();
		this.detail.copyFrom(powerProfile.getDetail());
	}


	public static PowerProfileAudit findRecentHistory(PowerProfile powerProfile) {
		String[] parmNames = {"powerProfile", "date" };
		Object[] parms =     {powerProfile,   DateUtils.getValidToDateTime()};

		return (PowerProfileAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
