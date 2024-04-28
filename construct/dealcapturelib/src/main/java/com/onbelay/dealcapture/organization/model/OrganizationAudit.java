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
package com.onbelay.dealcapture.organization.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.organization.snapshot.OrganizationDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "ORGANIZATION_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = OrganizationAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT organizationAudit " +
			   "  FROM OrganizationAudit organizationAudit " +
       		    "WHERE organizationAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND organizationAudit.organization = :organization")
})
public class OrganizationAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "OrganizationAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private Organization organization;
	
	private OrganizationDetail detail = new OrganizationDetail();
	
	protected OrganizationAudit() {
		
	}
	
	protected OrganizationAudit(Organization organization) {
		this.organization = organization;
	}
	
	protected static OrganizationAudit create(Organization organization) {
		OrganizationAudit audit = new OrganizationAudit(organization);
		audit.copyFrom(organization);
		return audit;
	}
	

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="OrganizationAuditGen", sequenceName="ORGANIZATION_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "OrganizationAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer organizationId) {
		this.id = organizationId;
	}

	
	@ManyToOne
	@JoinColumn(name="ENTITY_ID")
	public Organization getOrganization() {
		return organization;
	}

	private void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Embedded
	public OrganizationDetail getDetail() {
		return detail;
	}

	private void setDetail(OrganizationDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return organization;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		Organization organization = (Organization) entity;
		this.detail.copyFrom(organization.getDetail());
	}


	public static OrganizationAudit findRecentHistory(Organization organization) {
		String[] parmNames = {"organization", "date" };
		Object[] parms =     {organization,   DateUtils.getValidToDateTime()};

		return (OrganizationAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}



}
