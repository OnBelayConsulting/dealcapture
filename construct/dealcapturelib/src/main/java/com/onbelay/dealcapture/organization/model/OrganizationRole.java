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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleDetail;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;

import jakarta.persistence.*;

@Entity
@Table (name = "ORGANIZATION_ROLE")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
       name = OrganizationRoleRepositoryBean.FIND_BY_ORGANIZATION_ID,
       query = "SELECT role " +
			   "  FROM OrganizationRole role " +
       	     "   WHERE role.organization.id = :organizationId"),
    @NamedQuery(
       name = OrganizationRoleRepositoryBean.GET_BY_SHORT_NAME_ROLE_TYPE,
       query = "SELECT role " +
			   "  FROM OrganizationRole role " +
       	     "   WHERE role.organization.detail.shortName = :shortName " +
			   "   AND role.organizationRoleTypeCode = :roleType"),
    @NamedQuery(
       name = OrganizationRoleRepositoryBean.FIND_BY_SHORT_NAME,
       query = "SELECT role " +
			   "  FROM OrganizationRole role " +
       	     "   WHERE role.organization.detail.shortName = :shortName")
})
public abstract class OrganizationRole extends TemporalAbstractEntity {

	private Integer id;
	
    protected String organizationRoleTypeCode;

	protected  Organization organization;

    protected OrganizationRoleDetail roleDetail = new OrganizationRoleDetail();

	protected OrganizationRole() {
	}

	protected OrganizationRole(OrganizationRoleType roleType) {
		this.organizationRoleTypeCode = roleType.getCode();
		
	}

	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="orgRoleGenerator", sequenceName="ORGANIZATION_ROLE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "orgRoleGenerator")

	public Integer getId() {
		return id;
	}


	public void setId(Integer organizationRoleId) {
		this.id = organizationRoleId;
	}


	@ManyToOne
	@JoinColumn(name = "ORGANIZATION_ID")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Column(name = "ORG_ROLE_TYPE_CODE")
	protected String getOrganizationRoleTypeCode() {
    	return organizationRoleTypeCode;
	}
    
    protected void setOrganizationRoleTypeCode(String role) {
    	this.organizationRoleTypeCode = role;
    }


    public void updateWith(OrganizationRoleSnapshot snapshot) {
    	super.updateWith(snapshot);
    	roleDetail.copyFrom(snapshot.getRoleDetail());
    }
    
    public void updateWith(OrganizationRoleDetail roleDetailIn) {
    	this.roleDetail.copyFrom(roleDetailIn);
    	update();
    }
	
	protected void validate() throws OBValidationException {
		roleDetail.validate();
	}
    
    protected void createWith(
			Organization organization,
			OrganizationRoleSnapshot snapshot) {
		roleDetail.setDefaults();
    	super.createWith(snapshot);
    	roleDetail.copyFrom(snapshot.getRoleDetail());
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

	@Transient
	protected OrganizationRoleRepositoryBean geOrganizationRoleRepository() {
		return (OrganizationRoleRepositoryBean) ApplicationContextFactory.getBean(OrganizationRoleRepositoryBean.BEAN_NAME);
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return OrganizationRoleAudit.findRecentHistory(this);
	}

	
}
