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
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "ORGANIZATION_ROLE_AUDIT")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
       name = OrganizationRoleAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT deallOrganizationRoleAudit " +
			   "  FROM OrganizationRoleAudit deallOrganizationRoleAudit " +
       		    "WHERE deallOrganizationRoleAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND deallOrganizationRoleAudit.organizationRole = :role")
})
public abstract class OrganizationRoleAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "OrganizationRoleAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private String organizationRoleTypeCode;

	private OrganizationRole organizationRole;

	private Organization organization;
	
	private OrganizationRoleDetail roleDetail = new OrganizationRoleDetail();

	protected OrganizationRoleAudit() {
	}

	protected OrganizationRoleAudit(
			OrganizationRoleType organizationRoleType,
			OrganizationRole organizationRole) {

		this.organizationRoleTypeCode = organizationRoleType.getCode();
		this.organizationRole = organizationRole;
		this.organization = organizationRole.getOrganization();
	}


	@Column(name = "ORG_ROLE_TYPE_CODE")
	protected String getOrganizationRoleTypeCode() {
		return organizationRoleTypeCode;
	}

	protected void setOrganizationRoleTypeCode(String organizationRoleTypeCode) {
		this.organizationRoleTypeCode = organizationRoleTypeCode;
	}


	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="OrgRoleAuditGen", sequenceName="ORGANIZATION_ROLE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "OrgRoleAuditGen")

    public Integer getId() {
		return id;
	}

	private void setId(Integer organizationRoleAuditId) {
		this.id = organizationRoleAuditId;
	}
	
	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public OrganizationRole getOrganizationRole() {
		return organizationRole;
	}

	private void setOrganizationRole(OrganizationRole dealOrganizationRole) {
		this.organizationRole = dealOrganizationRole;
	}

	@ManyToOne
	@JoinColumn(name ="ORGANIZATION_ID")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Embedded
	public OrganizationRoleDetail getRoleDetail() {
		return roleDetail;
	}

	public void setRoleDetail(OrganizationRoleDetail roleDetail) {
		this.roleDetail = roleDetail;
	}

	@Transient
	public OrganizationRoleType getOrganizationRoleType() {
		return OrganizationRoleType.lookUp(organizationRoleTypeCode);
	}
	
	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return organizationRole;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		OrganizationRole dealOrganizationRole = (OrganizationRole) entity;
		roleDetail.copyFrom(dealOrganizationRole.getRoleDetail());
	}


	public static  OrganizationRoleAudit findRecentHistory(OrganizationRole role) {
		String[] parmNames = {"role", "date" };
		Object[] parms =     {role,   DateUtils.getValidToDateTime()};

		return (OrganizationRoleAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}

}
