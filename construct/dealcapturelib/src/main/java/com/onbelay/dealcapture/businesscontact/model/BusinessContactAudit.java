/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "BUSINESS_CONTACT_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = BusinessContactAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT businessContactAudit " +
			   "  FROM BusinessContactAudit businessContactAudit " +
       		    "WHERE businessContactAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND businessContactAudit.businessContact = :businessContact")
})

public class BusinessContactAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "BusinessContactAudit.FIND_AUDIT_BY_TO_DATE";
	private Integer id;
	
	private BusinessContact businessContact;
	
	private BusinessContactDetail detail = new BusinessContactDetail();
	
	
	protected BusinessContactAudit() {
		
	}
	
    protected static BusinessContactAudit create(BusinessContact businessContact) {
    	BusinessContactAudit audit = new BusinessContactAudit();
    	audit.businessContact = businessContact;
    	audit.copyFrom(businessContact);
    	return audit;
	}

	@Id
	@Column(name="AUDIT_ID", updatable = false, insertable = false)
    @SequenceGenerator(name="BusinessContactAuditGen", sequenceName="BUSINESS_CONTACT_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "BusinessContactAuditGen")
    
	public Integer getId() {
		return id;
	}
	
	private void setId(Integer businessContactId) {
		this.id = businessContactId;
	}
	
	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public BusinessContact getBusinessContact() {
		return businessContact;
	}

	private void setBusinessContact(BusinessContact businessContact) {
		this.businessContact = businessContact;
	}

	@Embedded
	public BusinessContactDetail getDetail() {
		return detail;
	}
	
	protected void setDetail(BusinessContactDetail detail) {
		this.detail = detail;
	}


	public static BusinessContactAudit findRecentHistory(BusinessContact businessContact) {
		String[] parmNames = {"businessContact", "date" };
		Object[] parms =     {businessContact,   DateUtils.getValidToDateTime()};

		return (BusinessContactAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return businessContact;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		BusinessContact businessContact = (BusinessContact) entity;
		this.detail.copyFrom(businessContact.getDetail());
		
	}
	
	
	

}
