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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

@Entity
@Table (name = "POWER_PROFILE_IDX_MAP_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PowerProfileIndexMappingAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM PowerProfileIndexMappingAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.powerProfileIndexMapping = :powerProfileIndexMapping")
})
public class PowerProfileIndexMappingAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PowerProfileIndexMappingAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PowerProfile powerProfile;

	private PriceIndex priceIndex;
	
	private PowerProfileIndexMapping powerProfileIndexMapping;
	
	private PowerProfileIndexMappingDetail detail = new PowerProfileIndexMappingDetail();

	public static PowerProfileIndexMappingAudit create(PowerProfileIndexMapping mapping) {
		PowerProfileIndexMappingAudit audit = new PowerProfileIndexMappingAudit();
		audit.setPowerProfileIndexMapping(mapping);
		audit.setPowerProfile(mapping.getPowerProfile());
		audit.copyFrom(mapping);
		return audit;
	}
	
	protected PowerProfileIndexMappingAudit() {

	}

	protected PowerProfileIndexMappingAudit(PowerProfileIndexMapping powerProfileIndexMapping) {
		this.powerProfileIndexMapping = powerProfileIndexMapping;
		this.powerProfile = powerProfileIndexMapping.getPowerProfile();
		this.priceIndex = powerProfileIndexMapping.getPriceIndex();
		this.detail.copyFrom(powerProfileIndexMapping.getDetail());
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PowerProfileIndexMappingAuditGen", sequenceName="POWER_PROFILE_IDX_MAP_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PowerProfileIndexMappingAuditGen")
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


	@ManyToOne()
	@JoinColumn(name = "PRICE_INDEX_ID")
	public PriceIndex getPriceIndex() {
		return priceIndex;
	}

	public void setPriceIndex(PriceIndex priceIndex) {
		this.priceIndex = priceIndex;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public PowerProfileIndexMapping getPowerProfileIndexMapping() {
		return powerProfileIndexMapping;
	}

	public void setPowerProfileIndexMapping(PowerProfileIndexMapping powerProfileDay) {
		this.powerProfileIndexMapping = powerProfileDay;
	}

	@Embedded
	public PowerProfileIndexMappingDetail getDetail() {
		return detail;
	}

	private void setDetail(PowerProfileIndexMappingDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return powerProfileIndexMapping;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PowerProfileIndexMapping mapping = (PowerProfileIndexMapping) entity;
		this.priceIndex = mapping.getPriceIndex();
		this.detail.copyFrom(mapping.getDetail());
	}


	public static PowerProfileIndexMappingAudit findRecentHistory(PowerProfileIndexMapping mapping) {
		String[] parmNames = {"powerProfileIndexMapping", "date" };
		Object[] parms =     {mapping,   DateUtils.getValidToDateTime()};

		return (PowerProfileIndexMappingAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
