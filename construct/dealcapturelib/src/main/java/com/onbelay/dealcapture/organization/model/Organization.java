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
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import com.onbelay.dealcapture.organization.snapshot.OrganizationDetail;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OB_ORGANIZATION")
@NamedQueries({
    @NamedQuery(
       name = OrganizationRepositoryBean.FIND_BY_SHORT_NAME,
       query = "SELECT organization " +
			   "  FROM Organization organization " +
       	     "   WHERE organization.detail.shortName = :shortName ")
    
})
public class Organization extends TemporalAbstractEntity {

	private Integer id;

	private OrganizationDetail detail = new OrganizationDetail();
	

	protected Organization() {
	}
	
	
	
	public Organization(OrganizationDetail detailIn) {
		this.detail.copyFrom(detailIn);
		save();
	}

	public Organization(OrganizationSnapshot snapshot) {
		createWith(snapshot);
	}


	protected void createWith(OrganizationSnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		save();
	}
	
	public void updateWith(OrganizationSnapshot snapshot) {
		detail.copyFrom(snapshot.getDetail());
		update();
	}

	protected void addOrganizationRole(OrganizationRole role) {
		role.setOrganization(this);
		role.save();
	}

	@Transient
	public List<OrganizationRole> getOrganizationRoles() {
		return getOrganizationRoleRepository().fetchByOrganizationId(getId());
	}

	public List<EntityId> saveOrganizationRoles(List<OrganizationRoleSnapshot> snapshots) {
		ArrayList<EntityId> ids = new ArrayList<>();
		for (OrganizationRoleSnapshot snapshot : snapshots) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				OrganizationRole role = OrganizationRoleFactory.newOrganizationRole(snapshot.getOrganizationRoleType());
				role.createWith(
						this,
						snapshot);
				ids.add(role.generateEntityId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				OrganizationRole role = getOrganizationRoleRepository().load(snapshot.getEntityId());
				role.updateWith(snapshot);
				ids.add(role.generateEntityId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				OrganizationRole role = getOrganizationRoleRepository().load(snapshot.getEntityId());
				role.delete();
			}
		}
		return ids;
	}
	
	protected void validate() throws OBValidationException {
		detail.validate();
	}

	@Override
	public EntityId generateEntityId() {
		return new EntityId(
				getId(),
				detail.getShortName(),
				detail.getLegalName(),
				getIsExpired());
	}
    

	
	@Override
	@Transient
	public String getEntityName() {
		return "Organization";
	}
	
	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="OrganizationGen", sequenceName="ORGANIZATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "OrganizationGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer organizationId) {
		this.id = organizationId;
	}

	@Embedded
	public OrganizationDetail getDetail() {
		return detail;
	}

	public void setDetail(OrganizationDetail detail) {
		this.detail = detail;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		return OrganizationAudit.create(this);
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return OrganizationAudit.findRecentHistory(this);
	}


	private static OrganizationRoleRepository getOrganizationRoleRepository() {
		return (OrganizationRoleRepository) ApplicationContextFactory.getBean(OrganizationRoleRepository.BEAN_NAME);
	}
}
