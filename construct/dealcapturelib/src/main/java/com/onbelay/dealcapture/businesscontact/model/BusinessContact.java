/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactDetail;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import jakarta.persistence.*;

@Entity
@Table(name = "BUSINESS_CONTACT")
@NamedQueries({
    @NamedQuery(
       name = BusinessContactRepositoryBean.FETCH_ALL_BUSINESS_CONTACTS,
       query = "SELECT contact "
       		+ "   FROM BusinessContact contact " +
       	     "ORDER BY contact.detail.lastName DESC"),
	@NamedQuery(
		name = BusinessContactRepositoryBean.FIND_BY_EMAIL,
		query = "SELECT contact "
			+ "    FROM BusinessContact contact " +
				"WHERE contact.detail.email = :email"),
		@NamedQuery(
				name = BusinessContactRepositoryBean.FIND_BY_EXTERNAL_REFERENCE,
				query = "SELECT contact "
						+ "    FROM BusinessContact contact " +
						"WHERE contact.detail.externalReferenceId = :externalReferenceId"),
})
public class BusinessContact extends TemporalAbstractEntity {

	private Integer id;
	private BusinessContactDetail detail = new BusinessContactDetail();

	protected BusinessContact() {
		
	}
	
	@Override
	@Transient
	public String getEntityName() {
		return "BusinessContact";
	}

	
	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="BusinessContactGen", initialValue=1, sequenceName="BUSINESS_CONTACT_SEQ", allocationSize = 1 )
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "BusinessContactGen")
    
	public Integer getId() {
		return id;
	}
	
	private void setId(Integer organizationId) {
		this.id = organizationId;
	}

	public static BusinessContact create(BusinessContactSnapshot snapshot) {
		BusinessContact organization = new BusinessContact();
		organization.createWith(snapshot);
		return organization;
	}

	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		detail.validate();
	}

	protected void createWith(BusinessContactSnapshot snapshot) {
		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		save();
	}
	
	public void updateWith(BusinessContactSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		update();
	}

	@Override
	public EntityId generateEntityId() {
		return new EntityId(
				getId(),
				detail.getEmail(), buildFullName(), getIsExpired());
	}

	public String buildFullName() {
		return detail.getFirstName() + " " + detail.getLastName() +"(" + detail.getEmail() + ")";
	}

	@Embedded
	public BusinessContactDetail getDetail() {
		return detail;
	}
	
	protected void setDetail(BusinessContactDetail detail) {
		this.detail = detail;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		return BusinessContactAudit.create(this);
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return BusinessContactAudit.findRecentHistory(this);
	}


}
