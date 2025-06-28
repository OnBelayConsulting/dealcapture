/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.snapshot;

import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import org.hibernate.type.YesNoConverter;

public class BusinessContactDetail extends AbstractDetail {

	private Integer externalReferenceId;

	private String firstName;
	private boolean isFirstNameNull;
	
	private String lastName;
	private boolean isLastNameNull;

	private String email;
	private boolean isEmailNull;

	private Boolean isCompanyTrader;
	private Boolean isCounterpartyTrader;
	private Boolean isAdministrator;

	
	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		isFirstNameNull = isNull(firstName);
		this.firstName = firstName;
	}
	
	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		isLastNameNull = isNull(lastName);
		this.lastName = lastName;
	}


	@Column(name = "CONTACT_EMAIL")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		isEmailNull = isNull(email);
		this.email = email;
	}

	@Column(name = "IS_COMPANY_TRADER")
	@Convert(
			converter = YesNoConverter.class
	)
	public Boolean getIsCompanyTrader() {
		return isCompanyTrader;
	}

	public void setIsCompanyTrader(Boolean companyTrader) {
		isCompanyTrader = companyTrader;
	}

	@Column(name = "IS_COUNTERPARTY_TRADER")
	@Convert(
			converter = YesNoConverter.class
	)
	public Boolean getIsCounterpartyTrader() {
		return isCounterpartyTrader;
	}

	public void setIsCounterpartyTrader(Boolean counterpartyTrader) {
		isCounterpartyTrader = counterpartyTrader;
	}

	@Column(name = "IS_ADMINISTRATOR")
	@Convert(
			converter = YesNoConverter.class
	)
	public Boolean getIsAdministrator() {
		return isAdministrator;
	}

	public void setIsAdministrator(Boolean administrator) {
		isAdministrator = administrator;
	}

	@Override
	public void validate() throws OBValidationException {
		super.validate();
	}

	public void copyFrom(BusinessContactDetail copy) {
		
		if (copy.firstName != null || copy.isFirstNameNull)
			this.firstName = copy.firstName;
		
		if (copy.lastName != null || copy.isLastNameNull)
			this.lastName = copy.lastName;

		if (copy.email != null || copy.isEmailNull)
			this.email = copy.email;

		if (copy.externalReferenceId != null)
			this.externalReferenceId = copy.externalReferenceId;

		if (copy.isAdministrator != null)
			this.isAdministrator = copy.isAdministrator;

		if (copy.isCompanyTrader != null)
			this.isCompanyTrader = copy.isCompanyTrader;

		if (copy.isCounterpartyTrader != null)
			this.isCounterpartyTrader = copy.isCounterpartyTrader;
	}
	
	public String toString() {
		return firstName + " : " + lastName;
	}


	@Column(name = "EXTERNAL_REF_ID")
	public Integer getExternalReferenceId() {
		return externalReferenceId;
	}

	public void setExternalReferenceId(Integer externalReferenceId) {
		this.externalReferenceId = externalReferenceId;
	}

}
