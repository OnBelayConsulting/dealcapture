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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.shared.PricingLocationDetail;

@Entity
@Table(name = "PRICING_LOCATION_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = PricingLocationAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT pricingLocationAudit " +
			   "  FROM PricingLocationAudit pricingLocationAudit " +
       		    "WHERE pricingLocationAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND pricingLocationAudit.pricingLocation = :pricingLocation")
})
public class PricingLocationAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "PricingLocationAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private PricingLocation pricingLocation;
	
	private PricingLocationDetail detail = new PricingLocationDetail();
	
	protected PricingLocationAudit() {
		
	}
	
	protected PricingLocationAudit(PricingLocation pricingLocation) {
		this.pricingLocation = pricingLocation;
	}
	
	protected static PricingLocationAudit create(PricingLocation pricingLocation) {
		PricingLocationAudit audit = new PricingLocationAudit(pricingLocation);
		audit.copyFrom(pricingLocation);
		return audit;
	}
	

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PricingLocationAuditGen", sequenceName="PRICING_LOCATION_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PricingLocationAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingLocationId) {
		this.id = pricingLocationId;
	}

	
	@ManyToOne
	@JoinColumn(name="ENTITY_ID")
	public PricingLocation getPricingLocation() {
		return pricingLocation;
	}

	private void setPricingLocation(PricingLocation pricingLocation) {
		this.pricingLocation = pricingLocation;
	}

	@Embedded
	public PricingLocationDetail getDetail() {
		return detail;
	}

	private void setDetail(PricingLocationDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return pricingLocation;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		PricingLocation pricingLocation = (PricingLocation) entity;
		this.detail.copyFrom(pricingLocation.getDetail());
	}


	public static PricingLocationAudit findRecentHistory(PricingLocation pricingLocation) {
		String[] parmNames = {"pricingLocation", "date" };
		Object[] parms =     {pricingLocation,   DateUtils.getValidToDateTime()};

		return (PricingLocationAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}



}
